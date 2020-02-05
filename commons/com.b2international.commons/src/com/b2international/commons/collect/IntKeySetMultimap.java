/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.collect;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.ints.IntSet;

/**
 * Type safe set multimap implementation using primitive integer keys. 
 */
public class IntKeySetMultimap<V> {

	private final IntKeyMap<Set<V>> map;

	public IntKeySetMultimap() {
		this(PrimitiveMaps.<Set<V>>newIntKeyOpenHashMap());
	}

	public IntKeySetMultimap(IntKeyMap<Set<V>> map) {
		this.map = map;
	}

	public void clear() {
		map.clear();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int size() {
		return map.size();
	}

	public void trimToSize() {
		map.trimToSize();
	}

	public boolean containsKey(int key) {
		return map.containsKey(key);
	}

	public IntSet keySet() {
		return map.keySet();
	}

	public Set<V> remove(int key) {
		return map.remove(key);
	}

	public Collection<Set<V>> values() {
		return map.values();
	}
	
	public boolean put(final int key, final V value) {
		Set<V> values = delegateGet(key);
		
		if (values == null) {
			values = Sets.newHashSet();
			delegatePut(key, values);
		}
		
		return values.add(value);
	}
	
	public Set<V> get(final int key) {
		Set<V> values = delegateGet(key);
		return (values != null) ? Collections.<V>unmodifiableSet(values) : Collections.<V>emptySet();
	}
	
	private Set<V> delegatePut(int key, Set<V> value) {
		return map.put(key, value);
	}

	private Set<V> delegateGet(int key) {
		return map.get(key);
	}
}
