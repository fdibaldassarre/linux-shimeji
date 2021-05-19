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
	NextBehavior("次の行動リスト", "NextBehavior"),
	Add("追加", "Add"),
	BehaviorReference("行動参照", "BehaviorReference")

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
	
}
