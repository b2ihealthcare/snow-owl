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

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.codesystem.*;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.provider.ICodeSystemApiProvider;
import com.b2international.snowowl.fhir.core.search.FhirFilterParameter;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;

import io.swagger.annotations.*;

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
@RequestMapping(value="/CodeSystem", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class FhirCodeSystemController extends AbstractFhirResourceController<CodeSystem> {
	
	@Override
	protected Class<CodeSystem> getModelClass() {
		return CodeSystem.class;
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
		@ApiResponse(code = HTTP_OK, message = "OK")
	})
	@GetMapping
	public Bundle getCodeSystems(@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Pair<Set<FhirFilterParameter>, Set<FhirSearchParameter>> requestParameters = processParameters(parameters);
		Set<FhirFilterParameter> filterParameters = requestParameters.getA();
		
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
		String uri = MvcUriComponentsBuilder.fromController(FhirCodeSystemController.class).build().toString();
		
		Bundle.Builder builder = Bundle.builder(UUID.randomUUID().toString())
			.type(BundleType.SEARCHSET)
			.addLink(uri);
		
		int total = 0;

		//collect the hits from the providers
		Collection<ICodeSystemApiProvider> providers = codeSystemProviderRegistry.getProviders(getBus(), locales);
		
		for (ICodeSystemApiProvider codeSystemProvider : providers) {
			Collection<CodeSystem> codeSystems = codeSystemProvider.getCodeSystems(requestParameters.getB());
			for (CodeSystem codeSystem : codeSystems) {
				applyResponseContentFilter(codeSystem, filterParameters);
				String resourceUrl = String.join("/", uri, codeSystem.getId().getIdValue());
				Entry entry = new Entry(new Uri(resourceUrl), codeSystem);
				builder.addEntry(entry);
				total++;
			}
		}
		return builder.total(total).build();
	}
	
	/**
	 * HTTP Get for retrieving a code system by its code system id
	 * @param codeSystemId
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
	@RequestMapping(value="/{codeSystemId:**}", method=RequestMethod.GET)
	public MappingJacksonValue getCodeSystem(@PathVariable("codeSystemId") String codeSystemId, 
			@RequestParam(required=false) MultiValueMap<String, String> parameters) {
		
		Pair<Set<FhirFilterParameter>, Set<FhirSearchParameter>> fhirParameters = processParameters(parameters);
		
		ResourceURI codeSystemURI = com.b2international.snowowl.core.codesystem.CodeSystem.uri(codeSystemId);
		ICodeSystemApiProvider codeSystemProvider = codeSystemProviderRegistry.getCodeSystemProvider(getBus(), locales, codeSystemURI);
		CodeSystem codeSystem = codeSystemProvider.getCodeSystem(codeSystemURI);
		
		return applyResponseContentFilter(codeSystem, fhirParameters.getA());
	}
	
}
