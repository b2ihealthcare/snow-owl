/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.gen;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.index.DocSearcher;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.IndexRead;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.ReservationRangeImpl;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BoundType;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

/**
 * An item identifier generation strategy that assigns item identifiers to components in a sequential
 * fashion.
 * 
 * @since 5.4
 */
public class SequentialItemIdGenerationStrategy implements ItemIdGenerationStrategy {

	private class ItemIdCounter {

		private final Range<Long> allowedRange;
		private final RangeSet<Long> excludedRanges;
		private final AtomicLong counter;
		
		public ItemIdCounter(final String namespace, final ComponentCategory category) {
			this.allowedRange = Range.closed(getLowerInclusiveId(namespace), getUpperExclusiveId(namespace));
			
			final SctId lastSctId = getLastSctId(namespace, category);
			if (lastSctId != null) {
				this.counter = new AtomicLong(lastSctId.getSequence());
			} else {
				// getNextItemId() will add 1 to this value immediately
				this.counter = new AtomicLong(allowedRange.lowerEndpoint() - 1L);
			}
			
			final ImmutableRangeSet.Builder<Long> excludedRangesBuilder = ImmutableRangeSet.builder();
			FluentIterable.from(reservationService.getReservations())
				.filter(ReservationRangeImpl.class)
				.filter(rangeReservation -> rangeReservation.affects(namespace, category))
				.transform(ReservationRangeImpl::getItemIdRange)
				.forEach(range -> excludedRangesBuilder.add(range));
			
			this.excludedRanges = excludedRangesBuilder.build();

			for (final Range<Long> excludedRange : excludedRanges.asRanges()) {
				if (!excludedRange.hasLowerBound() || !excludedRange.hasUpperBound()) {
					throw new IllegalStateException("All excluded ranges should have an lower and upper bound; found: " + excludedRange + ".");
				}
				
				if (!BoundType.CLOSED.equals(excludedRange.lowerBoundType()) || !BoundType.CLOSED.equals(excludedRange.upperBoundType())) {
					throw new IllegalStateException("All excluded ranges should have a closed lower and upper bound; found: " + excludedRange + ".");
				}
			}
		}

		private SctId getLastSctId(final String namespace, final ComponentCategory category) {
			return store.read(new IndexRead<SctId>() {
				@Override
				public SctId execute(final DocSearcher index) throws IOException {
					
					final Expression idsByNamespaceAndType = Expressions.builder()
							.filter(SctId.Expressions.namespace(namespace))
							.filter(SctId.Expressions.partitionId(namespace, category))
							.build();
					
					final Hits<SctId> hits = index.search(Query.select(SctId.class)
							.where(idsByNamespaceAndType)
							.sortBy(SortBy.field(SctId.Fields.SEQUENCE, Order.DESC))
							.limit(1)
							.build());

					return Iterables.getOnlyElement(hits, null);
				}
			});
		}
		
		public long getNextItemId() {
			return counter.updateAndGet(current -> {
				long next = snapToLowerBound(current + 1L);
				
				Range<Long> firstRange = null;
				while (excludedRanges.contains(next)) {
				
					final Range<Long> container = excludedRanges.rangeContaining(next);
					if (firstRange == null) {
						firstRange = container;
					} else if (firstRange.equals(container)) {
						throw new IllegalStateException("Range visited twice while generating next item identifier: " + container);
					}
					
					// Step over this exclusion range and try again
					next = snapToLowerBound(container.upperEndpoint() + 1L);
					
					// "current" should also be skipped, so we eventually try to visit the first exclusion range seen, and throw an exception above
					if (next == current) {
						next = snapToLowerBound(next + 1L);
					}
				}
				
				return next;
			});
		}
		
		private long snapToLowerBound(final long value) {
			return allowedRange.contains(value) ? value : allowedRange.lowerEndpoint();
		}
	}
	
	private static long getLowerInclusiveId(final String namespace) {
		return CompareUtils.isEmpty(namespace) ? SnomedIdentifiers.MIN_INT_ITEMID : SnomedIdentifiers.MIN_NAMESPACE_ITEMID;
	}
	
	private static long getUpperExclusiveId(final String namespace) {
		return CompareUtils.isEmpty(namespace) ? SnomedIdentifiers.MAX_INT_ITEMID : SnomedIdentifiers.MAX_NAMESPACE_ITEMID;
	}
	
	private final Index store;
	private final ISnomedIdentiferReservationService reservationService;
	private final LoadingCache<Pair<String, ComponentCategory>, ItemIdCounter> lastItemIds;
	
	public SequentialItemIdGenerationStrategy(final Index store, final ISnomedIdentiferReservationService reservationService) {
		this.store = store;
		this.reservationService = reservationService;
		this.lastItemIds = CacheBuilder.newBuilder().build(CacheLoader.from(namespaceCategoryPair -> new ItemIdCounter(namespaceCategoryPair.getA(), namespaceCategoryPair.getB())));
	}

	@Override
	public String generateItemId(final String namespace, final ComponentCategory category) {
		final Pair<String, ComponentCategory> key = Pair.identicalPairOf(Strings.emptyToNull(namespace), category);
		final long nextItemId = lastItemIds.getUnchecked(key).getNextItemId();
		return Long.toString(nextItemId);
	}
}
