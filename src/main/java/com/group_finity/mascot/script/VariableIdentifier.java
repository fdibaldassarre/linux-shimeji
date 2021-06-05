package com.group_finity.mascot.script;

public enum VariableIdentifier {
	mascot,
	action,
	footX("FootX");
	
	private final String[] additionalIdentifiers;

	private VariableIdentifier(String...additionalIdentifiers) {
		this.additionalIdentifiers = additionalIdentifiers;
	}
	
	public String[] getAdditionalIdentifiers() {
		return additionalIdentifiers;
	}
}
