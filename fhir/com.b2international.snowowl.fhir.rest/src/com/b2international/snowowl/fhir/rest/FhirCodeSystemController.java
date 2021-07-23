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

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.fhir.core.codesystems.PropertyType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.lcs.core.domain.property.LocalTerminologyConceptPropertyDefinition;
import com.b2international.snowowl.lcs.core.domain.property.PropertyCardinality;
import com.b2international.snowowl.lcs.core.domain.property.LocalTerminologyConceptPropertyDefinition.Builder;
import com.b2international.snowowl.lcs.core.domain.property.LocalTerminologyConceptPropertyDefinition.PropertyValueType;
import com.b2international.snowowl.lcs.core.request.LcsRequests;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
@Api(value = "CodeSystem", description="FHIR CodeSystem Resource", tags = { "CodeSystem" })
@RestController
@RequestMapping(value="/CodeSystem")
//, produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class FhirCodeSystemController extends AbstractFhirResourceController<CodeSystem> {
	
	@Override
	protected Class<CodeSystem> getModelClass() {
		return CodeSystem.class;
	}
	
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(@RequestBody final CodeSystem codeSystem) {
		
		
		List<LocalTerminologyConceptPropertyDefinition> propertyDefinitions = codeSystem.getProperties().stream().map(p -> {
			Builder builder = LocalTerminologyConceptPropertyDefinition.builder(UUID.randomUUID().toString())
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
		
		/*
		LcsRequests.prepareCreateTerminology()
			.setTitle(codeSystem.getName())
			.setDescription(codeSystem.getDescription())
			.setLanguage(codeSystem.getLanguage().getCodeValue())
			//.setOid(codeSystem.)
		
			//.setName(codeSystem.getName())
			//.setMaintainingOrganizationLink(ORG_LINK)
			//.setDescription(LCS_DESCRIPTION)
			//.setComments(LCS_COMMENTS)
			//.setOid(LCS_NAME)
			.setPropertyDefinitions(propertyDefinitions)
			.buildAsync()
			.execute(getBus())
			.getSync();
			*/
		
		System.out.println("CS: " + codeSystem.getName());
		
		Collection<SupportedConceptProperty> properties = codeSystem.getProperties();
		for (SupportedConceptProperty property : properties) {
			System.out.println("Property: " +property.getCodeValue() );
		}
		
		Collection<Concept> concepts = codeSystem.getConcepts();
		for (Concept concept : concepts) {
			System.out.println("Concept: " + concept.getDisplay());
		}
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * CodeSystems
	 * @param parameters - request parameters
	 * @return bundle of code systems
	 */
	@ApiOperation(
			value="Retrieve all code systems",
			notes="Returns a collection of the supported code systems.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad Request", response = OperationOutcome.class),
	})
	@GetMapping
	public Promise<Bundle> getCodeSystems(FhirCodeSystemSearchParameters params) {
		return FhirRequests.codeSystems().prepareSearch()
				.filterByIds(asList(params.get_id()))
				.filterByNames(asList(params.getName()))
				.filterByTitle(params.getTitle())
				.filterByContent(params.get_content())
				.filterByLastUpdated(params.get_lastUpdated())
				.setSearchAfter(params.get_after())
				.setCount(params.get_count())
				// XXX _summary=count may override the default _count=10 value, so order of method calls is important here
				.setSummary(params.get_summary())
				.setElements(asList(params.get_elements()))
				.sortByFields(params.get_sort())
				.buildAsync()
				.execute(getBus());
		
		// TODO convert returned Bundle entries to have a fullUrl, either here or supply current url to request via header param
		
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
//		String uri = MvcUriComponentsBuilder.fromController(FhirCodeSystemController.class).build().toString();
		
		//collect the hits from the providers
//		Collection<ICodeSystemApiProvider> providers = codeSystemProviderRegistry.getProviders(getBus(), locales);
//		
//		for (ICodeSystemApiProvider codeSystemProvider : providers) {
//			Collection<CodeSystem> codeSystems = codeSystemProvider.getCodeSystems(requestParameters.getB());
//			for (CodeSystem codeSystem : codeSystems) {
//				applyResponseContentFilter(codeSystem, filterParameters);
//				String resourceUrl = String.join("/", uri, codeSystem.getId().getIdValue());
//				Entry entry = new Entry(new Uri(resourceUrl), codeSystem);
//				builder.addEntry(entry);
//				total++;
//			}
//		}
//		return builder.total(total).build();
	}
	
	/**
	 * HTTP Get for retrieving a code system by its code system id
	 * @param id
	 * @param parameters - request parameters
	 * @return
	 */
	@ApiOperation(
			response=CodeSystem.class,
			value="Retrieve the code system by id",
			notes="Retrieves the code system specified by its logical id.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{id:**}", method=RequestMethod.GET)
	public Promise<CodeSystem> getCodeSystem(
			@ApiParam(value = "The identifier of the Code System resource")
			@PathVariable(value = "id") 
			final String id,
			
			final FhirResourceSelectors selectors) {
		
		return FhirRequests.codeSystems().prepareGet(id)
				.setSummary(selectors.get_summary())
				.setElements(asList(selectors.get_elements()))
				.buildAsync()
				.execute(getBus());
		
//		ResourceURI codeSystemURI = com.b2international.snowowl.core.codesystem.CodeSystem.uri(codeSystemId);
//		ICodeSystemApiProvider codeSystemProvider = codeSystemProviderRegistry.getCodeSystemProvider(getBus(), locales, codeSystemURI);
//		CodeSystem codeSystem = codeSystemProvider.getCodeSystem(codeSystemURI);
		
//		return applyResponseContentFilter(codeSystem, fhirParameters.getA());
	}
	
}
