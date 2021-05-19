package com.group_finity.mascot.config;

public enum BehaviourName {

	Fall("落下する", "Fall"),
	Thrown("投げられる", "Thrown"),
	Dragged("ドラッグされる", "Dragged")
	;

	private final String jpnId;
	private final String engId;

	BehaviourName(String jpnId, String engId) {
		this.jpnId = jpnId;
		this.engId = engId;
	}
	
	public String getName(XmlLanguages lang) {
		if(lang == XmlLanguages.JPN) {
			return jpnId;
		} else {
			return engId;
		}
	}

	public static BehaviourName parseString(String name, XmlLanguages language) {
		for(BehaviourName behaviour: BehaviourName.values()) {
			String localized = behaviour.getName(language);
			if(localized.equals(name)) {
				return behaviour;
			}
		}
		return null;
	}
}
