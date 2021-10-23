package com.group_finity.mascot.action;

import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

/**
 * 振り返るアクション.
 * @author Yuki Yamada
 */
public class Look extends InstantAction {

	public Look(final VariableMap params) {
		super(params);
	}

	@Override
	protected void apply() throws VariableException {
		getMascot().setLookRight(isLookRight());
	}

	private Boolean isLookRight() throws VariableException {
		return eval(XmlIdentifiers.LookRight, Boolean.class, !getMascot().isLookRight());
	}
}
