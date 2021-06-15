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
import java.util.stream.Collectors;

import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemSearchRequest.OptionKey;

/**
 * @since 8.0
 */
public final class FhirCodeSystemSearchRequestBuilder 
		extends SearchResourceRequestBuilder<FhirCodeSystemSearchRequestBuilder, RepositoryContext, Bundle>
		implements ResourceRepositoryRequestBuilder<Bundle> {

	@Override
	protected SearchResourceRequest<RepositoryContext, Bundle> createSearch() {
		return new FhirCodeSystemSearchRequest();
	}

	public FhirCodeSystemSearchRequestBuilder filterByNames(Iterable<String> names) {
		return addOption(OptionKey.NAME, names);
	}
	
	public FhirCodeSystemSearchRequestBuilder filterByTitle(String titles) {
		return addOption(OptionKey.TITLE, titles);
	}

	public FhirCodeSystemSearchRequestBuilder sortByFields(String...sort) {
		if (sort != null) {
			return sortBy(
				List.of(sort)
					.stream()
					.map(field -> {
						// TODO validate and report if any sort fields use unrecognized fields
						if (field.startsWith("-")) {
							return SearchResourceRequest.SortField.of(field.substring(1), false);
						} else {
							return SearchResourceRequest.SortField.of(field, true);
						}
					})
					.collect(Collectors.toList())
			);
		} else {
			return getSelf();
		}
	}

	public FhirCodeSystemSearchRequestBuilder setSummary(String summary) {
		// TODO convert this to a proper setFields where the list of fields represent the summary fields, based on the input, if the defined summary value is not a valid value throw a BadRequest
		return getSelf();
	}
	
	public FhirCodeSystemSearchRequestBuilder setElements(String[] elements) {
		// TODO convert this to a proper setFields where the list of fields represent the summary fields, based on the input, if the defined summary value is not a valid value throw a BadRequest
		return getSelf();
	}
	
	public FhirCodeSystemSearchRequestBuilder setCount(int count) {
		return setLimit(count);
	}

	public FhirCodeSystemSearchRequestBuilder filterByContent(String content) {
		return addOption(OptionKey.CONTENT, content);
	}

	public FhirCodeSystemSearchRequestBuilder filterByLastUpdated(String lastUpdated) {
		return addOption(OptionKey.LAST_UPDATED, lastUpdated);
	}

}
