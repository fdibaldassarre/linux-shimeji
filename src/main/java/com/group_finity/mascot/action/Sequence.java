package com.group_finity.mascot.action;


import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

/**
 * 複数のアクションを直列に一つにまとめたアクション.
 */
public class Sequence extends ComplexAction {

	private static final boolean DEFAULT_LOOP = false;

	public Sequence(final VariableMap params, final Action... actions) {
		super(params, actions);
	}

	@Override
	public boolean hasNext() throws VariableException {

		seek();

		return super.hasNext();
	}

	@Override
	protected void setCurrentAction(final int currentAction) throws VariableException {
		super.setCurrentAction(isLoop() ? currentAction % getActions().length : currentAction);
	}

	private Boolean isLoop() throws VariableException {
		return eval(XmlIdentifiers.Loop, Boolean.class, DEFAULT_LOOP);
	}

}
