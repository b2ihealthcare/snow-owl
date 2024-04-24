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
package com.b2international.snowowl.fhir.core.request.valueset;

import java.util.Date;

import org.elasticsearch.common.Strings;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.Enumerations.FilterOperator;
import org.hl7.fhir.r5.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r5.model.ValueSet;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.ConceptSearchRequestBuilder;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.fhir.core.R5ObjectFields;
import com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

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
							.addAll(R5ObjectFields.ValueSet.SUMMARY)
							.add(R5ObjectFields.ValueSet.STATUS)
							.add(R5ObjectFields.ValueSet.COMPOSE)
							.build())
					.buildAsync()
					.execute(context);
			return context.service(RepositoryManager.class)
					.get(valueSet.getUserString(TerminologyResource.Fields.TOOLING_ID))
					.optionalService(FhirValueSetExpander.class)
					.orElse(FhirValueSetExpander.NOOP)
					.expand(context, valueSet, request);
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
				.getEntry().stream().findFirst()
				.map(Bundle.BundleEntryComponent.class::cast)
				.map(Bundle.BundleEntryComponent::getResource)
				.map(CodeSystem.class::cast)
				.orElse(null);
		
		// if no CodeSystem stored to use as Value Set source, return NotFound response
		if (codeSystem == null) {
			return null;
		}
		
		// return the content of the CodeSystem as Value Set
		String id = Hashing.goodFastHash(8).hashString(urlValue, Charsets.UTF_8).toString();
		ValueSet valueSet = (ValueSet) new ValueSet()
				.setUrl(urlValue)
				// according to https://terminology.hl7.org/SNOMEDCT.html#snomed-ct-implicit-value-sets publication status is always ACTIVE
				.setStatus(PublicationStatus.ACTIVE)
				.setId(id);
		
		final ValueSet.ValueSetExpansionComponent expansion = new ValueSet.ValueSetExpansionComponent()
				.setIdentifier(id)
				.setTimestamp(new Date());
		
		ConceptSearchRequestBuilder req = CodeSystemRequests.prepareSearchConcepts()
				.filterByCodeSystemUri(new ResourceURI(codeSystem.getUserString(TerminologyResource.Fields.RESOURCE_URI)))
				.filterByActive(request.getActiveOnly())
				.filterByTerm(request.getFilter())
				.setLimit(request.getCount() == null ? 10 : request.getCount())
				.setSearchAfter(request.getAfter())
				// SNOMED only preferred display support (VS should always use FSN)
				.setPreferredDisplay("FSN") 
				.setLocales(FhirRequest.extractLocales(request.getDisplayLanguage()))
				// always return sorted results for consistency, in case of term filtering return by score otherwise by ID
				.sortBy(!CompareUtils.isEmpty(request.getFilter()) ? SearchIndexResourceRequest.SCORE : SearchResourceRequest.Sort.fieldAsc("id"));

		ValueSet.ValueSetComposeComponent compose = null;
		
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
					.setName(String.format("%s Concepts matching %s", codeSystem.getName(), ecl))
					.setDescription(String.format("All SNOMED CT concepts that match the expression constraint %s", ecl));
				// configure compose for ECL
				compose = new ValueSet.ValueSetComposeComponent();
				compose.addInclude(
					new ValueSet.ConceptSetComponent()
						.addFilter(
							new ValueSet.ConceptSetFilterComponent()
								.setProperty("constraint")
								.setOp(FilterOperator.EQUAL)
						)
				);
			} else if (fhirVsValue.startsWith("isa/")) {
				String parent = fhirVsValue.replace("isa/", "");
				req.filterByAncestor(parent);
				// configure Value Set for IS A
				valueSet
					.setName(String.format("%s Concept %s and descendants", codeSystem.getName(), parent))
					.setDescription(String.format("All SNOMED CT concepts for %s", parent));
				// configure compose for IS A
				compose = new ValueSet.ValueSetComposeComponent();
				compose.addInclude(
					new ValueSet.ConceptSetComponent()
						.setSystem(baseUrl)
						.addFilter(
							new ValueSet.ConceptSetFilterComponent()
								.setProperty("constraint")
								.setOp(FilterOperator.ISA)
								.setValue(parent)
						)
				);
			} else if (fhirVsValue.startsWith("refset/")) {
				String refsetId = fhirVsValue.replace("refset/", "");
				if (Strings.isNullOrEmpty(refsetId)) {
					// TODO support refset identifier concept search
					return null;
				} else {
					req.filterByQuery("^"+refsetId);
					// configure Value Set for REFSET
					valueSet
						.setName(String.format("%s Reference Set %s", codeSystem.getName(), refsetId))
						.setDescription(String.format("All SNOMED CT concepts in the reference set %s", refsetId));
					// configure compose for REFSET
					compose = new ValueSet.ValueSetComposeComponent();
					compose.addInclude(
						new ValueSet.ConceptSetComponent()
							.setSystem(baseUrl)
							.addFilter(
								new ValueSet.ConceptSetFilterComponent()
									.setProperty("concept")
									.setOp(FilterOperator.IN)
									.setValue(refsetId)
							)
					);
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
			.setTotal(concepts.getTotal())
			.setUserData("after", concepts.getSearchAfter());
		
		for (Concept concept : concepts) {
			expansion.addContains(
				new ValueSet.ValueSetExpansionContainsComponent()
					.setCode(concept.getId())
					.setSystem(baseUrl)
					.setDisplay(concept.getTerm())
			);
		}
		
		return valueSet
				.setCompose(compose)
				.setExpansion(expansion);
	}

}
