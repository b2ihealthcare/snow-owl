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
package com.b2international.snowowl.snomed.reasoner.server.diff;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndMolduleAssigner;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * Compares two collections of change subjects and calls template methods whenever a removed, added or unmodified
 * element is encountered.
 * 
 * @param <T> the change subject's type
 * 
 */
public abstract class OntologyChangeProcessor<T extends Serializable> {
	
	private NamespaceAndMolduleAssigner relationshipNamespaceAllocator;

	public OntologyChangeProcessor(NamespaceAndMolduleAssigner relationshipNamespaceAllocator) {
		Preconditions.checkNotNull(relationshipNamespaceAllocator);
		this.relationshipNamespaceAllocator = relationshipNamespaceAllocator;
	}
	
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
		
		//collect the inferred properties per concept
		Multimap<String, T> newPropertiesMultiMap = HashMultimap.create(); 
		for (final T newMini : sortedNew) {

			if (subMonitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			if (ordering.binarySearch(sortedOld, newMini) < 0) {
				newPropertiesMultiMap.put(String.valueOf(conceptId), newMini);
			}
			
			subMonitor.worked(1);
		}
		handleAddedSubjects(newPropertiesMultiMap);
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
	 * Returns the relationship namespace and module allocator assigned to this change processor.
	 * @return
	 */
	protected NamespaceAndMolduleAssigner getRelationshipNamespaceAllocator() {
		return relationshipNamespaceAllocator;
	}

	/**
	 * Handles the concept id to new inferred properties map.
	 * Subclasses can overwrite to add custom behavior before handling the new properties for each concept.
	 * @param properties multi map
	 */
	protected void handleAddedSubjects(Multimap<String, T> propertiesMultiMap) {
		for (Map.Entry<String, T> entry : propertiesMultiMap.entries()) {
			handleAddedSubject(entry.getKey(), entry.getValue());
		}
	}
	

	protected void handleRemovedSubject(final String conceptId, final T removedSubject) {
		// Subclasses should override		
	}

	protected void handleAddedSubject(final String conceptId, final T addedSubject) {
		// Subclasses should override
	}
}