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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemSearchRequest.OptionKey;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

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

	public FhirCodeSystemSearchRequestBuilder filterByName(String name) {
		return addOption(OptionKey.NAME, name);
	}
	
	public FhirCodeSystemSearchRequestBuilder filterByNames(Iterable<String> names) {
		return addOption(OptionKey.NAME, names);
	}
	
	public FhirCodeSystemSearchRequestBuilder filterByUrl(String url) {
		return addOption(OptionKey.URL, url);
	}
	
	public FhirCodeSystemSearchRequestBuilder filterByUrls(Iterable<String> url) {
		return addOption(OptionKey.URL, url);
	}
	
	public FhirCodeSystemSearchRequestBuilder filterByVersion(String version) {
		return addOption(OptionKey.VERSION, version);
	}
	
	public FhirCodeSystemSearchRequestBuilder filterByVersions(Iterable<String> versions) {
		return addOption(OptionKey.VERSION, versions);
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
		if (summary == null || Summary.FALSE.equalsIgnoreCase(summary)) {
			return getSelf();
		} else if (Summary.TRUE.equalsIgnoreCase(summary)) {
			return setElements(CodeSystem.Fields.SUMMARY);
		} else if (Summary.TEXT.equalsIgnoreCase(summary)) {
			return setElements(CodeSystem.Fields.SUMMARY_TEXT);
		} else if (Summary.DATA.equalsIgnoreCase(summary)) {
			return setElements(CodeSystem.Fields.SUMMARY_DATA);
		} else if (Summary.COUNT.equalsIgnoreCase(summary)) {
			return setLimit(0);
		} else {
			throw new BadRequestException(String.format("'%s' is unrecognized or not yet supported _summary value. Supported values are: '%s'", summary, Summary.VALUES));
		}
	}
	
	public FhirCodeSystemSearchRequestBuilder setElements(Iterable<String> elements) {
		if (elements == null) {
			return getSelf();
		} else {
			final Set<String> fields = new LinkedHashSet<>();
			// when called with a non-null value, make sure mandatory fields are implicitly included
			fields.addAll(CodeSystem.Fields.MANDATORY);
			// add all other fields
			elements.forEach(fields::add);
			
			Set<String> unrecognizedElements = Sets.difference(fields, CodeSystem.Fields.ALL);
			if (!unrecognizedElements.isEmpty()) {
				throw new BadRequestException(String.format(
					"'%s' %s unrecognized or not yet supported _elements value(s). Supported values are: '%s'", 
					unrecognizedElements, 
					unrecognizedElements.size() == 1 ? "is" : "are",
					CodeSystem.Fields.ALL
				));
			}
			
			return setFields(ImmutableList.copyOf(fields));
		}
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
