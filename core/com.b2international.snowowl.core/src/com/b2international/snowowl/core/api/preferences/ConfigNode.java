/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.core.api.preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * A generic configuration node for storing settings. It is more lightweight than a Preferences node.
 * 
 *
 * @param <V> value type
 * @param <C> child type
 */
public class ConfigNode<V, C> {

	private final String key;
	private V value;
	private Map<String, C> children;
	private String defaultChildKey;
	
	public ConfigNode(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public V getValue() {
		return value;
	}
	public ConfigNode<V, C> setValue(V value) {
		this.value = value;
		return this;
	}
	
	public ConfigNode<V, C> addChild(String key, C child) {
		if(children == null) {
			children = new HashMap<String, C>();
		}
		children.put(key, child);
		return this;
	}
	
	public C getChild(String key) {
		return hasChildren() ? children.get(key) : null;
	}
	
	public boolean hasChild(String key) {
		return hasChildren() && children.containsKey(key);
	}
	
	public ConfigNode<V, C> removeChild(String key) {
		if(hasChildren()) {
			children.remove(key);
		}
		return this;
	}
	
	public Map<String, C> getChildren() {
		return children;
	}
	
	public String getDefaultChildKey() {
		return defaultChildKey;
	}
	public ConfigNode<V, C> setDefaultChildKey(String key) {
		defaultChildKey = key;
		return this;
	}
	public C getDefaultChild() {
		return defaultChildKey == null ? null : getChild(defaultChildKey);
	}
	
	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}
}