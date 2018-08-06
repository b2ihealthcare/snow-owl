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
package com.b2international.snowowl.snomed.core.merge;

import static com.b2international.snowowl.datastore.utils.ComponentUtils2.getDetachedObjects;
import static com.b2international.snowowl.datastore.utils.ComponentUtils2.getNewObjects;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 4.7
 */
public class SnomedRefsetMemberReferencingDetachedComponentRule extends AbstractSnomedMergeConflictRule {

	@Override
	public Collection<MergeConflict> validate(CDOTransaction transaction) {

		List<MergeConflict> conflicts = newArrayList();
		
		// If SNOMED CT components were detached on target, but SNOMED CT reference set members are referencing them on source branch:
		
		Map<String, String> idToComponentTypeMap = newHashMap();
		
		for (Component component : getDetachedObjects(transaction, Component.class)) {
			idToComponentTypeMap.put(component.getId(), component.eClass().getName());
		}
		
		if (!idToComponentTypeMap.isEmpty()) { // if there was any detached Component on target branch
			
			final Set<String> detachedMemberIds = FluentIterable.from(getDetachedObjects(transaction, SnomedRefSetMember.class))
					.transform(MEMBER_TO_ID_FUNCTION).toSet();

			final SnomedReferenceSetMembers membersReferencingDetachedComponents = SnomedRequests
					.prepareSearchMember()
					.filterByReferencedComponent(idToComponentTypeMap.keySet())
					.all()
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, BranchPathUtils.createPath(transaction).getPath())
					.execute(getEventBus())
					.getSync();
			
			for (SnomedReferenceSetMember member : membersReferencingDetachedComponents) {
				if (!detachedMemberIds.contains(member.getId())) {
					conflicts.add(MergeConflictImpl.builder()
									.componentId(member.getReferencedComponent().getId())
									.componentType(idToComponentTypeMap.get(member.getReferencedComponent().getId()))
									.type(ConflictType.CAUSES_MISSING_REFERENCE)
									.build());
				}
			}
			
		}
		
		// If SNOMED CT components were detached on source, but SNOMED CT reference set members are referencing them on target:
		
		Multimap<String, Pair<String, String>> referencedComponentIdToRefsetMemberMap = HashMultimap.<String, Pair<String, String>> create();
		
		for (SnomedRefSetMember member : getNewObjects(transaction, SnomedRefSetMember.class)) {
			referencedComponentIdToRefsetMemberMap.put(member.getReferencedComponentId(),
					Pair.<String, String>of(member.eClass().getName(), member.getUuid()));
		}

		if (!referencedComponentIdToRefsetMemberMap.isEmpty()) { // if there was any new reference set member on target
			
			Set<String> conceptIds = newHashSet(
					FluentIterable.from(getNewObjects(transaction, Concept.class)).transform(COMPONENT_TO_ID_FUNCTION).toSet());
			Set<String> descriptionIds = newHashSet(
					FluentIterable.from(getNewObjects(transaction, Description.class)).transform(COMPONENT_TO_ID_FUNCTION).toSet());
			Set<String> relationshipIds = newHashSet(
					FluentIterable.from(getNewObjects(transaction, Relationship.class)).transform(COMPONENT_TO_ID_FUNCTION).toSet());

			String branchPath = BranchPathUtils.createPath(transaction).getPath();
			Set<String> referencedComponentIds = referencedComponentIdToRefsetMemberMap.keySet();
			
			Set<String> referencedConceptIds = referencedComponentIds.stream()
					.filter(id -> SnomedIdentifiers.getComponentCategory(id) == ComponentCategory.CONCEPT).collect(toSet());
			Set<String> referencedRelationshipIds = referencedComponentIds.stream()
					.filter(id -> SnomedIdentifiers.getComponentCategory(id) == ComponentCategory.RELATIONSHIP).collect(toSet());
			Set<String> referencedDescriptionIds = referencedComponentIds.stream()
					.filter(id -> SnomedIdentifiers.getComponentCategory(id) == ComponentCategory.DESCRIPTION).collect(toSet());

			if (!referencedConceptIds.isEmpty()) {
				conceptIds.addAll(FluentIterable
						.from(SnomedRequests.prepareSearchConcept()
								.filterByIds(referencedConceptIds)
								.setLimit(referencedConceptIds.size())
								.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
								.execute(getEventBus())
								.getSync())
						.transform(IComponent.ID_FUNCTION).toSet());
			}
			
			if (!referencedDescriptionIds.isEmpty()) {
				descriptionIds.addAll(FluentIterable
						.from(SnomedRequests.prepareSearchDescription()
								.filterByIds(referencedDescriptionIds)
								.setLimit(referencedDescriptionIds.size())
								.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
								.execute(getEventBus())
						.getSync()).transform(IComponent.ID_FUNCTION).toSet());
			}

			if (!referencedRelationshipIds.isEmpty()) {
				relationshipIds.addAll(FluentIterable
						.from(SnomedRequests.prepareSearchRelationship()
								.filterByIds(referencedRelationshipIds)
								.setLimit(referencedRelationshipIds.size())
								.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
								.execute(getEventBus())
						.getSync()).transform(IComponent.ID_FUNCTION).toSet());
			}

			Set<String> missingConceptIds = Sets.difference(referencedComponentIds, Sets.union(Sets.union(conceptIds, descriptionIds), relationshipIds));

			for (String id : missingConceptIds) {
				for (Pair<String, String> entry : referencedComponentIdToRefsetMemberMap.get(id)) {
					conflicts.add(MergeConflictImpl
							.builder()
							.componentId(entry.getB())
							.componentType(entry.getA())
							.conflictingAttribute(ConflictingAttributeImpl.builder().property("referencedComponent").value(id).build())
							.type(ConflictType.HAS_MISSING_REFERENCE).build());
				}
			}
			
		}

		return conflicts;
	}

}
