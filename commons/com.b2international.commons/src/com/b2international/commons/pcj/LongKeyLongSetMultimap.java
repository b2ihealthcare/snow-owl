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

import static com.b2international.commons.pcj.LongCollections.emptySet;
import static com.b2international.commons.pcj.LongHashFunctionAdapter.hashOf;
import static com.b2international.commons.pcj.LongSets.newLongSet;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.hash.Hashing.murmur3_32;
import static java.lang.Boolean.valueOf;
import static java.lang.Long.parseLong;

import java.util.Collection;

import bak.pcj.map.AbstractLongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongSet;

/**
 * A long key long value set multimap implementation.
 *
 */
public class LongKeyLongSetMultimap extends AbstractLongKeyMap {

	private final LongKeyOpenHashMap delegate;

	public LongKeyLongSetMultimap() {
		delegate = new LongKeyOpenHashMap(hashOf(murmur3_32()));
	}

	@Override
	public LongKeyLongSetMultimapIterator entries() {
		return new LongKeyLongSetMultimapIterator(delegate.entries());
	}

	@Override
	public LongSet keySet() {
		return delegate.keySet();
	}

	/**
	 * Stores a primitive long key-value pair in the set multimap.           
	 * @param key key to store in the set multimap.                          
	 * @param value value to store in the set multimap.                      
	 * @return {@code true} if the method increased the size of the multimap,
	 * or {@code false} if the multimap already contained the key-value pair.
	 */
	public boolean put(final long key, final long value) {
		final Object object = delegate.get(key);
		if (object instanceof LongSet) {
			return ((LongSet) object).add(value);
		} else {
			delegate.put(key, newLongSet(value));
			return true;
		}
	}
	
	/**
	 * Described at {@link #put(long, long)}.                                                   
	 * <p>Note: for values only {@link Number} or {@link String} that can                       
	 * be parsed as a long are accepted.                                                        
	 * @param key key to store in the set multimap.                                             
	 * @param value value to store in the set multimap. Can be a {@link Number} or              
	 * a {@link String} that can be parsed into a long value via {@link Long#parseLong(String)}.
	 * @return {@code true} if the method increased the size of the multimap,                   
	 * or {@code false} if the multimap already contained the key-value pair.                   
	 */
	@Override
	public Boolean put(final long key, final Object value) {
		checkNotNull(value, "value");
		final long longValue;
		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
		} else if (value instanceof String) {
			longValue = parseLong(String.valueOf(value));
		} else {
			longValue = handleUnexpectedValueType();
		}
		return valueOf(put(key, longValue));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<LongSet> values() {
		return delegate.values();
	}

	@Override
	public LongSet get(final long key) {
		final Object value = delegate.get(key);
		return value instanceof LongSet ? (LongSet) value : emptySet();
	}
	
	private long handleUnexpectedValueType() {
		throw new IllegalArgumentException("Unexpected value type. Only long "
				+ "values and strings that can be parsed to a long value are allowed.");
	}
	

}