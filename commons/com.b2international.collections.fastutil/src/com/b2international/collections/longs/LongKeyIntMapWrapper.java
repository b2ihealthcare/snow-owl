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

import com.b2international.collections.ints.IntCollection;
import com.b2international.collections.ints.IntCollectionWrapper;
import com.google.common.primitives.Longs;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

/**
 * @since 4.7
 */
public final class LongKeyIntMapWrapper implements LongKeyIntMap {

	private final Long2IntMap delegate;
	
	LongKeyIntMapWrapper(Long2IntMap delegate) {
		this.delegate = delegate;
	}

	@Override
	public int hashCode() {
		int h = 0;
		final LongIterator i = keySet().iterator();
        while (i.hasNext()) {
            long key = i.next();
            int value = get(key);
            h += Longs.hashCode(key) ^ value;
        }
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof LongKeyIntMap)) return false;
		
		final LongKeyIntMap other = (LongKeyIntMap) obj;
        if (other.size() != size()) return false;

        final LongIterator i = keySet().iterator();
        while (i.hasNext()) {
            long key = i.next();
            int value = get(key);
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
	public int get(long key) {
		return delegate.get(key);
	}

	@Override
	public LongSet keySet() {
		return LongSetWrapper.wrap(delegate.keySet());
	}

	@Override
	public int put(long key, int value) {
		return delegate.put(key, value);
	}

	@Override
	public int remove(long key) {
		return delegate.remove(key);
	}

	@Override
	public IntCollection values() {
		return IntCollectionWrapper.wrap(delegate.values());
	}

	public static LongKeyIntMap create(LongKeyIntMap map) {
		if (map instanceof LongKeyIntMapWrapper) {
			final Long2IntMap sourceDelegate = ((LongKeyIntMapWrapper) map).delegate;
			return new LongKeyIntMapWrapper(clone(sourceDelegate));
		} else {
			final LongKeyIntMap result = createWithExpectedSize(map.size());
			final LongIterator keys = map.keySet().iterator();
			while (keys.hasNext()) {
				final long key = keys.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}
	
	public static LongKeyIntMap create() {
		return new LongKeyIntMapWrapper(new Long2IntOpenHashMap());
	}
	
	public static LongKeyIntMap createWithExpectedSize(int expectedSize) {
		return new LongKeyIntMapWrapper(new Long2IntOpenHashMap(expectedSize));
	}
	
	// FastUtil helpers
	
	private static Long2IntMap clone(Long2IntMap map) {
		if (map instanceof Long2IntOpenHashMap) {
			return ((Long2IntOpenHashMap) map).clone();
		} else if (map instanceof Long2IntOpenCustomHashMap) {
			return ((Long2IntOpenCustomHashMap) map).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
	private static void trim(Long2IntMap map) {
		if (map instanceof Long2IntOpenHashMap) {
			((Long2IntOpenHashMap) map).trim();
		} else if (map instanceof Long2IntOpenCustomHashMap) {
			((Long2IntOpenCustomHashMap) map).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}

}
