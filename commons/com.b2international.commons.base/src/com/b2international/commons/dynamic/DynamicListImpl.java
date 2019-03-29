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
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class DynamicListImpl extends AbstractDynamicMap implements DynamicList {

	private final List<DynamicValue> valueList;
	private final Set<String> keySet;
	
	public DynamicListImpl(List<?> list) {
		
		valueList = ImmutableList.copyOf(Lists.transform(list, valueConverter));
		
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		for (int i = 0; i < valueList.size(); i++) builder.add(Integer.toString(i));
		keySet = builder.build();
	}

	@Override
	public int size() {
		return valueList.size();
	}

	@Override
	public boolean isEmpty() {
		return valueList.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return keySet;
	}

	@Override
	public Collection<DynamicValue> values() {
		return valueList;
	}

	@Override
	public boolean containsKey(String key) {

		try {
			int idx = Integer.parseInt(key);
			return idx >= 0 && idx < valueList.size();
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public DynamicValue get(String key, Object defaultValue) {

		try {
			int idx = Integer.parseInt(key);
			return get(idx, defaultValue);
		} catch (NumberFormatException e) {
			return DynamicValueImpl.create(defaultValue);
		}
	}

	@Override
	public Collection<Entry> getProperty(String property) {

		Deque<String> propertyParts = new ArrayDeque<String>(Lists.newArrayList(Splitter.on('.').split(property)));
		final String propertyFirstPart = propertyParts.removeFirst();
		
		if (containsKey(propertyFirstPart)) {
			
			DynamicValue value = get(propertyFirstPart);
			
			if (!propertyParts.isEmpty()) {
				
				DynamicMap map = value.as(DynamicMap.class);
				
				if (map != null) {
					return getPrefixedFollowers(map, propertyFirstPart, propertyParts);
				}
				
				return Collections.emptyList();
			
			} else {
				return Collections.<Entry>singletonList(new EntryImpl(propertyFirstPart, value));
			}
		}
		
		if (isLegalIndex(propertyFirstPart)) {
			return Collections.emptyList();
		}

		if (propertyFirstPart.equals("*")) {
			
			if (propertyParts.isEmpty()) {
				ImmutableList.Builder<Entry> builder = ImmutableList.builder();
				
				for (int i = 0; i < valueList.size(); i++) {
					builder.add(new EntryImpl(Integer.toString(i), valueList.get(i)));
				}
				
				return builder.build();
			}
			
			return Collections.emptyList();
		}
		
		// Re-add the first part, as we're not going to use it here
		return getPrefixedContainedFollowers(property);
	}

	private Collection<Entry> getPrefixedContainedFollowers(String property) {

		ImmutableList.Builder<Entry> builder = ImmutableList.builder();
		
		for (int i = 0; i < valueList.size(); i++) {
			
			DynamicValue value = valueList.get(i);
			DynamicMap map = value.as(DynamicMap.class);
			
			if (map != null) {
				builder.addAll(getPrefixedFollowers(map, Integer.toString(i), property));
			}
		}
		
		return builder.build();
	}
	
	@Override
	public boolean propertyContainsList(String property) {

		Deque<String> propertyParts = new ArrayDeque<String>(Lists.newArrayList(Splitter.on('.').split(property)));
		final String propertyFirstPart = propertyParts.removeFirst();

		if (containsKey(propertyFirstPart)) {
			
			if (propertyParts.isEmpty()) {
				return false;
			}
				
			DynamicValue value = get(propertyFirstPart);
			DynamicMap map = value.as(DynamicMap.class);
			
			if (map != null) {
				return map.propertyContainsList(Joiner.on('.').join(propertyParts));
			}
			
			return false;
		}
		
		if (isLegalIndex(propertyFirstPart)) {
			return false;
		}

		return true;		
	}
	
	private boolean isLegalIndex(String key) {

		try {
			return (Integer.parseInt(key) >= 0);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public DynamicValue get(int idx) {

		if (idx >= 0 && idx < valueList.size()) {
			return valueList.get(idx);
		} else {
			return DynamicValue.MISSING;
		}
	}

	@Override
	public DynamicValue get(int idx, Object defaultValue) {

		if (idx >= 0 && idx < valueList.size()) {
			return valueList.get(idx);
		} else {
			return DynamicValueImpl.create(defaultValue);
		}
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("valueList", valueList).toString();
	}
}