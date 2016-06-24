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
package com.b2international.snowowl.datastore.server.snomed.merge.rules;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static java.util.Collections.emptySet;

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
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class SnomedInvalidRelationshipMergeConflictRule extends AbstractSnomedMergeConflictRule {

	@Override
	public Collection<MergeConflict> validate(CDOTransaction transaction) {
		
		Iterable<Relationship> relationships = Iterables.concat(ComponentUtils2.getDirtyObjects(transaction, Relationship.class), ComponentUtils2.getNewObjects(transaction, Relationship.class));
		
		Set<String> relationshipConceptIds = FluentIterable.from(relationships).transformAndConcat(new Function<Relationship, Collection<String>>() {
			@Override
			public Collection<String> apply(Relationship input) {
				if (input.isActive()) {
					Set<String> ids = newHashSetWithExpectedSize(3);
					ids.add(input.getSource().getId());
					ids.add(input.getDestination().getId());
					ids.add(input.getType().getId());
					return ids;
				}
				return emptySet();
			}
		}).toSet();
		
		if (!relationshipConceptIds.isEmpty()) {
			
			SnomedConcepts snomedConcepts = SnomedRequests.prepareSearchConcept()
				.setComponentIds(relationshipConceptIds)
				.filterByActive(false)
				.all()
				.build(BranchPathUtils.createPath(transaction).getPath())
				.executeSync(getEventBus());
				
			if (!snomedConcepts.getItems().isEmpty()) {
				
				List<MergeConflict> conflicts = newArrayList();
				
				Set<String> inactiveConceptIds = FluentIterable.from(snomedConcepts).transform(IComponent.ID_FUNCTION).toSet();
				
				for (Relationship relationship : relationships) {
					
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
		return emptySet();
	}
}
