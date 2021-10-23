package com.group_finity.mascot.script;

public enum VariableIdentifier {
	mascot,
	action,
	footX("FootX"),
	maxCount,
	TargetX("目的地X"),
	TargetY("目的地Y")
	;
	
	private final String[] additionalIdentifiers;

	private VariableIdentifier(String...additionalIdentifiers) {
		this.additionalIdentifiers = additionalIdentifiers;
	}
	
	public String[] getAdditionalIdentifiers() {
		return additionalIdentifiers;
	}
}
