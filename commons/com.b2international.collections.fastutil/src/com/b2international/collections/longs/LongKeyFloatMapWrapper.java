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

import com.b2international.collections.floats.FloatCollection;
import com.b2international.collections.floats.FloatCollectionWrapper;
import com.google.common.primitives.Longs;

import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenCustomHashMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;

/**
 * @since 4.7
 */
public final class LongKeyFloatMapWrapper implements LongKeyFloatMap {

	private final Long2FloatMap delegate;
	
	LongKeyFloatMapWrapper(Long2FloatMap delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int hashCode() {
		int h = 0;
		final LongIterator i = keySet().iterator();
        while (i.hasNext()) {
            long key = i.next();
            float value = get(key);
            h += Longs.hashCode(key) ^ Float.floatToIntBits(value);
        }
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof LongKeyFloatMap)) return false;
		
		final LongKeyFloatMap other = (LongKeyFloatMap) obj;
        if (other.size() != size()) return false;

        final LongIterator i = keySet().iterator();
        while (i.hasNext()) {
            long key = i.next();
            float value = get(key);
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
	public float get(long key) {
		return delegate.get(key);
	}

	@Override
	public LongSet keySet() {
		return LongSetWrapper.wrap(delegate.keySet());
	}

	@Override
	public float put(long key, float value) {
		return delegate.put(key, value);
	}

	@Override
	public float remove(long key) {
		return delegate.remove(key);
	}

	@Override
	public FloatCollection values() {
		return FloatCollectionWrapper.wrap(delegate.values());
	}

	public static LongKeyFloatMap create(LongKeyFloatMap map) {
		if (map instanceof LongKeyFloatMapWrapper) {
			final Long2FloatMap sourceDelegate = ((LongKeyFloatMapWrapper) map).delegate;
			return new LongKeyFloatMapWrapper(clone(sourceDelegate));
		} else {
			final LongKeyFloatMap result = createWithExpectedSize(map.size());
			final LongIterator keys = map.keySet().iterator();
			while (keys.hasNext()) {
				final long key = keys.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}
	
	public static LongKeyFloatMap create() {
		return new LongKeyFloatMapWrapper(new Long2FloatOpenHashMap());
	}
	
	public static LongKeyFloatMap createWithExpectedSize(int expectedSize) {
		return new LongKeyFloatMapWrapper(new Long2FloatOpenHashMap(expectedSize));
	}
	
	// FastUtil helpers
	
	private static Long2FloatMap clone(Long2FloatMap map) {
		if (map instanceof Long2FloatOpenHashMap) {
			return ((Long2FloatOpenHashMap) map).clone();
		} else if (map instanceof Long2FloatOpenCustomHashMap) {
			return ((Long2FloatOpenCustomHashMap) map).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}
	
	private static void trim(Long2FloatMap map) {
		if (map instanceof Long2FloatOpenHashMap) {
			((Long2FloatOpenHashMap) map).trim();
		} else if (map instanceof Long2FloatOpenCustomHashMap) {
			((Long2FloatOpenCustomHashMap) map).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported map implementation: " + map.getClass().getSimpleName());
		}
	}

}
