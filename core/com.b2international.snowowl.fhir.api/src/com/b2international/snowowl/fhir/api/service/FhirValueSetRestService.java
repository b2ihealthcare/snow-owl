/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
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
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.provider.IValueSetApiProvider;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Value Set contains codes from one or more code systems.
 *  
 * @see <a href="https://www.hl7.org/fhir/valueset.html">FHIR:ValueSet</a>
 * @see <a href="https://www.hl7.org/fhir/valueset-operations.html">FHIR:ValueSet:Operations</a>
 * @since 6.4
 */
@Api(value = "ValueSet", description="FHIR ValueSet Resource", tags = { "ValueSet" })
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/ValueSet", produces = { BaseFhirResourceRestService.APPLICATION_FHIR_JSON })
public class FhirValueSetRestService extends BaseFhirResourceRestService<ValueSet> {
	
	/**
	 * ValueSets
	 * @param request parameters
	 * @return bundle of value sets
	 */
	@ApiOperation(
			value="Retrieve all value sets",
			notes="Returns a collection of the supported value sets.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public Bundle getValueSets(@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		SearchRequestParameters requestParameters = new SearchRequestParameters(multiMap); 
		
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
		ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(FhirValueSetRestService.class);
		String uri = linkBuilder.toUri().toString();
		
		Bundle.Builder builder = Bundle.builder(UUID.randomUUID().toString())
			.type(BundleType.SEARCHSET)
			.addLink(uri);
		
		int total = 0;
		for (IValueSetApiProvider fhirProvider : IValueSetApiProvider.Registry.getProviders()) {
			Collection<ValueSet> valueSets = fhirProvider.getValueSets();
			for (ValueSet valueSet : valueSets) {
				applyResponseContentFilter(valueSet, requestParameters);
				
				String resourceUrl = String.format("%s/%s", uri, valueSet.getId().getIdValue());
				
				Entry entry = new Entry(new Uri(resourceUrl), valueSet);
				builder.addEntry(entry);
				total++;
			}
		}
		return builder.total(total).build();
	}
	
	/**
	 * HTTP Get for retrieving a value set by its value set id
	 * @param valueSetId
	 * @param request parameters
	 * @return
	 */
	@ApiOperation(
			response=CodeSystem.class,
			value="Retrieve the value set by id",
			notes="Retrieves the value set specified by its logical id.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{valueSetId:**}", method=RequestMethod.GET)
	public MappingJacksonValue getValueSet(@PathVariable("valueSetId") String valueSetId, 
			@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		SearchRequestParameters requestParameters = new SearchRequestParameters(multiMap);
		
		LogicalId logicalId = LogicalId.fromIdString(valueSetId);
		ValueSet valueSet = IValueSetApiProvider.Registry
			.getValueSetProvider(logicalId) 
			.getValueSet(logicalId);

		return applyResponseContentFilter(valueSet, requestParameters);
	}
	
	
}
