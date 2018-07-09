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

import java.util.Arrays;
import java.util.Collection;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;

/**
 * Stores sets of SCTIDs in integer form, using an {@link InternalIdMap} as the
 * mapping between SCTIDs and integers.
 * 
 * @since 7.0
 */
public final class InternalSctIdSet {

	public static final Builder builder(final InternalIdMap internalIdMap) {
		return new Builder(internalIdMap);
	}

	public static final class Builder {

		private final InternalIdMap internalIdMap;
		private final IntSet internalIds = PrimitiveSets.newIntOpenHashSet();

		public Builder(final InternalIdMap internalIdMap) {
			this.internalIdMap = internalIdMap;
		}

		public Builder addAll(final Collection<String> sctIds) {
			final LongSet longSctIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(sctIds.size()); 

			sctIds.stream()
				.map(Long::parseLong)
				.forEachOrdered(longSctIds::add);

			return addAll(longSctIds);
		}

		public Builder addAll(final LongSet sctIdsToAdd) {
			for (final LongIterator itr = sctIdsToAdd.iterator(); itr.hasNext(); /*empty*/) {
				final long sctId = itr.next();
				final int internalId = internalIdMap.getInternalId(sctId);
				internalIds.add(internalId);
			}

			return this;
		}

		public InternalSctIdSet build() {
			final int[] internalIdArray = internalIds.toArray();
			Arrays.sort(internalIdArray);
			return new InternalSctIdSet(internalIdMap, internalIdArray);
		}
	}

	private final InternalIdMap internalIdMap;
	private final int[] internalIdArray;

	private InternalSctIdSet(final InternalIdMap internalIdMap, final int[] internalIdArray) {
		this.internalIdMap = internalIdMap;
		this.internalIdArray = internalIdArray;
	}

	public boolean contains(final String sctId) {
		return contains(Long.parseLong(sctId));
	}

	public boolean contains(final long sctId) {
		final int internalId = internalIdMap.getInternalId(sctId);

		if (InternalIdMap.NO_INTERNAL_ID == internalId) {
			return false;
		} else {
			return Arrays.binarySearch(internalIdArray, internalId) >= 0;
		}
	}
}
