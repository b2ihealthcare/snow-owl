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
package com.b2international.snowowl.fhir.core.request;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.MetadataResource;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.GetResourceRequest;

import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;

/**
 * @since 8.0 
 * @param <R>
 */
public abstract class FhirResourceGetRequest<
	SB extends FhirResourceSearchRequestBuilder<SB>, 
	R extends MetadataResource> 
	extends GetResourceRequest<SB, RepositoryContext, Bundle, R> {

	private static final long serialVersionUID = 1L;

	private SummaryEnum summary;
	private List<String> elements;

	protected FhirResourceGetRequest(final String idOrUrl) {
		super(idOrUrl);
	}

	final void setSummary(final SummaryEnum summary) {
		this.summary = summary;
	}

	final void setElements(final List<String> elements) {
		this.elements = elements;
	}

	@Override
	protected final SB createSearchRequestBuilder() {
		if (SummaryEnum.COUNT.equals(summary)) {
			throw new InvalidRequestException("_summary=count is not supported on single resource operations");
		}

		return prepareSearch()
			.setSummary(summary)
			.addElements(elements);
	}

	protected abstract SB prepareSearch();

	@Override
	protected final Optional<R> extractFirst(final Bundle searchSet) {
		if (!searchSet.hasEntry()) {
			return Optional.empty();
		} else {
			return Optional.of((R) searchSet.getEntryFirstRep().getResource());
		}
	}
}
