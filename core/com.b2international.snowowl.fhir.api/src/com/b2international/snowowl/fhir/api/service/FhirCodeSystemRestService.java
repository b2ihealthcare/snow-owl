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

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.StringUtils;
import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.api.model.dt.Coding;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * 
 *  Code system resource operations:
 *  <ul>
 *  <li>Concept lookup and decomposition</li>
 *  <li>Subsumption testing</li>
 *  <li>Code composition based on supplied properties</li>
 *  </ul>
 *  @see <a href="https://www.hl7.org/fhir/codesystem-operations.html">FHIR:CodeSystem:Operations</a>
 * 
 */
@Api("Code Systems")
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/CodeSystem")
public class FhirCodeSystemRestService {
	
	private static final String DATE_TIME_REGEXP = "-?[0-9]{4}(-(0[1-9]|1[0-2])(-(0[0-9]|[1-2][0-9]|3[0-1]))?)?"; //$NON-NLS-N$
	private static final String FHIR_EXTENSION_POINT = "com.b2international.snowowl.fhir.api.provider"; //$NON-NLS-N$
	
	@ApiOperation(
			value="FHIR REST API Ping Test",
			notes="This is only an FHIR ping test.")
	@RequestMapping(value="/ping", method=RequestMethod.GET)
	public String ping() {
		System.err.println("FHIR Rest service called.");
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
	 */
	@ApiOperation(
			value="Concept lookup",
			notes="Given a code/system, or a Coding, get additional details about the concept.\n"
					+ "https://www.hl7.org/fhir/2016May/datatypes.html#dateTime")
	@RequestMapping(value="/$lookup", method=RequestMethod.GET)
	public void lookupViaParameters(
		
		@ApiParam(value="The code to look up") @RequestParam(value="code") final String code,
		@ApiParam(value="The code system uri") @RequestParam(value="uri") final String uri,
		@ApiParam(value="The code system version") @RequestParam(value="version", required=false) final String version,
		@ApiParam(value="Lookup date in datetime format") @RequestParam(value="date", required=false) final String date,
		@ApiParam(value="Language code for display") @RequestParam(value="displayLanguage", required=false) final String displayLanguage,
		@ApiParam(value="Properties to return in the output") @RequestParam(value="property", required=false) Set<String> properties) {
		
		System.err.println("Code: " + code + " uri: " + uri +
				" version:" + version + " lookup date: " + date + " display language: " + displayLanguage);
		
		if (properties !=null) {
			System.out.println(" properties: " + Arrays.toString(properties.toArray()));
		}
		
		Coding coding = Coding.builder()
			.code(code)
			.system(uri)
			.version(version)
			.build();
		
		//validateParameters(coding, date, displayLanguage);
		
		//all good, now do something
		lookup(coding);
	}
	
	/**
	 * POST-based 'mixed' lookup endpoint.
	 * TODO: This should probably be removed.
	 * @param coding
	 * @param date
	 * @param displayLanguage
	 * @param properties
	 */
	@ApiOperation(
			value="Concept lookup",
			notes="Given a code/system, or a Coding, get additional details about the concept.\n"
					+ "https://www.hl7.org/fhir/2016May/datatypes.html#dateTime")
	@RequestMapping(value="/$lookup", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void lookupViaCodingAndParameters(
		
		@ApiParam(value="The coding definition to look up") @RequestBody final Coding coding,
		@ApiParam(value="Lookup date in datetime format") @RequestParam(value="date", required=false) final String date,
		@ApiParam(value="Language code for display") @RequestParam(value="displayLanguage", required=false) final String displayLanguage,
		@ApiParam(value="Properties to return in the output") @RequestParam(value="property", required=false) Set<String> properties) {
		
		System.err.println("Coding: " + coding + ", lookup date: " + date + " properties: ");
		if (properties !=null) {
			System.out.println(" properties: " + Arrays.toString(properties.toArray()));
		}
		
		//validateParameters(coding, date, displayLanguage);
		
		//all good, now do something
		lookup(coding);
	}
	
	private void lookup(Coding coding) {
		
		Collection<IFhirProvider> fhirProviders = Extensions.getExtensions(FHIR_EXTENSION_POINT, IFhirProvider.class);
		
		Optional<IFhirProvider> fhirProviderOptional = fhirProviders.stream().findFirst();
		
		fhirProviderOptional.orElseThrow(() -> new BadRequestException("Did not find FHIR provider for URI: " + coding.getSystem()));
		
		IFhirProvider iFhirProvider = fhirProviderOptional.get();
		iFhirProvider.lookup(coding.getVersion(), coding.getCode());
		
	}
	
//	private void validateParameters(Coding coding, String date, String displayLanguage) {
//		coding.validate();
//		
//		if (!StringUtils.isEmpty(date) && !date.matches(DATE_TIME_REGEXP)) {
//			throw new BadRequestException("Date format is incorrect.");
//		}
//		
//		if (!StringUtils.isEmpty(displayLanguage) && displayLanguage.matches(Coding.CODE_REGEXP)) {
//			throw new BadRequestException("Display language code format is incorrect: " + displayLanguage);
//		}
//		
//	}



}
