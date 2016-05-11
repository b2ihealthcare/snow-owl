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
package com.b2international.collections.bytes;

import java.util.Collection;

import com.google.common.primitives.Bytes;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;

/**
 * @since 4.7
 */
public final class ByteKeyMapWrapper<V> implements ByteKeyMap<V> {

	private final Byte2ObjectMap<V> delegate;

	ByteKeyMapWrapper(Byte2ObjectMap<V> delegate) {
		this.delegate = delegate;
	}

	@Override
	public int hashCode() {
		int h = 0;
		final ByteIterator i = keySet().iterator();
        while (i.hasNext()) {
            byte key = i.next();
            V value = get(key);
            h += Bytes.hashCode(key) ^ (value==null ? 0 : value.hashCode());
        }
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ByteKeyMap)) return false;
		
		final ByteKeyMap<?> other = (ByteKeyMap<?>) obj;
        if (other.size() != size()) return false;

        final ByteIterator i = keySet().iterator();
        while (i.hasNext()) {
            byte key = i.next();
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
	public boolean containsKey(byte key) {
		return delegate.containsKey(key);
	}

	@Override
	public V get(byte key) {
		return delegate.get(key);
	}

	@Override
	public ByteSet keySet() {
		return ByteSetWrapper.wrap(delegate.keySet());
	}

	@Override
	public V put(byte key, V value) {
		return delegate.put(key, value);
	}

	@Override
	public V remove(byte key) {
		return delegate.remove(key);
	}

	@Override
	public Collection<V> values() {
		return delegate.values();
	}
	
	public static <V> ByteKeyMap<V> createWithExpectedSize(int expectedSize) {
		return new ByteKeyMapWrapper<>(new Byte2ObjectOpenHashMap<V>(expectedSize));
	}
	
	public static <V> ByteKeyMap<V> create(ByteKeyMap<V> map) {
		if (map instanceof ByteKeyMapWrapper) {
			final Byte2ObjectMap<V> sourceDelegate = ((ByteKeyMapWrapper<V>) map).delegate;
			return new ByteKeyMapWrapper<>(clone(sourceDelegate));
		} else {
			final ByteKeyMap<V> result = createWithExpectedSize(map.size());
			final ByteIterator iter = map.keySet().iterator();
			while (iter.hasNext()) {
				final byte key = iter.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}

	// Move to FastUtil helper methods
	private static <V> Byte2ObjectMap<V> clone(Byte2ObjectMap<V> sourceDelegate) {
		if (sourceDelegate instanceof Byte2ObjectOpenHashMap) {
			return ((Byte2ObjectOpenHashMap<V>) sourceDelegate).clone();
		} else if (sourceDelegate instanceof Byte2ObjectOpenCustomHashMap) {
			return ((Byte2ObjectOpenCustomHashMap<V>) sourceDelegate).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + sourceDelegate.getClass().getSimpleName());
		}
	}
	
	private static <V> void trim(Byte2ObjectMap<V> map) {
		if (map instanceof Byte2ObjectOpenHashMap) {
			((Byte2ObjectOpenHashMap<V>) map).trim();
		} else if (map instanceof Byte2ObjectOpenCustomHashMap) {
			((Byte2ObjectOpenCustomHashMap<V>) map).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
}
