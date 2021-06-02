package com.group_finity.mascot.action;

import java.awt.Point;

import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

/**
 * 少しずれるアクション.
 * @author Yuki Yamada
 */
public class Offset extends InstantAction {
	
	private static final int DEFAULT_OFFSETX = 0;

	private static final int DEFAULT_OFFSETY = 0;

	public Offset(final VariableMap params) {
		super(params);
	}

	@Override
	protected void apply() throws VariableException {
		getMascot().setAnchor(
				new Point(getMascot().getAnchor().x + getOffsetX(), getMascot().getAnchor().y + getOffsetY()));
	}

	private int getOffsetY() throws VariableException {
		return eval(XmlIdentifiers.Y, Number.class, DEFAULT_OFFSETY).intValue();
	}

	private int getOffsetX() throws VariableException {
		return eval(XmlIdentifiers.X, Number.class, DEFAULT_OFFSETX).intValue();
	}

}
