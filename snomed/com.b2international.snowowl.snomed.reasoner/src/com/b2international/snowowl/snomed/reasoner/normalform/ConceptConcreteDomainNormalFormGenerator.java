/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.normalform;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.reasoner.classification.ReasonerTaxonomyWalker;
import com.b2international.snowowl.snomed.reasoner.diff.OntologyChangeProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

public class ConceptConcreteDomainNormalFormGenerator extends NormalFormGenerator<ConcreteDomainFragment> {

	private final LongKeyMap<Set<ConcreteDomainFragment>> concreteDomainCache = PrimitiveMaps.newLongKeyOpenHashMap();

	public ConceptConcreteDomainNormalFormGenerator(final ReasonerTaxonomyBuilder taxonomyBuilder,
			final ReasonerTaxonomyWalker taxonomyWalker, 
			final OntologyChangeProcessor<ConcreteDomainFragment> processor,
			final Ordering<ConcreteDomainFragment> ordering) {

		super(taxonomyBuilder, taxonomyWalker, processor, ordering);
	}

	@Override
	public Collection<ConcreteDomainFragment> getExistingComponents(final long conceptId) {
		return taxonomyBuilder.getInferredConcreteDomainFragments(conceptId);
	}

	@Override
	public Collection<ConcreteDomainFragment> getGeneratedComponents(final long conceptId, final LongSet parentIds, final LongSet ancestorIds) {
		final Set<ConcreteDomainFragment> computedItems = newHashSet(taxonomyBuilder.getStatedConcreteDomainFragments(conceptId));

		for (final LongIterator itr = parentIds.iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			computedItems.addAll(getCachedComponents(parentId));
		}

		concreteDomainCache.put(conceptId, computedItems);
		return computedItems;
	}

	private Collection<ConcreteDomainFragment> getCachedComponents(final long conceptId) {
		final Set<ConcreteDomainFragment> existingSet = concreteDomainCache.get(conceptId);

		if (null != existingSet) {
			return existingSet;
		} else {
			return ImmutableSet.of();
		}
	}
}
