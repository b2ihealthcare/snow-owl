/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.concurrent.atomic.AtomicLong;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.id.reservations.ISnomedIdentiferReservationService;
import com.b2international.snowowl.snomed.datastore.internal.id.reservations.ReservationRangeImpl;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BoundType;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.primitives.Ints;

/**
 * An item identifier generation strategy that assigns item identifiers to components in a sequential
 * fashion.
 * 
 * @since 5.4
 */
public class SequentialItemIdGenerationStrategy implements ItemIdGenerationStrategy {

	private static final int MAX_ATTEMPT = 512;

	private class ItemIdCounter {

		private final Range<Long> allowedRange;
		private final RangeSet<Long> excludedRanges;
		private final AtomicLong counter;
		
		public ItemIdCounter(final String namespace, final ComponentCategory category) {
			this.allowedRange = Range.closedOpen(getLowerInclusiveId(namespace), getUpperExclusiveId(namespace));
			
			// getNextItemId() will add 1 to this value immediately
			this.counter = new AtomicLong(allowedRange.lowerEndpoint() - 1L);
			
			final FluentIterable<Range<Long>> excludedRangesIterable = FluentIterable.from(reservationService.getReservations())
				.filter(ReservationRangeImpl.class)
				.filter(new Predicate<ReservationRangeImpl>() {
					@Override public boolean apply(ReservationRangeImpl input) {
						return input.affects(namespace, category);
					}
				})
				.transform(new Function<ReservationRangeImpl, Range<Long>>() {
					@Override public Range<Long> apply(ReservationRangeImpl input) {
						return input.getItemIdRange();
					}
				});

			final ImmutableRangeSet.Builder<Long> excludedRangesBuilder = ImmutableRangeSet.builder();
			for (final Range<Long> range : excludedRangesIterable) {
				excludedRangesBuilder.add(range);
			}
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

		public long getNextItemId(long stepSize) {
			long current;
			long next;
			
	        do {
	        	
	            current = counter.get();
				next = snapToLowerBound(current + stepSize);
				
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

	        } while (!counter.compareAndSet(current, next));
	        
	        return next;
		}
		
		private long snapToLowerBound(final long value) {
			if (value < allowedRange.lowerEndpoint()) { // inclusive
				return allowedRange.upperEndpoint() - (allowedRange.lowerEndpoint() - value);
			} else if (value >= allowedRange.upperEndpoint()) { // exclusive
				return allowedRange.lowerEndpoint() + (value - allowedRange.upperEndpoint());
			} else {
				return value;
			}
		}
	}
	
	private static long getLowerInclusiveId(final String namespace) {
		return CompareUtils.isEmpty(namespace) ? SnomedIdentifiers.MIN_INT_ITEMID : SnomedIdentifiers.MIN_NAMESPACE_ITEMID;
	}
	
	private static long getUpperExclusiveId(final String namespace) {
		return CompareUtils.isEmpty(namespace) ? SnomedIdentifiers.MAX_INT_ITEMID : SnomedIdentifiers.MAX_NAMESPACE_ITEMID;
	}
	
	private final ISnomedIdentiferReservationService reservationService;
	private final LoadingCache<Pair<String, ComponentCategory>, ItemIdCounter> lastItemIds;
	
	public SequentialItemIdGenerationStrategy(final ISnomedIdentiferReservationService reservationService) {
		this.reservationService = reservationService;
		this.lastItemIds = CacheBuilder.newBuilder().build(CacheLoader.from(new Function<Pair<String, ComponentCategory>, ItemIdCounter>() {
			@Override public ItemIdCounter apply(final Pair<String,ComponentCategory> input) {
				return new ItemIdCounter(input.getA(), input.getB());
			}
		}));
	}

	@Override
	public String generateItemId(final String namespace, final ComponentCategory category, int attempt) {
		final Pair<String, ComponentCategory> key = Pair.identicalPairOf(Strings.emptyToNull(namespace), category);
		final int limitedAttempt = Ints.min(attempt, MAX_ATTEMPT); // 512^512 = 256K will be the biggest jump forward
		final int stepSize = 2 * limitedAttempt - 1; 
		final long nextItemId = lastItemIds.getUnchecked(key).getNextItemId(stepSize);
		return Long.toString(nextItemId);
	}
}
