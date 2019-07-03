/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;

/**
 * <i>Builder</i> class to build requests responsible for creating SNOMED CT descriptions.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * 
 * @since 4.5
 */
public final class SnomedDescriptionCreateRequestBuilder extends SnomedComponentCreateRequestBuilder<SnomedDescriptionCreateRequestBuilder> {

	private CaseSignificance caseSignificance = CaseSignificance.INITIAL_CHARACTER_CASE_INSENSITIVE;
	private String term;
	private String conceptId;
	private String typeId = Concepts.SYNONYM;
	private String languageCode = "en";
	private Map<String, Acceptability> acceptabilityMap = newHashMap();
	private DescriptionInactivationIndicator inactivationIndicator = DescriptionInactivationIndicator.RETIRED;
	
	SnomedDescriptionCreateRequestBuilder() { 
		super();
	}
	
	public SnomedDescriptionCreateRequestBuilder setCaseSignificance(CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
		return getSelf();
	}
	
	public SnomedDescriptionCreateRequestBuilder setTerm(String term) {
		this.term = term;
		return getSelf();
	}

	public SnomedDescriptionCreateRequestBuilder setConceptId(String conceptId) {
		this.conceptId = conceptId;
		return getSelf();
	}
	
	public SnomedDescriptionCreateRequestBuilder setTypeId(String typeId) {
		this.typeId = typeId;
		return getSelf();
	}
	
	public SnomedDescriptionCreateRequestBuilder setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
		return getSelf();
	}
	
	public SnomedDescriptionCreateRequestBuilder acceptableIn(String languageReferenceSetId) {
		this.acceptabilityMap.put(languageReferenceSetId, Acceptability.ACCEPTABLE);
		return getSelf();
	}
	
	public SnomedDescriptionCreateRequestBuilder preferredIn(String languageReferenceSetId) {
		this.acceptabilityMap.put(languageReferenceSetId, Acceptability.PREFERRED);
		return getSelf();
	}
	
	public SnomedDescriptionCreateRequestBuilder setAcceptability(Map<String, Acceptability> acceptabilityMap) {
		this.acceptabilityMap.putAll(acceptabilityMap);
		return getSelf();
	}
	
	public SnomedDescriptionCreateRequestBuilder setInactivationIndicator(DescriptionInactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
		return getSelf();
	}
	
	@Override
	protected BaseSnomedComponentCreateRequest createRequest() {
		return new SnomedDescriptionCreateRequest();
	}
	
	@Override
	protected void init(BaseSnomedComponentCreateRequest request) {
		final SnomedDescriptionCreateRequest req = (SnomedDescriptionCreateRequest) request;
		req.setCaseSignificance(caseSignificance);
		req.setTerm(term);
		req.setConceptId(conceptId);
		req.setTypeId(typeId);
		req.setLanguageCode(languageCode);
		req.setAcceptability(acceptabilityMap);
		req.setInactivationIndicator(inactivationIndicator);
	}


}
