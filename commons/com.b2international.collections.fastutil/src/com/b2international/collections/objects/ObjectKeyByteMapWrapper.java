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

import com.b2international.collections.bytes.ByteCollection;
import com.b2international.collections.bytes.ByteCollectionWrapper;
import com.b2international.collections.bytes.ByteValueMap;
import com.google.common.primitives.Bytes;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

/**
 * @since 4.7
 */
public final class ObjectKeyByteMapWrapper<K> implements ByteValueMap<K> {

	private final Object2ByteMap<K> delegate;

	private ObjectKeyByteMapWrapper(Object2ByteMap<K> delegate) {
		this.delegate = delegate;
	}

	@Override
	public int hashCode() {
		int h = 0;
		final Iterator<K> i = keySet().iterator();
        while (i.hasNext()) {
            K key = i.next();
            byte value = get(key);
            h += (key==null ? 0 : key.hashCode()) ^ Bytes.hashCode(value);
        }
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ByteValueMap)) return false;
		
		try {
			final ByteValueMap<K> other = (ByteValueMap<K>) obj;
			if (other.size() != size()) return false;
			
			final Iterator<K> i = keySet().iterator();
			while (i.hasNext()) {
				K key = i.next();
				byte value = get(key);
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
	public byte get(K key) {
		return delegate.getByte(key);
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet();
	}

	@Override
	public byte put(K key, byte value) {
		return delegate.put(key, value);
	}

	@Override
	public byte remove(K key) {
		return delegate.removeByte(key);
	}

	@Override
	public ByteCollection values() {
		return ByteCollectionWrapper.wrap(delegate.values());
	}
	
	// Builder methods

	public static <K> ByteValueMap<K> create(ByteValueMap<K> map) {
		if (map instanceof ObjectKeyByteMapWrapper) {
			final Object2ByteMap<K> sourceDelegate = ((ObjectKeyByteMapWrapper<K>) map).delegate;
			return new ObjectKeyByteMapWrapper<>(clone(sourceDelegate));
		} else {
			final ByteValueMap<K> result = createWithExpectedSize(map.size());
			final Iterator<K> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				final K key = keys.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}
	
	public static <K> ByteValueMap<K> createWithExpectedSize(int expectedSize) {
		return new ObjectKeyByteMapWrapper<>(new Object2ByteOpenHashMap<K>(expectedSize));
	}
	
	public static <K> ByteValueMap<K> create() {
		return new ObjectKeyByteMapWrapper<>(new Object2ByteOpenHashMap<K>());
	}
	
	// FastUtil helpers
	
	private static <K> Object2ByteMap<K> clone(Object2ByteMap<K> map) {
		if (map instanceof Object2ByteOpenHashMap) {
			return ((Object2ByteOpenHashMap<K>) map).clone();
		} else if (map instanceof Object2ByteOpenCustomHashMap) {
			return ((Object2ByteOpenCustomHashMap<K>) map).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
	private static <K> void trim(Object2ByteMap<K> map) {
		if (map instanceof Object2ByteOpenHashMap) {
			((Object2ByteOpenHashMap<K>) map).trim();
		} else if (map instanceof Object2ByteOpenCustomHashMap) {
			((Object2ByteOpenCustomHashMap<K>) map).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}

}
