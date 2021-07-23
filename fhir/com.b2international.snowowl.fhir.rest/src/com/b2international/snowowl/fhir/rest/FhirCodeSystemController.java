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
package com.b2international.snowowl.fhir.rest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.fhir.core.codesystems.PropertyType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.property.ConceptProperty;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.lcs.core.domain.property.LocalTerminologyConceptProperty;
import com.b2international.snowowl.lcs.core.domain.property.LocalTerminologyConceptPropertyDefinition;
import com.b2international.snowowl.lcs.core.domain.property.LocalTerminologyConceptPropertyDefinition.PropertyValueType;
import com.b2international.snowowl.lcs.core.request.LcsRequests;
import com.google.common.collect.ImmutableList;
import com.b2international.snowowl.lcs.core.domain.property.PropertyCardinality;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Code system resource REST endpoint.
 * <ul>
 * <li>Concept lookup and decomposition</li>
 * <li>Subsumption testing</li>
 * <li>Code composition based on supplied properties</li>
 * </ul>
 *  
 * @see <a href="https://www.hl7.org/fhir/codesystems.html">FHIR:CodeSystem</a>
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html">FHIR:CodeSystem:Operations</a>
 * 
 * @since 8.0
 */
@Tag(description = "CodeSystem", name = "CodeSystem")
@RestController
@RequestMapping(value="/CodeSystem", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class FhirCodeSystemController extends AbstractFhirResourceController<CodeSystem> {
	
	@Override
	protected Class<CodeSystem> getModelClass() {
		return CodeSystem.class;
	}
	
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	//@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(@RequestBody final CodeSystem codeSystem) {
		
		List<LocalTerminologyConceptPropertyDefinition> propertyDefinitions = codeSystem.getProperties().stream().map(p -> {
			LocalTerminologyConceptPropertyDefinition.Builder builder = LocalTerminologyConceptPropertyDefinition.builder(UUID.randomUUID().toString())
					.name(p.getCodeValue())
					.cardinality(PropertyCardinality.ZERO_TO_MANY)
					.description(p.getDescription());
			
			Code type = p.getType();
			PropertyType propertyType = PropertyType.forValue(type.getCodeValue());
			
			switch (propertyType) {
			case BOOLEAN:
				builder.valueType(PropertyValueType.BOOLEAN);
				break;

			default:
				builder.valueType(PropertyValueType.BOOLEAN);
				break;
			}
			return builder.build();
			
		}).collect(Collectors.toList());
		
		for (LocalTerminologyConceptPropertyDefinition localTerminologyConceptPropertyDefinition : propertyDefinitions) {
			System.out.println("Def: " + localTerminologyConceptPropertyDefinition);
		}
		
		LcsRequests.prepareCreateTerminology()
			.setId(codeSystem.getId().getIdValue())
			.setDescription(codeSystem.getDescription())
			.setLanguage(codeSystem.getLanguage().getCodeValue())
			.setCopyright(codeSystem.getCopyright())
			.setStatus(codeSystem.getStatus().getCodeValue())
			.setOwner(codeSystem.getPublisher())
			//.setUrl(url)
			.setPropertyDefinitions(propertyDefinitions)
			.setTitle(codeSystem.getTitle())
			.buildAsync()
			.execute(getBus());
		
		Collection<Concept> concepts = codeSystem.getConcepts();
		
		for (Concept concept : concepts) {
			
			Collection<ConceptProperty> properties = concept.getProperties();
			List<LocalTerminologyConceptProperty> lcsProperties = properties.stream()
				.map(p -> {
					LocalTerminologyConceptPropertyDefinition definition = propertyDefinitions.stream()
							.filter(d -> d.getName().equals(p.getCodeValue()))
							.findFirst()
							.orElseThrow(() -> new IllegalArgumentException("Undefined property"));
					
					LocalTerminologyConceptProperty lcsProperty = definition.createProperty();
					lcsProperty.setValue(p.getValue());
					return lcsProperty;
				
			}).collect(Collectors.toList());
			
			ResourceURI uri = com.b2international.snowowl.core.codesystem.CodeSystem.uri(codeSystem.getId().getIdValue());
			LcsRequests.localTerminologyConcepts()
				.prepareCreate()
				.setActive(true)
				.setTerm(concept.getDisplay())
				.setAlternativeTerms(ImmutableList.of(concept.getDefinition()))
				.setProperties(lcsProperties)
				.build(uri, "user", "Commit comment")
				.execute(getBus());
			
		}
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * CodeSystems
	 * @param parameters - request parameters
	 * @return bundle of code systems
	 */
	@Operation(
		summary="Retrieve all code systems",
		description="Returns a collection of the supported code systems."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
	})
	@GetMapping
	public Promise<Bundle> getCodeSystems(@ParameterObject FhirCodeSystemSearchParameters params) {
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
//		String uri = MvcUriComponentsBuilder.fromController(FhirCodeSystemController.class).build().toString();
		
		return FhirRequests.codeSystems().prepareSearch()
				.filterByIds(asList(params.get_id()))
				.filterByNames(asList(params.getName()))
				.filterByTitle(params.getTitle())
				.filterByContent(params.get_content())
				.filterByLastUpdated(params.get_lastUpdated())
				.filterByUrls(Collections3.intersection(params.getUrl(), params.getSystem())) // values defined in both url and system match the same field, compute intersection to simulate ES behavior here
				.filterByVersions(params.getVersion())
				.setSearchAfter(params.get_after())
				.setCount(params.get_count())
				// XXX _summary=count may override the default _count=10 value, so order of method calls is important here
				.setSummary(params.get_summary())
				.setElements(asList(params.get_elements()))
				.sortByFields(params.get_sort())
				.buildAsync()
				.execute(getBus());
		// TODO convert returned Bundle entries to have a fullUrl, either here or supply current url to request via header param
//				String resourceUrl = String.join("/", uri, codeSystem.getId().getIdValue());
//				Entry entry = new Entry(new Uri(resourceUrl), codeSystem);
	}
	
	/**
	 * HTTP Get for retrieving a code system by its code system id
	 * @param id
	 * @param parameters - request parameters
	 * @return
	 */
	@Operation(
		summary = "Retrieve the code system by id",
		description = "Retrieves the code system specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Code system not found")
	})
	@RequestMapping(value="/{id:**}", method=RequestMethod.GET)
	public Promise<CodeSystem> getCodeSystem(
			@Parameter(description = "The identifier of the Code System resource")
			@PathVariable(value = "id") 
			final String id,
			
			@ParameterObject
			final FhirResourceSelectors selectors) {
		
		return FhirRequests.codeSystems().prepareGet(id)
				.setSummary(selectors.get_summary())
				.setElements(asList(selectors.get_elements()))
				.buildAsync()
				.execute(getBus());
	}
	
}
