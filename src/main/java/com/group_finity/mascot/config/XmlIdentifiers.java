package com.group_finity.mascot.config;

public enum XmlIdentifiers {
	
	// TODO: split tags and attributes

	Constant("定数", "Constant"),
	Name("名前", "Name"),
	Type("種類", "Type"),
	Value("値", "Value"),
	ClassName("クラス", "Class"),
	
	Animation("アニメーション", "Animation"),
	ActionReference("動作参照", "ActionReference"),
	
	Frequency("頻度", "Frequency"),
	
	Image("画像", "Image"),
	ImageAnchor("基準座標", "ImageAnchor"),
	Velocity("移動速度", "Velocity"),
	Duration("長さ", "Duration"),
	
	ActionList("動作リスト", "ActionList"),
	Action("動作", "Action"),
	BehaviorList("行動リスト", "BehaviorList"),
	Condition("条件", "Condition"),
	Behavior("行動", "Behavior"),
	NextBehavior("次の行動", "NextBehavior"),
	NextBehaviorList("次の行動リスト", "NextBehaviorList"),
	Add("追加", "Add"),
	BehaviorReference("行動参照", "BehaviorReference"),
	BorderType("枠", "BorderType"),
	
	IeOffsetX("IEの端X", "IeOffsetX"),
	IeOffsetY("IEの端Y", "IeOffsetY"),
	
	Gravity("重力", "Gravity"),
	InitialVX("初速X", "InitialVX"),
	InitialVY("初速Y", "InitialVY"),
	VelocityParam("速度", "VelocityParam"),
	RegistanceX("空気抵抗X", "RegistanceX"),
	RegistanceY("空気抵抗Y", "RegistanceY"),
	
	Loop("繰り返し", "Loop"),
	
	X("X", "X"),
	Y("Y", "Y"),
	
	TargetX("目的地X", "TargetX"),
	TargetY("目的地Y", "TargetY"),
	
	LookRight("右向き", "LookRight"),
	
	Gap("ずれ", "Gap"),
	
	BornBehavior("生まれた時の行動", "BornBehavior"),
	BornX("生まれる場所X", "BornX"),
	BornY("生まれる場所Y", "BornY"),

	// ENG-only?
	Hidden("", "Hidden")
	;

	private final String jpnId;
	private final String engId;

	private XmlIdentifiers(String jpnId, String engId) {
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
	
	public static XmlIdentifiers parseString(String name, XmlLanguages language) {
		for(XmlIdentifiers behaviour: XmlIdentifiers.values()) {
			String localized = behaviour.getName(language);
			if(localized.equals(name)) {
				return behaviour;
			}
		}
		throw new IllegalArgumentException("Invalid identifier " + name);
	}
	
}
