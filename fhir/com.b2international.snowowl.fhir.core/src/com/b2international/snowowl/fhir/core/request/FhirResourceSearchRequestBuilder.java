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
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequest.OptionKey;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * @since 8.0
 */
public abstract class FhirResourceSearchRequestBuilder<B extends FhirResourceSearchRequestBuilder<B>> 
		extends SearchResourceRequestBuilder<B, RepositoryContext, Bundle>
		implements ResourceRepositoryRequestBuilder<Bundle> {

	public final B filterByName(String name) {
		return addOption(OptionKey.NAME, name);
	}
	
	public final B filterByNames(Iterable<String> names) {
		return addOption(OptionKey.NAME, names);
	}
	
	public final B filterByUrl(String url) {
		return addOption(OptionKey.URL, url);
	}
	
	public final B filterByUrls(Iterable<String> url) {
		return addOption(OptionKey.URL, url);
	}
	
	public final B filterByVersion(String version) {
		return addOption(OptionKey.VERSION, version);
	}
	
	public final B filterByVersions(Iterable<String> versions) {
		return addOption(OptionKey.VERSION, versions);
	}
	
	public final B filterByTitle(String titles) {
		return addOption(OptionKey.TITLE, titles);
	}

	public final B sortByFields(String...sort) {
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

	public final B setSummary(String summary) {
		if (summary == null || Summary.FALSE.equalsIgnoreCase(summary)) {
			return getSelf();
		} else if (Summary.TRUE.equalsIgnoreCase(summary)) {
			return setElements(getSummaryFields());
		} else if (Summary.TEXT.equalsIgnoreCase(summary)) {
			return setElements(getSummaryTextFields());
		} else if (Summary.DATA.equalsIgnoreCase(summary)) {
			return setElements(getSummaryDataFields());
		} else if (Summary.COUNT.equalsIgnoreCase(summary)) {
			return setLimit(0);
		} else {
			throw new BadRequestException(String.format("'%s' is unrecognized or not yet supported _summary value. Supported values are: '%s'", summary, Summary.VALUES));
		}
	}
	
	public final B setElements(Iterable<String> elements) {
		if (elements == null) {
			return getSelf();
		} else {
			final Set<String> fields = new LinkedHashSet<>();
			// when called with a non-null value, make sure mandatory fields are implicitly included
			fields.addAll(getMandatoryFields());
			// add all other fields
			elements.forEach(fields::add);
			
			Set<String> unrecognizedElements = Sets.difference(fields, getKnownResourceFields());
			if (!unrecognizedElements.isEmpty()) {
				throw new BadRequestException(String.format(
					"'%s' %s unrecognized or not yet supported _elements value(s). Supported values are: '%s'", 
					unrecognizedElements, 
					unrecognizedElements.size() == 1 ? "is" : "are",
					getKnownResourceFields()
				));
			}
			
			return setFields(ImmutableList.copyOf(fields));
		}
	}

	protected abstract Set<String> getMandatoryFields();
	protected abstract Set<String> getSummaryFields();
	protected abstract Set<String> getSummaryTextFields();
	protected abstract Set<String> getSummaryDataFields();
	protected abstract Set<String> getKnownResourceFields();
	
	public B filterByLastUpdated(String lastUpdated) {
		return addOption(OptionKey.LAST_UPDATED, lastUpdated);
	}
	
	public B setCount(int count) {
		return setLimit(count);
	}
	
}
