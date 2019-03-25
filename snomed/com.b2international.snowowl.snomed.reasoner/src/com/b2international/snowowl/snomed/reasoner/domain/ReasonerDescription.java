/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.domain;

import java.io.Serializable;
import java.util.Map;

import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;

/**
 * @since 6.14
 */
public final class ReasonerDescription implements Serializable {

	private String originDescriptionId;
	private Boolean released;

	/*
	 * Note that the rest of the values below can be completely different (or even
	 * absent) when compared to the "origin" description, especially if
	 * the change is a new inference!
	 */
	private SnomedConcept concept;
	private SnomedConcept type;
	private String languageCode;
	private String term;
	private CaseSignificance caseSignificance;
	private Map<String, Acceptability> acceptabilityMap;

	// Default constructor is used in JSON de-serialization
	public ReasonerDescription() { }

	public ReasonerDescription(final String originDescriptionId) {
		setOriginDescriptionId(originDescriptionId);
	}

	public String getOriginDescriptionId() {
		return originDescriptionId;
	}

	private void setOriginDescriptionId(final String originDescriptionId) {
		this.originDescriptionId = originDescriptionId;
	}

	public Boolean isReleased() {
		return released;
	}

	public void setReleased(final Boolean released) {
		this.released = released;
	}

	public SnomedConcept getConcept() {
		return concept;
	}

	public void setConcept(final SnomedConcept concept) {
		this.concept = concept;
	}

	public SnomedConcept getType() {
		return type;
	}

	public void setType(final SnomedConcept type) {
		this.type = type;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(final String languageCode) {
		this.languageCode = languageCode;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(final String term) {
		this.term = term;
	}

	public CaseSignificance getCaseSignificance() {
		return caseSignificance;
	}

	public void setCaseSignificance(final CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	public Map<String, Acceptability> getAcceptabilityMap() {
		return acceptabilityMap;
	}

	public void setAcceptabilityMap(final Map<String, Acceptability> acceptabilityMap) {
		this.acceptabilityMap = acceptabilityMap;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ReasonerDescription [originDescriptionId=");
		builder.append(originDescriptionId);
		builder.append(", released=");
		builder.append(released);
		builder.append(", concept=");
		builder.append(concept);
		builder.append(", type=");
		builder.append(type);
		builder.append(", languageCode=");
		builder.append(languageCode);
		builder.append(", term=");
		builder.append(term);
		builder.append(", caseSignificance=");
		builder.append(caseSignificance);
		builder.append(", acceptabilityMap=");
		builder.append(acceptabilityMap);
		builder.append("]");
		return builder.toString();
	}
}
