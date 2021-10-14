/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request.conceptmap;

import java.util.Set;

import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequestBuilder;

/**
 * @since 8.0
 */
public final class FhirConceptMapSearchRequestBuilder 
		extends FhirResourceSearchRequestBuilder<FhirConceptMapSearchRequestBuilder>
		implements ResourceRepositoryRequestBuilder<Bundle> {

	@Override
	protected SearchResourceRequest<RepositoryContext, Bundle> createSearch() {
		return new FhirConceptMapSearchRequest();
	}
	
	@Override
	protected Set<String> getKnownResourceFields() {
		return ConceptMap.Fields.ALL;
	}
	
	@Override
	protected Set<String> getMandatoryFields() {
		return ConceptMap.Fields.MANDATORY;
	}
	
	@Override
	protected Set<String> getSummaryFields() {
		return ConceptMap.Fields.SUMMARY;
	}
	
	@Override
	protected Set<String> getSummaryTextFields() {
		return ConceptMap.Fields.SUMMARY_TEXT;
	}
	
	@Override
	protected Set<String> getSummaryDataFields() {
		return ConceptMap.Fields.SUMMARY_DATA;
	}

}
