/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.diff.concretedomain;

import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * Compares {@link ConcreteDomainFragment} instances for ontology change processing. 
 */
public final class ConcreteDomainChangeOrdering extends Ordering<ConcreteDomainFragment> {
	
	public static final Ordering<ConcreteDomainFragment> INSTANCE = new ConcreteDomainChangeOrdering();
	
	private ConcreteDomainChangeOrdering() {
		// Prevents instantiation
	}
	
	@Override
	public int compare(final ConcreteDomainFragment left, final ConcreteDomainFragment right) {
		
		final int refSetDelta = Longs.compare(left.getRefSetId(), right.getRefSetId());
		if (refSetDelta != 0) return refSetDelta;
		
		final int typeIdDelta = Longs.compare(left.getTypeId(), right.getTypeId());
		if (typeIdDelta != 0) return typeIdDelta;
		
		final int groupDelta = Ints.compare(left.getGroup(), right.getGroup());
		if (groupDelta != 0) return groupDelta;

		return left.getSerializedValue().compareTo(right.getSerializedValue());
	}
}
