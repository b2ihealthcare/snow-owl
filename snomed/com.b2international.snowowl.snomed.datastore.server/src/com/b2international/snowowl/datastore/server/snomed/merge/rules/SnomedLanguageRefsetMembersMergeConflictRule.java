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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class SnomedLanguageRefsetMembersMergeConflictRule extends AbstractSnomedMergeConflictRule {

	@Override
	public Collection<MergeConflict> validate(CDOTransaction transaction) {

		List<MergeConflict> conflicts = newArrayList();

		Iterable<SnomedLanguageRefSetMember> newLanguageRefSetMembers = ComponentUtils2.getNewObjects(transaction, SnomedLanguageRefSetMember.class);
		
		if (!Iterables.isEmpty(newLanguageRefSetMembers)) {
			
			final IBranchPath branchPath = BranchPathUtils.createPath(transaction);
			
			final Set<String> synonymAndDescendantIds = SnomedRequests.prepareGetSynonyms()
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.then(new Function<SnomedConcepts, Set<String>>() {
						@Override
						public Set<String> apply(SnomedConcepts input) {
							return FluentIterable.from(input).transform(IComponent.ID_FUNCTION).toSet();
						}
					})
					.getSync();
			
			final Set<SnomedLanguageRefSetMember> membersToRemove = newHashSet();
			
			label: for (SnomedLanguageRefSetMember newLanguageRefSetMember : newLanguageRefSetMembers) {
				
				if (!newLanguageRefSetMember.isActive()) {
					continue;
				}
				
				Description description = (Description) newLanguageRefSetMember.eContainer();
				
				if (!description.isActive()) {
					continue;
				}
				
				String descriptionTypeId = description.getType().getId();
				String acceptabilityId = newLanguageRefSetMember.getAcceptabilityId();
				String languageRefSetId = newLanguageRefSetMember.getRefSetIdentifierId();
				
				Concept concept = description.getConcept();
				
				for (Description conceptDescription : concept.getDescriptions()) {
					
					if (!conceptDescription.isActive()) {
						continue;
					}
					
					String conceptDescriptionTypeId = conceptDescription.getType().getId();
					
					if (!descriptionTypeId.equals(conceptDescriptionTypeId) && !(synonymAndDescendantIds.contains(descriptionTypeId) && synonymAndDescendantIds.contains(conceptDescriptionTypeId))) {
						continue;
					}
					
					for (SnomedLanguageRefSetMember conceptDescriptionMember : conceptDescription.getLanguageRefSetMembers()) {
						
						if (!conceptDescriptionMember.isActive()) {
							continue;
						}
						
						if (!languageRefSetId.equals(conceptDescriptionMember.getRefSetIdentifierId())) {
							continue;
						}
						
						if (conceptDescriptionMember.equals(newLanguageRefSetMember)) {
							continue;
						}
						
						if (acceptabilityId.equals(conceptDescriptionMember.getAcceptabilityId())) {
							if (description.equals(conceptDescription)) {
								membersToRemove.add(newLanguageRefSetMember);
								continue label;
							} else if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(acceptabilityId)) {
								
								// Two SNOMED CT Descriptions selected as preferred terms.
								conflicts.add(MergeConflictImpl.builder()
										.componentId(newLanguageRefSetMember.getUuid())
										.componentType(newLanguageRefSetMember.eClass().getName())
										.conflictingAttribute(ConflictingAttributeImpl.builder().property("acceptabilityId").value(newLanguageRefSetMember.getAcceptabilityId()).build())
										.type(ConflictType.CONFLICTING_CHANGE)
										.build());
							}
						} else {
							if (description.equals(conceptDescription)) {
								
								// Different acceptability selected for the same description
								conflicts.add(MergeConflictImpl.builder()
										.componentId(newLanguageRefSetMember.getUuid())
										.componentType(newLanguageRefSetMember.eClass().getName())
										.conflictingAttribute(ConflictingAttributeImpl.builder().property("acceptabilityId").value(newLanguageRefSetMember.getAcceptabilityId()).build())
										.type(ConflictType.CONFLICTING_CHANGE)
										.build());
							}
						}
					}
				}
			}
			
			for (SnomedLanguageRefSetMember memberToRemove : membersToRemove) {
				EcoreUtil.remove(memberToRemove);
			}
			
		}

		return conflicts;
	}
	
}
