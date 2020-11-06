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
package com.b2international.snowowl.snomed.core.rest.domain;

import java.util.Map;

import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionUpdateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 1.0
 */
public class SnomedDescriptionRestUpdate extends AbstractSnomedComponentRestUpdate {

	private String caseSignificanceId;
	private Map<String, Acceptability> acceptability;
	private String typeId;
	private String term;
	private String languageCode;

	public String getCaseSignificanceId() {
		return caseSignificanceId;
	}

	public Map<String, Acceptability> getAcceptability() {
		return acceptability;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getTerm() {
		return term;
	}
	
	public String getTypeId() {
		return typeId;
	}

	public void setCaseSignificance(final String caseSignificanceId) {
		this.caseSignificanceId = caseSignificanceId;
	}

	public void setAcceptability(final Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public SnomedDescriptionUpdateRequestBuilder toRequestBuilder(final String descriptionId) {
		return SnomedRequests
				.prepareUpdateDescription(descriptionId)
				.setActive(isActive())
				.setEffectiveTime(getEffectiveTime())
				.setModuleId(getModuleId())
				.setInactivationProperties(getInactivationProperties())
				.setCaseSignificanceId(getCaseSignificanceId())
				.setAcceptability(getAcceptability())
				.setTypeId(getTypeId())
				.setTerm(getTerm())
				.setLanguageCode(getLanguageCode());
	}

}