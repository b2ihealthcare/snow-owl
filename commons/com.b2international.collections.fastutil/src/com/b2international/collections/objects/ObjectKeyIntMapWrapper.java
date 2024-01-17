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

import com.b2international.collections.ints.IntCollection;
import com.b2international.collections.ints.IntCollectionWrapper;
import com.b2international.collections.ints.IntValueMap;
import com.google.common.primitives.Ints;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * @since 4.7
 */
public final class ObjectKeyIntMapWrapper<K> implements IntValueMap<K> {

	private final Object2IntMap<K> delegate;

	private ObjectKeyIntMapWrapper(Object2IntMap<K> delegate) {
		this.delegate = delegate;
	}

	@Override
	public int hashCode() {
		int h = 0;
		final Iterator<K> i = keySet().iterator();
        while (i.hasNext()) {
            K key = i.next();
            int value = get(key);
            h += (key==null ? 0 : key.hashCode()) ^ Ints.hashCode(value);
        }
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof IntValueMap)) return false;
		
		try {
			final IntValueMap<K> other = (IntValueMap<K>) obj;
			if (other.size() != size()) return false;
			
			final Iterator<K> i = keySet().iterator();
			while (i.hasNext()) {
				K key = i.next();
				int value = get(key);
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
	public int get(K key) {
		return delegate.getInt(key);
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public int put(K key, int value) {
		return delegate.put(key, value);
	}

	@Override
	public int remove(K key) {
		return delegate.removeInt(key);
	}

	@Override
	public IntCollection values() {
		return IntCollectionWrapper.wrap(delegate.values());
	}
	
	// Builder methods

	public static <K> IntValueMap<K> create(IntValueMap<K> map) {
		if (map instanceof ObjectKeyIntMapWrapper) {
			final Object2IntMap<K> sourceDelegate = ((ObjectKeyIntMapWrapper<K>) map).delegate;
			return new ObjectKeyIntMapWrapper<>(clone(sourceDelegate));
		} else {
			final IntValueMap<K> result = createWithExpectedSize(map.size());
			final Iterator<K> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				final K key = keys.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}
	
	public static <K> IntValueMap<K> createWithExpectedSize(int expectedSize) {
		return new ObjectKeyIntMapWrapper<>(new Object2IntOpenHashMap<K>(expectedSize));
	}
	
	public static <K> IntValueMap<K> create() {
		return new ObjectKeyIntMapWrapper<>(new Object2IntOpenHashMap<K>());
	}
	
	// FastUtil helpers
	
	private static <K> Object2IntMap<K> clone(Object2IntMap<K> map) {
		if (map instanceof Object2IntOpenHashMap) {
			return ((Object2IntOpenHashMap<K>) map).clone();
		} else if (map instanceof Object2IntOpenCustomHashMap) {
			return ((Object2IntOpenCustomHashMap<K>) map).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
	private static <K> void trim(Object2IntMap<K> map) {
		if (map instanceof Object2IntOpenHashMap) {
			((Object2IntOpenHashMap<K>) map).trim();
		} else if (map instanceof Object2IntOpenCustomHashMap) {
			((Object2IntOpenCustomHashMap<K>) map).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}

}
