package com.group_finity.mascot.script;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import com.group_finity.mascot.config.XmlIdentifiers;
import com.group_finity.mascot.config.XmlLanguages;
import com.group_finity.mascot.exception.VariableException;

public class VariableMap {
	
	
	private class DelayedEvaluationBindings implements Bindings {

		private final Map<String, Object> additional = new HashMap<>();
		private final Map<String, Variable> variables;
		private final VariableMap originalMap;

		DelayedEvaluationBindings(VariableMap originalMap, Map<String, Variable> variables) {
			this.originalMap = originalMap;
			this.variables = variables;
		}

		@Override
		public int size() {
			return variables.size();
		}

		@Override
		public boolean isEmpty() {
			return variables.isEmpty();
		}

		@Override
		public boolean containsValue(Object value) {
			return variables.containsValue(value);
		}

		@Override
		public void clear() {
			variables.clear();
		}

		@Override
		public Set<String> keySet() {
			return variables.keySet();
		}

		@Override
		public Collection<Object> values() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<Entry<String, Object>> entrySet() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object put(String name, Object value) {
			return additional.put(name, value);
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> toMerge) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsKey(Object key) {
			return variables.containsKey(key) || additional.containsKey(key);
		}

		@Override
		public Object get(Object key) {
			try {
				if(variables.containsKey(key)) {
					return variables.get(key).get(originalMap);
				} else if(additional.containsKey(key)) {
					return additional.get(key);
				} else {
					return null;
				}
				
			} catch (VariableException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}
	}
	
	private final Map<String, Variable> bindings = new HashMap<>();
	private final XmlLanguages lang;
	
	public VariableMap(XmlLanguages lang) {
		this.lang = lang;
	}
	
	public XmlLanguages getLanguage() {
		return this.lang;
	}
	
	public void put(VariableIdentifier ide, Variable value) {
		bindings.put(ide.toString(), value);
	}
	
	public void put(XmlIdentifiers ide, Variable value) {
		bindings.put(ide.toString(), value);
	}
	
	public Variable get(VariableIdentifier ide) {
		return (Variable) bindings.get(ide.toString());
	}
	
	public Variable get(XmlIdentifiers ide) {
		return (Variable) bindings.get(ide.toString());
	}
	
	public void init() {
		for (Object o : bindings.values()) {
			((Variable) o).init();
		}
	}

	public void initFrame() {
		for (Object o : bindings.values()) {
			((Variable) o).initFrame();
		}
	}
	
	public Bindings getBindings() {
		// Need a delayed evaluation map to avoid infinite recursion since the code sucks badly
		return new DelayedEvaluationBindings(this, bindings);
	}

}
