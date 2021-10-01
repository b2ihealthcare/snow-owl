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
package com.b2international.snowowl.fhir.core.request.valueset;

import java.util.Set;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequestBuilder;

/**
 * @since 8.0
 */
public final class FhirValueSetSearchRequestBuilder extends FhirResourceSearchRequestBuilder<FhirValueSetSearchRequestBuilder> {

	@Override
	protected SearchResourceRequest<RepositoryContext, Bundle> createSearch() {
		return new FhirValueSetSearchRequest();
	}
	
	@Override
	protected Set<String> getKnownResourceFields() {
		return ValueSet.Fields.ALL;
	}
	
	@Override
	protected Set<String> getMandatoryFields() {
		return ValueSet.Fields.MANDATORY;
	}
	
	@Override
	protected Set<String> getSummaryFields() {
		return ValueSet.Fields.SUMMARY;
	}
	
	@Override
	protected Set<String> getSummaryTextFields() {
		return ValueSet.Fields.SUMMARY_TEXT;
	}
	
	@Override
	protected Set<String> getSummaryDataFields() {
		return ValueSet.Fields.SUMMARY_DATA;
	}

}
