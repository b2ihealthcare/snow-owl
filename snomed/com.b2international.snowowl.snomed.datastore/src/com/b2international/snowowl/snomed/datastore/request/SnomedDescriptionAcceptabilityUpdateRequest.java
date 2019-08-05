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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;

/**
 * @since 4.5
 */
final class SnomedDescriptionAcceptabilityUpdateRequest implements Request<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedDescriptionAcceptabilityUpdateRequest.class);
	
	private final String descriptionId;
	private final Map<String, Acceptability> newAcceptabilityMap;
	private final boolean create;

	public SnomedDescriptionAcceptabilityUpdateRequest(final String descriptionId, final String moduleId, final Map<String, Acceptability> newAcceptabilityMap, final boolean create) {
		this.descriptionId = descriptionId;
		this.newAcceptabilityMap = newAcceptabilityMap;
		this.create = create;
	}
	
	@Override
	public Void execute(TransactionContext context) {
		// Null leaves lang. members unchanged, empty map clears all lang. members
		if (newAcceptabilityMap == null) {
			return null;
		} else {
			updateAcceptabilityMap(context, descriptionId, newAcceptabilityMap);
			return null;
		}
	}

	private void updateAcceptabilityMap(final TransactionContext context, final String descriptionId, Map<String, Acceptability> acceptabilityMap) {
		final SnomedDescriptionIndexEntry description = context.lookup(descriptionId, SnomedDescriptionIndexEntry.class);
		final Iterable<SnomedReferenceSetMember> existingMembers = create ? Collections.emptySet() : SnomedRequests.prepareSearchMember()
				.all()
				.filterByReferencedComponent(descriptionId)
				.filterByRefSetType(Collections.singleton(SnomedRefSetType.LANGUAGE))
				.build()
				.execute(context);
		
		final Map<String, Acceptability> newLanguageMembersToCreate = newHashMap(acceptabilityMap);
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
				ensureMemberActive(existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());
//				// Exact match: make sure that the member is active
//				
//				final boolean changed = ensureMemberActive(context, existingMember);
//				// If the member status needs to be changed back to active, place it in the supplied module
//				if (changed) {
//					updateModule(context, existingMember, moduleIdSupplier.apply(description));
//					unsetEffectiveTime(existingMember);
//				}
				
				// Remove it from the working list, as we have found a match
				newLanguageMembersToCreate.remove(languageReferenceSetId);
				
			} else if (newLanguageMembersToCreate.containsKey(languageReferenceSetId)) {
				// Change acceptability, set status to active if required, place it in the supplied module
				final Acceptability newAcceptability = newLanguageMembersToCreate.get(languageReferenceSetId);
				updatedMember.field(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, newAcceptability.getConceptId());
				ensureMemberActive(existingMember, updatedMember);
				updateModule(context, existingMember, updatedMember, moduleIdSupplier.apply(description));
				unsetEffectiveTime(existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());
				newLanguageMembersToCreate.remove(languageReferenceSetId);
			} else {
				removeOrDeactivate(context, existingMember, updatedMember);
				context.update(oldRevision, updatedMember.build());
//				// Not acceptable in this language reference set, remove or inactivate if already released
//				
//				final boolean changed = removeOrDeactivate(context, existingMember);
//				// If the member needs inactivation, place it in the supplied module
//				if (changed) {
//					updateModule(context, existingMember, moduleIdSupplier.apply(description));
//					unsetEffectiveTime(existingMember);
//				}
			}
		}
		
		for (final Entry<String, Acceptability> languageMemberEntry : newLanguageMembersToCreate.entrySet()) {
			SnomedComponents.newLanguageMember()
				.withAcceptability(languageMemberEntry.getValue())
				.withRefSet(languageMemberEntry.getKey())
				.withModule(moduleIdSupplier.apply(description))
				.withReferencedComponent(descriptionId)
				.addTo(context);
		}
	}
	
	private void removeOrDeactivate(final TransactionContext context, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (!existingMember.isReleased()) {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Removing language member {}.", existingMember.getId()); }
			context.delete(updatedMember.build());
			
		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating language member {}.", existingMember.getId()); }
			updatedMember.active(false);
			unsetEffectiveTime(existingMember, updatedMember);
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Language member {} already inactive, not updating.", existingMember.getId()); }
			
		}
	}

	private void updateModule(TransactionContext context, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember, String moduleId) {

		if (!existingMember.getModuleId().equals(moduleId)) {
			
			if (LOG.isDebugEnabled()) { 
				LOG.debug("Changing language member {} module from {} to {}.", 
					existingMember.getId(),
					existingMember.getModuleId(),
					moduleId); 
			}
			
			existingMember.setModuleId(moduleId);
			unsetEffectiveTime(existingMember, updatedMember);
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Language member {} already in the expected module, not updating.", existingMember.getId()); }
			
		}
	}
	
	private void ensureMemberActive(final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (!existingMember.isActive()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating language member {}.", existingMember.getId()); }
			updatedMember.active(true);
			unsetEffectiveTime(existingMember, updatedMember);
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Language member {} already active, not updating.", existingMember.getId()); }
		}
	}
	
	private void unsetEffectiveTime(SnomedReferenceSetMember existingMember, SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (existingMember.getEffectiveTime() != null) {
			if (LOG.isDebugEnabled()) { LOG.debug("Unsetting effective time on language member {}.", existingMember.getId()); }
			updatedMember.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Effective time on language member {} already unset, not updating.", existingMember.getId()); }
		}
	}
}
