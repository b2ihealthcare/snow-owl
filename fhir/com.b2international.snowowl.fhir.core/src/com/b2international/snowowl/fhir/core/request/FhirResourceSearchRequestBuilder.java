/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.r5.model.Bundle;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequest.OptionKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;

/**
 * @since 8.0
 */
public abstract class FhirResourceSearchRequestBuilder<B extends FhirResourceSearchRequestBuilder<B>>
	extends SearchResourceRequestBuilder<B, RepositoryContext, Bundle>
	implements ResourceRepositoryRequestBuilder<Bundle> {

	public static final String BASE_RESOURCE_ID = "id";
	public static final String BASE_RESOURCE_META = "meta";
	
	public static final String RESOURCE_LANGUAGE = "language";
	
	public static final String DOMAIN_RESOURCE_TEXT = "text";
	
	public static final String CANONICAL_RESOURCE_DATE = "date";
	public static final String CANONICAL_RESOURCE_NAME = "name";
	public static final String CANONICAL_RESOURCE_PUBLISHER = "publisher";
	public static final String CANONICAL_RESOURCE_STATUS = "status";
	public static final String CANONICAL_RESOURCE_TITLE = "title";
	public static final String CANONICAL_RESOURCE_URL = "url";
	
	public static final String METADATA_RESOURCE_DESCRIPTION = "description";
	public static final String METADATA_RESOURCE_VERSION = "version";
	public static final String METADATA_RESOURCE_PURPOSE = "purpose";
	
	protected static final Set<String> MANDATORY_FIELDS = ImmutableSet.of(
		BASE_RESOURCE_ID,
		BASE_RESOURCE_META,
		CANONICAL_RESOURCE_STATUS);
	
	public static final Set<String> SUMMARY_FIELDS = ImmutableSet.<String>builder()
		.addAll(MANDATORY_FIELDS)
		.add(CANONICAL_RESOURCE_URL)
		.add(METADATA_RESOURCE_VERSION)
		.add(CANONICAL_RESOURCE_NAME)
		.add(CANONICAL_RESOURCE_TITLE)
		.add(CANONICAL_RESOURCE_DATE)
		.add(CANONICAL_RESOURCE_PUBLISHER)
		.build();


	public final B filterByName(final String name) {
		return addOption(OptionKey.NAME, name);
	}

	public final B filterByNames(final Iterable<String> names) {
		return addOption(OptionKey.NAME, names);
	}

	public final B filterByUrl(final String url) {
		return addOption(OptionKey.URL, url);
	}

	public final B filterByUrls(final Iterable<String> url) {
		return addOption(OptionKey.URL, url);
	}

	public final B filterByVersion(final String version) {
		return addOption(OptionKey.VERSION, version);
	}

	public final B filterByVersions(final Iterable<String> versions) {
		return addOption(OptionKey.VERSION, versions);
	}

	public final B filterByTitle(final String titles) {
		return addOption(OptionKey.TITLE, titles);
	}

	public final B sortByFields(final String...sort) {
		if (CompareUtils.isEmpty(sort)) {
			return getSelf();
		}

		// TODO validate and report if any sort fields use unrecognized fields
		final List<Sort> parsedSortBy = Arrays.stream(sort)
			.map(field -> {
				if (field.startsWith("-")) {
					return SearchResourceRequest.SortField.of(field.substring(1), false);
				} else {
					return SearchResourceRequest.SortField.of(field, true);
				}
			})
			.collect(Collectors.toList());

		return sortBy(parsedSortBy);
	}

	public final B setSummary(final SummaryEnum summary) {
		if (summary == null) {
			return getSelf();
		}

		switch (summary) {
			case FALSE:
				return getSelf();
			case COUNT:
				return setLimit(0);
			case TRUE:
				return addElements(getSummaryFields());
			case TEXT:
				return addElements(getSummaryTextFields());
			case DATA:
				return addElements(getSummaryDataFields());
			default:
				throw new InvalidRequestException(String.format("'%s' is an unrecognized or unsupported _summary value.", summary.getCode()));
		}
	}

	protected abstract Set<String> getSummaryFields();
	protected abstract Set<String> getSummaryTextFields();
	protected abstract Set<String> getSummaryDataFields();

	public final B addElements(final Iterable<String> elements) {
		if (CompareUtils.isEmpty(elements)) {
			return getSelf();
		}

		// Start out with existing field selection
		final Set<String> fields = newLinkedHashSet(fields());
		// Always include mandatory elements
		fields.addAll(getMandatoryFields());
		// Add explicitly requested elements to the end
		Iterables.addAll(fields, elements);

		final Set<String> unrecognizedElements = Sets.difference(fields, getKnownFields());
		if (!unrecognizedElements.isEmpty()) {
			throw new InvalidRequestException(String.format(
				"'%s' %s unrecognized or unsupported value%s for _elements. Supported values are: '%s'",
				unrecognizedElements,
				unrecognizedElements.size() == 1 ? "is an" : "are",
				unrecognizedElements.size() == 1 ? "" : "s",
				getKnownFields()
			));
		}

		return setFields(ImmutableList.copyOf(fields));
	}

	protected abstract Set<String> getMandatoryFields();
	protected abstract Set<String> getKnownFields();

	public B filterByLastUpdated(final String lastUpdated) {
		return addOption(OptionKey.LAST_UPDATED, lastUpdated);
	}

	public B setCount(final int count) {
		return setLimit(count);
	}
}
