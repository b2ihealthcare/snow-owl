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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.datastore.server.snomed.index.taxonomy.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.classification.ReasonerTaxonomyInferrer;
import com.b2international.snowowl.snomed.reasoner.diff.OntologyChangeProcessor;
import com.google.common.collect.Ordering;

/**
 * Base class for different implementations, which generate a set of components in normal form, based on a subsumption
 * hierarchy encapsulated in a reasoner.
 * 
 * @param <T> the generated component type
 * @since
 */
public abstract class NormalFormGenerator<T extends Serializable> {

	protected final ReasonerTaxonomy taxonomy;

	public NormalFormGenerator(final ReasonerTaxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}
	
	/**
	 * Computes and returns all changes as a result of normal form computation.
	 * 
	 * @param monitor   the progress monitor to use for reporting progress to the
	 *                  user. It is the caller's responsibility to call
	 *                  <code>done()</code> on the given monitor. Accepts
	 *                  <code>null</code>, indicating that no progress should be
	 *                  reported and that the operation cannot be cancelled.
	 * @param processor the change processor to route changes to
	 * @param ordering  an ordering defined over existing and generated components,
	 *                  used for detecting changes
	 * @return the total number of generated components
	 */
	public final int collectNormalFormChanges(final IProgressMonitor monitor, final OntologyChangeProcessor<T> processor, final Ordering<T> ordering) {

		final LongList entries = taxonomy.getIterationOrder();
		LongSet previousLayer = null;
		LongSet currentLayer = PrimitiveSets.newLongOpenHashSet();
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Generating normal form...", entries.size());
		int generatedComponentCount = 0;
		
		try {
		
			for (final LongIterator itr = entries.iterator(); itr.hasNext(); /* empty */) {
				final long conceptId = itr.next();
				
				if (conceptId == ReasonerTaxonomyInferrer.DEPTH_CHANGE) {
					if (previousLayer != null) {
						invalidate(previousLayer);
					}
					
					previousLayer = currentLayer;
					currentLayer = PrimitiveSets.newLongOpenHashSet();
				} else {
					final Collection<T> existingComponents = getExistingComponents(conceptId);
					final Collection<T> generatedComponents = getGeneratedComponents(conceptId);
					processor.apply(conceptId, existingComponents, generatedComponents, ordering, subMonitor.newChild(1));
					generatedComponentCount += generatedComponents.size();
					currentLayer.add(conceptId);
				}
			}
			
		} finally {
			subMonitor.done();
		}
		
		return generatedComponentCount; 
	}

	/**
	 * Indicates that the BFS iteration has reached a new level in the tree.
	 * Generators are free to drop caches associated with components in the previous
	 * layer.
	 * 
	 * @param keysToInvalidate 
	 */
	protected void invalidate(final LongSet keysToInvalidate) {
		return;
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
	 * @param concept the concept for which components should be generated
	 * @return the generated components of the specified concept in normal form
	 */
	public abstract Collection<T> getGeneratedComponents(final long conceptId);
}
