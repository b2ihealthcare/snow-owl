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
package com.b2international.snowowl.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates unpersisted <em>new, detached and changed</em> terminology components.
 * @param <K> type of the unique identifier of the component
 * @param <M> type of the component
 */
public final class ComponentChangeCache<K, V> {

	private final Map<K, V> newComponents;
	private final Map<K, V> detachedComponents;
	private final Map<K, V> changedComponents;
	
	/**
	 * Creates a new empty cache.
	 */
	public ComponentChangeCache() {
		this(new HashMap<K, V>(), new HashMap<K, V>(), new HashMap<K, V>());
	}
	
	/**
	 * Creates a new cache initialized with the passed in new, detached and changed component maps. 
	 * @param newComponents new components 
	 * @param detachedComponents detached components
	 * @param changedComponents changed components
	 */
	public ComponentChangeCache(final Map<K, V> newMembers, final Map<K, V> detachedMembers, final Map<K, V> changedMembers) {
		this.newComponents = newMembers;
		this.detachedComponents = detachedMembers;
		this.changedComponents = changedMembers;
	}
	
	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		if (null != newComponents)
			newComponents.clear();
		if (null != detachedComponents)
			detachedComponents.clear();
		if (null != changedComponents)
			changedComponents.clear();
	}
	
	/**
	 * Returns with the cache of the new components.
	 * @return new components
	 */
	public Map<K, V> getNewComponents() {
		return newComponents;
	}

	/**
	 * Returns with the detached components. 
	 * @return the components marked as detached
	 */
	public Map<K, V> getDetachedComponents() {
		return detachedComponents;
	}
	
	/**
	 * Returns with all changed components.
	 * @return the changed components.
	 */
	public Map<K, V> getChangedComponents() {
		return changedComponents;
	}
	
}