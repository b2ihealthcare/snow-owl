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

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.UserIdGenerationStrategy;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;

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
	private CaseSignificance caseSignificance;

	@NotEmpty
	private Map<String, Acceptability> acceptability;

	SnomedDescriptionCreateRequest() {
	}
	
	public String getConceptId() {
		return conceptId;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getTerm() {
		return term;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public CaseSignificance getCaseSignificance() {
		return caseSignificance;
	}

	public Map<String, Acceptability> getAcceptability() {
		return acceptability;
	}

	public void setConceptId(final String conceptId) {
		this.conceptId = conceptId;
	}

	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}

	public void setTerm(final String term) {
		this.term = term;
	}

	public void setLanguageCode(final String languageCode) {
		this.languageCode = languageCode;
	}

	public void setCaseSignificance(final CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	public void setAcceptability(final Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
	}

	@Override
	public String execute(TransactionContext context) {
		if (getIdGenerationStrategy() instanceof UserIdGenerationStrategy) {
			try {
				final String componentId = getIdGenerationStrategy().generate(context);
				SnomedRequests.prepareGetDescription().setComponentId(componentId).build().execute(context);
				throw new AlreadyExistsException("Description", componentId);
			} catch (ComponentNotFoundException e) {
				// ignore
			}
		}
		
		try {
			final Description description = SnomedComponents.newDescription()
				.withId(getIdGenerationStrategy())
				.withModule(getModuleId())
				.withCaseSignificance(getCaseSignificance())
				.withTerm(getTerm())
				.withType(getTypeId())
				.withLanguageCode(getLanguageCode())
				.withConcept(getConceptId())
				.build(context);
			
			final SnomedDescriptionAcceptabilityUpdateRequest acceptabilityUpdate = new SnomedDescriptionAcceptabilityUpdateRequest();
			acceptabilityUpdate.setAcceptability(acceptability);
			acceptabilityUpdate.setDescriptionId(description.getId());
			acceptabilityUpdate.execute(context);
			
			return description.getId();
		} catch (ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}
	
}