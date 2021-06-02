package com.group_finity.mascot.action;

import java.awt.Point;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.environment.Area;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

/**
 * IEを持って歩くアクション.
 * @author Yuki Yamada
 */
public class WalkWithIE extends Move {

	private static final Logger log = Logger.getLogger(Stay.class.getName());

	private static final int DEFAULT_IEOFFSETX = 0;

	private static final int DEFAULT_IEOFFSETY = 0;

	public WalkWithIE(final List<Animation> animations, final VariableMap params) {
		super(animations, params);

	}

	@Override
	protected void tick() throws LostGroundException, VariableException {

		final Area activeIE = getEnvironment().getActiveIE();
		if (!activeIE.isVisible()) {
			log.log(Level.INFO, "IEが非表示になった({0},{1})", new Object[]{ getMascot(), this } ); 
			throw new LostGroundException();
		} 

		final int offsetX = getIEOffsetX();
		final int offsetY = getIEOffsetY();

		// IE をちゃんと持てているかチェック
		if (getMascot().isLookRight()) {
			if ((getMascot().getAnchor().x - offsetX != activeIE.getLeft())
					|| (getMascot().getAnchor().y + offsetY != activeIE.getBottom())) {
				log.log(Level.INFO, "IEから離れた({0},{1})", new Object[]{ getMascot(), this } ); 
				throw new LostGroundException();
			}
		} else {
			if ((getMascot().getAnchor().x + offsetX != activeIE.getRight())
					|| (getMascot().getAnchor().y + offsetY != activeIE.getBottom())) {
				log.log(Level.INFO, "IEから離れた({0},{1})", new Object[]{ getMascot(), this } ); 
				throw new LostGroundException();
			}
		}

		super.tick();

		// IE を移動
		if (activeIE.isVisible()) {
			if (getMascot().isLookRight()) {
				getEnvironment().moveActiveIE(new Point(getMascot().getAnchor().x - offsetX, getMascot().getAnchor().y
						+ offsetY - activeIE.getHeight()));
			} else {
				getEnvironment().moveActiveIE(new Point(getMascot().getAnchor().x + offsetX - activeIE.getWidth(),
						getMascot().getAnchor().y + offsetY - activeIE.getHeight()));
			}
		}

	}

	private int getIEOffsetY() throws VariableException {
		return eval(XmlIdentifiers.IeOffsetY, Number.class, DEFAULT_IEOFFSETY).intValue();
	}

	private int getIEOffsetX() throws VariableException {
		return eval(XmlIdentifiers.IeOffsetX, Number.class, DEFAULT_IEOFFSETX).intValue();
	}
}
