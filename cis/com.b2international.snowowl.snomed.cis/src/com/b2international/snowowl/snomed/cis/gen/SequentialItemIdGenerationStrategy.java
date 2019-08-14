/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.gen;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newLinkedHashSetWithExpectedSize;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.cis.internal.reservations.ReservationRangeImpl;
import com.b2international.snowowl.snomed.cis.reservations.ISnomedIdentifierReservationService;
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
public final class SequentialItemIdGenerationStrategy implements ItemIdGenerationStrategy {

	private static final int MAX_ATTEMPT = 10;
	
	public final class ItemIdCounter {

		private final Range<Long> allowedRange;
		private final RangeSet<Long> excludedRanges;
		private final AtomicLong counter;
		
		public ItemIdCounter(final String namespace, final ComponentCategory category) {
			this.allowedRange = Range.closedOpen(getLowerInclusiveId(namespace), getUpperExclusiveId(namespace));
			this.counter = new AtomicLong(allowedRange.lowerEndpoint());
			
			final ImmutableRangeSet.Builder<Long> excludedRangesBuilder = ImmutableRangeSet.builder();
			FluentIterable.from(reservations.getReservations())
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
		
		public void setCounter(long newCounter) {
			this.counter.set(newCounter);
		}

		private synchronized Set<String> getNextItemIds(int quantity, int stepSize) {
			
			long current = counter.get();
			final long oldCurrent = current;

	        final Set<String> generatedItemIds = newLinkedHashSetWithExpectedSize(quantity);
	        while (quantity > 0) {
	        	final Range<Long> containingRange = excludedRanges.rangeContaining(current);
	        	if (containingRange != null) {
	        		current = snapToLowerBound(containingRange.upperEndpoint() + 1L);
	        	} else {
	        		generatedItemIds.add(Long.toString(current));
	        		quantity--;
	        		current = snapToLowerBound(current + stepSize + 1L);
	        	}
	        	if (oldCurrent == current) {
	        		throw new IllegalArgumentException("No more itemIds are available in this counter");
	        	}
	        }
		
	        // set the currently available itemId for the next generation
	        counter.set(current);
	        
	        return generatedItemIds;
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
	
	private final ISnomedIdentifierReservationService reservations;
	private final LoadingCache<Pair<String, ComponentCategory>, ItemIdCounter> lastItemIds;
	
	public SequentialItemIdGenerationStrategy(ISnomedIdentifierReservationService reservations) {
		this.reservations = reservations;
		this.lastItemIds = CacheBuilder.newBuilder().build(CacheLoader.from(namespaceCategoryPair -> new ItemIdCounter(namespaceCategoryPair.getA(), namespaceCategoryPair.getB())));
	}
	
	public ItemIdCounter getOrCreateCounter(String namespace, ComponentCategory category) {
		return lastItemIds.getUnchecked(Pair.identicalPairOf(Strings.nullToEmpty(namespace), category));
	}
	
	@Override
	public Set<String> generateItemIds(final String namespace, final ComponentCategory category, int quantity, int attempt) {
		checkArgument(quantity > 0, "At least 1 quantity must be generated");
		final int limitedAttempt = Ints.min(attempt - 1, MAX_ATTEMPT);
		final int stepSize = (1 << limitedAttempt) - 1; 
		return getOrCreateCounter(namespace, category).getNextItemIds(quantity, stepSize);
	}
}
