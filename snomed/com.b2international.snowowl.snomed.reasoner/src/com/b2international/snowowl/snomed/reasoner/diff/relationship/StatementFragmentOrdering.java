/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.diff.relationship;

import static com.google.common.base.Preconditions.checkState;

import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragmentWithDestination;
import com.b2international.snowowl.snomed.datastore.StatementFragmentWithValue;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Longs;

/**
 * Compares {@link StatementFragment} instances for change processing. 
 *
 */
public final class StatementFragmentOrdering extends Ordering<StatementFragment> {
	
	public static final Ordering<StatementFragment> INSTANCE = new StatementFragmentOrdering();
	
	private static final Ordering<Class<?>> CLASS_ORDERING = Ordering.explicit(
		StatementFragmentWithDestination.class, 
		StatementFragmentWithValue.class);
	
	private StatementFragmentOrdering() {
		// Prevents instantiation
	}
	
	@Override
	public int compare(final StatementFragment o1, final StatementFragment o2) {

		final int classDelta = CLASS_ORDERING.compare(o1.getClass(), o2.getClass());
		if (classDelta != 0) 
			return classDelta;
		
		final int attributeDelta = Longs.compare(o1.getTypeId(), o2.getTypeId());
		if (attributeDelta != 0)
			return attributeDelta;

		final int groupDelta = o1.getGroup() - o2.getGroup();
		if (groupDelta != 0)
			return groupDelta;
		
		final int unionGroupDelta = o1.getUnionGroup() - o2.getUnionGroup();
		if (unionGroupDelta != 0)
			return unionGroupDelta;

		final int universalDelta = Booleans.compare(o1.isUniversal(), o2.isUniversal());
		if (universalDelta != 0)
			return universalDelta;

		if (o1 instanceof StatementFragmentWithDestination) {
			checkState(o2 instanceof StatementFragmentWithDestination);
			
			final StatementFragmentWithDestination d1 = (StatementFragmentWithDestination) o1;
			final StatementFragmentWithDestination d2 = (StatementFragmentWithDestination) o2;
			
			final int destinationDelta = Longs.compare(d1.getDestinationId(), d2.getDestinationId());
			if (destinationDelta != 0)
				return destinationDelta;
			
			final int destinationNegatedDelta = Booleans.compare(d1.isDestinationNegated(), d2.isDestinationNegated());
			return destinationNegatedDelta;
		}
		
		if (o1 instanceof StatementFragmentWithValue) {
			checkState(o2 instanceof StatementFragmentWithValue);
			
			final StatementFragmentWithValue v1 = (StatementFragmentWithValue) o1;
			final StatementFragmentWithValue v2 = (StatementFragmentWithValue) o2;
			
			final int valueDelta = v1.getValue().compareTo(v2.getValue());
			return valueDelta;
		}
		
		throw new IllegalStateException("Statement fragment ordering is incomplete.");
	}
}