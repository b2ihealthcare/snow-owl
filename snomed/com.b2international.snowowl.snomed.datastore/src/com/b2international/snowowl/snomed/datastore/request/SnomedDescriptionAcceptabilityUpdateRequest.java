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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * @since 4.5
 */
final class SnomedDescriptionAcceptabilityUpdateRequest implements Request<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedDescriptionAcceptabilityUpdateRequest.class);
	
	private final String descriptionId;
	private final String moduleId;
	private final Map<String, Acceptability> newAcceptabilityMap;

	private final Function<TransactionContext, String> referenceBranchFunction = CacheBuilder.newBuilder().build(new CacheLoader<TransactionContext, String>() {
		@Override
		public String load(TransactionContext context) throws Exception {
			return SnomedComponentUpdateRequest.getLatestReleaseBranch(context);
		}
	});

	public SnomedDescriptionAcceptabilityUpdateRequest(final String descriptionId, final String moduleId, final Map<String, Acceptability> newAcceptabilityMap) {
		this.descriptionId = descriptionId;
		this.moduleId = moduleId;
		this.newAcceptabilityMap = newAcceptabilityMap;
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
		final Iterable<SnomedReferenceSetMember> existingMembers = SnomedRequests.prepareSearchMember()
				.all()
				.filterByReferencedComponent(descriptionId)
				.filterByRefSetType(Collections.singleton(SnomedRefSetType.LANGUAGE))
				.build()
				.execute(context);
		
		final Map<String, Acceptability> newLanguageMembersToCreate = newHashMap(acceptabilityMap);

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
				if (ensureMemberActive(existingMember, updatedMember)) {
					if (updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember, updatedMember)) {
						context.update(oldRevision, updatedMember.build());
					}
				}
				newLanguageMembersToCreate.remove(languageReferenceSetId);
			} else if (newLanguageMembersToCreate.containsKey(languageReferenceSetId)) {
				final Acceptability newAcceptability = newLanguageMembersToCreate.get(languageReferenceSetId);
				ensureMemberActive(existingMember, updatedMember);
				updatedMember.field(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, newAcceptability.getConceptId());
				updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember, updatedMember); // Always check; we know that the acceptabilityId has changed
				context.update(oldRevision, updatedMember.build());
				newLanguageMembersToCreate.remove(languageReferenceSetId);
			} else {
				if (removeOrDeactivate(context, existingMember, updatedMember)) {
					context.update(oldRevision, updatedMember.build());
				}
			}
			
		}
		
		for (final Entry<String, Acceptability> languageMemberEntry : newLanguageMembersToCreate.entrySet()) {
			SnomedComponents
				.newLanguageMember()
				.withAcceptability(languageMemberEntry.getValue())
				.withRefSet(languageMemberEntry.getKey())
				.withModule(moduleId)
				.withReferencedComponent(descriptionId)
				.addTo(context);
		}
	}
	
	private boolean removeOrDeactivate(final TransactionContext context, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (!existingMember.isReleased()) {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Removing language member {}.", existingMember.getId()); }
			context.delete(updatedMember.build());
			return false;
			
		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating language member {}.", existingMember.getId()); }
			updatedMember.active(false);
			updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember, updatedMember);
			return true;
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Language member {} already inactive, not updating.", existingMember.getId()); }
			return false;
			
		}
	}
	
	private String getLatestReleaseBranch(final TransactionContext context) {
		return referenceBranchFunction.apply(context);
	}
	
	private boolean ensureMemberActive(final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (!existingMember.isActive()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating language member {}.", existingMember.getId()); }
			updatedMember.active(true);
			return true;
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Language member {} already active, not updating.", existingMember.getId()); }
			return false;
		}
	}
	
	private boolean updateEffectiveTime(final TransactionContext context, final String referenceBranch, final SnomedReferenceSetMember existingMember, final SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		
		if (existingMember.isReleased()) {
			
			final SnomedReferenceSetMember referenceMember = SnomedRequests.prepareGetMember(existingMember.getId())
					.build(context.id(), referenceBranch)
					.execute(context.service(IEventBus.class))
					.getSync();

			final String referenceAcceptabilityId = (String) referenceMember.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID);
			final String existingAcceptabilityId = (String) existingMember.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID);
			
			boolean restoreEffectiveTime = true;
			restoreEffectiveTime = restoreEffectiveTime && existingMember.isActive() == referenceMember.isActive();
			restoreEffectiveTime = restoreEffectiveTime && existingMember.getModuleId().equals(referenceMember.getModuleId());
			restoreEffectiveTime = restoreEffectiveTime && existingAcceptabilityId.equals(referenceAcceptabilityId);

			if (restoreEffectiveTime) {

				if (LOG.isDebugEnabled()) { 
					LOG.debug("Restoring effective time on language member {} to reference value {}.", 
							existingMember.getId(), 
							EffectiveTimes.format(referenceMember.getEffectiveTime(), DateFormats.SHORT));
				}

				updatedMember.effectiveTime(referenceMember.getEffectiveTime().getTime());
				return true;
			} else {
				return unsetEffectiveTime(existingMember, updatedMember);
			}
			
		} else {
			return unsetEffectiveTime(existingMember, updatedMember);
		}
	}
	
	private boolean unsetEffectiveTime(SnomedReferenceSetMember existingMember, SnomedRefSetMemberIndexEntry.Builder updatedMember) {
		if (existingMember.getEffectiveTime() != null) {
			if (LOG.isDebugEnabled()) { LOG.debug("Unsetting effective time on language member {}.", existingMember.getId()); }
			updatedMember.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
			return true;
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Effective time on language member {} already unset, not updating.", existingMember.getId()); }
			return false;
		}
	}
}
