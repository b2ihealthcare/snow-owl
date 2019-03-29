/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.List;
import java.util.Map;

import com.b2international.snowowl.datastore.request.TransactionalRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 1.0
 */
public class SnomedDescriptionRestUpdate extends AbstractSnomedComponentRestUpdate {

	private CaseSignificance caseSignificance;
	private Map<String, Acceptability> acceptability;
	private DescriptionInactivationIndicator inactivationIndicator;
	private Map<AssociationType, List<String>> associationTargets;
	private String typeId;
	private String term;
	private String languageCode;

	public CaseSignificance getCaseSignificance() {
		return caseSignificance;
	}

	public Map<String, Acceptability> getAcceptability() {
		return acceptability;
	}

	public DescriptionInactivationIndicator getInactivationIndicator() {
		return inactivationIndicator;
	}
	
	public Map<AssociationType, List<String>> getAssociationTargets() {
		return associationTargets;
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

	public void setCaseSignificance(final CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	public void setAcceptability(final Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
	}

	public void setInactivationIndicator(DescriptionInactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
	}
	
	public void setAssociationTargets(Map<AssociationType, List<String>> associationTargets) {
		this.associationTargets = associationTargets;
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

	public TransactionalRequestBuilder<Boolean> toRequestBuilder(String descriptionId) {
		return SnomedRequests
			.prepareUpdateDescription(descriptionId)
			.setActive(isActive())
			.setModuleId(getModuleId())
			.setAssociationTargets(getAssociationTargetsMultimap(this.associationTargets))
			.setInactivationIndicator(getInactivationIndicator())
			.setCaseSignificance(getCaseSignificance())
			.setAcceptability(getAcceptability())
			.setTypeId(getTypeId())
			.setTerm(getTerm())
			.setLanguageCode(getLanguageCode());
	}

}