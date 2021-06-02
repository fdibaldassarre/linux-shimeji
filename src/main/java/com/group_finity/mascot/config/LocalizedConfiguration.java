package com.group_finity.mascot.config;

import java.awt.Point;
import java.io.IOException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.action.Action;
import com.group_finity.mascot.behavior.Behavior;
import com.group_finity.mascot.config.behaviour.BehaviorBuilder;
import com.group_finity.mascot.exception.ActionInstantiationException;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.ConfigurationException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.Constant;
import com.group_finity.mascot.script.VariableIdentifier;
import com.group_finity.mascot.script.VariableMap;

public class LocalizedConfiguration implements Configuration {

	private static final Logger log = Logger.getLogger(LocalizedConfiguration.class.getName());

	//private final Map<String, String> constants = new LinkedHashMap<String, String>();

	private final Map<String, ActionBuilder> actionBuilders = new LinkedHashMap<>();

	private final Map<BehaviourName, BehaviorBuilder> behaviorBuilders = new LinkedHashMap<>();

	private final Path shimejiImgFolder;
	
	private final XmlLanguages language;
	
	protected LocalizedConfiguration(Path shimejiImgFolder, XmlLanguages language) {
		this.shimejiImgFolder = shimejiImgFolder;
		this.language = language;
	}

	@Override
	public void load(final Entry configurationNode) throws IOException, ConfigurationException {

		log.log(Level.INFO, "Start reading settings");

		// TODO: constants support
//		for (final Entry constant : configurationNode.selectChildren(XmlIdentifiers.Constant)) {
//			log.log(Level.INFO, "Add constant");
//			constants.put( constant.getAttribute(XmlIdentifiers.Name), constant.getAttribute(XmlIdentifiers.Value) );
//		}

		for (final Entry list : configurationNode.selectChildren(XmlIdentifiers.ActionList)) {
			log.log(Level.INFO, "Reading action list");
			for (final Entry node : list.selectChildren(XmlIdentifiers.Action)) {
				final ActionBuilder action = new ActionBuilder(language, this, node);
				if ( this.getActionBuilders().containsKey(action.getName())) {
					throw new ConfigurationException("Duplicate action name:"+action.getName());
				}
				this.getActionBuilders().put(action.getName(), action);
			}
		}

		for (final Entry list : configurationNode.selectChildren(XmlIdentifiers.BehaviorList)) {
			log.log(Level.INFO, "Reading action list");
			loadBehaviors(list, new ArrayList<String>());
		}
		log.log(Level.INFO, "Read complete");
		
	}

	private void loadBehaviors(final Entry list, final List<String> conditions) {
		for (final Entry node : list.getChildren()) {
			if (node.getName().equals(XmlIdentifiers.Condition)) {

				final List<String> newConditions = new ArrayList<String>(conditions);
				newConditions.add(node.getAttribute(XmlIdentifiers.Condition));

				loadBehaviors(node, newConditions);

			} else if (node.getName().equals(XmlIdentifiers.Behavior)) {
				final BehaviorBuilder behavior = new BehaviorBuilder(language, this, node, conditions);
				behaviorBuilders.put(behavior.getName(), behavior);
			}
		}
	}
	
	@Override
	public Action buildAction(final String name, final Map<XmlIdentifiers, String> params) throws ActionInstantiationException {

		final ActionBuilder factory = this.actionBuilders.get(name);
		if (factory == null) {
			throw new ActionInstantiationException("No behaviiur found with name: " + name);
		}

		return factory.buildAction(params);
	}
	
	public void validate() throws ConfigurationException{

		for(final ActionBuilder builder : getActionBuilders().values()) {
			builder.validate();
		}
		for(final BehaviorBuilder builder : behaviorBuilders.values()) {
			builder.validate();
		}
	}
	
	@Override
	public Behavior buildBehavior(BehaviourName previousName, Mascot mascot) throws BehaviorInstantiationException {
		final VariableMap context = new VariableMap(language);
		context.put(VariableIdentifier.mascot, new Constant(mascot));

		// TODO ここ以外で必要な場合は？根本的につくりを見直すべき
//		for( Map.Entry<String, String> e : constants.entrySet() ) {
//			context.put(e.getKey(), e.getValue());
//		}

		final List<BehaviorBuilder> candidates = new ArrayList<BehaviorBuilder>();
		long totalFrequency = 0;
		for (final BehaviorBuilder behaviorFactory : behaviorBuilders.values()) {
			try {
				if (behaviorFactory.isEffective(context)) {
					candidates.add(behaviorFactory);
					totalFrequency += behaviorFactory.getFrequency();
				}
			} catch (final VariableException e) {
				log.log(Level.WARNING, "An error occurred while evaluating the frequency of actions", e);
			}
		}

		if (previousName != null) {
			final BehaviorBuilder previousBehaviorFactory = behaviorBuilders.get(previousName);
			if (!previousBehaviorFactory.isNextAdditive()) {
				totalFrequency = 0;
				candidates.clear();
			}
			for (final BehaviorBuilder behaviorFactory : previousBehaviorFactory.getNextBehaviorBuilders()) {
				try {
					if (behaviorFactory.isEffective(context)) {
						candidates.add(behaviorFactory);
						totalFrequency += behaviorFactory.getFrequency();
					}
				} catch (final VariableException e) {
					log.log(Level.WARNING, "An error occurred while evaluating the total frequency of actions", e);
				}
			}
		}

		if (totalFrequency == 0) {
			mascot.setAnchor(new Point(
					(int) (Math.random() * (mascot.getEnvironment().getScreen().getRight()
							- mascot.getEnvironment()
					.getScreen().getLeft()))
					+ mascot.getEnvironment().getScreen().getLeft(), mascot.getEnvironment().getScreen().getTop() - 256));
			return buildBehavior(BehaviourName.Fall);
		}

		double random = Math.random() * totalFrequency;

		for (final BehaviorBuilder behaviorFactory : candidates) {
			random -= behaviorFactory.getFrequency();
			if (random < 0) {
				return behaviorFactory.buildBehavior();
			}
		}

		return null;
	}

	@Override
	public Behavior buildBehavior(final String name) throws BehaviorInstantiationException {
		System.out.println("Dep" + name);
		BehaviourName behaviourName = BehaviourName.parseString(name, language);
		return buildBehavior(behaviourName);
	}
	
	@Override
	public Behavior buildBehavior(BehaviourName name) throws BehaviorInstantiationException {
		return behaviorBuilders.get(name).buildBehavior();
	}

	public Map<String, ActionBuilder> getActionBuilders() {
		return this.actionBuilders;
	}

	@Override
	public Path getShimejiImgFolder() {
		return shimejiImgFolder;
	}

	@Override
	public XmlLanguages getLanguage() {
		return language;
	}

}
