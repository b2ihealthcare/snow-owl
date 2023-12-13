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
package com.b2international.collections.ints;

import java.util.Collection;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @since 4.7
 */
public final class IntKeyMapWrapper<V> implements IntKeyMap<V> {

	private final Int2ObjectMap<V> delegate;

	private IntKeyMapWrapper(Int2ObjectMap<V> delegate) {
		this.delegate = delegate;
	}

	@Override
	public int hashCode() {
		int h = 0;
		final IntIterator i = keySet().iterator();
        while (i.hasNext()) {
            int key = i.next();
            V value = get(key);
            h += key ^ (value==null ? 0 : value.hashCode());
        }
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof IntKeyMap)) return false;
		
		final IntKeyMap<?> other = (IntKeyMap<?>) obj;
        if (other.size() != size()) return false;

        final IntIterator i = keySet().iterator();
        while (i.hasNext()) {
            int key = i.next();
            V value = get(key);
            if (value == null) {
                if (!(other.get(key) == null && other.containsKey(key)))
                    return false;
            } else {
            	if (!value.equals(other.get(key))) {
            		return false;
            	}
            }
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
	public boolean containsKey(int key) {
		return delegate.containsKey(key);
	}

	@Override
	public V get(int key) {
		return delegate.get(key);
	}

	@Override
	public IntSet keySet() {
		return IntSetWrapper.wrap(delegate.keySet());
	}

	@Override
	public V put(int key, V value) {
		return delegate.put(key, value);
	}

	@Override
	public V remove(int key) {
		return delegate.remove(key);
	}

	@Override
	public Collection<V> values() {
		return delegate.values();
	}
	
	public static <V> IntKeyMap<V> create() {
		return new IntKeyMapWrapper<>(new Int2ObjectOpenHashMap<V>());
	}
	
	public static <V> IntKeyMap<V> createWithExpectedSize(int expectedSize) {
		return new IntKeyMapWrapper<>(new Int2ObjectOpenHashMap<V>(expectedSize));
	}
	
	public static <V> IntKeyMap<V> create(IntKeyMap<V> map) {
		if (map instanceof IntKeyMapWrapper) {
			final Int2ObjectMap<V> sourceDelegate = ((IntKeyMapWrapper<V>) map).delegate;
			return new IntKeyMapWrapper<>(clone(sourceDelegate));
		} else {
			final IntKeyMap<V> result = createWithExpectedSize(map.size());
			final IntIterator iter = map.keySet().iterator();
			while (iter.hasNext()) {
				final int key = iter.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}
	
	// FastUtil helpers
	
	private static <V> Int2ObjectMap<V> clone(Int2ObjectMap<V> map) {
		if (map instanceof Int2ObjectOpenHashMap) {
			return ((Int2ObjectOpenHashMap<V>) map).clone();
		} else if (map instanceof Int2ObjectOpenCustomHashMap) {
			return ((Int2ObjectOpenCustomHashMap<V>) map).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
	private static <V> void trim(Int2ObjectMap<V> map) {
		if (map instanceof Int2ObjectOpenHashMap) {
			((Int2ObjectOpenHashMap<V>) map).trim();
		} else if (map instanceof Int2ObjectOpenCustomHashMap) {
			((Int2ObjectOpenCustomHashMap<V>) map).trim(); 
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
}
