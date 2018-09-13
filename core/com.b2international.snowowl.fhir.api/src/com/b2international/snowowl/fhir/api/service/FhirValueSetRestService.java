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
import static java.net.HttpURLConnection.HTTP_OK;

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
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest.Builder;
import com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.provider.IValueSetApiProvider;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
	
	/**
	 * HTTP Get request to expand the value set to return its members.
	 * @param valueSetId
	 * @return expanded {@link ValueSet}
	 */
	@ApiOperation(
			response=ValueSet.class,
			value="Expand a value set",
			notes="Expand a value set specified by its logical id.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Value set not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{valueSetId:**}/$expand", method=RequestMethod.GET)
	public ValueSet expand(
			@ApiParam(value="The id of the value set to expand") @PathVariable("valueSetId") String valueSetId) {
		
		LogicalId logicalId = LogicalId.fromIdString(valueSetId);
		
		IValueSetApiProvider valueSetProvider = IValueSetApiProvider.Registry.getValueSetProvider(logicalId);
		ValueSet valueSet = valueSetProvider.expandValueSet(logicalId);
		return valueSet;
	}
	
	/**
	 * HTTP Get request to expand a value set specified by its URL
	 * @param url
	 * @return expanded {@link ValueSet}
	 */
	@ApiOperation(
			value="Expand a value set",
			notes="Expand a value set specified by its url.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Value set not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$expand", method=RequestMethod.GET)
	public ValueSet expandByURL(
			@ApiParam(value="Canonical URL of the value set") @RequestParam(value="url") final String url) {
		
		IValueSetApiProvider valueSetProvider = IValueSetApiProvider.Registry.getValueSetProvider(url);
		ValueSet valueSet = valueSetProvider.expandValueSet(url);
		return valueSet;
	}
	
	/**
	 * HTTP Get request to validate that a coded value is in the set of codes allowed by a value set.
	 * @param valueSetId
	 * @return validation results as {@link OperationOutcome}
	 */
	@ApiOperation(
			response=ValueSet.class,
			value="Validate a code in a value set",
			notes="Validate that a coded value is in the set of codes allowed by a value set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Value set not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{valueSetId:**}/$validate-code", method=RequestMethod.GET)
	public Parameters.Fhir validateCode(
			@ApiParam(value="The id of the value set to validate") @PathVariable("valueSetId") String valueSetId, 
			@ApiParam(value="The code to to be validated") @RequestParam(value="code") final String code,
			@ApiParam(value="The system uri of the code to be validated") @RequestParam(value="system") final String system,
			@ApiParam(value="The code system version of the code to be validated") @RequestParam(value="version", required=false) final String version)
	{
		
		LogicalId logicalId = LogicalId.fromIdString(valueSetId);
		System.out.println("Logical Id: " + logicalId);
		
		ValidateCodeRequest validateCodeRequest = ValidateCodeRequest.builder()
			.code(code)
			.system(system)
			.version(version)
			.build();
		
		IValueSetApiProvider valueSetProvider = IValueSetApiProvider.Registry.getValueSetProvider(logicalId);
		ValidateCodeResult validateCodeResult = valueSetProvider.validateCode(validateCodeRequest, logicalId);
		return toResponse(validateCodeResult);
	}
	
	
}
