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

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.elasticsearch.common.Strings;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.ConceptSearchRequestBuilder;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.fhir.core.codesystems.FilterOperator;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.ResourceResponseEntry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.*;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet.Builder;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;

/**
 * @since 8.0
 */
final class FhirValueSetExpandRequest implements Request<ServiceProvider, ValueSet> {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	@Valid
	@JsonProperty
	@JsonUnwrapped
	private ExpandValueSetRequest request;

	public FhirValueSetExpandRequest(ExpandValueSetRequest request) {
		this.request = request;
	}
	
	@Override
	public ValueSet execute(ServiceProvider context) {
		final String uri = request.getUrl().getUriValue();
		ValueSet valueSet = null;
		try {
			valueSet = FhirRequests.valueSets().prepareGet(uri)
					.setElements(ImmutableList.<String>builder()
							.addAll(ValueSet.Fields.MANDATORY)
							.add(ValueSet.Fields.COMPOSE)
							.build())
					.buildAsync()
					.execute(context);
			return context.optionalService(FhirValueSetExpander.class).orElse(FhirValueSetExpander.NOOP).expand(context, valueSet, request);
		} catch (NotFoundException e) {
			// if there is no Value Set present for the given URL, then try to parse the URL to a meaningful value if possible and evaluate it
			if (uri.startsWith("http://")) {
				return computeFhirValueSetUsingUrl(context, uri);
			}
			
			throw e;
		}
	}

	private ValueSet computeFhirValueSetUsingUrl(ServiceProvider context, String urlValue) {
		// only URLs with query parts are supported, every other case is rejected for now
		if (urlValue.contains("#")) {
			return null;
		}
		
		// extract the non-query part from the URL value
		String baseUrl = urlValue.split("\\?")[0];
		String query = "";
		if (urlValue.contains("?")) {
			query = urlValue.split("\\?")[1];
		}
		
		// if this is the base URI string, then always append the core module to represent the International Edition properly
		if (Uri.SNOMED_BASE_URI_STRING.equals(baseUrl)) {
			baseUrl = baseUrl.concat("/900000000000207008");
		}
		
		// try to lookup the CodeSystem using the baseUrl
		CodeSystem codeSystem = FhirRequests.codeSystems().prepareSearch()
				.one()
				.filterByUrl(baseUrl)
				.buildAsync()
				.execute(context)
				.first()
				.map(ResourceResponseEntry.class::cast)
				.map(ResourceResponseEntry::getResponseResource)
				.map(CodeSystem.class::cast)
				.orElse(null);
		
		// if no CodeSystem stored to use as Value Set source, return NotFound response
		if (codeSystem == null) {
			return null;
		}
		
		// return the content of the CodeSystem as Value Set
		String id = Hashing.goodFastHash(8).hashString(urlValue, Charsets.UTF_8).toString();
		Builder valueSet = ValueSet.builder(id)
				.url(urlValue)
				// according to http://hl7.org/fhir/r4/snomedct.html#implicit they always ACTIVE
				.status(PublicationStatus.ACTIVE);
		
		final Expansion.Builder expansion = Expansion.builder()
				.identifier(id)
				.timestamp(new Date());
		
		ConceptSearchRequestBuilder req = CodeSystemRequests.prepareSearchConcepts()
				.filterByCodeSystemUri(codeSystem.getResourceURI())
				.filterByActive(request.getActiveOnly())
				.filterByTerm(request.getFilter())
				.setLimit(request.getCount() == null ? 10 : request.getCount())
				.setSearchAfter(request.getAfter())
				// SNOMED only preferred display support (VS should always use FSN)
				.setPreferredDisplay("FSN") 
				// always return sorted results for consistency, in case of term filtering return by score otherwise by ID
				.sortBy(!CompareUtils.isEmpty(request.getFilter()) ? SearchIndexResourceRequest.SCORE : SearchResourceRequest.Sort.fieldAsc("id"));

		Compose compose = null;
		
		// configure query based on fhir_vs query parameter and also build the compose declaration for this implicit Value Set
		if (Strings.isNullOrEmpty(query) || "fhir_vs".equals(query)) {
			// do nothing, search all concepts
		} else if (query.startsWith("fhir_vs=")) {
			String fhirVsValue = query.replace("fhir_vs=", "");
			if (fhirVsValue.startsWith("ecl/")) {
				String ecl = fhirVsValue.replace("ecl/", "");
				req.filterByQuery(ecl);
				// configure Value Set for ECL
				valueSet
					.name(String.format("%s Concepts matching %s", codeSystem.getName(), ecl))
					.description(String.format("All SNOMED CT concepts that match the expression constraint %s", ecl));
				// configure compose for ECL
				compose = Compose.builder().addInclude(Include.builder()
						.addFilters(
							ValueSetFilter.builder()
								.property("constraint")
								.operator(FilterOperator.EQUALS)
								.value(ecl)
							.build()
						)
						.build()).build();
			} else if (fhirVsValue.startsWith("isa/")) {
				String parent = fhirVsValue.replace("isa/", "");
				req.filterByAncestor(parent);
				// configure Value Set for IS A
				valueSet
					.name(String.format("%s Concept %s and descendants", codeSystem.getName(), parent))
					.description(String.format("All SNOMED CT concepts for %s", parent));
				// configure compose for IS A
				compose = Compose.builder().addInclude(Include.builder()
						.system(baseUrl)
						.addFilters(
							ValueSetFilter.builder()
								.property("concept")
								.operator(FilterOperator.IS_A)
								.value(parent)
							.build()
						)
						.build()).build();
			} else if (fhirVsValue.startsWith("refset/")) {
				String refsetId = fhirVsValue.replace("refset/", "");
				if (Strings.isNullOrEmpty(refsetId)) {
					// TODO support refset identifier concept search
					return null;
				} else {
					req.filterByQuery("^"+refsetId);
					// configure Value Set for REFSET
					valueSet
						.name(String.format("%s Reference Set %s", codeSystem.getName(), refsetId))
						.description(String.format("All SNOMED CT concepts in the reference set %s", refsetId));
					// configure compose for REFSET
					compose = Compose.builder().addInclude(Include.builder()
							.addFilters(
								ValueSetFilter.builder()
									.property("concept")
									.operator(FilterOperator.IN)
									.value(refsetId)
								.build()
							)
							.build()).build();
				}
			} else {
				// no support for this unknown filter, return 404
				// TODO return unsupported maybe?
				// TODO check against declared filter values in CodeSystem
				return null;
			}
		}
		
		Concepts concepts = req.buildAsync().execute(context);
		
		expansion
			.total(concepts.getTotal())
			.after(concepts.getSearchAfter());
		
		for (Concept concept : concepts) {
			expansion.addContains(Contains.builder()
					.code(concept.getId())
					.system(baseUrl)
					.display(concept.getTerm())
					.build());
		}
		
		return valueSet
				.compose(compose)
				.expansion(expansion.build())
				.build();
	}

}
