package com.group_finity.mascot.config;

public enum XmlActionType {

	Embedded("組み込み", "Embedded"),
	Move("移動", "Move"),
	Stay("静止", "Stay"),
	Animate("固定", "Animate"),
	Sequence("複合", "Sequence"),
	Select("選択", "Select")
	;
	
	private final String jpnId;
	private final String engId;

	XmlActionType(String jpnId, String engId) {
		this.jpnId = jpnId;
		this.engId = engId;
	}
	
	protected String getName(XmlLanguages lang) {
		if(lang == XmlLanguages.JPN) {
			return jpnId;
		} else {
			return engId;
		}
	}
	
	public static XmlActionType parseString(String name, XmlLanguages language) {
		for(XmlActionType behaviour: XmlActionType.values()) {
			String localized = behaviour.getName(language);
			if(localized.equals(name)) {
				return behaviour;
			}
		}
		return null;
	}
}
