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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @since 4.5
 */
public interface Options {

	/**
	 * Returns the number of key-value mappings in this map. If the map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 *
	 * @return the number of key-value mappings in this map
	 */
	int size();

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 *
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	boolean isEmpty();

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified key. More formally, returns <tt>true</tt> if and only if this map
	 * contains a mapping for a key <tt>k</tt> such that <tt>(key==null ? k==null : key.equals(k))</tt>. (There can be at most one such mapping.)
	 *
	 * @param key
	 *            key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified key
	 * @throws NullPointerException
	 *             if the specified key is <code>null</code>
	 */
	boolean containsKey(Object key);

	/**
	 * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key.
	 *
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise it returns {@code null}. (There can be at most one such mapping.)
	 *
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
	 * @throws NullPointerException
	 *             if the specified key is <code>null</code>
	 */
	Object get(String key);

	/**
	 * Returns the value for the given key as instance of the given expectedType.
	 * 
	 * @param key
	 *            - the key whose associated value is to be returned
	 * @param expectedType
	 *            - the expected type of the returned value
	 * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
	 * @throws IllegalArgumentException
	 *             - if the value is not the instance of the given type
	 * @throws NullPointerException
	 *             if the specified key is <code>null</code>
	 */
	<T> T get(String key, Class<T> expectedType);

	/**
	 * Returns a boolean value from this options with the specified key. If the key contains another type of value, an
	 * {@link IllegalArgumentException} is raised. If the key does not exists in the configuration, then a <code>false</code> value is returned.
	 * Otherwise the stored configuration value is returned.
	 * 
	 * @param key
	 *            - the key whose associated {@link Boolean} value is to be returned
	 * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
	 * @throws NullPointerException
	 *             if the specified key is <code>null</code>
	 * @see #get(String, Class)
	 */
	boolean getBoolean(String key);

	/**
	 * Returns a string value from the configuration map with the specified key. If the key contains another type of value, an
	 * {@link IllegalArgumentException} is raised. If the key does not exists in the configuration, then <code>null</code> value is returned.
	 * Otherwise the stored configuration value is returned.
	 * 
	 * @param key
	 *            - the key whose associated {@link String} value is to be returned
	 * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
	 * @see #get(String, Class)
	 */
	String getString(String key);

	/**
	 * Returns a collection of values conform to the given class type found on the given key. If a single value (not a {@link Collection}) is mapped
	 * to the given key, then it wraps the value in a {@link Collections#singleton(Object) singleton set} and returns it. Otherwise it tries to get
	 * the value as a {@link Collection} and return it.
	 * 
	 * @param key
	 *            - the key whose associated value is to be returned
	 * @param type
	 *            - the type of the items if a collection is mapped to the given key in this map
	 * @return a {@link Collection} mapped to the given key, or an empty collection if there was no mapping for the key, never <code>null</code>.
	 * @throws IllegalArgumentException
	 *             - if the elements in the collection is not applicable to the given type.
	 */
	<T> Collection<T> getCollection(String key, Class<T> type);

	/**
	 * Returns a list of values conform to the given class type found on the given key. If a single value (not a {@link List}) is mapped to the given
	 * key, then it wraps the value in a {@link Collections#singletonList(Object) singleton list} and returns it. Otherwise it tries to get the value
	 * as a {@link List} and return it.
	 * 
	 * @param key
	 *            - the key whose associated value is to be returned
	 * @param type
	 *            - the type of the items if a list is mapped to the given key in this map
	 * @return a {@link List} mapped to the given key, or an empty list if there was no mapping for the key, never <code>null</code>.
	 * @throws IllegalArgumentException
	 *             - if the elements in the list is not applicable to the given type.
	 */
	<T> List<T> getList(String key, Class<T> type);

	/**
	 * Returns a nested {@link Options} from this options found on the given key, or an empty {@link Options} if not found.
	 * 
	 * @param key
	 *            - the key whose associated value is to be returned
	 * @return a non-null {@link Options} instance
	 * @throws IllegalArgumentException
	 *             - if the mapped item is not applicable to {@link Options}.
	 */
	Options getOptions(String key);

	/**
	 * Returns a {@link Set} view of the keys contained in this map. The set is backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in progress (except through the iterator's own <tt>remove</tt>
	 * operation), the results of the iteration are undefined. The set supports element removal, which removes the corresponding mapping from the map,
	 * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not
	 * support the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the keys contained in this map
	 */
	Set<String> keySet();
	
	/**
	 * Creates a new {@link Options} instance using {@link OptionsBuilder}.
	 * 
	 * @param map
	 * 			  - the map that the new {@link Options} instance should contain.
	 * @return
	 */
	static Options from(Map<String, Object> map) {
		return Options.builder().putAll(map).build();
	}
	
	/**
	 * Creates a new {@link OptionsBuilder} instance to build {@link Options}.
	 * @return
	 */
	static OptionsBuilder builder() {
		return OptionsBuilder.newBuilder();
	}
	
}
