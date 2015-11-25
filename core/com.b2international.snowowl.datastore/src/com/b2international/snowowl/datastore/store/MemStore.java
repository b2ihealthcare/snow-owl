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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.b2international.snowowl.datastore.store.query.Query;
import com.b2international.snowowl.datastore.store.query.Where;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
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
	public void putAll(Map<String, T> map) {
		values.putAll(map);
	}

	@Override
	public T get(String key) {
		return values.get(key);
	}

	@Override
	public boolean containsKey(String key) {
		return values.containsKey(key);
	}

	@Override
	public T remove(String key) {
		return values.remove(key);
	}
	
	@Override
	public Collection<T> removeAll(Collection<String> keys) {
		final Collection<T> values = Lists.newArrayList();
		for (final String key : keys) {
			values.add(remove(key));
		}
		
		return values;
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
	
	@Override
	public Collection<T> search(Query query) {
		return search(query, 0, Integer.MAX_VALUE);
	}
	
	@Override
	public Collection<T> search(Query query, int offset, int limit) {
		checkArgument(query != null, "Query may not be null");
		checkArgument(offset >= 0, "Offset must be zero or positive");
		checkArgument(limit >= 1, "Limit should be at least one");
		return FluentIterable.from(values()).skip(offset).limit(limit).filter(Predicates.and(toPredicates(query))).toSet();
	}
	
	@Override
	public void configureSearchable(String property) {
		// No-op for MemStore, which can access all properties of a stored item reflectively
	}
	
	private Iterable<Predicate<T>> toPredicates(Query query) {
		return FluentIterable.from(query.clauses()).filter(Where.class).transform(new Function<Where, Predicate<T>>() {
			@Override
			public Predicate<T> apply(final Where where) {
				return where.toPredicate();
			}
		}).toSet();
	}
}
