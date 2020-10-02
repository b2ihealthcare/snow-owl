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
package com.b2international.snowowl.fhir.rest;

import java.util.UUID;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.structuredefinition.StructureDefinition;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * A definition of a FHIR structure. 
 * This resource is used to describe the underlying resources, data types defined in FHIR, 
 * and also for describing extensions and constraints on resources and data types.
 *  
 * @see <a href="https://www.hl7.org/fhir/structuredefinition.html">StructureDefinition</a>
 * @since 7.1
 */
@Tag(description="FHIR StructureDefinition Resource", name = "StructureDefinition")
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/StructureDefinition", produces = { BaseFhirResourceRestService.APPLICATION_FHIR_JSON })
public class StructureDefinitionService extends BaseFhirResourceRestService<StructureDefinition> {
	
	/**
	 * StructureDefinitions
	 * @param parameters
	 * @return bundle of {@link StructureDefinition}s
	 */
	@Operation(
		summary = "Retrieve all structure definitions",
		description = "Returns a collection of the supported structure definitions."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public Bundle getStructureDefinitions(@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		SearchRequestParameters requestParameters = new SearchRequestParameters(multiMap); 
		
		String uri = MvcUriComponentsBuilder.fromController(StructureDefinitionService.class).build().toString();
		
		Bundle.Builder builder = Bundle.builder(UUID.randomUUID().toString())
			.type(BundleType.SEARCHSET)
			.addLink(uri);
		
		int total = 0;
		//TODO:
		
		return builder.total(total).build();
	}
	
	/**
	 * HTTP Get for retrieving a structure definition by its id
	 * @param structureDefinitionId
	 * @param parameters
	 * @return @link {@link StructureDefinition}
	 */
	@Operation(
		summary = "Retrieve the structure definition by id",
		description = "Retrieves the structure definition specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK"),
		@ApiResponse(responseCode = "400", description="Bad request"),
		@ApiResponse(responseCode = "404", description="Structure definition not found")
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
