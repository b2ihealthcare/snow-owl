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
package com.b2international.snowowl.snomed.core.merge;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class SnomedInvalidRelationshipMergeConflictRule extends AbstractSnomedMergeConflictRule {

	@Override
	public Collection<MergeConflict> validate(CDOTransaction transaction) {
		
		Iterable<Relationship> newOrDirtyRelationships = Iterables.concat(ComponentUtils2.getDirtyObjects(transaction, Relationship.class),
				ComponentUtils2.getNewObjects(transaction, Relationship.class));

		Set<String> relationshipConceptIds = newHashSet();
		
		for (Relationship relationship : newOrDirtyRelationships) {
			if (relationship.isActive()) {
				relationshipConceptIds.add(relationship.getSource().getId());
				relationshipConceptIds.add(relationship.getDestination().getId());
				relationshipConceptIds.add(relationship.getType().getId());
			}
		}
		
		// Either there were no relationships added or modified, or all of them were inactive
		if (relationshipConceptIds.isEmpty()) {
			return emptySet();
		}
		
		Set<String> inactiveConceptIds = SnomedRequests.prepareSearchConcept()
				.filterByIds(relationshipConceptIds)
				.filterByActive(false)
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, BranchPathUtils.createPath(transaction).getPath())
				.execute(getEventBus())
				.then(concepts -> concepts.getItems().stream().map(IComponent::getId).collect(toSet()))
				.getSync();

		Iterable<Concept> newOrDirtyConcepts = Iterables.concat(ComponentUtils2.getDirtyObjects(transaction, Concept.class),
				ComponentUtils2.getNewObjects(transaction, Concept.class));

		for (Concept concept : newOrDirtyConcepts) {
			String conceptId = concept.getId();
			
			if (relationshipConceptIds.contains(conceptId)) {
				if (concept.isActive()) {
					inactiveConceptIds.remove(conceptId);
				} else {
					inactiveConceptIds.add(conceptId);
				}
			}
		}

		// None of the concepts referenced by an active relationship were inactive
		if (inactiveConceptIds.isEmpty()) {
			return emptySet();
		}
		
		List<MergeConflict> conflicts = newArrayList();
			
		for (Relationship relationship : newOrDirtyRelationships) {
			
			if (inactiveConceptIds.contains(relationship.getSource().getId())) {
				conflicts.add(MergeConflictImpl.builder()
								.componentId(relationship.getId())
								.componentType("Relationship")
								.conflictingAttribute(ConflictingAttributeImpl.builder().property("sourceId").value(relationship.getSource().getId()).build())
								.type(ConflictType.HAS_INACTIVE_REFERENCE)
								.build());	
			}
			
			if (inactiveConceptIds.contains(relationship.getDestination().getId())) {
				conflicts.add(MergeConflictImpl.builder()
						.componentId(relationship.getId())
						.componentType("Relationship")
						.conflictingAttribute(ConflictingAttributeImpl.builder().property("destinationId").value(relationship.getDestination().getId()).build())
						.type(ConflictType.HAS_INACTIVE_REFERENCE)
						.build());
			}
			
			if (inactiveConceptIds.contains(relationship.getType().getId())) {
				conflicts.add(MergeConflictImpl.builder()
						.componentId(relationship.getId())
						.componentType("Relationship")
						.conflictingAttribute(ConflictingAttributeImpl.builder().property("typeId").value(relationship.getType().getId()).build())
						.type(ConflictType.HAS_INACTIVE_REFERENCE)
						.build());
			}
		}
		
		return conflicts;
	}
}
