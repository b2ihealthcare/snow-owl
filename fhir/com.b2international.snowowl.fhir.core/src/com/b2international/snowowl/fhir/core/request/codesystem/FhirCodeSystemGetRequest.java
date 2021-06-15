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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.util.Optional;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * @since 8.0
 */
final class FhirCodeSystemGetRequest extends GetResourceRequest<FhirCodeSystemSearchRequestBuilder, RepositoryContext, Bundle, CodeSystem> {

	private static final long serialVersionUID = 1L;

	public FhirCodeSystemGetRequest(String idOrUrl) {
		super(idOrUrl);
	}

	@Override
	protected FhirCodeSystemSearchRequestBuilder createSearchRequestBuilder() {
		return new FhirCodeSystemSearchRequestBuilder();
	}
	
	@Override
	protected Optional<CodeSystem> extractFirst(Bundle items) {
		return items.first().map(Entry::getResource).map(CodeSystem.class::cast);
	}

}
