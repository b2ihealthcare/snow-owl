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
package com.b2international.commons.pcj;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import bak.pcj.map.AbstractIntKeyMap;
import bak.pcj.map.IntKeyMapIterator;
import bak.pcj.map.IntKeyOpenHashMap;
import bak.pcj.set.IntSet;

/**
 * Type safe set multimap implementation using primitive integer keys. 
 *
 */
public class IntKeySetMultimap<T> extends AbstractIntKeyMap {

	private final Class<? extends T> valueClass;
	private final IntKeyOpenHashMap map;

	public IntKeySetMultimap(final Class<? extends T> valueClass) {
		this.valueClass = checkNotNull(valueClass, "valueClass");
		map = new IntKeyOpenHashMap();
	} 
	
	@Override
	public IntKeyMapIterator entries() {
		return map.entries();
	}

	@Override
	public IntSet keySet() {
		return map.keySet();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean put(final int key, final Object value) {
		checkValue(value);
		final Object values = map.get(key);
		if (values instanceof Set) {
			return Boolean.valueOf(((Set) values).add(value));
		} else {
			map.put(key, newHashSet(value));
			return Boolean.TRUE;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<Collection<T>> values() {
		return map.values();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Set<T> get(final int key) {
		final Object values = map.get(key);
		return values instanceof Set ? unmodifiableSet((Set<T>) values) : Collections.<T>emptySet();
	}
	
	private void checkValue(final Object value) {
		checkNotNull(value, "value");
		checkArgument(valueClass.isAssignableFrom(value.getClass()), new StringBuilder()
			.append("Expected a type of ")
			.append(valueClass.getSimpleName())
			.append(" but was a ")
			.append(value.getClass().getSimpleName())
			.toString());
	}

}