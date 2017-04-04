/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
public final class SnomedDescriptionUpdateRequestBuilder extends BaseSnomedComponentUpdateRequestBuilder<SnomedDescriptionUpdateRequestBuilder, SnomedDescriptionUpdateRequest> {

	private Map<String, Acceptability> acceptability;
	private Multimap<AssociationType, String> associationTargets;
	private CaseSignificance caseSignificance;
	private DescriptionInactivationIndicator inactivationIndicator;
	private String languageCode;
	private String typeId;
	private String term;

	SnomedDescriptionUpdateRequestBuilder(String componentId) {
		super(componentId);
	}

	public SnomedDescriptionUpdateRequestBuilder setAcceptability(Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
		return getSelf();
	}
	
	public SnomedDescriptionUpdateRequestBuilder setAssociationTargets(Multimap<AssociationType, String> associationTargets) {
		this.associationTargets = associationTargets;
		return getSelf();
	}
	
	public SnomedDescriptionUpdateRequestBuilder setCaseSignificance(CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
		return getSelf();
	}
	
	public SnomedDescriptionUpdateRequestBuilder setInactivationIndicator(DescriptionInactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
		return getSelf();
	}
	
	public SnomedDescriptionUpdateRequestBuilder setTypeId(String typeId) {
		this.typeId = typeId;
		return getSelf();
	}
	
	public SnomedDescriptionUpdateRequestBuilder setTerm(String term) {
		this.term = term;
		return getSelf();
	}
	
	public SnomedDescriptionUpdateRequestBuilder setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
		return getSelf();
	}
	
	@Override
	protected SnomedDescriptionUpdateRequest create(String componentId) {
		return new SnomedDescriptionUpdateRequest(componentId);
	}
	
	@Override
	protected void init(SnomedDescriptionUpdateRequest req) {
		super.init(req);
		req.setAcceptability(acceptability);
		req.setAssociationTargets(associationTargets);
		req.setCaseSignificance(caseSignificance);
		req.setInactivationIndicator(inactivationIndicator);
		req.setLanguageCode(languageCode);
		req.setTypeId(typeId);
		req.setTerm(term);
	}
	
}
