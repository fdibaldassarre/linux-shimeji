package com.group_finity.mascot.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.action.Action;
import com.group_finity.mascot.behavior.Behavior;
import com.group_finity.mascot.exception.ActionInstantiationException;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.ConfigurationException;

public interface Configuration {
	
	XmlLanguages getLanguage();

	void load(Entry node) throws IOException, ConfigurationException;
	
	void validate() throws ConfigurationException;
	
	@Deprecated
	Behavior buildBehavior(String name) throws BehaviorInstantiationException;
	
	Behavior buildBehavior(BehaviourName name) throws BehaviorInstantiationException;
	
	@Deprecated
	Behavior buildBehavior(String previousName, Mascot mascot) throws BehaviorInstantiationException;
	
	Behavior buildBehavior(BehaviourName previousName, Mascot mascot) throws BehaviorInstantiationException;
	
	Path getShimejiImgFolder();
	
	Map<String, ActionBuilder> getActionBuilders();
	
	Action buildAction(final String name, final Map<String, String> params) throws ActionInstantiationException;
	
	
}
