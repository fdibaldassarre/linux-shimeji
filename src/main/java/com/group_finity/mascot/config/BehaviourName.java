package com.group_finity.mascot.config;

public enum BehaviourName {

	Fall("落下する", "Fall"),
	Thrown("投げられる", "Thrown"),
	Dragged("ドラッグされる", "Dragged"),
	ChaseMouse("マウスの周りに集まる", "ChaseMouse"),
	SitAndFaceMouse("座ってマウスのほうを見る", "SitAndFaceMouse"),
	SitAndSpinHead("座ってマウスのほうを見てたら首が回った", "SitAndSpinHead"),
	SitWhileDanglingLegs("座って足をぶらぶらさせる", "SitWhileDanglingLegs"),
	PullUp("引っこ抜かれる", "PullUp"),
	Divided("分裂した", "Divided"),
	StandUp("立ってボーっとする", "StandUp"),
	SitDown("座ってボーっとする", "SitDown"),
	LieDown("寝そべってボーっとする", "LieDown"),
	HoldOntoWall("壁に掴まってボーっとする", "HoldOntoWall"),
	FallFromWall("壁から落ちる", "FallFromWall"),
	HoldOntoCeiling("天井に掴まってボーっとする", "HoldOntoCeiling"),
	FallFromCeiling("天井から落ちる", "FallFromCeiling"),
	WalkAlongWorkAreaFloor("ワークエリアの下辺を歩く", "WalkAlongWorkAreaFloor"),
	RunAlongWorkAreaFloor("ワークエリアの下辺を走る", "RunAlongWorkAreaFloor"),
	CrawlAlongWorkAreaFloor("ワークエリアの下辺でずりずり", "CrawlAlongWorkAreaFloor"),
	WalkLeftAlongFloorAndSit("ワークエリアの下辺の左の端っこで座る", "WalkLeftAlongFloorAndSit"),
	WalkRightAlongFloorAndSit("ワークエリアの下辺の右の端っこで座る", "WalkRightAlongFloorAndSit"),
	
	SplitIntoTwo("分裂する", "SplitIntoTwo"),
	PullUpShimeji("引っこ抜く", "PullUpShimeji"),
	
	GrabWorkAreaBottomLeftWall("ワークエリアの下辺から左の壁によじのぼる", "GrabWorkAreaBottomLeftWall"),
	GrabWorkAreaBottomRightWall("ワークエリアの下辺から右の壁によじのぼる", "GrabWorkAreaBottomRightWall"),
	WalkLeftAndSit("走ってワークエリアの下辺の左の端っこで座る", "WalkLeftAndSit"),
	WalkRightAndSit("走ってワークエリアの下辺の右の端っこで座る", "WalkRightAndSit"),
	WalkAndGrabBottomLeftWall("走ってワークエリアの下辺から左の壁によじのぼる", "WalkAndGrabBottomLeftWall"),
	WalkAndGrabBottomRightWall("走ってワークエリアの下辺から右の壁によじのぼる", "WalkAndGrabBottomRightWall"),
	ClimbHalfwayAlongWall("ワークエリアの壁を途中まで登る", "ClimbHalfwayAlongWall"),
	
	ClimbAlongWall("ワークエリアの壁を登る", "ClimbAlongWall"),
	ClimbAlongCeiling("ワークエリアの上辺を伝う", "ClimbAlongCeiling"),
	
	JumpFromLeftWall("左の壁に飛びつく", "JumpFromLeftWall", true),
	JumpFromRightWall("右の壁に飛びつく", "JumpFromRightWall", true),
	
	
	// IE
	WalkAlongIECeiling("IEの天井を歩く", "WalkAlongIECeiling"),
	RunAlongIECeiling("IEの天井を走る", "RunAlongIECeiling"),
	CrawlAlongIECeiling("IEの天井でずりずり", "CrawlAlongIECeiling"),
	SitOnTheLeftEdgeOfIE("IEの天井の左の端っこで座る", "SitOnTheLeftEdgeOfIE"),
	SitOnTheRightEdgeOfIE("IEの天井の右の端っこで座る", "SitOnTheRightEdgeOfIE"),
	JumpFromLeftEdgeOfIE("IEの天井の左の端っこから飛び降りる", "JumpFromLeftEdgeOfIE"),
	JumpFromRightEdgeOfIE("IEの天井の右の端っこから飛び降りる", "JumpFromRightEdgeOfIE"),
	WalkLeftAlongIEAndSit("走ってIEの天井の左の端っこで座る", "WalkLeftAlongIEAndSit"),
	WalkRightAlongIEAndSit("走ってIEの天井の右の端っこで座る", "WalkRightAlongIEAndSit"),
	WalkLeftAlongIEAndJump("走ってIEの天井の左の端っこから飛び降りる", "WalkLeftAlongIEAndJump"),
	WalkRightAlongIEAndJump("走ってIEの天井の右の端っこから飛び降りる", "WalkRightAlongIEAndJump"),
	
	HoldOntoIEWall("IEの壁を途中まで登る", "HoldOntoIEWall"),
	ClimbIEWall("IEの壁を登る", "ClimbIEWall"),
	ClimbIEBottom("IEの下辺を伝う", "ClimbIEBottom"),
	GrabIEBottomLeftWall("IEの下辺から左の壁によじのぼる", "GrabIEBottomLeftWall"),
	GrabIEBottomRightWall("IEの下辺から右の壁によじのぼる", "GrabIEBottomRightWall"),
	
	JumpFromBottomOfIE("IEの下に飛びつく", "JumpFromBottomOfIE", true),
	
	JumpOnIELeftWall("IEの左に飛びつく", "JumpOnIELeftWall", true),
	JumpOnIERightWall("IEの右に飛びつく", "JumpOnIERightWall", true),
	ThrowIEFromLeft("IEを右に投げる", "ThrowIEFromLeft", false, true),
	ThrowIEFromRight("IEを左に投げる", "ThrowIEFromRight", false, true),
	
	WalkAndThrowIEFromRight("走ってIEを右に投げる", "WalkAndThrowIEFromRight", false, true),
	WalkAndThrowIEFromLeft("走ってIEを左に投げる", "WalkAndThrowIEFromLeft", false, true)
	;

	private final String jpnId;
	private final String engId;
	private final boolean isIeThrow;
	private boolean isJump;
	
	BehaviourName(String jpnId, String engId) {
		this(jpnId, engId, false, false);
	}
	
	BehaviourName(String jpnId, String engId, boolean isJump) {
		this(jpnId, engId, isJump, false);
	}

	BehaviourName(String jpnId, String engId, boolean isJump, boolean isIeThrow) {
		this.jpnId = jpnId;
		this.engId = engId;
		this.isJump = isJump;
		this.isIeThrow = isIeThrow;
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
		throw new IllegalArgumentException("Invalid behaviour name " + name);
	}
	
	public boolean isIeThrow() {
		return isIeThrow;
	}
	
	public boolean isJump() {
		return isJump;
	}
}
