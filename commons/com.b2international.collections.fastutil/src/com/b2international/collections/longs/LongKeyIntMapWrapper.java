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
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyIntMap;
import com.b2international.collections.longs.LongSet;

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
	public LongKeyIntMap dup() {
		return create(this);
	}

	@Override
	public int get(long key) {
		return delegate.get(key);
	}

	@Override
	public LongSet keySet() {
		return LongOpenHashSetWrapper.wrap(delegate.keySet());
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

	private LongKeyIntMap create(LongKeyIntMap map) {
		if (map instanceof LongKeyIntMapWrapper) {
			final Long2IntMap sourceDelegate = ((LongKeyIntMapWrapper) map).delegate;
			return wrap(clone(sourceDelegate));
		} else {
			final LongKeyIntMap result = create(map.size());
			final LongIterator keys = map.keySet().iterator();
			while (keys.hasNext()) {
				final long key = keys.next();
				result.put(key, map.get(key));
			}
			return result;
		}
	}
	
	public static LongKeyIntMap wrap(Long2IntMap delegate) {
		return new LongKeyIntMapWrapper(delegate);
	}
	
	public static LongKeyIntMap create() {
		return wrap(new Long2IntOpenHashMap());
	}
	
	public static LongKeyIntMap create(int expectedSize) {
		return wrap(new Long2IntOpenHashMap(expectedSize));
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
