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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.Set;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Functions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Longs;

/**
 * Class for collecting IDs of concepts that are marked for inactivation.
 * Concept inactivation is calculated based on the below rules:
 * <p>Let assume the followings:
 * <ul>
 * <li><b>concept_a</b> is a transitive descendant of the <b>SCT root</b>.</li>
 * <li><b>concept_b</b> is a transitive descendant of the <b>SCT root</b>.</li>
 * <li>neither direct nor transitive association exists between <b>concept_a</b> and <b>concept_b</b> via active IS_A relationships.</li>
 * <li><b>concept_c</b> is the direct descendant of <b>concept_a</b>.</li>
 * <li><b>concept_c</b> is a transitive descendant of <b>concept_b</b>.</li>
 * </ul>
 * <p>Expected behavior when one retires <b>concept_a</b> and all its descendants:
 * <ul>
 * <li><b>concept_a</b> and <b>concept_c-IS_A-concept_a</b> relationship gets retired, 
 * but <b>concept_c </b>remains active since it has an active IS_A relationship to <b>concept_b.</b></li>
 * </ul>
 * 
 *
 */
public class SnomedConceptInactivationIdCollector {
	
	/**
	 * Returns with a collection of SNOMED&nbsp;CT concept IDs representing a subset of concepts that has to be retired
	 * when retiring the given concepts.
	 * survivor
	 * @param branchPath the branch path.
	 * @param conceptIdsToInactivate concept IDs to retire with all their corresponding descendants.
	 * @return a collections of concept IDs to retire.
	 */
	public Collection<String> collectSelfAndDescendantConceptIds(final IBranchPath branchPath, final String... conceptIdsToInactivate) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(conceptIdsToInactivate, "conceptIdsToInactivate");
		
		final Collection<String> focusConceptIds = newHashSet(conceptIdsToInactivate);
		final Collection<String> markedForInactivationIds = newHashSet();
		final Collection<String> survivorFocusConceptSupertypeIs = newHashSet();
		
		for (final String focusConceptId : focusConceptIds) {
		
			final Collection<String> selfAndAllSubTypeIds = getSelfAndAllSubTypeIds(branchPath, focusConceptId);
			final Collection<String> selfAndAllSuperTypsIds = getSelfAndAllSuperTypsIds(branchPath, focusConceptId);
			
			markedForInactivationIds.addAll(selfAndAllSubTypeIds);
			survivorFocusConceptSupertypeIs.addAll(selfAndAllSuperTypsIds);
			survivorFocusConceptSupertypeIs.removeAll(selfAndAllSubTypeIds);
			
			for (final String descendantId : selfAndAllSubTypeIds) {
				if (focusConceptIds.contains(descendantId)) {
					continue; //delete not matter what
				}
				
				final Collection<String> allSuperTypeIds = getAllSuperTypeIds(branchPath, descendantId);
			
				allSuperTypeIds.removeAll(selfAndAllSuperTypsIds);
				allSuperTypeIds.removeAll(markedForInactivationIds);
				allSuperTypeIds.removeAll(survivorFocusConceptSupertypeIs);
				
				if (!isEmpty(allSuperTypeIds)) {
					markedForInactivationIds.remove(descendantId);
				}
				
			}
			
		} 
		
		return unmodifiableCollection(markedForInactivationIds);
		
	}

	/**
	 * Returns with a collection of all ancestor concept IDs plus the ID argument. 
	 * @param branchPath the branch path.
	 * @param id the unique ID of the focus concept.
	 * @return a collection of ancestor concept IDs and the argument ID.
	 */
	protected Collection<String> getSelfAndAllSuperTypsIds(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(id, "id");
		final Collection<String> selfAndAllSuperTypsIds = getAllSuperTypeIds(branchPath, id);
		selfAndAllSuperTypsIds.add(id);
		return selfAndAllSuperTypsIds;
	}

	/**
	 * Returns with a collection of all descendant concept IDs plus the ID argument. 
	 * @param branchPath the branch path.
	 * @param id the unique ID of the focus concept.
	 * @return a collection of descendant concept IDs and the argument ID.
	 */
	protected Collection<String> getSelfAndAllSubTypeIds(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(id, "id");
		final Collection<String> selfAndAllSubTypeIds = getAllSubTypeIds(branchPath, id);
		selfAndAllSubTypeIds.add(id);
		return selfAndAllSubTypeIds;
	}

	/**
	 * Returns with all ancestor concept IDs of the concept argument. 
	 * @param branchPath the branch path.
	 * @param id the unique ID of the focus concept.
	 * @return a collection of ancestor concept IDs.
	 */
	protected Collection<String> getAllSuperTypeIds(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(id, "id");
		
		final ISnomedConcept concept = SnomedRequests.prepareGetConcept()
			.setComponentId(id)
			.build(branchPath.getPath())
			.execute(getServiceForClass(IEventBus.class))
			.getSync();
		
		final ImmutableSet.Builder<Long> longIds = ImmutableSet.builder();
		if (concept.getParentIds() != null) {
			longIds.addAll(Longs.asList(concept.getParentIds()));
		}
		if (concept.getAncestorIds() != null) {
			longIds.addAll(Longs.asList(concept.getAncestorIds()));
		}
		
		final Set<String> allSuperTypeIds = newHashSet();
		FluentIterable.from(longIds.build())
			.transform(Functions.toStringFunction())
			.copyInto(allSuperTypeIds);
		
		return allSuperTypeIds;
	}

	/**
	 * Returns with all descendant concept IDs of the concept argument. 
	 * @param branchPath the branch path.
	 * @param id the unique ID of the focus concept.
	 * @return a collection of descendant concept IDs.
	 */
	protected Collection<String> getAllSubTypeIds(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(id, "id");

		final SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.filterByAncestor(id)
				.build(branchPath.getPath())
				.execute(getServiceForClass(IEventBus.class))
				.getSync();

		final Set<String> allSubTypeIds = newHashSet();
		for (ISnomedConcept concept : concepts) {
			allSubTypeIds.add(concept.getId());
		}

		return allSubTypeIds;
	}
}
