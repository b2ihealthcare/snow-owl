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
package com.b2international.snowowl.snomed.datastore.index.taxonomy;

import java.util.Collection;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyIntMap;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;

/**
 * Bidirectionally maps component SCTIDs to an integer internal identifier. 
 * 
 * @since 7.0
 */
public final class InternalIdMap {

	/**
	 * The default expected size for the internal ID map.
	 */
	private static final int EXPECTED_SIZE = 600_000;

	/**
	 * Integer value indicating that no internal ID mapping exists for the SCTID
	 */
	public static final int NO_INTERNAL_ID = -1;

	public static final Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private final LongKeyIntMap sctIdToInternal = PrimitiveMaps.newLongKeyIntOpenHashMapWithExpectedSize(EXPECTED_SIZE);
		private final LongList internalToSctId = PrimitiveLists.newLongArrayListWithExpectedSize(EXPECTED_SIZE);

		public Builder addAll(final Collection<String> sctIds) {
			final LongSet longSctIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(sctIds.size()); 

			sctIds.stream()
				.map(Long::parseLong)
				.forEachOrdered(longSctIds::add);

			return addAll(longSctIds);
		}

		private Builder addAll(final LongSet sctIdsToAdd) {
			sctIdsToAdd.removeAll(sctIdToInternal.keySet());

			for (final LongIterator itr = sctIdsToAdd.iterator(); itr.hasNext(); /*empty*/) {
				final long sctId = itr.next();
				sctIdToInternal.put(sctId, internalToSctId.size());
				internalToSctId.add(sctId);
			}

			return this;
		}

		public InternalIdMap build() {
			return new InternalIdMap(sctIdToInternal, internalToSctId);
		}
	}

	private final LongKeyIntMap sctIdToInternal;
	private final LongList internalToSctId;

	private InternalIdMap(final LongKeyIntMap sctIdToInternal, final LongList internalToSctId) {
		this.sctIdToInternal = sctIdToInternal;
		this.internalToSctId = internalToSctId;
	}

	public long getSctId(final int internalId) {
		return internalToSctId.get(internalId);
	}

	public int getInternalId(final String sctId) {
		return getInternalId(Long.parseLong(sctId));
	}

	public int getInternalId(final long sctId) {
		if (sctIdToInternal.containsKey(sctId)) {
			return sctIdToInternal.get(sctId);
		} else {
			return NO_INTERNAL_ID;
		}
	}

	public int getInternalIdChecked(final long sctId) {
		if (sctIdToInternal.containsKey(sctId)) {
			return sctIdToInternal.get(sctId);
		} else {
			throw new IllegalArgumentException(String.format("No internal ID exists for SCTID '%s'.", sctId));
		}
	}

	public boolean containsKey(final String sctId) {
		return containsKey(Long.parseLong(sctId));
	}

	public boolean containsKey(final long sctId) {
		return sctIdToInternal.containsKey(sctId);
	}

	public int size() {
		return sctIdToInternal.size();
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("InternalIdMap [");
		builder.append(internalToSctId.size());
		builder.append(" entries");
		builder.append("]");
		return builder.toString();
	}

	public LongIterator getSctIds() {
		return sctIdToInternal.keySet().iterator();
	}
}
