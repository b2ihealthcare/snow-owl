/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.ResourceResponseEntry;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.provider.IValueSetApiProvider;
import com.b2international.snowowl.fhir.core.search.FhirFilterParameter;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;

import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Value Set contains codes from one or more code systems.
 *  
 * @see <a href="https://www.hl7.org/fhir/valueset.html">FHIR:ValueSet</a>
 * @see <a href="https://www.hl7.org/fhir/valueset-operations.html">FHIR:ValueSet:Operations</a>
 * @since 6.4
 */
@Tag(description = "ValueSet", name = "ValueSet")
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/ValueSet", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class FhirValueSetController extends AbstractFhirResourceController<ValueSet> {
	
	@Autowired
	private IValueSetApiProvider.Registry valueSetProviderRegistry;
	
	@Override
	protected Class<ValueSet> getModelClass() {
		return ValueSet.class;
	}
	
	/**
	 * ValueSets
	 * @param parameters - request parameters
	 * @return bundle of value sets
	 */
	@Operation(
		summary="Retrieve all value sets",
		description="Returns a collection of the supported value sets."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public Bundle getValueSets(@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Pair<Set<FhirFilterParameter>, Set<FhirSearchParameter>> requestParameters = processParameters(parameters); 
		
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
		String uri = MvcUriComponentsBuilder.fromController(FhirValueSetController.class).build().toString();
		
		Bundle.Builder builder = Bundle.builder(UUID.randomUUID().toString())
			.type(BundleType.SEARCHSET)
			.addLink(uri);
		
		int total = 0;
		
		Collection<IValueSetApiProvider> providers = valueSetProviderRegistry.getProviders(getBus(), locales);

		for (IValueSetApiProvider fhirProvider : providers) {
			Collection<ValueSet> valueSets = fhirProvider.getValueSets(requestParameters.getB());
			for (ValueSet valueSet : valueSets) {
				applyResponseContentFilter(valueSet, requestParameters.getA());
				String resourceUrl = String.join("/", uri, valueSet.getId().getIdValue());
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
	 * @param parameters - request parameters
	 * @return
	 */
	@Operation(
		summary="Retrieve the value set by id",
		description="Retrieves the value set specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@RequestMapping(value="/{valueSetId:**}", method=RequestMethod.GET)
	public MappingJacksonValue getValueSet(@PathVariable("valueSetId") String valueSetId, 
			@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Pair<Set<FhirFilterParameter>, Set<FhirSearchParameter>> fhirParameters = processParameters(parameters); 
		
		ComponentURI componentURI = ComponentURI.of(valueSetId);
		
		ValueSet valueSet = valueSetProviderRegistry
			.getValueSetProvider(getBus(), locales, componentURI) 
			.getValueSet(componentURI);

		return applyResponseContentFilter(valueSet, fhirParameters.getA());
	}
	
	/**
	 * HTTP Get request to expand the value set to return its members.
	 * @param valueSetId
	 * @return expanded {@link ValueSet}
	 */
	@Operation(
		summary="Expand a value set",
		description="Expand a value set specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@RequestMapping(value="/{valueSetId:**}/$expand", method=RequestMethod.GET)
	public ValueSet expand(@Parameter(description = "The id of the value set to expand") @PathVariable("valueSetId") String valueSetId) {
		
		ComponentURI componentURI = ComponentURI.of(valueSetId);
		
		IValueSetApiProvider valueSetProvider = valueSetProviderRegistry.getValueSetProvider(getBus(), locales, componentURI);
		ValueSet valueSet = valueSetProvider.expandValueSet(componentURI);
		
		applyEmptyContentFilter(valueSet);
		return valueSet;
	}
	
	/**
	 * HTTP Get request to expand a value set specified by its URL
	 * @param url
	 * @return expanded {@link ValueSet}
	 */
	@Operation(
		summary="Expand a value set",
		description="Expand a value set specified by its url."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@RequestMapping(value="/$expand", method=RequestMethod.GET)
	public ValueSet expandByURL(
			@Parameter(description = "Canonical URL of the value set") @RequestParam(value="url") final String url) {
		
		IValueSetApiProvider valueSetProvider = valueSetProviderRegistry.getValueSetProvider(getBus(), locales, url);
		ValueSet valueSet = valueSetProvider.expandValueSet(url);
		
		applyEmptyContentFilter(valueSet);
		return valueSet;
	}
	
	/**
	 * HTTP Post request to expand a value set
	 * @param body - FHIR parameters
	 * @return expanded {@link ValueSet}
	 */
	@Operation(
		summary="Expand a value set",
		description="Expand a value set specified by a request body."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@RequestMapping(value="/$expand", method=RequestMethod.POST, consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public ValueSet expandBodyRequest(
			@Parameter(description = "The lookup request parameters")
			@RequestBody 
			Parameters.Fhir body) {
		
		final ExpandValueSetRequest request = toRequest(body, ExpandValueSetRequest.class);
		
		if (request.getUrl() == null && request.getValueSet() == null) {
			throw new BadRequestException("Both URL and ValueSet parameters are null.", "ExpandValueSetRequest");
		}

		if (request.getUrl() == null || request.getUrl().getUriValue() == null) {
			throw new BadRequestException("Expand request URL is not defined.", "ExpandValueSetRequest");
		}
		
		if (request.getUrl() != null && 
				request.getValueSet() != null && 
				request.getUrl().getUriValue() != null &&
				request.getValueSet().getUrl().getUriValue() != null &&
				!request.getUrl().getUriValue().equals(request.getValueSet().getUrl().getUriValue())) {
			throw new BadRequestException("URL and ValueSet.URL parameters are different.", "ExpandValueSetRequest");
		}
		
		IValueSetApiProvider valueSetProvider = valueSetProviderRegistry.getValueSetProvider(getBus(), locales, request.getUrl().getUriValue());
		ValueSet valueSet = valueSetProvider.expandValueSet(request);
		
		applyEmptyContentFilter(valueSet);
		
		return valueSet;
	}
	
	/**
	 * HTTP Get request to validate that a coded value is in the set of codes allowed by a value set.
	 * The value set is identified by its Value Set ID
	 * @param valueSetId the logical ID of the valueSet
	 * @param code code to validate
	 * @param system the code system of the code to validate
	 * @param systemVersion the optional version of the code to validate
	 *
	 * @return validation results as {@link OperationOutcome}
	 */
	@Operation(
		summary="Validate a code in a value set",
		description="Validate that a coded value is in the set of codes allowed by a value set."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@RequestMapping(value="/{valueSetId:**}/$validate-code", method=RequestMethod.GET)
	public Parameters.Fhir validateCode(
			@Parameter(description = "The id of the value set to validate against") @PathVariable("valueSetId") String valueSetId, 
			@Parameter(description = "The code to to be validated") @RequestParam(value="code") final String code,
			@Parameter(description = "The system uri of the code to be validated") @RequestParam(value="system") final String system,
			@Parameter(description = "The code system version of the code to be validated") @RequestParam(value="version", required=false) final String systemVersion) {
		
		ComponentURI componentURI = ComponentURI.of(valueSetId);
		
		ValidateCodeRequest validateCodeRequest = ValidateCodeRequest.builder()
			.code(code)
			.system(system)
			.systemVersion(systemVersion)
			.build();
		
		IValueSetApiProvider valueSetProvider = valueSetProviderRegistry.getValueSetProvider(getBus(), locales, componentURI);
		ValidateCodeResult validateCodeResult = valueSetProvider.validateCode(validateCodeRequest, componentURI);
		return toResponse(validateCodeResult);
	}
	
	/**
	 * HTTP Get request to validate that a coded value is in the set of codes allowed by a value set.
	 * The value set is identified by its canonical URL (SNOMED CT for example)
	 
	 * @param url the canonical URL of the value set to validate the code against
	 * @param code code to validate
	 * @param system the code system of the code to validate
	 * @param systemVersion the optional version of the code to validate
	 * @return validation results as {@link OperationOutcome}
	 */
	@Operation(
		summary="Validate a code in a value set defined by its URL",
		description="Validate that a coded value is in the set of codes allowed by a value set."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@RequestMapping(value="/$validate-code", method=RequestMethod.GET)
	public Parameters.Fhir validateCodeByURL(
			@Parameter(description = "Canonical URL of the value set") @RequestParam(value="url") final String url,
			@Parameter(description = "The code to to be validated") @RequestParam(value="code") final String code,
			@Parameter(description = "The system uri of the code to be validated") @RequestParam(value="system") final String system,
			@Parameter(description = "The code system version of the code to be validated") @RequestParam(value="version", required=false) final String systemVersion) {
		
		IValueSetApiProvider valueSetProvider = valueSetProviderRegistry.getValueSetProvider(getBus(), locales, url);
		
		
		ValidateCodeRequest validateCodeRequest = ValidateCodeRequest.builder()
			.url(url)
			.code(code)
			.system(system)
			.systemVersion(systemVersion)
			.build();
		
		ValidateCodeResult validateCodeResult = valueSetProvider.validateCode(validateCodeRequest);
		return toResponse(validateCodeResult);
	}
	
}
