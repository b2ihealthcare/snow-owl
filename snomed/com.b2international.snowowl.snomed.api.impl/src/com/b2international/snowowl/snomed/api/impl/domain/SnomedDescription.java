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

import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;

/**
 * Represents a SNOMED&nbsp;CT description.
 *
 */
public class SnomedDescription extends AbstractSnomedComponent implements ISnomedDescription {

	private String conceptId;
	private String typeId;
	private String term;
	private String languageCode;
	private CaseSignificance caseSignificance;
	private DescriptionInactivationIndicator descriptionInactivationIndicator;
	private Map<String, Acceptability> acceptabilityMap;

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
	public Map<String, Acceptability> getAcceptabilityMap() {
		return acceptabilityMap;
	}

	@Override
	public DescriptionInactivationIndicator getDescriptionInactivationIndicator() {
		return descriptionInactivationIndicator;
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

	public void setAcceptabilityMap(final Map<String, Acceptability> acceptabilityMap) {
		this.acceptabilityMap = acceptabilityMap;
	}
	
	public void setDescriptionInactivationIndicator(final DescriptionInactivationIndicator descriptionInactivationIndicator) {
		this.descriptionInactivationIndicator = descriptionInactivationIndicator;
	}


	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedDescription [getId()=");
		builder.append(getId());
		builder.append(", isReleased()=");
		builder.append(isReleased());
		builder.append(", isActive()=");
		builder.append(isActive());
		builder.append(", getEffectiveTime()=");
		builder.append(getEffectiveTime());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getTypeId()=");
		builder.append(getTypeId());
		builder.append(", getTerm()=");
		builder.append(getTerm());
		builder.append(", getLanguageCode()=");
		builder.append(getLanguageCode());
		builder.append(", getCaseSignificance()=");
		builder.append(getCaseSignificance());
		builder.append(", getAcceptabilityMap()=");
		builder.append(getAcceptabilityMap());
		if (null != descriptionInactivationIndicator) {
			builder.append(", getDescriptionInactivationIndicator()=")
				.append(descriptionInactivationIndicator);
			
		}
		builder.append("]");
		return builder.toString();
	}
}