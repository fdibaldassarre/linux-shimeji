package com.group_finity.mascot.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.group_finity.mascot.action.Action;
import com.group_finity.mascot.exception.ActionInstantiationException;
import com.group_finity.mascot.exception.ConfigurationException;

public class ActionRef implements IActionBuilder {

	private static final Logger log = Logger.getLogger(ActionRef.class.getName());

	private final Configuration configuration;

	private final String name;

	private final Map<XmlIdentifiers, String> params = new LinkedHashMap<>();

	public ActionRef(final Configuration configuration, final Entry refNode) {
		this.configuration = configuration;

		this.name = refNode.getAttribute(XmlIdentifiers.Name);
		this.getParams().putAll(refNode.getAttributes());
		log.log(Level.INFO, "Created({0})", this);

	// Convert every non-jump ActionReference to multiwindow format.
	// Necessary for using 'standard' configurations. 
		// TODO
		/*
		if (!name.contains("ジャンプ")) {
			String s = params.get("目的地X");
			if (s != null) {
				if (s.contains("environment.activeIE")) {
					s = s.replaceAll("environment.activeIE","curIE");
					params.put("目的地X",s);
				}
			}
			s = params.get("目的地Y");
			if (s != null) {
				if (s.contains("environment.activeIE")) {
					s = s.replaceAll("environment.activeIE","curIE");
					params.put("目的地Y",s);
				}
			}
			s = params.get("条件");
			if (s != null) {
				if (s.contains("environment.activeIE")) {
					s = s.replaceAll("environment.activeIE","curIE");
					params.put("目的地X",s);
				}
			}
		}
		*/
	}

	@Override
	public String toString() {
		return "ActionRef(" + getName() + ")";
	}

	private String getName() {
		return this.name;
	}

	private Map<XmlIdentifiers, String> getParams() {
		return this.params;
	}

	@Override
	public void validate() throws ConfigurationException {
		if (!configuration.getActionBuilders().containsKey(getName())) {
			throw new ConfigurationException("No such behaviour(" + this + ")");
		}
	}

	public Action buildAction(final Map<XmlIdentifiers, String> params) throws ActionInstantiationException {
		final Map<XmlIdentifiers, String> newParams = new LinkedHashMap<>(params);
		newParams.putAll(getParams());
		return configuration.buildAction(getName(), newParams);
	}
}
