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
package com.b2international.collections.longs;

import com.google.common.primitives.Longs;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenCustomHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;

/**
 * @since 4.7
 */
public final class LongKeyLongMapWrapper implements LongKeyLongMap {

	private final Long2LongMap delegate;
	
	LongKeyLongMapWrapper(Long2LongMap delegate) {
		this.delegate = delegate;
	}

	@Override
	public int hashCode() {
		int h = 0;
		final LongIterator i = keySet().iterator();
        while (i.hasNext()) {
            long key = i.next();
            long value = get(key);
            h += Longs.hashCode(key) ^ Longs.hashCode(value);
        }
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof LongKeyLongMap)) return false;
		
		final LongKeyLongMap other = (LongKeyLongMap) obj;
        if (other.size() != size()) return false;

        final LongIterator i = keySet().iterator();
        while (i.hasNext()) {
            long key = i.next();
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
	public boolean containsKey(long key) {
		return delegate.containsKey(key);
	}

	@Override
	public long get(long key) {
		return delegate.get(key);
	}

	@Override
	public LongSet keySet() {
		return LongSetWrapper.wrap(delegate.keySet());
	}

	@Override
	public long put(long key, long value) {
		return delegate.put(key, value);
	}

	@Override
	public long remove(long key) {
		return delegate.remove(key);
	}

	@Override
	public LongCollection values() {
		return LongCollectionWrapper.wrap(delegate.values());
	}

	public static LongKeyLongMap create(LongKeyLongMap map) {
		if (map instanceof LongKeyLongMapWrapper) {
			final Long2LongMap sourceDelegate = ((LongKeyLongMapWrapper) map).delegate;
			return new LongKeyLongMapWrapper(clone(sourceDelegate));
		} else {
			final LongKeyLongMap result = createWithExpectedSize(map.size());
			final LongIterator keys = map.keySet().iterator();
			while (keys.hasNext()) {
				final long key = keys.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}
	
	public static LongKeyLongMap create() {
		return new LongKeyLongMapWrapper(new Long2LongOpenHashMap());
	}
	
	public static LongKeyLongMap createWithExpectedSize(int expectedSize) {
		return new LongKeyLongMapWrapper(new Long2LongOpenHashMap(expectedSize));
	}
	
	// FastUtil helpers
	
	private static Long2LongMap clone(Long2LongMap map) {
		if (map instanceof Long2LongOpenHashMap) {
			return ((Long2LongOpenHashMap) map).clone();
		} else if (map instanceof Long2LongOpenCustomHashMap) {
			return ((Long2LongOpenCustomHashMap) map).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
	private static void trim(Long2LongMap map) {
		if (map instanceof Long2LongOpenHashMap) {
			((Long2LongOpenHashMap) map).trim();
		} else if (map instanceof Long2LongOpenCustomHashMap) {
			((Long2LongOpenCustomHashMap) map).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}

}
