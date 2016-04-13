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
package com.b2international.collections.map;

import java.util.Collection;

import com.b2international.collections.ByteIterator;
import com.b2international.collections.set.ByteOpenHashSetWrapper;
import com.b2international.collections.set.ByteSet;

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
	public ByteKeyMap<V> dup() {
		return create(this);
	}

	@Override
	public V get(byte key) {
		return delegate.get(key);
	}

	@Override
	public ByteSet keySet() {
		return ByteOpenHashSetWrapper.wrap(delegate.keySet());
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
	
	public static <V> ByteKeyMap<V> wrap(Byte2ObjectMap<V> delegate) {
		return new ByteKeyMapWrapper<>(delegate);
	}
	
	public static <V> ByteKeyMap<V> create(int expectedSize) {
		return wrap(new Byte2ObjectOpenHashMap<V>(expectedSize));
	}
	
	public static <V> ByteKeyMap<V> create(ByteKeyMap<V> map) {
		if (map instanceof ByteKeyMapWrapper) {
			final Byte2ObjectMap<V> sourceDelegate = ((ByteKeyMapWrapper<V>) map).delegate;
			return wrap(clone(sourceDelegate));
		} else {
			final ByteKeyMap<V> result = create(map.size());
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
