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
package com.b2international.snowowl.snomed.reasoner.normalform;

import java.io.Serializable;
import java.util.Collection;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.reasoner.classification.ReasonerTaxonomyWalker;
import com.b2international.snowowl.snomed.reasoner.classification.TaxonomyCallback;
import com.b2international.snowowl.snomed.reasoner.diff.OntologyChangeProcessor;
import com.google.common.collect.Ordering;

/**
 * Base class for different implementations, which generate a set of components
 * in normal form, based on a subsumption hierarchy encapsulated in a reasoner.
 * 
 * @param <T> the generated component type
 * @since
 */
public abstract class NormalFormGenerator<T extends Serializable> implements TaxonomyCallback {

	protected final ReasonerTaxonomyBuilder taxonomyBuilder;
	protected final ReasonerTaxonomyWalker taxonomyWalker;

	private final OntologyChangeProcessor<T> processor;
	private final Ordering<T> ordering;

	protected NormalFormGenerator(final ReasonerTaxonomyBuilder taxonomyBuilder,
			final ReasonerTaxonomyWalker taxonomyWalker,
			final OntologyChangeProcessor<T> processor,
			final Ordering<T> ordering) {

		this.taxonomyBuilder = taxonomyBuilder;
		this.taxonomyWalker = taxonomyWalker;
		this.processor = processor;
		this.ordering = ordering;
	}

	@Override
	public final void onConcept(final long conceptId, final LongSet parentIds, final LongSet ancestorIds) {
		final Collection<T> existingComponents = getExistingComponents(conceptId);
		final Collection<T> generatedComponents = getGeneratedComponents(conceptId, parentIds, ancestorIds);
		processor.apply(conceptId, existingComponents, generatedComponents, ordering);
	}

	/**
	 * Returns the set of currently persisted components for the specified concept.
	 * <p>
	 * The returned collection might not be in normal form.
	 * 
	 * @param concept the concept for which components should be generated
	 * @return the existing components of the specified concept
	 */
	public abstract Collection<T> getExistingComponents(final long conceptId);

	/**
	 * Computes and returns a set of components in normal form for the specified
	 * concept.
	 * 
	 * @param concept     the concept for which components should be generated
	 * @param parentIds   the inferred direct parents of the concept
	 * @param ancestorIds the inferred ancestors (both direct and indirect) of the
	 *                    concept
	 * @return the generated components of the specified concept in normal form
	 */
	public abstract Collection<T> getGeneratedComponents(final long conceptId, final LongSet parentIds, final LongSet ancestorIds);
}
