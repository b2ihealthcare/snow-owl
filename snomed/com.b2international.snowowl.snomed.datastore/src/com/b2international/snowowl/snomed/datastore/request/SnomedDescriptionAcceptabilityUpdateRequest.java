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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;

/**
 * @since 4.5
 */
final class SnomedDescriptionAcceptabilityUpdateRequest implements Request<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedDescriptionAcceptabilityUpdateRequest.class);
	
	private String descriptionId;
	private Map<String, Acceptability> newAcceptabilityMap;

	@Override
	public Void execute(TransactionContext context) {
		// Null leaves lang. members unchanged, empty map clears all lang. members
		if (newAcceptabilityMap == null) {
			return null;
		} else {
			final Description description = context.lookup(descriptionId, Description.class);
			updateAcceptabilityMap(context, description, newAcceptabilityMap);
			return null;
		}
	}

	public void setDescriptionId(String descriptionId) {
		this.descriptionId = descriptionId;
	}

	public void setAcceptability(Map<String, Acceptability> acceptability) {
		this.newAcceptabilityMap = acceptability;
	}
	
	private void removeOrDeactivate(final TransactionContext context, final SnomedLanguageRefSetMember existingMember) {
		
		if (!existingMember.isReleased()) {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Removing language member {}.", existingMember.getUuid()); }
			SnomedModelExtensions.remove(existingMember);

		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating language member {}.", existingMember.getUuid()); }
			existingMember.setActive(false);
			updateEffectiveTime(context, existingMember);
			
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Language member {} already inactive, not updating.", existingMember.getUuid()); }
		}
	}
	
	private void updateAcceptabilityMap(final TransactionContext context, final Description description, Map<String, Acceptability> acceptabilityMap) {
		final List<SnomedLanguageRefSetMember> existingMembers = newArrayList(description.getLanguageRefSetMembers());
		final Map<String, Acceptability> newLanguageMembersToCreate = newHashMap(acceptabilityMap);

		// check if there are existing matches
		for (SnomedLanguageRefSetMember existingMember : existingMembers) {
			final Acceptability acceptability = Acceptability.getByConceptId(existingMember.getAcceptabilityId());
			final String languageReferenceSetId = existingMember.getRefSetIdentifierId();
			
			if (null == acceptability) {
				continue;	
			}
			
			if (acceptability.equals(newLanguageMembersToCreate.get(languageReferenceSetId))) {
				if (ensureMemberActive(context, existingMember)) { updateEffectiveTime(context, existingMember); }
				newLanguageMembersToCreate.remove(languageReferenceSetId);
			} else if (newLanguageMembersToCreate.containsKey(languageReferenceSetId)) {
				final Acceptability newAcceptability = newLanguageMembersToCreate.get(languageReferenceSetId);
				ensureMemberActive(context, existingMember);
				existingMember.setAcceptabilityId(newAcceptability.getConceptId());
				updateEffectiveTime(context, existingMember); // Always check; we know that the acceptabilityId has changed
				newLanguageMembersToCreate.remove(languageReferenceSetId);
			} else {
				removeOrDeactivate(context, existingMember);
			}
			
		}
		
		for (final Entry<String, Acceptability> languageMemberEntry : newLanguageMembersToCreate.entrySet()) {
			SnomedComponents
				.newLanguageMember()
				.withAcceptability(languageMemberEntry.getValue())
				.withRefSet(languageMemberEntry.getKey())
				.withModule(description.getModule().getId())
				.withReferencedComponent(description.getId())
				.addTo(context);
		}
	}
	
	private boolean ensureMemberActive(final TransactionContext context, final SnomedLanguageRefSetMember existingMember) {
		if (!existingMember.isActive()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating language member {}.", existingMember.getUuid()); }
			existingMember.setActive(true);
			return true;
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Language member {} already active, not updating.", existingMember.getUuid()); }
			return false;
		}
	}
	
	private void updateEffectiveTime(final TransactionContext context, final SnomedLanguageRefSetMember existingMember) {
		
		SnomedComponentUpdateRequest.tryUpdateMemberEffectiveTime(
			context, existingMember,
			branch -> SnomedRequests.prepareGetMember(existingMember.getUuid())
						.build(context.id(), branch)
						.execute(context.service(IEventBus.class))
						.getSync(),
			(cdoMember, snomedMember) ->
				!(cdoMember.isActive() ^ snomedMember.isActive()) &&
				cdoMember.getModuleId().equals(snomedMember.getModuleId()) &&
				cdoMember.getReferencedComponentId().equals(snomedMember.getReferencedComponent().getId()) &&
				cdoMember.getAcceptabilityId().equals(snomedMember.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID))
		);
		
	}
	
}
