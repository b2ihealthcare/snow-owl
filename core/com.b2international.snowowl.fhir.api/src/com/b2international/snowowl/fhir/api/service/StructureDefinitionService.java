/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.service;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.UUID;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.structuredefinition.StructureDefinition;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * A definition of a FHIR structure. 
 * This resource is used to describe the underlying resources, data types defined in FHIR, 
 * and also for describing extensions and constraints on resources and data types.
 *  
 * @see <a href="https://www.hl7.org/fhir/structuredefinition.html">StructureDefinition</a>
 * @since 7.1
 */
@Api(value = "StructureDefinition", description="FHIR StructureDefinition Resource", tags = { "StructureDefinition" })
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/StructureDefinition", produces = { BaseFhirResourceRestService.APPLICATION_FHIR_JSON })
public class StructureDefinitionService extends BaseFhirResourceRestService<StructureDefinition> {
	
	/**
	 * StructureDefinitions
	 * @param request parameters
	 * @return bundle of {@link StructureDefinition}s
	 */
	@ApiOperation(
		value="Retrieve all structure definitions",
		notes="Returns a collection of the supported structure definitions.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public Bundle getStructureDefinitions(@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		SearchRequestParameters requestParameters = new SearchRequestParameters(multiMap); 
		
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
		ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(StructureDefinitionService.class);
		String uri = linkBuilder.toUri().toString();
		
		Bundle.Builder builder = Bundle.builder(UUID.randomUUID().toString())
			.type(BundleType.SEARCHSET)
			.addLink(uri);
		
		int total = 0;
		//TODO:
		
		return builder.total(total).build();
	}
	
	/**
	 * HTTP Get for retrieving a structure definition by its id
	 * @param structure definition id
	 * @param request parameters
	 * @return @link {@link StructureDefinition}
	 */
	@ApiOperation(
		response=StructureDefinition.class,
		value="Retrieve the structure definition by id",
		notes="Retrieves the structure definition specified by its logical id.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Structure definition not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{structureDefinitionId:**}", method=RequestMethod.GET)
	public MappingJacksonValue getStructureDefinition(@PathVariable("structureDefinitionId") String structureDefinitionId, 
			@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		SearchRequestParameters requestParameters = new SearchRequestParameters(multiMap);
		
		LogicalId logicalId = LogicalId.fromIdString(structureDefinitionId);
		
		StructureDefinition structureDefinition = StructureDefinition.builder(logicalId.toString()).build();

		return applyResponseContentFilter(structureDefinition, requestParameters);
	}
	
}
