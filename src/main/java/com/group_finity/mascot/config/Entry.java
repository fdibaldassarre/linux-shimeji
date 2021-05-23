package com.group_finity.mascot.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Entry {

	private final Element element;
	
	private Map<XmlIdentifiers, String> attributes;
	
	private List<Entry> children;
	
	private Map<XmlIdentifiers, List<Entry> > selected = new HashMap<>();

	private final XmlLanguages language;
	
	public Entry(Element element, XmlLanguages language){
		this.element = element;
		this.language = language;
	}
	
	public XmlIdentifiers getName() {
		String tagName = this.element.getTagName();
		return XmlIdentifiers.parseString(tagName, language);
	}

	
	public Map<XmlIdentifiers, String> getAttributes() {
		if ( this.attributes!=null) {
			return this.attributes;
		}
		
		this.attributes = new LinkedHashMap<>();
		final NamedNodeMap attrs = this.element.getAttributes();
		for(int i = 0; i<attrs.getLength(); ++i ) {
			final Attr attr = (Attr)attrs.item(i);
			XmlIdentifiers ide = XmlIdentifiers.parseString(attr.getName(), language);
			this.attributes.put(ide, attr.getValue());
		}
		
		return this.attributes;
	}
	
	public String getAttribute(XmlIdentifiers attributeIdentifier){
		String attributeName = attributeIdentifier.getName(language);
		final Attr attribute = this.element.getAttributeNode(attributeName);
		if ( attribute==null ) {
			return null;
		}
		return attribute.getValue();
	}
	
	public List<Entry> selectChildren(XmlIdentifiers tagIdentifier) {

		List<Entry> children = this.selected.get(tagIdentifier);
		if ( children!=null ) {
			return children;
		}
		children = new ArrayList<Entry>();
		for( final Entry child : getChildren() ) {
			if ( child.getName().equals(tagIdentifier)) {
				children.add(child);
			}
		}
		
		this.selected.put(tagIdentifier, children);
		
		return children;
	}


	public List<Entry> getChildren() {
		
		if ( this.children!=null) {
			return this.children;
		}
		
		this.children = new ArrayList<Entry>();
		final NodeList childNodes = this.element.getChildNodes();
		for( int i = 0; i<childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if ( childNode instanceof Element ) {
				this.children.add(new Entry((Element)childNode, language));
			}
		}
		
		return this.children;
	}
}
