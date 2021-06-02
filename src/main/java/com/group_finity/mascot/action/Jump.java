package com.group_finity.mascot.action;

import java.awt.Point;
import java.util.List;

import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;


/**
 * ジャンプするアクション.
 */
public class Jump extends ActionBase {
	
	private static final int DEFAULT_PARAMETERX = 0;

	private static final int DEFAULT_PARAMETERY = 0;

	private static final double DEFAULT_VELOCITY = 20.0;

	public Jump(final List<Animation> animations, final VariableMap params) {
		super(animations, params);
		
		
	}
	
	@Override
	public boolean hasNext() throws VariableException {
		final int targetX = getTargetX();
		final int targetY = getTargetY();

		final double distanceX = targetX - getMascot().getAnchor().x;
		final double distanceY = targetY - getMascot().getAnchor().y - Math.abs(distanceX)/2;

		final double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
		
		return super.hasNext() && (distance != 0);
	}

	@Override
	protected void tick() throws LostGroundException, VariableException {

		final int targetX = getTargetX();
		final int targetY = getTargetY();

		getMascot().setLookRight(getMascot().getAnchor().x < targetX);

		final double distanceX = targetX - getMascot().getAnchor().x;
		final double distanceY = targetY - getMascot().getAnchor().y - Math.abs(distanceX)/2;

		final double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

		final double velocity = getVelocity();
		
		if (distance != 0) {
			final int velocityX = (int) (velocity * distanceX / distance);
			final int velocityY = (int) (velocity * distanceY / distance);

			getMascot().setAnchor(new Point(getMascot().getAnchor().x + velocityX, 
					getMascot().getAnchor().y + velocityY));
			getAnimation().next(getMascot(),getTime());
		}

		if (distance <= velocity) {
			getMascot().setAnchor(new Point(targetX, targetY));
		}

	}

	private double getVelocity() throws VariableException {
		return eval(XmlIdentifiers.VelocityParam, Number.class, DEFAULT_VELOCITY).doubleValue();
	}

	private int getTargetY() throws VariableException{
		return eval(XmlIdentifiers.TargetY, Number.class, DEFAULT_PARAMETERY).intValue();
	}

	private int getTargetX() throws VariableException {
		return eval(XmlIdentifiers.TargetX, Number.class, DEFAULT_PARAMETERX).intValue();
	}

}
