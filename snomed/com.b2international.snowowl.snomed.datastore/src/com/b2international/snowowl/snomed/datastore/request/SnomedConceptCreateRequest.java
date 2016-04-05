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
package com.b2international.snowowl.snomed.datastore.request;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

/**
 * @since 4.5
 */
public final class SnomedConceptCreateRequest extends BaseSnomedComponentCreateRequest {

	@Size(min = 2)
	private List<SnomedDescriptionCreateRequest> descriptions = Collections.emptyList();

	@NotEmpty
	private String parentId;

	@NotNull
	private IdGenerationStrategy isAIdGenerationStrategy;
	
	@NotNull
	private DefinitionStatus definitionStatus = DefinitionStatus.PRIMITIVE;

	SnomedConceptCreateRequest() {
	}
	
	public String getParentId() {
		return parentId;
	}
	
	void setDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
	}
	
	void setParentId(final String parentId) {
		this.parentId = parentId;
	}

	void setIsAIdGenerationStrategy(final IdGenerationStrategy isAIdGenerationStrategy) {
		this.isAIdGenerationStrategy = isAIdGenerationStrategy;
	}

	void setDescriptions(final List<SnomedDescriptionCreateRequest> descriptions) {
		this.descriptions = ImmutableList.copyOf(descriptions);
	}

	@Override
	public String execute(TransactionContext context) {
		ensureUniqueId("Concept", context);
		
		final Concept concept = convertConcept(context);
		context.add(concept);

		convertDescriptions(context, concept.getId());
		convertRelationships(context, concept.getId());

		return concept.getId();
	}

	private Concept convertConcept(final TransactionContext context) {
		try {
			return SnomedComponents.newConcept()
					.withId(getIdGenerationStrategy())
					.withModule(getModuleId())
					.withDefinitionStatus(definitionStatus)
					.build(context);
		} catch (final ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	private void convertDescriptions(TransactionContext context, final String conceptId) {
		final IBranchPath branchPath = context.branch().branchPath();
		final Set<String> requiredDescriptionTypes = newHashSet(Concepts.FULLY_SPECIFIED_NAME, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
		final Multiset<String> preferredLanguageRefSetIds = HashMultiset.create();
		final Set<String> synonymAndDescendantIds = context.service(ISnomedComponentService.class).getSynonymAndDescendantIds(branchPath);

		for (final SnomedDescriptionCreateRequest descriptionRequest : descriptions) {

			descriptionRequest.setConceptId(conceptId);

			if (null == descriptionRequest.getModuleId()) {
				descriptionRequest.setModuleId(getModuleId());
			}

			descriptionRequest.execute(context);

			final String typeId = descriptionRequest.getTypeId();

			if (synonymAndDescendantIds.contains(typeId)) {
				for (final Entry<String, Acceptability> acceptability : descriptionRequest.getAcceptability().entrySet()) {
					if (Acceptability.PREFERRED.equals(acceptability.getValue())) {
						preferredLanguageRefSetIds.add(acceptability.getKey());
						requiredDescriptionTypes.remove(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
					}
				}
			}

			requiredDescriptionTypes.remove(typeId);
		}

		if (!requiredDescriptionTypes.isEmpty()) {
			throw new BadRequestException("At least one fully specified name and one preferred term must be supplied with the concept.");
		}

		for (final com.google.common.collect.Multiset.Entry<String> languageRefSetIdOccurence : preferredLanguageRefSetIds.entrySet()) {
			if (languageRefSetIdOccurence.getCount() > 1) {
				throw new BadRequestException("More than one preferred term has been added for language reference set %s.", languageRefSetIdOccurence.getElement());				
			}
		}
	}
	
	// TODO: Add support for multiple relationship creation requests
	private void convertRelationships(final TransactionContext context, String conceptId) {
		try {
			
			SnomedRequests.prepareNewRelationship()
					.setId(isAIdGenerationStrategy)
					.setModuleId(getModuleId())
					.setSourceId(conceptId)
					.setDestinationId(parentId)
					.setTypeId(IS_A)
					.build()
					.execute(context);
			
		} catch (final ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	@Override
	protected void checkComponentExists(TransactionContext context, String componentId) throws ComponentNotFoundException {
		SnomedRequests.prepareGetConcept().setComponentId(componentId).build().execute(context);
	}
}
