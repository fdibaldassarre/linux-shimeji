package com.group_finity.mascot.action;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.environment.MascotEnvironment;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.Constant;
import com.group_finity.mascot.script.Variable;
import com.group_finity.mascot.script.VariableIdentifier;
import com.group_finity.mascot.script.VariableMap;

/**
 * アクションの共通機能を実装する抽象クラス.
 * @author Yuki Yamada
 */
public abstract class ActionBase implements Action {

	private static final Logger log = Logger.getLogger(ActionBase.class.getName());

	private static final boolean DEFAULT_CONDITION = true;

	private static final int DEFAULT_DURATION = Integer.MAX_VALUE;

	private Mascot mascot;

	private int startTime;

	private List<Animation> animations;

	protected final VariableMap variables;

	public ActionBase(final List<Animation> animations, final VariableMap context) {
		this.animations = animations;
		this.variables = context;
	}

	@Override
	public String toString() {
		try {
			return "ActionBase(" + getClass().getSimpleName() + "," + getName() + ")";
		} catch (final VariableException e) {
			return "ActionBase(" + getClass().getSimpleName() + "," + null + ")";
		}
	}

	@Override
	public void init(final Mascot mascot) throws VariableException {
		this.setMascot(mascot);
		this.setTime(0);

		log.log(Level.INFO, "Action start({0},{1})", new Object[] { getMascot(), this });

		// スクリプトで使用できるようにmascotとactionを変数マップに追加しておく
		this.getVariables().put(VariableIdentifier.mascot, new Constant(mascot));
		this.getVariables().put(VariableIdentifier.action, new Constant(this));

		// 変数の値を初期化
		getVariables().init();

		// アニメーションを初期化
		for (final Animation animation : this.animations) {
			animation.init();
		}
	}

	@Override
	public void next() throws LostGroundException, VariableException {
		initFrame();
		tick();
	}

	private void initFrame() {

		// 変数の値(フレームごと)を初期化
		getVariables().initFrame();

		// アニメーションのフレームを初期化
		for (final Animation animation : getAnimations()) {
			animation.initFrame();
		}
	}

	private List<Animation> getAnimations() {
		return this.animations;
	}

	protected abstract void tick() throws LostGroundException, VariableException;

	@Override
	public boolean hasNext() throws VariableException {

		final boolean effective = isEffective();
		final boolean intime = getTime() < getDuration();

		return effective && intime;
	}

	private Boolean isEffective() throws VariableException {
		return eval(XmlIdentifiers.Condition, Boolean.class, DEFAULT_CONDITION);
	}

	private int getDuration() throws VariableException {
		return eval(XmlIdentifiers.Duration, Number.class, DEFAULT_DURATION).intValue();
	}

	private void setMascot(final Mascot mascot) {
		this.mascot = mascot;
	}

	protected Mascot getMascot() {
		return this.mascot;
	}

	protected int getTime() {
		return getMascot().getTime() - this.startTime;
	}

	protected void setTime(final int time) {
		this.startTime = getMascot().getTime() - time;
	}

	private String getName() throws VariableException {
		return this.eval(XmlIdentifiers.Name, String.class, null);
	}

	protected Animation getAnimation() throws VariableException {
		for (final Animation animation : getAnimations()) {
			if (animation.isEffective(getVariables())) {
				return animation;
			}
		}

		log.log(Level.SEVERE, "No valid animation was found({0},{1})", new Object[] { getMascot(), this });
		return null;
	}

	protected VariableMap getVariables() {
		return this.variables;
	}

	protected void putVariable(final VariableIdentifier key, final Constant value) {
		synchronized (getVariables()) {
			getVariables().put(key, value);
		}
	}

//	protected <T> T eval(final VariableIdentifier ide, final Class<T> type, final T defaultValue) throws VariableException {
//
//		synchronized (getVariables()) {
//			final Variable variable = getVariables().get(ide);
//			if (variable != null) {
//				return type.cast(variable.get(getVariables()));
//			}
//		}
//
//		return defaultValue;
//	}
	
	protected <T> T eval(final XmlIdentifiers ide, final Class<T> type, final T defaultValue) throws VariableException {

		synchronized (getVariables()) {
			final Variable variable = getVariables().get(ide);
			if (variable != null) {
				return type.cast(variable.get(getVariables()));
			}
		}

		return defaultValue;
	}

	protected MascotEnvironment getEnvironment() {
		return getMascot().getEnvironment();
	}
}
