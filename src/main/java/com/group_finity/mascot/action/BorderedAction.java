package com.group_finity.mascot.action;

import java.awt.Point;
import java.util.List;
import java.util.logging.Logger;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.environment.Border;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;
import com.group_finity.mascot.environment.NotOnVisibleBorder;


/**
 * 枠にくっついて動くアクションの基底クラス.
 * @author Yuki Yamada
 */
public abstract class BorderedAction extends ActionBase {

	public static final String DEFAULT_BORDERTYPE = null;

	private Border border;

	private BorderType borderType;

	public BorderedAction(final List<Animation> animations, final VariableMap params) {
		super(animations, params);
	}

	@Override
	public void init(final Mascot mascot) throws VariableException {
		super.init(mascot);

		borderType = getBorderType();

		if (borderType == BorderType.Ceiling) {
			this.setBorder(getEnvironment().getCeiling());
		} else if (borderType == BorderType.Wall) {
			this.setBorder(getEnvironment().getWall());
		} else if (borderType == BorderType.Floor) {
			this.setBorder(getEnvironment().getFloor());
		}

	}

	@Override
	protected void tick() throws LostGroundException, VariableException {

		if (getBorder() != null) {
			// 枠が動いているかも
			getMascot().setAnchor(getBorder().move(getMascot().getAnchor()));
		}
	}
	
	private BorderType getBorderType() throws VariableException {
		String borderRaw = eval(XmlIdentifiers.BorderType, String.class, DEFAULT_BORDERTYPE);
		if(borderRaw == null) {
			return null;
		}
		return BorderType.parseString(borderRaw, getVariables().getLanguage());
	}

	private void setBorder(final Border border) {
		this.border = border;

	}
	protected Border getBorder() {
		return this.border;
	}

	@Override
	public boolean hasNext() throws VariableException {
		Point p = new Point();
		if (borderType == BorderType.Ceiling) {
			p = getMascot().getAnchor();
				for (int i=2;i>1;i--) {
				int x = p.x;
				int y = p.y;
				getMascot().setAnchor(new Point(x+i,y));
				if (getEnvironment().getCeiling() instanceof NotOnVisibleBorder) return false;
				if (getMascot().isLookRight() && (getEnvironment().getWorkArea().getLeft() == getMascot().getAnchor().getX())) return false;
				//if (i == 0) continue;
				getMascot().setAnchor(new Point(x-i,y));
				if (!getMascot().isLookRight() && getEnvironment().getWorkArea().getLeft() == (getMascot().getAnchor().getX())) return false;
				if (getEnvironment().getCeiling() instanceof NotOnVisibleBorder) return false;
			}
			getMascot().setAnchor(p);
		}
		if (borderType == BorderType.Wall) {
			p = getMascot().getAnchor();
				for (int i=2;i>0;i--) {
				int x = p.x;
				int y = p.y;
				getMascot().setAnchor(new Point(x,y+i));
				if (getEnvironment().getWall() instanceof NotOnVisibleBorder) return false;
				if (i == 0) continue;
				getMascot().setAnchor(new Point(x,y-i));
				if (getEnvironment().getWall() instanceof NotOnVisibleBorder) return false;
			}
			getMascot().setAnchor(p);
		}
		if (borderType == BorderType.Floor) {
			p = getMascot().getAnchor();
				for (int i=2;i>0;i--) {
				int x = p.x;
				int y = p.y;
				getMascot().setAnchor(new Point(x+i,y));
				if (getEnvironment().getFloor() instanceof NotOnVisibleBorder) return false;
				if (i == 0) continue;
				getMascot().setAnchor(new Point(x-i,y));
				if (getEnvironment().getFloor() instanceof NotOnVisibleBorder) return false;
			}
			getMascot().setAnchor(p);
		}
		return super.hasNext();
	}

}
