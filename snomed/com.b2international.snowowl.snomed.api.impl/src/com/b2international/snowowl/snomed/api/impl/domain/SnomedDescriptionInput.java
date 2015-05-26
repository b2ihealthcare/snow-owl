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
package com.b2international.snowowl.snomed.api.impl.domain;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.snomed.api.domain.Acceptability;
import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescriptionInput;

/**
 */
public class SnomedDescriptionInput extends AbstractSnomedComponentInput implements ISnomedDescriptionInput {

	private String conceptId;
	
	@NotEmpty
	private String typeId;
	
	@NotEmpty
	private String term;
	
	@NotEmpty
	private String languageCode;
	
	@NotNull
	private CaseSignificance caseSignificance = CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE;
	
	@NotEmpty
	private Map<String, Acceptability> acceptability;

	@Override
	public String getConceptId() {
		return conceptId;
	}

	@Override
	public String getTypeId() {
		return typeId;
	}

	@Override
	public String getTerm() {
		return term;
	}

	@Override
	public String getLanguageCode() {
		return languageCode;
	}

	@Override
	public CaseSignificance getCaseSignificance() {
		return caseSignificance;
	}

	@Override
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
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedDescriptionInput [getIdGenerationStrategy()=");
		builder.append(getIdGenerationStrategy());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getCodeSystemShortName()=");
		builder.append(getCodeSystemShortName());
		builder.append(", getBranchPath()=");
		builder.append(getBranchPath());
		builder.append(", getConceptId()=");
		builder.append(getConceptId());
		builder.append(", getTypeId()=");
		builder.append(getTypeId());
		builder.append(", getTerm()=");
		builder.append(getTerm());
		builder.append(", getLanguageCode()=");
		builder.append(getLanguageCode());
		builder.append(", getCaseSignificance()=");
		builder.append(getCaseSignificance());
		builder.append(", getAcceptability()=");
		builder.append(getAcceptability());
		builder.append("]");
		return builder.toString();
	}
}