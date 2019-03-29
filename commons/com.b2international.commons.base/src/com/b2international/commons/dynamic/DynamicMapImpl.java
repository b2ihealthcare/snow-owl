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
package com.b2international.commons.dynamic;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DynamicMapImpl extends AbstractDynamicMap {

	private static final Predicate<Object> stringKeyFilter = new Predicate<Object>() {
		@Override
		public boolean apply(Object input) {
			return input instanceof String;
		}
	};
	
	private final Map<String, DynamicValue> valueMap;
	
	@SuppressWarnings("unchecked")
	public DynamicMapImpl(Map<?, ?> map) {

		valueMap = ImmutableMap.copyOf((Map<String, DynamicValue>) Maps.transformValues(
				Maps.filterKeys(map, stringKeyFilter), valueConverter));
	}
	
	@Override
	public Object get() {
		return this;
	}

	@Override
	public int size() {
		return valueMap.size();
	}

	@Override
	public boolean isEmpty() {
		return valueMap.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return valueMap.keySet();
	}

	@Override
	public Collection<DynamicValue> values() {
		return valueMap.values();
	}

	@Override
	public boolean containsKey(String key) {
		return valueMap.containsKey(key);
	}

	@Override
	public DynamicValue get(String key, Object defaultValue) {
		return (valueMap.containsKey(key)) ? valueMap.get(key) : DynamicValueImpl.create(defaultValue);
	}

	@Override
	public Collection<Entry> getProperty(String property) {

		Deque<String> propertyParts = new ArrayDeque<String>(Lists.newArrayList(Splitter.on('.').split(property)));
		final String propertyFirstPart = propertyParts.removeFirst();
		
		if (!containsKey(propertyFirstPart)) {
			return Collections.emptyList();
		}
		
		DynamicValue value = get(propertyFirstPart);
		
		if (propertyParts.isEmpty()) {
			return Collections.<Entry>singletonList(new EntryImpl(propertyFirstPart, value));
		}

		// DynamicList is a DynamicMap, both cases are handled here
		DynamicMap map = value.as(DynamicMap.class);
		
		if (map != null) {
			return getPrefixedFollowers(map, propertyFirstPart, propertyParts);
		}
		
		return Collections.emptyList();
	}
	
	@Override
	public boolean propertyContainsList(String property) {

		Deque<String> propertyParts = new ArrayDeque<String>(Lists.newArrayList(Splitter.on('.').split(property)));
		final String propertyFirstPart = propertyParts.removeFirst();
		
		if (!containsKey(propertyFirstPart)) {
			return false;
		}
		
		if (propertyParts.isEmpty()) {
			return false;
		}

		// DynamicList is a DynamicMap, both cases are handled here
		DynamicValue value = get(propertyFirstPart);
		DynamicMap map = value.as(DynamicMap.class);
		
		if (map != null) {
			return map.propertyContainsList(Joiner.on('.').join(propertyParts));
		}
		
		return false;		
	}	
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("valueMap", valueMap).toString();
	}
}