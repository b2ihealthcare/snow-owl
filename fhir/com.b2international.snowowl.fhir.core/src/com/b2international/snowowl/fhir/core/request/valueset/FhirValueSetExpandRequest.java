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

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.ConceptSearchRequestBuilder;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.ResourceResponseEntry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
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
		Builder vs = ValueSet.builder(id)
				.url(urlValue)
				.status(PublicationStatus.DRAFT); // TODO on-the-fly expanded Value Set's publication status???
		
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
				// always return sorted results for consistency
				.sortBy("id:asc");

		// configure query based on fhir_vs query parameter
		if (Strings.isNullOrEmpty(query) || "fhir_vs".equals(query)) {
			// do nothing, search all concepts
		} else if (query.startsWith("fhir_vs=")) {
			String fhirVsValue = query.replace("fhir_vs=", "");
			if (fhirVsValue.startsWith("ecl/")) {
				String ecl = fhirVsValue.replace("ecl/", "");
				req.filterByQuery(ecl);
			} else if (fhirVsValue.startsWith("isa/")) {
				String parent = fhirVsValue.replace("isa/", "");
				req.filterByAncestor(parent);
			} else if (fhirVsValue.startsWith("refset/")) {
				String refsetId = fhirVsValue.replace("refset/", "");
				req.filterByQuery("^"+refsetId);
			} else {
				// do nothing, search all concepts
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
		
		return vs.expansion(expansion.build()).build();
	}

}
