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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.datastore.request.ModuleRequest.ModuleIdProvider;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 4.5
 */
final class SnomedDescriptionAcceptabilityUpdateRequest implements Request<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedDescriptionAcceptabilityUpdateRequest.class);
	
	private String descriptionId;
	
	private Map<String, Acceptability> acceptability;

	void setDescriptionId(String descriptionId) {
		this.descriptionId = descriptionId;
	}

	void setAcceptability(Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
	}
	
	@Override
	public Void execute(TransactionContext context) {
		// Null leaves lang. members unchanged, empty map clears all lang. members
		if (acceptability == null) {
			return null;
		} else {
			final Description description = context.lookup(descriptionId, Description.class);
			updateAcceptabilityMap(context, description, acceptability);
			return null;
		}
	}

	private void updateAcceptabilityMap(final TransactionContext context, final Description description, Map<String, Acceptability> acceptabilityMap) {
		final List<SnomedLanguageRefSetMember> existingMembers = newArrayList(description.getLanguageRefSetMembers());
		final Map<String, Acceptability> newLanguageMembersToCreate = newHashMap(acceptabilityMap);
		final ModuleIdProvider moduleIdSupplier = context.service(ModuleIdProvider.class);
		
		for (SnomedLanguageRefSetMember existingMember : existingMembers) {
			final String languageReferenceSetId = existingMember.getRefSetIdentifierId();
			final Acceptability acceptability = Acceptability.getByConceptId(existingMember.getAcceptabilityId());
			
			if (null == acceptability) {
				continue;	
			}
			
			if (acceptability.equals(newLanguageMembersToCreate.get(languageReferenceSetId))) {
				// Exact match: make sure that the member is active
				
				final boolean changed = ensureMemberActive(context, existingMember);
				// If the member status needs to be changed back to active, place it in the supplied module
				if (changed) {
					updateModule(context, existingMember, moduleIdSupplier.apply(description));
					unsetEffectiveTime(existingMember);
				}
				
				// Remove it from the working list, as we have found a match
				newLanguageMembersToCreate.remove(languageReferenceSetId);
				
			} else if (newLanguageMembersToCreate.containsKey(languageReferenceSetId)) {
				// Same language reference set, different acceptability
				
				// Change acceptability, set status to active if required, place it in the supplied module
				final Acceptability newAcceptability = newLanguageMembersToCreate.remove(languageReferenceSetId);
				existingMember.setAcceptabilityId(newAcceptability.getConceptId());
				ensureMemberActive(context, existingMember);
				updateModule(context, existingMember, moduleIdSupplier.apply(description));
				unsetEffectiveTime(existingMember);
				
			} else {
				// Not acceptable in this language reference set, remove or inactivate if already released
				
				final boolean changed = removeOrDeactivate(context, existingMember);
				// If the member needs inactivation, place it in the supplied module
				if (changed) {
					updateModule(context, existingMember, moduleIdSupplier.apply(description));
					unsetEffectiveTime(existingMember);
				}
			}
		}
		
		for (final Entry<String, Acceptability> languageMemberEntry : newLanguageMembersToCreate.entrySet()) {
			SnomedComponents.newLanguageMember()
				.withAcceptability(languageMemberEntry.getValue())
				.withRefSet(languageMemberEntry.getKey())
				.withModule(moduleIdSupplier.apply(description))
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

	private boolean removeOrDeactivate(final TransactionContext context, final SnomedLanguageRefSetMember existingMember) {
		
		if (!existingMember.isReleased()) {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Removing language member {}.", existingMember.getUuid()); }
			SnomedModelExtensions.remove(existingMember);
			return false;

		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating language member {}.", existingMember.getUuid()); }
			existingMember.setActive(false);
			return true;
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Language member {} already inactive, not updating.", existingMember.getUuid()); }
			return false;
		}
	}
	
	private boolean updateModule(TransactionContext context, SnomedLanguageRefSetMember existingMember, String moduleId) {

		if (!existingMember.getModuleId().equals(moduleId)) {
			
			if (LOG.isDebugEnabled()) { 
				LOG.debug("Changing language member {} module from {} to {}.", 
					existingMember.getUuid(),
					existingMember.getModuleId(),
					moduleId); 
			}
			
			existingMember.setModuleId(moduleId);
			return true;
			
		} else {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Language member {} already in the expected module, not updating.", existingMember.getUuid()); }
			return false;
		}
	}
	
	private void unsetEffectiveTime(SnomedRefSetMember existingMember) {
		
		if (existingMember.isSetEffectiveTime()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Unsetting effective time on language member {}.", existingMember.getUuid()); }
			existingMember.unsetEffectiveTime();
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Effective time on language member {} already unset, not updating.", existingMember.getUuid()); }
		}
	}
}
