/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.index.taxonomy;

import java.util.Collection;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongSet;

/**
 * Allows associating SCTID keys in integer form to multiple SCTID values, using
 * an {@link InternalIdMap} as the mapping between SCTIDs and integers.
 * 
 * @since 7.0
 */
public final class InternalSctIdMultimap {

	public static final Builder builder(final InternalIdMap internalIdMap) {
		return new Builder(internalIdMap);
	}

	public static final class Builder {

		private final InternalIdMap internalIdMap;
		private final IntKeyMap<IntSet> internalIdMultimap;

		public Builder(final InternalIdMap internalIdMap) {
			this.internalIdMap = internalIdMap;
			this.internalIdMultimap = PrimitiveMaps.newIntKeyOpenHashMap();
		}

		public Builder putAll(final String key, final Collection<String> values) {
			return putAll(Long.parseLong(key), values);
		}

		private Builder putAll(final long key, final Collection<String> values) {
			final int internalId = internalIdMap.getInternalIdChecked(key);

			IntSet itemsForKey = internalIdMultimap.get(internalId);
			if (itemsForKey == null) {
				itemsForKey = PrimitiveSets.newIntOpenHashSet();
				internalIdMultimap.put(internalId, itemsForKey);
			}

			values.stream()
			.map(Long::parseLong)
			.map(internalIdMap::getInternalIdChecked)
			.forEachOrdered(itemsForKey::add);

			return this;
		}

		public InternalSctIdMultimap build() {
			return new InternalSctIdMultimap(internalIdMap, internalIdMultimap);
		}
	}

	private final InternalIdMap internalIdMap;
	private final IntKeyMap<IntSet> internalIdMultimap;

	private InternalSctIdMultimap(final InternalIdMap internalIdMap, final IntKeyMap<IntSet> internalIdMultimap) {
		this.internalIdMap = internalIdMap;
		this.internalIdMultimap = internalIdMultimap;
	}

	public LongSet get(final String key) {
		return get(Long.parseLong(key));
	}

	public LongSet get(final long key) {
		final int internalId = internalIdMap.getInternalIdChecked(key);
		final IntSet values = internalIdMultimap.get(internalId);
		if (values == null) {
			return PrimitiveSets.newLongOpenHashSet();
		} else {
			final LongSet sctIdValues = PrimitiveSets.newLongOpenHashSetWithExpectedSize(values.size());

			for (final IntIterator itr = values.iterator(); itr.hasNext(); /*empty*/) {
				final int valueInternalId = itr.next();
				final long valueSctId = internalIdMap.getSctId(valueInternalId);
				sctIdValues.add(valueSctId);
			}

			return sctIdValues;
		}
	}

	public LongSet keySet() {
		final LongSet keySet = PrimitiveSets.newLongOpenHashSetWithExpectedSize(internalIdMultimap.size());

		for (final IntIterator itr = internalIdMultimap.keySet().iterator(); itr.hasNext(); /*empty*/) {
			final int keyInternalId = itr.next();
			final long keySctId = internalIdMap.getSctId(keyInternalId);
			keySet.add(keySctId);
		}

		return keySet;
	}

	public boolean isEmpty() {
		return internalIdMultimap.isEmpty();
	}
}
