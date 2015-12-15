/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.server.request;

import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.5
 */
public final class SnomedDescriptionAcceptabilityUpdateRequest extends BaseRequest<TransactionContext, Void> {

	private String descriptionId;
	private Map<String, Acceptability> acceptability;

	@Override
	public Void execute(TransactionContext context) {
		final Description description = context.lookup(descriptionId, Description.class);
		updateAcceptabilityMap(acceptability, description, context);
		return null;
	}

	@Override
	protected Class<Void> getReturnType() {
		return Void.class;
	}
	
	public void setDescriptionId(String descriptionId) {
		this.descriptionId = descriptionId;
	}

	public void setAcceptability(Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
	}
	
	private void updateAcceptabilityMap(final Map<String, Acceptability> newAcceptabilityMap, final Description description, final TransactionContext context) {
		if (null == newAcceptabilityMap) {
			return;
		}
		final Set<String> synonymAndDescendantIds = context.service(ISnomedComponentService.class).getSynonymAndDescendantIds(context.branch().branchPath());

		final Map<String, Acceptability> languageMembersToCreate = newHashMap(newAcceptabilityMap);
		final List<SnomedLanguageRefSetMember> languageMembers = ImmutableList.copyOf(description.getLanguageRefSetMembers());
		for (final SnomedLanguageRefSetMember languageMember : languageMembers) {
			if (!languageMember.isActive()) {
				continue;
			}

			final String languageRefSetId = languageMember.getRefSetIdentifierId();
			final Acceptability currentAcceptability = Acceptability.getByConceptId(languageMember.getAcceptabilityId());
			final Acceptability newAcceptability = newAcceptabilityMap.get(languageRefSetId);

			if (!currentAcceptability.equals(newAcceptability)) {
				SnomedModelExtensions.removeOrDeactivate(languageMember);
			} else {
				languageMembersToCreate.remove(languageRefSetId);
			}
		}

		for (final Entry<String, Acceptability> languageMemberEntry : languageMembersToCreate.entrySet()) {
			SnomedComponents
				.newLanguageMember()
				.withAcceptability(languageMemberEntry.getValue())
				.withRefSet(languageMemberEntry.getKey())
				.addTo(context, description);
		}

		for (final Entry<String, Acceptability> languageMemberEntry : languageMembersToCreate.entrySet()) {
			if (description.isActive() && Acceptability.PREFERRED.equals(languageMemberEntry.getValue())) {
				if (synonymAndDescendantIds.contains(description.getType().getId())) {
					updateOtherPreferredDescriptions(description.getConcept().getDescriptions(), description, languageMemberEntry.getKey(), 
							synonymAndDescendantIds, context);
				}
			}
		}
	}
	
	private void updateOtherPreferredDescriptions(final List<Description> descriptions, final Description preferredDescription, final String languageRefSetId, 
			final Set<String> synonymAndDescendantIds, final TransactionContext context) {

		for (final Description description : descriptions) {
			if (!description.isActive() || description.equals(preferredDescription)) {
				continue;
			}

			if (!synonymAndDescendantIds.contains(description.getType().getId())) {
				continue;
			}

			for (final SnomedLanguageRefSetMember languageMember : description.getLanguageRefSetMembers()) {
				if (!languageMember.isActive()) {
					continue;
				}

				if (!languageMember.getRefSetIdentifierId().equals(languageRefSetId)) {
					continue;
				}

				if (languageMember.getAcceptabilityId().equals(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)) {
					SnomedModelExtensions.removeOrDeactivate(languageMember);
					SnomedComponents
						.newLanguageMember()
						.withRefSet(languageRefSetId)
						.addTo(context, description);
					break;
				}
			}
		}
	}

}
