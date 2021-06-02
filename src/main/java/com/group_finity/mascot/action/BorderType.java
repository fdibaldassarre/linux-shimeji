package com.group_finity.mascot.action;

import com.group_finity.mascot.config.XmlLanguages;

public enum BorderType {
	Floor("地面", "Floor"),
	Ceiling("天井", "Ceiling"),
	Wall("壁", "Wall")
	;
	
	private final String jpnId;
	private final String engId;
	
	private BorderType(String jpnId, String engId) {
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

	public static BorderType parseString(String name, XmlLanguages language) {
		for(BorderType border: BorderType.values()) {
			String localized = border.getName(language);
			if(localized.equals(name)) {
				return border;
			}
		}
		throw new IllegalArgumentException("Invalid identifier " + name);
	}
}
