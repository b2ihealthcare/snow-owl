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
package com.b2international.commons.options;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * FIXME make this class internal impl, but first remove subclasses from plugin projects
 * 
 * @since 4.5
 */
public class HashMapOptions extends HashMap<String, Object> implements Options {

	private static final long serialVersionUID = 4190786291142214160L;

	@Deprecated
	protected HashMapOptions() {
		this(5);
	}
	
	@Deprecated
	protected HashMapOptions(int initialCapacity) {
		super(initialCapacity);
	}
	
	HashMapOptions(Map<String, Object> options) {
		super(options);
	}
	
	@Override
	public final Object get(String key) {
		return super.get(key);
	}
	
	@Override
	public final boolean getBoolean(final String key) {
		final Boolean value = get(key, Boolean.class);
		return value == null ? false : value;
	}
	
	@Override
	public final String getString(final String key) {
		return get(key, String.class);
	}
	
	@Override
	public final <T> T get(final String key, final Class<T> expectedType) throws IllegalArgumentException {
		if (key != null && expectedType != null) {
			final Object value = get(key);
			if (value != null) {
				if (expectedType.isInstance(value)) {
					return expectedType.cast(value);
				}
				throw new IllegalArgumentException(String.format(
						"Expected type '%s' is not valid for the value '%s(%s)' returned for the key '%s'",
						expectedType.getSimpleName(), value, value.getClass().getSimpleName(), key));
			}
		}
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final <T> Collection<T> getCollection(String key, Class<T> type) {
		final Object value = get(key);
		if (type.isInstance(value)) {
			return Collections.singleton(type.cast(value));
		} else {
			final Collection<Object> collection = get(key, Collection.class);
			final Object first = collection != null ? Iterables.getFirst(collection, null) : null;
			if (first != null) {
				if (type.isInstance(first)) {
					return (Collection<T>) collection;
				}
				throw new IllegalArgumentException(String.format("The elements (%s) in the collection are not the instance of the given type (%s)", first.getClass(), type));
			}
			return emptySet();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final <T> List<T> getList(String key, Class<T> type) {
		final Object value = get(key);
		if (type.isInstance(value)) {
			return Collections.singletonList(type.cast(value));
		} else {
			final List<Object> List = get(key, List.class);
			final Object first = List != null ? Iterables.getFirst(List, null) : null;
			if (first != null) {
				if (type.isInstance(first)) {
					return (List<T>) List;
				}
				throw new IllegalArgumentException(String.format("The elements (%s) in the List are not the instance of the given type (%s)", first.getClass(), type));
			}
			return emptyList();
		}
	}
	
	@Override
	public final Options getOptions(String key) {
		return containsKey(key) ? get(key, Options.class) : OptionsBuilder.newBuilder().build();
	}
	
	protected final Iterable<String> toImmutableStringList(final Iterable<Object> elements) {
		if (elements == null) {
			return Collections.emptyList();
		}
		return Lists.transform(ImmutableList.copyOf(elements), new Function<Object, String>() {
			@Override
			public String apply(final Object input) {
				return String.valueOf(input);
			}
		});
	}

}
