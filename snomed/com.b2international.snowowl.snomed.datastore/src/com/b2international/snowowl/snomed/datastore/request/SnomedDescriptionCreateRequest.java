/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ConstantIdStrategy;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 4.5
 */
public final class SnomedDescriptionCreateRequest extends BaseSnomedComponentCreateRequest {

	private String conceptId;

	@NotEmpty
	private String typeId;

	@NotEmpty
	private String term;

	@NotEmpty
	private String languageCode;

	@NotNull
	private String caseSignificanceId;

	@NotEmpty
	private Map<String, Acceptability> acceptability;
	
	private String inactivationIndicatorId;

	SnomedDescriptionCreateRequest() {
	}
	
	String getConceptId() {
		return conceptId;
	}
	
	String getTypeId() {
		return typeId;
	}

	Map<String, Acceptability> getAcceptability() {
		return acceptability;
	}
	
	void setConceptId(final String conceptId) {
		this.conceptId = conceptId;
	}

	void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	void setTerm(final String term) {
		this.term = term;
	}

	void setLanguageCode(final String languageCode) {
		this.languageCode = languageCode;
	}

	void setCaseSignificanceId(final String caseSignificanceId) {
		this.caseSignificanceId = caseSignificanceId;
	}

	void setAcceptability(final Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
	}
	
	void setInactivationIndicatorId(String inactivationIndicatorId) {
		this.inactivationIndicatorId = inactivationIndicatorId;
	}
	
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		Builder<String> result = ImmutableSet.<String>builder()
				.add(caseSignificanceId)
				.add(typeId);
		
		acceptability.forEach((refSetId, acceptability) -> {
			result.add(refSetId);
			result.add(acceptability.getConceptId());
		});
		
		if (getModuleId() != null) {
			result.add(getModuleId());
		}
		if (conceptId != null) {
			result.add(conceptId);
		}
		return result.build();
	}

	@Override
	public String execute(TransactionContext context) {
		try {
			final String descriptionId = ((ConstantIdStrategy) getIdGenerationStrategy()).getId();
			final SnomedDescriptionIndexEntry description = SnomedComponents.newDescription()
				.withId(descriptionId)
				.withActive(isActive())
				.withModule(getModuleId())
				.withCaseSignificanceId(caseSignificanceId)
				.withTerm(term)
				.withType(typeId)
				.withLanguageCode(languageCode)
				.withConcept(conceptId)
				.build(context);
			
			new SnomedDescriptionAcceptabilityUpdateRequest(description, acceptability, true)
				.execute(context);
			
			// FIXME: Acceptability updates and member create requests can overlap
			convertMembers(context, descriptionId);
			context.add(description);
			
			if (inactivationIndicatorId != null) {
				final SnomedInactivationReasonUpdateRequest inactivationUpdate =  new SnomedInactivationReasonUpdateRequest(description, Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR);
				inactivationUpdate.setInactivationValueId(inactivationIndicatorId);
				inactivationUpdate.execute(context);
			}
			
			return description.getId();
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

}
