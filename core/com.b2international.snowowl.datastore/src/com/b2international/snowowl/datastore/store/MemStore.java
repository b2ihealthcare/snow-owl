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
package com.b2international.snowowl.datastore.store;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

/**
 * @since 4.1
 */
public class MemStore<T> implements Store<T> {

	private final ConcurrentMap<String, T> values = new MapMaker().makeMap();

	@Override
	public void put(String key, T value) {
		values.put(key, value);
	}

	@Override
	public T get(String key) {
		return values.get(key);
	}

	@Override
	public T remove(String key) {
		return values.remove(key);
	}

	@Override
	public boolean replace(String key, T oldValue, T newValue) {
		return values.replace(key, oldValue, newValue);
	}

	@Override
	public Collection<T> values() {
		return values.values();
	}
	
	@Override
	public void clear() {
		values.clear();
	}
	
}
