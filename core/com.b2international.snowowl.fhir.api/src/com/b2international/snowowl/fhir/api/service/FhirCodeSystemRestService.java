/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.eclipse.emf.common.util.URI;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.FhirUtils;
import com.b2international.snowowl.fhir.core.IFhirProvider;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest.Builder;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemUpdateRequestBuilder;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

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
 */
@Api(value ="CodeSystem")
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/CodeSystem")
public class FhirCodeSystemRestService {
	
	@ApiOperation(
			value="FHIR REST API Ping Test",
			notes="This is only an FHIR ping test.")
	@RequestMapping(value="/ping", method=RequestMethod.GET)
	public String ping() {
		System.out.println("FhirCodeSystemRestService.ping()");
		return "Ping!";
	}
	
	@ApiOperation(
			value="Retrieve the code system by its Id.",
			notes="Retrieves the code system specified by its logical id.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{codeSystemId:**}", method=RequestMethod.GET)
	public CodeSystem getCodeSystem(@PathVariable("codeSystemId") String codeSystemId) {

		Path codeSystemPath = Paths.get(codeSystemId);
		IFhirProvider fhirProvider = FhirUtils.getFhirProvider(codeSystemPath);
		CodeSystem codeSystem = fhirProvider.getCodeSystem(codeSystemPath);
		return codeSystem;
	}
	
	@ApiOperation(
			value="Retrieve all code systems",
			notes="Returns a list containing generic information about registered code systems.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public Bundle getCodeSystems() {
		
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
		ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(FhirCodeSystemRestService.class);
		java.net.URI uri = linkBuilder.toUri();
		
		com.b2international.snowowl.fhir.core.model.Bundle.Builder builder = Bundle.builder(UUID.randomUUID().toString())
			.type(BundleType.SEARCHSET)
			.addLink(uri.toString());
		
		Collection<IFhirProvider> fhirProviders = Extensions.getExtensions(FhirUtils.FHIR_EXTENSION_POINT, IFhirProvider.class);
		
		int total = 0;
		for (IFhirProvider fhirProvider : fhirProviders) {
			Collection<CodeSystem> codeSystems = fhirProvider.getCodeSystems();
			for (CodeSystem codeSystem : codeSystems) {
				String resourceUrl = uri.toString() + "/" + codeSystem.getId().getIdValue();
				Entry entry = new Entry(new Uri(resourceUrl), codeSystem);
				builder.addEntry(entry);
				total++;
			}
		}
		return builder.total(total).build();
	}
	
	/**
	 * GET-based lookup endpoint.
	 * @param code
	 * @param uri
	 * @param version
	 * @param date
	 * @param displayLanguage
	 * @param properties
	 * @throws ParseException 
	 */
	@ApiOperation(
			value="Concept lookup",
			notes="Given a code/system, or a Coding, get additional details about the concept.\n"
					+ "https://www.hl7.org/fhir/2016May/datatypes.html#dateTime")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$lookup", method=RequestMethod.GET)
	public LookupResult lookupViaParameters(
		
		@ApiParam(value="The code to look up") @RequestParam(value="code") final String code,
		@ApiParam(value="The code system uri") @RequestParam(value="uri") final String uri,
		@ApiParam(value="The code system version") @RequestParam(value="version", required=false) final String version,
		@ApiParam(value="Lookup date in datetime format") @RequestParam(value="date", required=false) final String date,
		@ApiParam(value="Language code for display") @RequestParam(value="displayLanguage", required=false) final String displayLanguage,
		@ApiParam(value="Properties to return in the output") @RequestParam(value="property", required=false) Set<String> properties) throws ParseException {
		
		System.err.println("Code: " + code + " uri: " + uri +
				" version:" + version + " lookup date: " + date + " display language: " + displayLanguage);
		
		if (properties !=null) {
			System.out.println(" properties: " + Arrays.toString(properties.toArray()));
		}
		
		Builder builder = LookupRequest.builder()
			.code(code)
			.system(uri)
			.version(version)
			.displayLanguage(displayLanguage);
		
		if (date != null) {
			builder.date(date);
		}
		
		if (properties != null) {
			builder.properties(properties);
		}
		
		//all good, now do something
		return lookup(builder.build());
	}
	
	/**
	 * POST-based lookup end-point.
	 * All parameters are in the request body.
	 * @param coding
	 * @param date
	 * @param displayLanguage
	 * @param properties
	 */
	@ApiOperation(value="Concept lookup", notes="Given a code/system, or a Coding, get additional details about the concept.\n"
					+ "https://www.hl7.org/fhir/2016May/datatypes.html#dateTime")
	
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$lookup", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public LookupResult lookupViaCodingAndParameters(
		@ApiParam(value="The lookup request parameters") 
		@Valid 
		@RequestBody 
		final LookupRequest lookupRequest) {
		
		//validate the code/system/version parameters BOTH in the request as well as possibly in the coding
		validateLookupRequest(lookupRequest);
		
		//all good, now do something
		return lookup(lookupRequest);
	}
	
	/*
	 * Perform the actual lookup by deferring the operation to the matching code system provider.
	 */
	private LookupResult lookup(LookupRequest lookupRequest) {
		
		String uriValue = lookupRequest.getSystem().getUriValue();
		IFhirProvider iFhirProvider = FhirUtils.getFhirProvider(uriValue);
		LookupResult lookupResult = iFhirProvider.lookup(lookupRequest);
		return lookupResult;
	}

	/*
	 * Cross-field validation of the incoming parameters
	 * @param lookupRequest
	 */
	private void validateLookupRequest(LookupRequest lookupRequest) {
		if (lookupRequest.getCode()!=null && lookupRequest.getSystem() == null) {
			throw new BadRequestException("Parameter 'system' is not specified while code is present in the request.", "LookupRequest.system");
		}
		
		if (lookupRequest.getCode() !=null && lookupRequest.getCoding() !=null) {
			Coding coding = lookupRequest.getCoding();
			if (!coding.getCode().equals(lookupRequest.getCode())) {
				throw new BadRequestException("Code and Coding.code are different. Probably would make sense to specify only one of them.", "LookupRequest");
			}
			
			if (!coding.getSystem().equals(lookupRequest.getSystem())) {
				throw new BadRequestException("System and Coding.system are different. Probably would make sense to specify only one of them.", "LookupRequest");
			}
			
			if (coding.getVersion() != null && lookupRequest.getVersion() == null || 
					coding.getVersion() == null && lookupRequest.getVersion() != null ||
					!coding.getVersion().equals(lookupRequest.getVersion())) {
				throw new BadRequestException("Version and Coding.version are different. Probably would make sense to specify only one of them.", "LookupRequest");
			}
		}
	}
	
	private IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
	
}
