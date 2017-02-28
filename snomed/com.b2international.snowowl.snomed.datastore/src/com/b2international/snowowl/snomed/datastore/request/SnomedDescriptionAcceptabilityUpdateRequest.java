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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

/**
 * @since 4.5
 */
final class SnomedDescriptionAcceptabilityUpdateRequest implements Request<TransactionContext, Void> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedDescriptionAcceptabilityUpdateRequest.class);
	
	private String descriptionId;
	private Map<String, Acceptability> newAcceptabilityMap;

	private final Function<TransactionContext, String> referenceBranchFunction = CacheBuilder.newBuilder().build(new CacheLoader<TransactionContext, String>() {
		@Override
		public String load(TransactionContext context) throws Exception {
			final TerminologyRegistryService registryService = context.service(TerminologyRegistryService.class);
			final List<ICodeSystemVersion> allVersions = registryService.getAllVersion().get(context.id());
			final ICodeSystemVersion systemVersion = allVersions.get(1);
			final IBranchPath branchPath = ICodeSystemVersion.TO_BRANCH_PATH_FUNC.apply(systemVersion);
			return branchPath.getPath();
		}
	});
	
	@Override
	public Void execute(TransactionContext context) {
		// Null leaves lang. members unchanged, empty map clears all lang. members
		if (newAcceptabilityMap == null) {
			return null;
		} else {
			final Description description = context.lookup(descriptionId, Description.class);
			updateAcceptabilityMap(context, description, newAcceptabilityMap);
			if (description.isActive()) {
				updateOtherDescriptionAcceptabilities(context, description);
			}
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
			
			if (LOG.isDebugEnabled()) { LOG.debug("Removing association member {}.", existingMember.getUuid()); }
			SnomedModelExtensions.remove(existingMember);

		} else if (existingMember.isActive()) {

			if (LOG.isDebugEnabled()) { LOG.debug("Inactivating association member {}.", existingMember.getUuid()); }
			existingMember.setActive(false);
			updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember);
			
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Association member {} already inactive, not updating.", existingMember.getUuid()); }
		}
	}
	
	private void updateAcceptabilityMap(final TransactionContext context, final Description description, Map<String, Acceptability> acceptabilityMap) {
		final List<SnomedLanguageRefSetMember> existingMembers = newArrayList(description.getLanguageRefSetMembers());
		final Map<String, Acceptability> newLanguageMembersToCreate = newHashMap(acceptabilityMap);
		final Set<String> synonymAndDescendantIds = getSynonymAndDescendantIds(context);

		// check if there are existing matches
		for (SnomedLanguageRefSetMember existingMember : existingMembers) {
			final Acceptability acceptability = Acceptability.getByConceptId(existingMember.getAcceptabilityId());
			final String languageReferenceSetId = existingMember.getRefSetIdentifierId();
			
			if (null == acceptability) {
				continue;	
			}
			
			if (acceptability.equals(newLanguageMembersToCreate.get(languageReferenceSetId))) {
				ensureMemberActive(context, existingMember);
				newLanguageMembersToCreate.remove(languageReferenceSetId);
			} else if (newLanguageMembersToCreate.containsKey(languageReferenceSetId)) {
				final Acceptability newAcceptability = newLanguageMembersToCreate.get(languageReferenceSetId);
				ensureMemberActive(context, existingMember);
				existingMember.setAcceptabilityId(newAcceptability.getConceptId());
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
	
	private void updateOtherDescriptionAcceptabilities(TransactionContext context, final Description description) {
		for (final Entry<String, Acceptability> languageMemberEntry : newAcceptabilityMap.entrySet()) {
			if (Acceptability.PREFERRED.equals(languageMemberEntry.getValue())) {
				updateOtherPreferredDescriptions(context, description, languageMemberEntry.getKey());
			}
		}
	}
	
	private String getLatestReleaseBranch(final TransactionContext context) {
		return referenceBranchFunction.apply(context);
	}
	
	private void ensureMemberActive(final TransactionContext context, final SnomedLanguageRefSetMember existingMember) {
		
		if (!existingMember.isActive()) {
			
			if (LOG.isDebugEnabled()) { LOG.debug("Reactivating association member {}.", existingMember.getUuid()); }
			existingMember.setActive(true);
			updateEffectiveTime(context, getLatestReleaseBranch(context), existingMember);
			
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Association member {} already active, not updating.", existingMember.getUuid()); }
		}
	}
	
	private void updateEffectiveTime(final TransactionContext context, final String referenceBranch, final SnomedLanguageRefSetMember existingMember) {
		
		if (existingMember.isReleased()) {
			
			final SnomedReferenceSetMember referenceMember = SnomedRequests.prepareGetMember()
					.setComponentId(existingMember.getUuid())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, referenceBranch)
					.execute(context.service(IEventBus.class))
					.getSync();

			boolean restoreEffectiveTime = true;
			restoreEffectiveTime = restoreEffectiveTime && existingMember.isActive() == referenceMember.isActive();
			restoreEffectiveTime = restoreEffectiveTime && existingMember.getModuleId().equals(referenceMember.getModuleId());
			restoreEffectiveTime = restoreEffectiveTime && existingMember.getAcceptabilityId().equals(referenceMember.getProperties().get(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID));

			if (restoreEffectiveTime) {

				if (LOG.isDebugEnabled()) { 
					LOG.debug("Restoring effective time on association member {} to reference value {}.", 
							existingMember.getUuid(), 
							EffectiveTimes.format(referenceMember.getEffectiveTime(), DateFormats.SHORT));
				}

				existingMember.setEffectiveTime(referenceMember.getEffectiveTime());
				
			} else {
				unsetEffectiveTime(existingMember);
			}
			
		} else {
			unsetEffectiveTime(existingMember);
		}
	}
	
	private void unsetEffectiveTime(SnomedRefSetMember existingMember) {
		
		if (existingMember.isSetEffectiveTime()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Unsetting effective time on association member {}.", existingMember.getUuid()); }
			existingMember.unsetEffectiveTime();
		} else {
			if (LOG.isDebugEnabled()) { LOG.debug("Effective time on association member {} already unset, not updating.", existingMember.getUuid()); }
		}
	}
	
	private Set<String> getSynonymAndDescendantIds(TransactionContext context) {
		final SnomedConcepts concepts = SnomedRequests.prepareGetSynonyms().build().execute(context);
		return FluentIterable.from(concepts).transform(IComponent.ID_FUNCTION).toSet();
	}
	
	private void updateOtherPreferredDescriptions(final TransactionContext context, final Description preferredDescription, final String languageRefSetId) {

		for (final Description description : preferredDescription.getConcept().getDescriptions()) {
			
			if (!description.isActive() || description.equals(preferredDescription)) {
				continue;
			}

			if (!preferredDescription.getType().getId().equals(description.getType().getId())) {
				continue;
			}

			final Map<String, Acceptability> acceptabilityMap = Maps.newHashMap();
			for (final SnomedLanguageRefSetMember languageMember : description.getLanguageRefSetMembers()) {
				if (!languageMember.isActive()) {
					continue;
				}

				if (!languageMember.getRefSetIdentifierId().equals(languageRefSetId)) {
					acceptabilityMap.put(languageMember.getRefSetIdentifierId(), Acceptability.getByConceptId(languageMember.getAcceptabilityId()));
				} else if (languageMember.getAcceptabilityId().equals(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)) {
					acceptabilityMap.put(languageMember.getRefSetIdentifierId(), Acceptability.ACCEPTABLE);
				}
			}
			
			updateAcceptabilityMap(context, description, acceptabilityMap);
		}
	}

}
