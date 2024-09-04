/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.request;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r5.model.Bundle;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.fhir.core.Summary;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;

/**
 * @since 8.0 
 * @param <R>
 */
public abstract class FhirResourceGetRequest<SB extends FhirResourceSearchRequestBuilder<SB>, R> extends GetResourceRequest<SB, RepositoryContext, Bundle, R> {

	private static final long serialVersionUID = 1L;
	
	private String summary;
	private List<String> elements;
	
	protected FhirResourceGetRequest(String idOrUrl) {
		super(idOrUrl);
	}
	
	final void setSummary(String summary) {
		this.summary = summary;
	}
	
	final void setElements(List<String> elements) {
		this.elements = elements;
	}
	
	@Override
	protected final SB createSearchRequestBuilder() {
		if (Summary.COUNT.equals(summary)) {
			throw new BadRequestException(String.format("_summary=count is not supported on single resource operations"));
		}
		
		return prepareSearch()
				.setSummary(summary)
				.setElements(elements);
	}
	
	protected abstract SB prepareSearch();

	@Override
	protected final Optional<R> extractFirst(Bundle items) {
		return items.getEntry().stream().findFirst()
				.map(Bundle.BundleEntryComponent.class::cast)
				.map(Bundle.BundleEntryComponent::getResource)
				.map(resource -> (R) resource);
	}
	
}
