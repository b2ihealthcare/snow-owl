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
import java.util.stream.Stream;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.ints.IntSet;
import com.google.common.collect.ImmutableList;

/**
 * Allows associating SCTID keys in integer form to multiple values of the
 * specified type, using an {@link InternalIdMap} as the mapping between SCTIDs
 * and integers.
 * 
 * @param <T> the value type
 * @since 7.0
 */
public final class InternalIdMultimap<T> {

	/**
	 * The default expected size for the map.
	 */
	private static final int EXPECTED_SIZE = 600_000;

	public static final <B> Builder<B> builder(final InternalIdMap internalIdMap) {
		return builder(internalIdMap, EXPECTED_SIZE);
	}

	public static final <B> Builder<B> builder(final InternalIdMap internalIdMap, final int expectedSize) {
		return new Builder<>(internalIdMap, expectedSize);
	}

	public static final class Builder<B> {

		private final InternalIdMap internalIdMap;
		private final IntKeyMap<Object> internalIdMultimap;

		public Builder(final InternalIdMap internalIdMap, final int expectedSize) {
			this.internalIdMap = internalIdMap;
			this.internalIdMultimap = PrimitiveMaps.newIntKeyOpenHashMapWithExpectedSize(expectedSize);
		}

		public Builder<B> putAll(final String key, final Collection<B> values) {
			return putAll(Long.parseLong(key), values);
		}

		@SuppressWarnings("unchecked")
		private Builder<B> putAll(final long key, final Collection<B> values) {
			final int internalId = internalIdMap.getInternalIdChecked(key);
			ImmutableList.Builder<B> itemsForKey = (ImmutableList.Builder<B>) internalIdMultimap.get(internalId);
			if (itemsForKey == null) {
				itemsForKey = ImmutableList.builder();
				internalIdMultimap.put(internalId, itemsForKey);
			}

			itemsForKey.addAll(values);
			return this;
		}

		@SuppressWarnings("unchecked")
		public InternalIdMultimap<B> build() {
			// Freeze lists, re-using the IntKeyMap in the process
			final IntSet internalIdKeys = internalIdMultimap.keySet();
			for (final IntIterator itr = internalIdKeys.iterator(); itr.hasNext(); /* empty */) {
				final int key = itr.next();
				final ImmutableList.Builder<B> itemsForKey = (ImmutableList.Builder<B>) internalIdMultimap.get(key);
				internalIdMultimap.put(key, itemsForKey.build());
			}

			return new InternalIdMultimap<B>(internalIdMap, internalIdMultimap);
		}
	}

	private final InternalIdMap internalIdMap;
	private final IntKeyMap<Object> internalIdMultimap;

	private InternalIdMultimap(final InternalIdMap internalIdMap, final IntKeyMap<Object> internalIdMultimap) {
		this.internalIdMap = internalIdMap;
		this.internalIdMultimap = internalIdMultimap;
	}

	public Collection<T> get(final String key) {
		return get(Long.parseLong(key));
	}

	@SuppressWarnings("unchecked")
	public Collection<T> get(final long key) {
		final int internalId = internalIdMap.getInternalIdChecked(key);
		final ImmutableList<T> values = (ImmutableList<T>) internalIdMultimap.get(internalId);
		if (values == null) {
			return ImmutableList.of();
		} else {
			return values;
		}
	}

	@SuppressWarnings("unchecked")
	public Stream<T> valueStream() {
		return internalIdMultimap.values()
				.stream()
				.flatMap(obj -> {
					return ((ImmutableList<T>) obj).stream();
				});
	}
}
