/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server.normalform;

import static com.google.common.collect.Sets.newHashSet;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bak.pcj.LongIterator;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.server.diff.concretedomain.ConcreteDomainChangeOrdering;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;

/**
 * 
 */
public class ConceptConcreteDomainNormalFormGenerator extends NormalFormGenerator<ConcreteDomainFragment> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConceptConcreteDomainNormalFormGenerator.class);
	
	private final LongKeyMap concreteDomainCache = new LongKeyOpenHashMap();

	public ConceptConcreteDomainNormalFormGenerator(final ReasonerTaxonomy reasonerTaxonomy, final InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder) {
		super(reasonerTaxonomy, reasonerTaxonomyBuilder);
	}

	@Override
	public Collection<ConcreteDomainFragment> getExistingComponents(final long conceptId) {
		return reasonerTaxonomyBuilder.getInferredConcreteDomainFragments(conceptId);
	}
	
	@Override
	public Collection<ConcreteDomainFragment> getGeneratedComponents(final long conceptId) {
		
		final Set<ConcreteDomainFragment> computedItems = newHashSet(reasonerTaxonomyBuilder.getStatedConcreteDomainFragments(conceptId));
		final LongSet parents = reasonerTaxonomy.getParents(conceptId);
		
		for (final LongIterator itr = parents.iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			computedItems.addAll(getCachedComponents(parentId));
		}
		
		concreteDomainCache.put(conceptId, computedItems);
		return computedItems;
	}

	@SuppressWarnings("unchecked")
	private Collection<ConcreteDomainFragment> getCachedComponents(final long conceptId) {
		final Collection<ConcreteDomainFragment> existingSet = (Collection<ConcreteDomainFragment>) concreteDomainCache.get(conceptId);
		
		if (null != existingSet) {
			return existingSet;
		} else {
			return ImmutableSet.of();
		}
	}

	public void collectNormalFormChanges(final IProgressMonitor monitor, final OntologyChangeProcessor<ConcreteDomainFragment> processor) {
		LOGGER.info(">>> Concept concrete domain entry normal form generation");
		final Stopwatch stopwatch = Stopwatch.createStarted();
		collectNormalFormChanges(monitor, processor, ConcreteDomainChangeOrdering.INSTANCE);
		LOGGER.info(MessageFormat.format("<<< Concept concrete domain entry normal form generation [{0}]", stopwatch.toString()));
	}
}