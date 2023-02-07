/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;
import com.google.common.base.Objects;

/**
 * @since 4.5
 */
final class SnomedDescriptionAcceptabilityUpdateRequest extends BaseComponentMemberUpdateRequest {

	private final Map<String, Acceptability> newAcceptabilityMap;
	private final boolean create;

	public SnomedDescriptionAcceptabilityUpdateRequest(final SnomedDescriptionIndexEntry descriptionToUpdate, final Map<String, Acceptability> newAcceptabilityMap, final boolean create) {
		super(descriptionToUpdate);
		this.newAcceptabilityMap = newAcceptabilityMap;
		this.create = create;
	}
	
	@Override
	protected String getMemberType() {
		return "Language-member";
	}
	
	@Override
	protected boolean canUpdate(TransactionContext context) {
		// Null leaves lang. members unchanged, empty map clears all lang. members
		return newAcceptabilityMap != null;
	}
	
	@Override
	protected void doExecute(TransactionContext context, SnomedComponentDocument componentToUpdate) {
		final List<SnomedReferenceSetMember> existingMembers = create ? Collections.emptyList() : SnomedRequests.prepareSearchMember()
				.all()
				.filterByReferencedComponent(componentToUpdate.getId())
				.filterByRefSetType(Collections.singleton(SnomedRefSetType.LANGUAGE))
				.sortBy(SnomedRefSetMemberIndexEntry.Fields.ID)
				.build()
				.execute(context)
				.getItems();
		
		final Map<String, Acceptability> newLanguageMembersToCreate = newHashMap();
		final ModuleIdProvider moduleIdSupplier = context.service(ModuleIdProvider.class);
		final String moduleId = moduleIdSupplier.apply(componentToUpdate);

		// check if there are existing matches
		for (String languageReferenceSet : newAcceptabilityMap.keySet()) {
			
			final Acceptability newAcceptability = newAcceptabilityMap.get(languageReferenceSet);
			final List<SnomedReferenceSetMember> refsetMatches = existingMembers.stream()
					.filter(m -> Objects.equal(languageReferenceSet, m.getReferenceSetId()))
					.collect(Collectors.toList());
			
			SnomedReferenceSetMember existingMember = null;
			
			if (!refsetMatches.isEmpty()) {
				// Case 1: Match(es) exist for language reference set
				final List<SnomedReferenceSetMember> acceptabilityMatches = refsetMatches.stream()
						.filter(m -> Objects.equal(newAcceptability.getConceptId(), m.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID)))
						.collect(Collectors.toList());
				
				if (acceptabilityMatches.isEmpty()) {
					// Case 1.1: No match for acceptability; use an existing member preferably an extension one
					existingMember = select(refsetMatches, moduleId, Concepts.MODULE_SCT_CORE);
					final SnomedRefSetMemberIndexEntry.Builder updatedMember = SnomedRefSetMemberIndexEntry.builder(existingMember);
					final SnomedRefSetMemberIndexEntry oldRevision = updatedMember.build();
					
					// Change acceptability, set status to active if required, place it in the supplied module
					updatedMember.field(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, newAcceptability.getConceptId());
					ensureMemberActive(context, existingMember, updatedMember);
					updateModule(context, existingMember, updatedMember, moduleIdSupplier.apply(componentToUpdate));
					unsetEffectiveTime(existingMember, updatedMember);
					context.update(oldRevision, updatedMember.build());

				} else {
					// Case 1.2: There is at least one match for acceptability
					final List<SnomedReferenceSetMember> activeMatches = acceptabilityMatches.stream()
							.filter(m -> m.isActive())
							.collect(Collectors.toList());
					
					if (activeMatches.isEmpty()) {
						// Case 1.2.1: There is no active match, reactivate one of the members, preferably an extension one
						existingMember = select(acceptabilityMatches, moduleId, Concepts.MODULE_SCT_CORE);
						final SnomedRefSetMemberIndexEntry.Builder updatedMember = SnomedRefSetMemberIndexEntry.builder(existingMember);
						final SnomedRefSetMemberIndexEntry oldRevision = updatedMember.build();
						
						if (ensureMemberActive(context, existingMember, updatedMember)) {
							context.update(oldRevision, updatedMember.build());
						}
					} else {
						// Case 1.2.2: There is at least one active match, keep the INT one if available
						existingMember = select(activeMatches, Concepts.MODULE_SCT_CORE, moduleId);
					}
				}
					
				// Remove/deactivate any remaining members in the same language reference set
				for (SnomedReferenceSetMember otherMember : refsetMatches) {
					
					if (existingMember == null || !otherMember.getId().equals(existingMember.getId())) {
						final SnomedRefSetMemberIndexEntry.Builder updatedMember = SnomedRefSetMemberIndexEntry.builder(otherMember);
						final SnomedRefSetMemberIndexEntry oldRevision = updatedMember.build();
						
						if (removeOrDeactivate(context, otherMember, updatedMember)) {
							context.update(oldRevision, updatedMember.build());
						}
					}
				}
				
			} else {
				// Case 2: No match exists for language reference set
				newLanguageMembersToCreate.put(languageReferenceSet, newAcceptability);
			}
		}
		
		// Remove/deactivate language reference set members in reference sets missing from the acceptability map
		for (SnomedReferenceSetMember member : existingMembers) {
			if (!newAcceptabilityMap.keySet().contains(member.getReferenceSetId())) {
				final SnomedRefSetMemberIndexEntry.Builder updatedMember = SnomedRefSetMemberIndexEntry.builder(member);
				final SnomedRefSetMemberIndexEntry oldRevision = updatedMember.build();
				
				if (removeOrDeactivate(context, member, updatedMember)) {
					context.update(oldRevision, updatedMember.build());
				}
			}
		}
		
		for (final Entry<String, Acceptability> languageMemberEntry : newLanguageMembersToCreate.entrySet()) {
			SnomedComponents.newLanguageMember()
				.withAcceptability(languageMemberEntry.getValue())
				.withRefSet(languageMemberEntry.getKey())
				.withModuleId(moduleIdSupplier.apply(componentToUpdate))
				.withReferencedComponent(componentToUpdate.getId())
				.addTo(context);
		}
	}
	
	private SnomedReferenceSetMember select(List<SnomedReferenceSetMember> members, final String modulePreference, final String secondaryModulePreference) {
		checkArgument(!members.isEmpty(), "Cannot select language reference set member to keep from an empty list.");
		return members.stream()
			.filter(member -> member.getModuleId().equals(modulePreference))
			.findFirst()
			.orElse(members.stream()
				.filter(member -> member.getModuleId().equals(secondaryModulePreference))
				.findFirst()
				.orElse(members.get(0)));
	}
	
}
