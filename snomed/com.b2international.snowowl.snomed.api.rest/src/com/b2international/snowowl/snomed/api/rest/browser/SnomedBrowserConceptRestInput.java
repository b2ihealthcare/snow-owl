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
package com.b2international.snowowl.snomed.api.rest.browser;

import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedConceptInput;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedDescriptionInput;
import com.b2international.snowowl.snomed.api.rest.domain.AbstractSnomedComponentRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRelationshipRestInput;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @since 1.0
 */
public class SnomedBrowserConceptRestInput extends AbstractSnomedComponentRestInput<SnomedConceptInput> {

	private List<SnomedDescriptionRestInput> descriptions = Collections.emptyList();
	private List<SnomedRelationshipRestInput> relationships = Collections.emptyList();

	@Override
	protected SnomedConceptInput createComponentInput() {
		return new SnomedConceptInput();
	}

	@Override
	public SnomedConceptInput toComponentInput(final String branchPath, final String codeSystemShortName) {
		final String parentRelationshipId = getParentId();

		final SnomedConceptInput result = super.toComponentInput(branchPath,codeSystemShortName);
		result.setIsAIdGenerationStrategy(createIdGenerationStrategy(parentRelationshipId));

		final List<SnomedDescriptionInput> descriptionInputs = newArrayList();
		for (SnomedDescriptionRestInput restDescription : getDescriptions()) {
			// Propagate namespace from concept if present, and the description does not already have one
			if (null == restDescription.getNamespaceId()) {
				restDescription.setNamespaceId(getNamespaceId());
			}
			
			descriptionInputs.add(restDescription.toComponentInput(branchPath,codeSystemShortName));
		}

		result.setDescriptions(descriptionInputs);
		result.setParentId(parentRelationshipId);

		return result;
	}

	private String getParentId() {
		SnomedRelationshipRestInput parentRelationship = null;
		for (SnomedRelationshipRestInput relationship : relationships) {
			if (SnomedConstants.Concepts.IS_A.equals(relationship.getTypeId())) {
				parentRelationship = relationship;
			}
		}
		if (parentRelationship != null) {
			return parentRelationship.getDestinationId();
		} else {
			throw new BadRequestException("At least one isA relationship is required.");
		}
	}

	@Override
	protected ComponentCategory getComponentCategory() {
		return ComponentCategory.CONCEPT;
	}

	public List<SnomedDescriptionRestInput> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<SnomedDescriptionRestInput> descriptions) {
		this.descriptions = descriptions;
	}

	public List<SnomedRelationshipRestInput> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<SnomedRelationshipRestInput> relationships) {
		this.relationships = relationships;
	}

}
