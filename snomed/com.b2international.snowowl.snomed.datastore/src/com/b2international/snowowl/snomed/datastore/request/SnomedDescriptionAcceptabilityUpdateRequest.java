/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;

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
		final Iterable<SnomedReferenceSetMember> existingMembers = create ? Collections.emptySet() : SnomedRequests.prepareSearchMember()
				.all()
				.filterByReferencedComponent(componentToUpdate.getId())
				.filterByRefSetType(Collections.singleton(SnomedRefSetType.LANGUAGE))
				.build()
				.execute(context);
		
		final Map<String, Acceptability> newLanguageMembersToCreate = newHashMap(newAcceptabilityMap);
		final ModuleIdProvider moduleIdSupplier = context.service(ModuleIdProvider.class);

		// check if there are existing matches
		for (SnomedReferenceSetMember existingMember : existingMembers) {
			final String acceptabilityId = (String) existingMember.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID);
			final Acceptability acceptability = Acceptability.getByConceptId(acceptabilityId);
			final String languageReferenceSetId = existingMember.getReferenceSetId();
			
			if (null == acceptability) {
				continue;	
			}
			
			final SnomedRefSetMemberIndexEntry.Builder updatedMember = SnomedRefSetMemberIndexEntry.builder(existingMember);
			final SnomedRefSetMemberIndexEntry oldRevision = updatedMember.build();
			
			if (acceptability.equals(newLanguageMembersToCreate.get(languageReferenceSetId))) {
				// Exact match: make sure that the member is active
				ensureMemberActive(context, existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());

				// Remove it from the working list, as we have found a match
				newLanguageMembersToCreate.remove(languageReferenceSetId);
			} else if (newLanguageMembersToCreate.containsKey(languageReferenceSetId)) {
				// Change acceptability, set status to active if required, place it in the supplied module
				final Acceptability newAcceptability = newLanguageMembersToCreate.get(languageReferenceSetId);
				updatedMember.field(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, newAcceptability.getConceptId());
				ensureMemberActive(context, existingMember, updatedMember);
				updateModule(context, existingMember, updatedMember, moduleIdSupplier.apply(componentToUpdate));
				unsetEffectiveTime(existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());
				newLanguageMembersToCreate.remove(languageReferenceSetId);
			} else {
				// Not acceptable in this language reference set, remove or inactivate if already released
				removeOrDeactivate(context, existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());
			}
		}
		
		for (final Entry<String, Acceptability> languageMemberEntry : newLanguageMembersToCreate.entrySet()) {
			SnomedComponents.newLanguageMember()
				.withAcceptability(languageMemberEntry.getValue())
				.withRefSet(languageMemberEntry.getKey())
				.withModule(moduleIdSupplier.apply(componentToUpdate))
				.withReferencedComponent(componentToUpdate.getId())
				.addTo(context);
		}
	}
	
}
