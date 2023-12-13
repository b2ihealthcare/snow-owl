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
package com.b2international.commons.collect;

import static com.google.common.hash.Hashing.murmur3_32;

import java.util.Collection;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;

/**
 * A long key long value set multimap implementation.
 */
public class LongKeyLongSetMultimap {

	private final LongKeyMap<LongSet> map;

	public LongKeyLongSetMultimap() {
		this(PrimitiveMaps.<LongSet>newLongKeyOpenHashMap(murmur3_32()));
	}

	public LongKeyLongSetMultimap(LongKeyMap<LongSet> map) {
		this.map = map;
	}

	public LongSet keySet() {
		return map.keySet();
	}

	public Collection<LongSet> values() {
		return map.values();
	}

	public boolean put(final long key, final long value) {
		LongSet values = delegateGet(key);
		
		if (values == null) {
			values = PrimitiveSets.newLongOpenHashSet();
			delegatePut(key, values);
		}
		
		return values.add(value);
	}

	public LongSet get(final long key) {
		final LongSet value = delegateGet(key);
		return (value != null) ? value : LongCollections.emptySet();
	}

	private void delegatePut(long key, LongSet values) {
		map.put(key, values);
	}

	private LongSet delegateGet(final long key) {
		return map.get(key);
	}
}
