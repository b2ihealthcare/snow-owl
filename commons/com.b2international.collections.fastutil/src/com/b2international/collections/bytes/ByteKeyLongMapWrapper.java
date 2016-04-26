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

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollectionWrapper;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;

import it.unimi.dsi.fastutil.bytes.Byte2LongMap;
import it.unimi.dsi.fastutil.bytes.Byte2LongOpenCustomHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2LongOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;

/**
 * @since 4.7
 */
public final class ByteKeyLongMapWrapper implements ByteKeyLongMap {

	private final Byte2LongMap delegate;

	ByteKeyLongMapWrapper(Byte2LongMap delegate) {
		this.delegate = delegate;
	}

	@Override
	public int hashCode() {
		int h = 0;
		final ByteIterator i = keySet().iterator();
        while (i.hasNext()) {
            byte key = i.next();
            long value = get(key);
            h += Bytes.hashCode(key) ^ Longs.hashCode(value);
        }
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ByteKeyLongMap)) return false;
		
		final ByteKeyLongMap other = (ByteKeyLongMap) obj;
        if (other.size() != size()) return false;

        final ByteIterator i = keySet().iterator();
        while (i.hasNext()) {
            byte key = i.next();
            long value = get(key);
            if (value != other.get(key)) {
            	return false;
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
	public long get(byte key) {
		return delegate.get(key);
	}

	@Override
	public ByteSet keySet() {
		return ByteSetWrapper.wrap(delegate.keySet());
	}

	@Override
	public long put(byte key, long value) {
		return delegate.put(key, value);
	}

	@Override
	public long remove(byte key) {
		return delegate.remove(key);
	}

	@Override
	public LongCollection values() {
		return LongCollectionWrapper.wrap(delegate.values());
	}
	
	public static  ByteKeyLongMap create() {
		return new ByteKeyLongMapWrapper(new Byte2LongOpenHashMap());
	}
	
	public static  ByteKeyLongMap createWithExpectedSize(int expectedSize) {
		return new ByteKeyLongMapWrapper(new Byte2LongOpenHashMap(expectedSize));
	}
	
	public static  ByteKeyLongMap create(ByteKeyLongMap map) {
		if (map instanceof ByteKeyLongMapWrapper) {
			final Byte2LongMap sourceDelegate = ((ByteKeyLongMapWrapper) map).delegate;
			return new ByteKeyLongMapWrapper(clone(sourceDelegate));
		} else {
			final ByteKeyLongMap result = createWithExpectedSize(map.size());
			final ByteIterator iter = map.keySet().iterator();
			while (iter.hasNext()) {
				final byte key = iter.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}

	// Move to FastUtil helper methods
	private static  Byte2LongMap clone(Byte2LongMap sourceDelegate) {
		if (sourceDelegate instanceof Byte2ObjectOpenHashMap) {
			return ((Byte2LongOpenHashMap) sourceDelegate).clone();
		} else if (sourceDelegate instanceof Byte2LongOpenCustomHashMap) {
			return ((Byte2LongOpenCustomHashMap) sourceDelegate).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + sourceDelegate.getClass().getSimpleName());
		}
	}
	
	private static  void trim(Byte2LongMap map) {
		if (map instanceof Byte2LongOpenHashMap) {
			((Byte2LongOpenHashMap) map).trim();
		} else if (map instanceof Byte2LongOpenCustomHashMap) {
			((Byte2LongOpenCustomHashMap) map).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
}
