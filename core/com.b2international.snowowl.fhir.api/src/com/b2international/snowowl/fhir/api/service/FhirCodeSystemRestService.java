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
import static java.net.HttpURLConnection.HTTP_OK;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.StringUtils;
import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.fhir.core.IFhirProvider;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.LookupRequest;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 *  Code system resource operations:
 *  <ul>
 *  <li>Concept lookup and decomposition</li>
 *  <li>Subsumption testing</li>
 *  <li>Code composition based on supplied properties</li>
 *  </ul>
 *  
 *  200 - OK
 *  400 - Bad Request
 *  
 *  
 *  @see <a href="https://www.hl7.org/fhir/codesystem-operations.html">FHIR:CodeSystem:Operations</a>
 * 
 */
@Api("Code Systems")
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/CodeSystem")
public class FhirCodeSystemRestService {
	
	private static final String FHIR_EXTENSION_POINT = "com.b2international.snowowl.fhir.api.provider"; //$NON-NLS-N$
	
	@ApiOperation(
			value="FHIR REST API Ping Test",
			notes="This is only an FHIR ping test.")
	@RequestMapping(value="/ping", method=RequestMethod.GET)
	public String ping() {
		System.out.println("FhirCodeSystemRestService.ping()");
		return "Ping!";
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
		@ApiResponse(code = 404, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$lookup", method=RequestMethod.GET)
	public void lookupViaParameters(
		
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
		
		LookupRequest lookupRequest = LookupRequest.builder()
			.code(code)
			.system(uri)
			.version(version)
			.date(date)
			.displayLanguage(displayLanguage)
			.properties(properties)
			.build();
		
		//all good, now do something
		lookup(lookupRequest);
	}
	
	/**
	 * POST-based lookup endpoint.
	 * All parameters are in the request body.
	 * @param coding
	 * @param date
	 * @param displayLanguage
	 * @param properties
	 */
	@ApiOperation(value="Concept lookup", notes="Given a code/system, or a Coding, get additional details about the concept.\n"
					+ "https://www.hl7.org/fhir/2016May/datatypes.html#dateTime")
	
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$lookup", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void lookupViaCodingAndParameters(
		@ApiParam(value="The lookup request parameters") 
		@Valid 
		@RequestBody 
		final LookupRequest lookupRequest) {
		
		//validate the code/system/version parameters BOTH in the request as well as possibly in the coding
		crossFieldValidate(lookupRequest);
		
		//all good, now do something
		lookup(lookupRequest);
	}
	
	private void lookup(LookupRequest lookupRequest) {
		
		Collection<IFhirProvider> fhirProviders = Extensions.getExtensions(FHIR_EXTENSION_POINT, IFhirProvider.class);
		
		Optional<IFhirProvider> fhirProviderOptional = fhirProviders.stream().findFirst();
		
		fhirProviderOptional.orElseThrow(() -> new BadRequestException("Did not find FHIR provider for URI: " + lookupRequest.getSystem()));
		
		IFhirProvider iFhirProvider = fhirProviderOptional.get();
		iFhirProvider.lookup(lookupRequest.getVersion(), lookupRequest.getCode().getCodeValue());
		
	}
	
	/**
	 * @param lookupRequest
	 */
	private void crossFieldValidate(LookupRequest lookupRequest) {
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

}
