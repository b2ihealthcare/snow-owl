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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;

/**
 * Allows associating SCTID key pairs, and computing the direct and indirect
 * successors in integer form using an {@link InternalIdMap} as the mapping
 * between SCTIDs and integers.
 * 
 * @since 7.0
 */
public final class InternalIdEdges {

	public static final Builder builder(final InternalIdMap internalIdMap) {
		return new Builder(internalIdMap);
	}

	public static final class Builder {

		private static final int[] EMPTY_ARRAY = new int[0];
		
		private final InternalIdMap internalIdMap;
		private final IntSet[] edges;

		public Builder(final InternalIdMap internalIdMap) {
			this.internalIdMap = internalIdMap;
			this.edges = new IntSet[internalIdMap.size()];
		}

		public Builder addEdges(final List<String> sourceSctIds, final List<String> destinationSctIds) {
			checkArgument(sourceSctIds.size() == destinationSctIds.size(), 
					"Lists are not of equal size (sources: %s, destinations: %s)", sourceSctIds.size(), destinationSctIds.size());

			for (int i = 0; i < sourceSctIds.size(); i++) {
				final long sourceSctId = Long.parseLong(sourceSctIds.get(i));
				final long destinationSctId = Long.parseLong(destinationSctIds.get(i));
				final int sourceId = internalIdMap.getInternalIdChecked(sourceSctId);
				final int destinationId = internalIdMap.getInternalIdChecked(destinationSctId);

				IntSet destinationsForSource = edges[sourceId];
				if (destinationsForSource == null) {
					destinationsForSource = PrimitiveSets.newIntOpenHashSet();
					edges[sourceId] = destinationsForSource;
				}

				destinationsForSource.add(destinationId);
			}

			return this;
		}

		public InternalIdEdges build() {
			// Convert IntLists to arrays
			final int[][] builtEdges = Arrays.asList(edges)
					.stream()
					.map(ds -> Optional.ofNullable(ds)
							.map(IntSet::toArray)
							.map(da -> {
								Arrays.sort(da);
								return da;
							})
							.orElse(EMPTY_ARRAY))
					.toArray(length -> new int[length][]);

			return new InternalIdEdges(internalIdMap, builtEdges);
		}
	}

	private final InternalIdMap internalIdMap;
	private final int[][] edges;

	private InternalIdEdges(final InternalIdMap internalIdMap, final int[][] edges) {
		this.internalIdMap = internalIdMap;
		this.edges = edges;
	}

	public Set<String> getDestinations(final String source, final boolean direct) {
		return LongSets.toStringSet(getDestinations(Long.parseLong(source), direct));
	}

	public LongSet getDestinations(final long key, final boolean direct) {
		final int internalId = internalIdMap.getInternalId(key);
		if (internalId == InternalIdMap.NO_INTERNAL_ID) {
			return PrimitiveSets.newLongOpenHashSet();
		}

		if (direct) {
			final int[] destinations = edges[internalId];
			return toSctIds(destinations);
		} else {
			final BitSet destinations = new BitSet(internalIdMap.size());
			collectIndirectDestinations(internalId, destinations);
			return toSctIds(destinations);
		}
	}

	private LongSet toSctIds(final int[] destinations) {
		if (destinations == null) {
			return PrimitiveSets.newLongOpenHashSet();
		}

		final LongSet sctIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(destinations.length); 
		for (int i = 0; i < destinations.length; i++) {
			sctIds.add(internalIdMap.getSctId(destinations[i]));
		}

		return sctIds;
	}

	private void collectIndirectDestinations(final int internalId, final BitSet destinations) {
		final int[] directDestinations = edges[internalId];

		if (directDestinations != null) {
			for (int i = 0; i < directDestinations.length; i++) {
				if (!destinations.get(directDestinations[i])) {
					destinations.set(directDestinations[i]);
					collectIndirectDestinations(directDestinations[i], destinations);
				}
			}
		}
	}

	private LongSet toSctIds(final BitSet destinations) {
		final LongSet sctIds = PrimitiveSets.newLongOpenHashSetWithExpectedSize(destinations.cardinality()); 
		for (int i = destinations.nextSetBit(0); i >= 0; i = destinations.nextSetBit(i + 1)) {
			sctIds.add(internalIdMap.getSctId(i));

			// Prevent integer overflow
			if (i == Integer.MAX_VALUE) {
				break;
			}
		}

		return sctIds;
	}
}
