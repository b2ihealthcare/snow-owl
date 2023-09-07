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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.util.Set;

import org.hl7.fhir.r5.model.Bundle;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequestBuilder;
import com.google.common.collect.ImmutableSet;

/**
 * @since 8.0
 */
public final class FhirCodeSystemSearchRequestBuilder extends FhirResourceSearchRequestBuilder<FhirCodeSystemSearchRequestBuilder> {

	public static final String CODE_SYSTEM_CONTENT = "content";
	public static final String CODE_SYSTEM_CONCEPT = "concept";
	public static final String CODE_SYSTEM_CONTACT = "contact";
	public static final String CODE_SYSTEM_COPYRIGHT = "copyright";
	public static final String CODE_SYSTEM_COUNT = "count";
	public static final String CODE_SYSTEM_FILTER = "filter";
	public static final String CODE_SYSTEM_PROPERTY = "property";
	public static final String CODE_SYSTEM_IDENTIFIER = "identifier";

	private static final Set<String> MANDATORY_FIELDS = ImmutableSet.<String>builder()
		.addAll(FhirResourceSearchRequestBuilder.MANDATORY_FIELDS)
		.add(CODE_SYSTEM_CONTENT)
		.build();

	private static final Set<String> SUMMARY_FIELDS = ImmutableSet.<String>builder()
		.addAll(FhirResourceSearchRequestBuilder.SUMMARY_FIELDS)
		.add(CODE_SYSTEM_COUNT)
		.add(CODE_SYSTEM_FILTER)
		.add(CODE_SYSTEM_PROPERTY)
		.add(CODE_SYSTEM_IDENTIFIER)
		.build();

	private static final Set<String> SUMMARY_TEXT_FIELDS = ImmutableSet.<String>builder()
		.addAll(MetadataResource.Fields.MANDATORY)
		.add(DOMAIN_RESOURCE_TEXT)
		.build();

	private static final Set<String> SUMMARY_DATA_FIELDS = MANDATORY_FIELDS;

	private static final Set<String> KNOWN_FIELDS = ImmutableSet.<String>builder()
		.addAll(MANDATORY_FIELDS)
		.addAll(SUMMARY_FIELDS)
		.add(DOMAIN_RESOURCE_TEXT)
		.add(CODE_SYSTEM_CONCEPT)
		.build();

	@Override
	protected SearchResourceRequest<RepositoryContext, Bundle> createSearch() {
		return new FhirCodeSystemSearchRequest();
	}

	@Override
	protected Set<String> getKnownFields() {
		return KNOWN_FIELDS;
	}

	@Override
	protected Set<String> getMandatoryFields() {
		return MANDATORY_FIELDS;
	}

	@Override
	protected Set<String> getSummaryFields() {
		return SUMMARY_FIELDS;
	}

	@Override
	protected Set<String> getSummaryTextFields() {
		return SUMMARY_TEXT_FIELDS;
	}

	@Override
	protected Set<String> getSummaryDataFields() {
		return SUMMARY_DATA_FIELDS;
	}
}
