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

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollectionWrapper;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;

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
	public LongKeyLongMap dup() {
		return create(this);
	}

	@Override
	public long get(long key) {
		return delegate.get(key);
	}

	@Override
	public LongSet keySet() {
		return LongOpenHashSetWrapper.wrap(delegate.keySet());
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

	private LongKeyLongMap create(LongKeyLongMap map) {
		if (map instanceof LongKeyLongMapWrapper) {
			final Long2LongMap sourceDelegate = ((LongKeyLongMapWrapper) map).delegate;
			return wrap(clone(sourceDelegate));
		} else {
			final LongKeyLongMap result = create(map.size());
			final LongIterator keys = map.keySet().iterator();
			while (keys.hasNext()) {
				final long key = keys.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}
	
	public static LongKeyLongMap wrap(Long2LongMap delegate) {
		return new LongKeyLongMapWrapper(delegate);
	}
	
	public static LongKeyLongMap create() {
		return wrap(new Long2LongOpenHashMap());
	}
	
	public static LongKeyLongMap create(int expectedSize) {
		return wrap(new Long2LongOpenHashMap(expectedSize));
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
