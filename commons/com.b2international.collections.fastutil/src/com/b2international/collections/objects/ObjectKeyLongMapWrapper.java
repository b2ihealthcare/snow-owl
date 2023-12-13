/*
 * Copyright 2011-2016 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.collections.objects;

import java.util.Iterator;
import java.util.Set;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollectionWrapper;
import com.b2international.collections.longs.LongValueMap;
import com.google.common.primitives.Longs;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

/**
 * @since 4.7
 */
public final class ObjectKeyLongMapWrapper<K> implements LongValueMap<K> {

	private final Object2LongMap<K> delegate;

	private ObjectKeyLongMapWrapper(Object2LongMap<K> delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int hashCode() {
		int h = 0;
		final Iterator<K> i = keySet().iterator();
        while (i.hasNext()) {
            K key = i.next();
            long value = get(key);
            h += (key==null ? 0 : key.hashCode()) ^ Longs.hashCode(value);
        }
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof LongValueMap)) return false;
		
		try {
			final LongValueMap<K> other = (LongValueMap<K>) obj;
			if (other.size() != size()) return false;
			
			final Iterator<K> i = keySet().iterator();
			while (i.hasNext()) {
				K key = i.next();
				long value = get(key);
				if (value != other.get(key)) {
					return false;
				}
			}
		} catch (ClassCastException e) {
			return false;
		}

        return true;
	}
	
	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public void trimToSize() {
		trim(delegate);
	}

	@Override
	public boolean containsKey(K key) {
		return delegate.containsKey(key);
	}

	@Override
	public long get(K key) {
		return delegate.getLong(key);
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public long put(K key, long value) {
		return delegate.put(key, value);
	}

	@Override
	public long remove(K key) {
		return delegate.removeLong(key);
	}

	@Override
	public LongCollection values() {
		return LongCollectionWrapper.wrap(delegate.values());
	}
	
	// Builder methods

	public static <K> LongValueMap<K> create(LongValueMap<K> map) {
		if (map instanceof ObjectKeyLongMapWrapper) {
			final Object2LongMap<K> sourceDelegate = ((ObjectKeyLongMapWrapper<K>) map).delegate;
			return new ObjectKeyLongMapWrapper<>(clone(sourceDelegate));
		} else {
			final LongValueMap<K> result = createWithExpectedSize(map.size());
			final Iterator<K> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				final K key = keys.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}
	
	public static <K> LongValueMap<K> createWithExpectedSize(int expectedSize) {
		return new ObjectKeyLongMapWrapper<>(new Object2LongOpenHashMap<K>(expectedSize));
	}
	
	public static <K> LongValueMap<K> create() {
		return new ObjectKeyLongMapWrapper<>(new Object2LongOpenHashMap<K>());
	}
	
	// FastUtil helpers
	
	private static <K> Object2LongMap<K> clone(Object2LongMap<K> map) {
		if (map instanceof Object2LongOpenHashMap) {
			return ((Object2LongOpenHashMap<K>) map).clone();
		} else if (map instanceof Object2LongOpenCustomHashMap) {
			return ((Object2LongOpenCustomHashMap<K>) map).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
	private static <K> void trim(Object2LongMap<K> map) {
		if (map instanceof Object2LongOpenHashMap) {
			((Object2LongOpenHashMap<K>) map).trim();
		} else if (map instanceof Object2LongOpenCustomHashMap) {
			((Object2LongOpenCustomHashMap<K>) map).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}

}
