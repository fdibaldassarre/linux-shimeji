package com.group_finity.mascot.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.group_finity.mascot.action.Action;
import com.group_finity.mascot.action.Animate;
import com.group_finity.mascot.action.Move;
import com.group_finity.mascot.action.Select;
import com.group_finity.mascot.action.Sequence;
import com.group_finity.mascot.action.Stay;
import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.exception.ActionInstantiationException;
import com.group_finity.mascot.exception.AnimationInstantiationException;
import com.group_finity.mascot.exception.ConfigurationException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

public class ActionBuilder implements IActionBuilder {

	private static final Logger log = Logger.getLogger(ActionBuilder.class.getName());

	private final XmlActionType type;

	private final String name;

	private final String className;

	private final Map<XmlIdentifiers, String> params = new LinkedHashMap<>();

	private final List<AnimationBuilder> animationBuilders = new ArrayList<AnimationBuilder>();

	private final List<IActionBuilder> actionRefs = new ArrayList<IActionBuilder>();

	private final AnimationBuilderFactory animationBuilderFactory;

	public ActionBuilder(XmlLanguages language, Configuration configuration, final Entry actionNode) throws IOException {
		animationBuilderFactory = new AnimationBuilderFactory(configuration);
		
		this.name = actionNode.getAttribute(XmlIdentifiers.Name);
		this.type = XmlActionType.parseString(actionNode.getAttribute(XmlIdentifiers.Type), language);
		this.className = actionNode.getAttribute(XmlIdentifiers.ClassName);

		log.log(Level.INFO, "Start reading({0})", this);

		this.getParams().putAll(actionNode.getAttributes());
		for (final Entry node : actionNode.selectChildren(XmlIdentifiers.Animation)) {
			this.getAnimationBuilders().add(animationBuilderFactory.create(node));
		}

		for (final Entry node : actionNode.getChildren()) {
			if (node.getName().equals(XmlIdentifiers.ActionReference)) {
				this.getActionRefs().add(new ActionRef(configuration, node));
			} else if (node.getName().equals(XmlIdentifiers.Action)) {
				this.getActionRefs().add(new ActionBuilder(language, configuration, node));
			}
		}

		log.log(Level.INFO, "Read complete");
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "ActionBuilder(" + name + "," + type + "," + className + ")";
	}

	@SuppressWarnings("unchecked")
	public Action buildAction(final Map<XmlIdentifiers, String> params) throws ActionInstantiationException {

		try {
			// 変数マップを生成
			final VariableMap variables = createVariables(params);

			// アニメーションを生成
			final List<Animation> animations = createAnimations();

			// 子アクションを生成
			final List<Action> actions = createActions();

			if (this.type == XmlActionType.Embedded) {
				try {
					final Class<? extends Action> cls = (Class<? extends Action>) Class.forName(className);
					try {

						try {
							return cls.getConstructor(List.class, VariableMap.class).newInstance(animations, variables);
						} catch (final Exception e) {
							// NOTE コンストラクタが無かったと思われるので次へ
						}

						return cls.getConstructor(VariableMap.class).newInstance(variables);
					} catch (final Exception e) {
						// NOTE コンストラクタが無かったと思われるので次へ
					}

					return cls.newInstance();
				} catch (final InstantiationException e) {
					throw new ActionInstantiationException("動作クラスの初期化に失敗(" + this + ")", e);
				} catch (final IllegalAccessException e) {
					throw new ActionInstantiationException("動作クラスにアクセスできません(" + this + ")", e);
				} catch (final ClassNotFoundException e) {
					throw new ActionInstantiationException("動作クラスが見つかりません(" + this + ")", e);
				}

			} else if (this.type == XmlActionType.Move) {
				return new Move(animations, variables);
			} else if (this.type== XmlActionType.Stay) {
				return new Stay(animations, variables);
			} else if (this.type == XmlActionType.Animate) {
				return new Animate(animations, variables);
			} else if (this.type == XmlActionType.Sequence) {
				return new Sequence(variables, actions.toArray(new Action[0]));
			} else if (this.type == XmlActionType.Select) {
				return new Select(variables, actions.toArray(new Action[0]));
			} else {
				throw new ActionInstantiationException("Unknown type(" + this + ")");
			}

		} catch (final AnimationInstantiationException e) {
			throw new ActionInstantiationException("Failed to create animation(" + this + ")", e);
		} catch (final VariableException e) {
			throw new ActionInstantiationException("Parameter evaluation failed(" + this + ")", e);
		}
	}

	public void validate() throws ConfigurationException {

		for (final IActionBuilder ref : this.getActionRefs()) {
			ref.validate();
		}
	}

	private List<Action> createActions() throws ActionInstantiationException {
		final List<Action> actions = new ArrayList<Action>();
		for (final IActionBuilder ref : this.getActionRefs()) {
			actions.add(ref.buildAction(new HashMap<XmlIdentifiers, String>()));
		}
		return actions;
	}

	private List<Animation> createAnimations() throws AnimationInstantiationException {
		final List<Animation> animations = new ArrayList<Animation>();
		for (final AnimationBuilder animationFactory : this.getAnimationBuilders()) {
			animations.add(animationFactory.buildAnimation());
		}
		return animations;
	}

	private VariableMap createVariables(final Map<XmlIdentifiers, String> params) throws VariableException {
		final VariableMap variables = new VariableMap();
		return variables;
		/*
		 * TODO: support for other languages
		for (final Map.Entry<XmlIdentifiers, String> param : this.getParams().entrySet()) {
			variables.put(param.getKey(), Variable.parse(param.getValue()));
		}
		for (final Map.Entry<XmlIdentifiers, String> param : params.entrySet()) {
			variables.put(param.getKey(), Variable.parse(param.getValue()));
		}
		return variables;
		*/
	}

	private Map<XmlIdentifiers, String> getParams() {
		return this.params;
	}

	private List<AnimationBuilder> getAnimationBuilders() {
		return this.animationBuilders;
	}

	private List<IActionBuilder> getActionRefs() {
		return this.actionRefs;
	}


}
