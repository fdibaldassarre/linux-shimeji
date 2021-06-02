package com.group_finity.mascot.action;

import java.awt.Point;
import java.util.List;

import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.environment.Area;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

/**
 * IE を投げるアクション.
 * @author Yuki Yamada
 */
public class ThrowIE extends Animate {

	private static final int DEFAULT_INITIALVX = 32;

	private static final int DEFAULT_INITIALVY = -10;

	private static final double DEFAULT_GRAVITY = 0.5;

	public ThrowIE(final List<Animation> animations, final VariableMap params) {
		super(animations, params);

	}

	@Override
	public boolean hasNext() throws VariableException {

		final boolean ieVisible = getEnvironment().getActiveIE().isVisible();

		return super.hasNext() && ieVisible;
	}

	@Override
	protected void tick() throws LostGroundException, VariableException {

		super.tick();

		final Area activeIE = getEnvironment().getActiveIE();

		if (activeIE.isVisible()) {
			if (getMascot().isLookRight()) {
				getEnvironment().moveActiveIE(new Point(activeIE.getLeft() + getInitialVx(), activeIE.getTop()
						+ getInitialVy() + (int) (getTime() * getGravity())));
			} else {
				getEnvironment().moveActiveIE(new Point(activeIE.getLeft() - getInitialVx(), activeIE.getTop()
						+ getInitialVy() + (int) (getTime() * getGravity())));
			}
		}

	}

	private double getGravity() throws VariableException {
		return eval(XmlIdentifiers.Gravity, Number.class, DEFAULT_GRAVITY).doubleValue();
	}

	private int getInitialVy() throws VariableException {
		return eval(XmlIdentifiers.InitialVY, Number.class, DEFAULT_INITIALVY).intValue();
	}

	private int getInitialVx() throws VariableException {
		return eval(XmlIdentifiers.InitialVX, Number.class, DEFAULT_INITIALVX).intValue();
	}
}
