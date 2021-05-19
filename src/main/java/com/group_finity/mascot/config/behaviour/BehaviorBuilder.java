package com.group_finity.mascot.config.behaviour;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.group_finity.mascot.behavior.Behavior;
import com.group_finity.mascot.behavior.UserBehavior;
import com.group_finity.mascot.config.BehaviourName;
import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.config.Entry;
import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.config.XmlLanguages;
import com.group_finity.mascot.exception.ActionInstantiationException;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.ConfigurationException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.Variable;
import com.group_finity.mascot.script.VariableMap;

public class BehaviorBuilder {

	private static final Logger log = Logger.getLogger(BehaviorBuilder.class.getName());

	private final Configuration configuration;

	private final String name;
	private final BehaviourName behaviourName;

	private final String actionName;

	private int frequency;

	private final List<String> conditions;

	private final boolean nextAdditive;

	private final List<BehaviorBuilder> nextBehaviorBuilders = new ArrayList<BehaviorBuilder>();

	private final Map<String, String> params = new LinkedHashMap<String, String>();

	private final XmlLanguages language;

	public BehaviorBuilder(XmlLanguages language, Configuration configuration, final Entry behaviorNode, final List<String> conditions) {
		this.language = language;
		this.configuration = configuration;
		this.name = behaviorNode.getAttribute(XmlIdentifiers.Name);
		this.behaviourName = BehaviourName.parseString(this.name, language);
		String action = behaviorNode.getAttribute(XmlIdentifiers.Action);
		this.actionName = action == null ? name : action;
		this.frequency = Integer.parseInt(behaviorNode.getAttribute(XmlIdentifiers.Frequency));
		this.conditions = new ArrayList<String>(conditions);
		this.getConditions().add(behaviorNode.getAttribute(XmlIdentifiers.Condition));

	// Conversion to multiwindow environment checks
	// Also set IE throw frequency to 0
		if (name.contains("投げる")) frequency = 0;
		if (name.equals("落下する")) frequency = 1;
		if (!name.contains("に飛びつく")) {
			if (conditions != null) {
				for (int i=0;i<conditions.size();i++) {
					String s = conditions.get(i);
					s = s.replaceAll("environment.activeIE","curIE");
					conditions.set(i,s);
				}
			}
		}

		log.log(Level.INFO, "行動読み込み開始({0})", this);

		this.getParams().putAll(behaviorNode.getAttributes());
		this.getParams().remove("名前");
		this.getParams().remove("動作");
		this.getParams().remove("頻度");
		this.getParams().remove("条件");

		boolean nextAdditive = true;

		for (final Entry nextList : behaviorNode.selectChildren(XmlIdentifiers.NextBehavior)) {

			log.log(Level.INFO, "次の行動リスト...");

			nextAdditive = Boolean.parseBoolean(nextList.getAttribute(XmlIdentifiers.Add));

			loadBehaviors(nextList, new ArrayList<String>());
		}

		this.nextAdditive = nextAdditive;

		log.log(Level.INFO, "行動読み込み完了({0})", this);

	}

	@Override
	public String toString() {
		return "BehaviorBuilder(" + getName() + "," + getFrequency() + "," + getActionName() + ")";
	}

	private void loadBehaviors(final Entry list, final List<String> conditions) {

		for (final Entry node : list.getChildren()) {

			if (node.getName().equals(XmlIdentifiers.Condition.getName(language))) {

				final List<String> newConditions = new ArrayList<String>(conditions);
				newConditions.add(node.getAttribute(XmlIdentifiers.Condition));

				loadBehaviors(node, newConditions);

			} else if (node.getName().equals(XmlIdentifiers.BehaviorReference.getName(language))) {
				final BehaviorBuilder behavior = new BehaviorBuilder(language, getConfiguration(), node, conditions);
				getNextBehaviorBuilders().add(behavior);
			}
		}
	}

	public void validate() throws ConfigurationException {

		if ( !getConfiguration().getActionBuilders().containsKey(getActionName()) ) {
			throw new ConfigurationException("No such behaviour("+this+")");
		}
	}

	public Behavior buildBehavior() throws BehaviorInstantiationException {

		try {
			return new UserBehavior(getName(),
						getConfiguration().buildAction(getActionName(),
								getParams()), getConfiguration() );
		} catch (final ActionInstantiationException e) {
			throw new BehaviorInstantiationException("Cannot initialize behaviour("+this+")", e);
		}
	}


	public boolean isEffective(final VariableMap context) throws VariableException {

		for (final String condition : getConditions()) {
			if (condition != null) {
				if (!(Boolean) Variable.parse(condition).get(context)) {
					return false;
				}
			}
		}

		return true;
	}

	public BehaviourName getName() {
		return behaviourName;
	}

	public int getFrequency() {
		return this.frequency;
	}

	private String getActionName() {
		return this.actionName;
	}

	private Map<String, String> getParams() {
		return this.params;
	}

	private List<String> getConditions() {
		return this.conditions;
	}

	private Configuration getConfiguration() {
		return this.configuration;
	}

	public boolean isNextAdditive() {
		return this.nextAdditive;
	}

	public List<BehaviorBuilder> getNextBehaviorBuilders() {
		return this.nextBehaviorBuilders;
	}
}
