/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server.diff;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * Compares two collections of change subjects and calls template methods whenever a removed, added or unmodified
 * element is encountered.
 * 
 * @param <T> the change subject's type
 */
public abstract class OntologyChangeProcessor<T extends Serializable> {
	
	public void apply(final long conceptId, final Collection<T> oldCollection, final Collection<T> newCollection, final Ordering<T> ordering) {
		apply(conceptId, oldCollection, newCollection, ordering, null);
	}
	
	public void apply(final long conceptId, final Collection<T> oldCollection, final Collection<T> newCollection, final Ordering<T> ordering, final IProgressMonitor monitor) {
		
		final int unitsOfWork = oldCollection.size() + newCollection.size();
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Processing changes...", unitsOfWork);

		final TreeSet<T> uniqueOlds = Sets.newTreeSet(ordering);
		final ImmutableList<T> sortedOld = ordering.immutableSortedCopy(oldCollection);
		final ImmutableList<T> sortedNew = ordering.immutableSortedCopy(newCollection);
		
		for (final T oldSubject : sortedOld) {
			
			if (subMonitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			final int idx = ordering.binarySearch(sortedNew, oldSubject);
			
			if (idx < 0 || !uniqueOlds.add(oldSubject)) {
				handleRemovedSubject(String.valueOf(conceptId), oldSubject);
			}
			
			subMonitor.worked(1);
		}
		
		for (final T newSubject : sortedNew) {

			if (subMonitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			if (ordering.binarySearch(sortedOld, newSubject) < 0) {
				handleAddedSubject(String.valueOf(conceptId), newSubject);
			}
			
			subMonitor.worked(1);
		}
	}
	
	public void apply(final Collection<OntologyChange<T>> changes, final IProgressMonitor monitor) {
		
		if (changes == null || changes.isEmpty()) {
			return;
		}
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Processing changes...", changes.size());
		
		for (final OntologyChange<T> change : changes) {
			final long conceptId = change.getConceptId();
			switch (change.getNature()) {
				case ADD:
					handleAddedSubject(String.valueOf(conceptId), change.getSubject());
					break;
				case REMOVE:
					handleRemovedSubject(String.valueOf(conceptId), change.getSubject());
					break;
				default:
					throw new IllegalStateException(MessageFormat.format("Unexpected change nature {0}.", change.getNature()));
			}
			
			subMonitor.worked(1);
		}
	}

	/**
	 * This method is called to handle a removed inferred change from the concept passed in
	 * @param conceptId
	 * @param addedSubject
	 */
	protected void handleRemovedSubject(final String conceptId, final T removedSubject) {
		// Subclasses should override		
	}

	/**
	 * This method is called to handle an added inferred change to the concept passed in
	 * @param conceptId
	 * @param addedSubject
	 */
	protected void handleAddedSubject(final String conceptId, final T addedSubject) {
		// Subclasses should override
	}
}
