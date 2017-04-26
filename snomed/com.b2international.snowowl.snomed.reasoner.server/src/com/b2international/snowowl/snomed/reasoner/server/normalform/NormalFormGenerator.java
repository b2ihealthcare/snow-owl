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

import java.io.Serializable;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.google.common.collect.Ordering;

import bak.pcj.LongIterator;
import bak.pcj.list.LongList;

/**
 * Base class for different implementations, which generate a set of components in normal form, based on a subsumption
 * hierarchy encapsulated in a reasoner.
 * 
 * @param <T> the generated component type
 * 
 */
public abstract class NormalFormGenerator<T extends Serializable> {

	protected final ReasonerTaxonomy reasonerTaxonomy;
	
	protected final InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder;
	
	public NormalFormGenerator(final ReasonerTaxonomy reasonerTaxonomy, InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder) {
		this.reasonerTaxonomy = reasonerTaxonomy;
		this.reasonerTaxonomyBuilder = reasonerTaxonomyBuilder;
	}
	
	/**
	 * Computes and returns all changes as a result of normal form computation.
	 * 
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call <code>done()</code> on the given monitor. Accepts <code>null</code>, indicating that no progress should
	 * be reported and that the operation cannot be cancelled.
	 * @param processor the change processor to route changes to
	 * @param ordering an ordering defined over existing and generated components, used for detecting changes
	 * @return the total number of generated components
	 */
	public final int collectNormalFormChanges(final IProgressMonitor monitor, final OntologyChangeProcessor<T> processor, final Ordering<T> ordering) {

		final LongList entries = reasonerTaxonomy.getConceptIds();
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Generating normal form...", entries.size());
		int generatedComponentCount = 0;
		
		try {
		
			for (final LongIterator itr = entries.iterator(); itr.hasNext(); /* empty */) {
				final long conceptId = itr.next();
				final Collection<T> existingComponents = getExistingComponents(conceptId);
				final Collection<T> generatedComponents = getGeneratedComponents(conceptId);
				processor.apply(conceptId, existingComponents, generatedComponents, ordering, subMonitor.newChild(1));
				generatedComponentCount += generatedComponents.size();
			}

			processor.handleAddedSubjects();
		} finally {
			subMonitor.done();
		}
		
		return generatedComponentCount; 
	}
	
	public abstract Collection<T> getExistingComponents(final long conceptId);

	/**
	 * Computes and returns a set of components in normal form for the specified concept.
	 * @param concept the concept for which components should be generated
	 * @return the generated components of the specified concept in normal form
	 */
	public abstract Collection<T> getGeneratedComponents(final long conceptId);
}