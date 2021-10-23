package com.group_finity.mascot.config;

public class BehaviourName {
	
	public static BehaviourName parseString(String name, XmlLanguages language) {
		KnownBehaviour known = KnownBehaviour.parseString(name, language);
		return new BehaviourName(name, known);
	}
	
	private final String name;
	private final KnownBehaviour known;
	
	private BehaviourName(String name) {
		this(name, null);
	}
	
	private BehaviourName(String name, KnownBehaviour known) {
		this.name = name;
		this.known = known;
	}

	public boolean isIeThrow() {
		return known != null && known.isIeThrow();
	}

	public boolean isFall() {
		return known == KnownBehaviour.Fall;
	}

	public boolean isJump() {
		return known != null && known.isJump();
	}

	public String getName() {
		return name;
	}
	
}
