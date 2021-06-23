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

import java.util.List;
import java.util.Optional;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.ResourceEntry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.search.Summary;

/**
 * @since 8.0
 */
final class FhirCodeSystemGetRequest extends GetResourceRequest<FhirCodeSystemSearchRequestBuilder, RepositoryContext, Bundle, CodeSystem> {

	private static final long serialVersionUID = 1L;
	
	private String summary;
	private List<String> elements;
	
	public FhirCodeSystemGetRequest(String idOrUrl) {
		super(idOrUrl);
	}
	
	void setSummary(String summary) {
		this.summary = summary;
	}
	
	void setElements(List<String> elements) {
		this.elements = elements;
	}

	@Override
	protected FhirCodeSystemSearchRequestBuilder createSearchRequestBuilder() {
		if (Summary.COUNT.equals(summary)) {
			throw new BadRequestException(String.format("_summary=count is not supported on single resource operations"));
		}
		
		return new FhirCodeSystemSearchRequestBuilder()
				.setSummary(summary)
				.setElements(elements);
	}
	
	@Override
	protected Optional<CodeSystem> extractFirst(Bundle items) {
		return items.first()
				.map(ResourceEntry.class::cast)
				.map(ResourceEntry::getResource)
				.map(CodeSystem.class::cast);
	}

}
