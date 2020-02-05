/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.provider.ICodeSystemApiProvider;
import com.b2international.snowowl.fhir.core.search.SearchRequestParameters;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
 * @since 6.4
 * 
 */
@Api(value = "CodeSystem", description="FHIR CodeSystem Resource", tags = { "CodeSystem" })
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/CodeSystem", produces = { BaseFhirResourceRestService.APPLICATION_FHIR_JSON })
public class FhirCodeSystemRestService extends BaseFhirResourceRestService<CodeSystem> {
	
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
		
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		SearchRequestParameters requestParameters = new SearchRequestParameters(multiMap); 
		
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
		String uri = MvcUriComponentsBuilder.fromController(FhirCodeSystemRestService.class).build().toString();
		
		Bundle.Builder builder = Bundle.builder(UUID.randomUUID().toString())
			.type(BundleType.SEARCHSET)
			.addLink(uri);
		
		int total = 0;
		
		//single code system
		String id = requestParameters.getId();
		if (id != null) {
			CodeSystem codeSystem = getCodeSystemById(id);
			applyResponseContentFilter(codeSystem, requestParameters);
			String resourceUrl = String.format("%s/%s", uri, codeSystem.getId().getIdValue());
			Entry entry = new Entry(new Uri(resourceUrl), codeSystem);
			builder.addEntry(entry);
			total = 1;
		
		//all code systems
		} else {
			for (ICodeSystemApiProvider fhirProvider : ICodeSystemApiProvider.Registry.getProviders(getBus(), locales)) {
				Collection<CodeSystem> codeSystems = fhirProvider.getCodeSystems();
				total = total + applySearchParameters(builder, uri, codeSystems,requestParameters);
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
		
		Multimap<String, String> multiMap = HashMultimap.create();
		parameters.keySet().forEach(k -> multiMap.putAll(k, parameters.get(k)));
		SearchRequestParameters requestParameters = new SearchRequestParameters(multiMap); 
		
		CodeSystem codeSystem = getCodeSystemById(codeSystemId);
		return applyResponseContentFilter(codeSystem, requestParameters);
	}
	
	/**
	 * GET-based FHIR lookup endpoint.
	 * @param code
	 * @param system
	 * @param version
	 * @param date
	 * @param displayLanguage
	 * @param properties
	 * @throws ParseException 
	 */
	@ApiOperation(
			value="Concept lookup and decomposition",
			notes="Given a code/version/system, or a Coding, get additional details about the concept.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$lookup", method=RequestMethod.GET)
	public Parameters.Fhir lookup(
		
		@ApiParam(value="The code to look up") @RequestParam(value="code") final String code,
		@ApiParam(value="The code system's uri") @RequestParam(value="system") final String system,
		@ApiParam(value="The code system version") @RequestParam(value="version") final Optional<String> version,
		@ApiParam(value="Lookup date in datetime format") @RequestParam(value="date") final Optional<String> date,
		@ApiParam(value="Language code for display") @RequestParam(value="displayLanguage") final Optional<String> displayLanguage,
		
		//Collection binding does not work with Optional!! (Optional<Set<String>> properties does not get populated with multiple properties, only the first one is present!)
		@ApiParam(value="Properties to return in the output") @RequestParam(value="property", required = false) Set<String> properties) {
		
		Builder builder = LookupRequest.builder()
			.code(code)
			.system(system);
		
		if (version.isPresent()) {
			builder.version(version.get());
		}
		
		if (date.isPresent()) {
			builder.date(date.get());
		}

		if (displayLanguage.isPresent()) {
			builder.displayLanguage(displayLanguage.get());
		}

		if (properties != null && !properties.isEmpty()) {
			builder.properties(properties);
		}
		
		//all good, now do something
		return toResponse(lookup(builder.build()));
	}
	
	/**
	 * POST-based lookup end-point.
	 * All parameters are in the request body.
	 * @param in - FHIR parameters
	 */
	@ApiOperation(value="Concept lookup and decomposition", notes="Given a code/version/system, or a Coding, get additional details about the concept.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$lookup", method=RequestMethod.POST, consumes = BaseFhirResourceRestService.APPLICATION_FHIR_JSON)
	public Parameters.Fhir lookup(
			@ApiParam(name = "body", value = "The lookup request parameters")
			@RequestBody Parameters.Fhir in) {
		
		final LookupRequest req = toRequest(in, LookupRequest.class);
		
		LookupResult result = lookup(req);
		return toResponse(result);
	}

	/*
	 * Subsumes GET method with no codeSystemId and parameters
	 */
	@ApiOperation(
			value="Subsumption testing",
			notes="Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning).")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$subsumes", method=RequestMethod.GET)
	public Parameters.Fhir subsumes(
			@ApiParam(value="The \"A\" code that is to be tested") @RequestParam(value="codeA") final String codeA,
			@ApiParam(value="The \"B\" code that is to be tested") @RequestParam(value="codeB") final String codeB,
			@ApiParam(value="The code system's uri") @RequestParam(value="system") final String system,
			@ApiParam(value="The code system version") @RequestParam(value="version", required=false) final String version) {
		
		validateSubsumptionRequest(codeA, codeB, system, version);
		
		final SubsumptionRequest req = SubsumptionRequest.builder()
				.codeA(codeA)
				.codeB(codeB)
				.system(system)
				.version(version)
				.build();
		
		ICodeSystemApiProvider codeSystemProvider = ICodeSystemApiProvider.Registry.getCodeSystemProvider(getBus(), locales, req.getSystem());
		final SubsumptionResult result = codeSystemProvider.subsumes(req);
		
		return toResponse(result);
	}
	
	/*
	 * Subsumes GET method with codeSystemId and parameters
	 */
	@ApiOperation(
			value="Subsumption testing",
			notes="Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning).")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="{codeSystemId:**}/$subsumes", method=RequestMethod.GET)
	public Parameters.Fhir subsumes(
			@ApiParam(value="The id of the code system to invoke the operation on") 	@PathVariable("codeSystemId") String codeSystemId,
			@ApiParam(value="The \"A\" code that is to be tested") @RequestParam(value="codeA") final String codeA,
			@ApiParam(value="The \"B\" code that is to be tested") @RequestParam(value="codeB") final String codeB,
			@ApiParam(value="The code system's uri") @RequestParam(value="system") final String system,
			@ApiParam(value="The code system version") @RequestParam(value="version", required=false) final String version	) {
		
		validateSubsumptionRequest(codeSystemId, codeA, codeB, system, version);
		
		final SubsumptionRequest req = SubsumptionRequest.builder()
			.codeA(codeA)
			.codeB(codeB)
			.system(codeSystemId)
			.version(version)
			.build();
		
		ICodeSystemApiProvider codeSystemProvider = ICodeSystemApiProvider.Registry.getCodeSystemProvider(getBus(), locales, req.getSystem());
		final SubsumptionResult result = codeSystemProvider.subsumes(req);
		
		return toResponse(result);
	}
	
	/*
	 * Subsumes POST method without codeSystemId and body
	 */
	@ApiOperation(value="Subsumption testing", notes="Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning).")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$subsumes", method=RequestMethod.POST, consumes = BaseFhirResourceRestService.APPLICATION_FHIR_JSON)
	public Parameters.Fhir subsumes(
			@ApiParam(name = "body", value = "The lookup request parameters")
			@RequestBody Parameters.Fhir in) {
		
		SubsumptionRequest request = toRequest(in, SubsumptionRequest.class);
		
		validateSubsumptionRequest(request);
		
		ICodeSystemApiProvider codeSystemProvider = ICodeSystemApiProvider.Registry.getCodeSystemProvider(getBus(), locales, request.getSystem());
		SubsumptionResult result = codeSystemProvider.subsumes(request);
		return toResponse(result);
	}
	
	/*
	 * Subsumes POST method with code system as path parameter
	 */
	@ApiOperation(value="Subsumption testing", notes="Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning).")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@RequestMapping(value="{codeSystemId:**}/$subsumes", method=RequestMethod.POST, consumes = BaseFhirResourceRestService.APPLICATION_FHIR_JSON)
	public Parameters.Fhir subsumes(
			@ApiParam(value="The id of the code system to invoke the operation on") 	@PathVariable("codeSystemId") String codeSystemId,
			@ApiParam(name = "body", value = "The lookup request parameters") @RequestBody Parameters.Fhir in) {
		
		SubsumptionRequest request = toRequest(in, SubsumptionRequest.class);
		
		validateSubsumptionRequest(request);
		
		SubsumptionResult result = ICodeSystemApiProvider.Registry.getCodeSystemProvider(getBus(), locales, request.getSystem()).subsumes(request);
		return toResponse(result);
	}
	
	@ApiOperation(
			value="FHIR REST API Ping Test",
			notes="This is only an FHIR ping test.")
	@RequestMapping(value="/ping", method=RequestMethod.GET)
	public String ping() {
		System.out.println("ServeFhirCodeSystemRestService.ping()");
		return "Ping!";
	}
	
	private CodeSystem getCodeSystemById(String codeSystemId) {
		LogicalId logicalId = LogicalId.fromIdString(codeSystemId);
		ICodeSystemApiProvider codeSystemProvider = ICodeSystemApiProvider.Registry.getCodeSystemProvider(getBus(), locales, logicalId);
		CodeSystem codeSystem = codeSystemProvider.getCodeSystem(logicalId);
		return codeSystem;
	}
	
	/*
	 * Perform the actual lookup by deferring the operation to the matching code system provider.
	 */
	private LookupResult lookup(LookupRequest lookupRequest) {
		ICodeSystemApiProvider codeSystemProvider = ICodeSystemApiProvider.Registry.getCodeSystemProvider(getBus(), locales, lookupRequest.getSystem());
		return codeSystemProvider.lookup(lookupRequest);
	}
	
	private void validateSubsumptionRequest(String codeA, String codeB, String system, String version) {
		validateSubsumptionRequest(null, codeA,  codeB, system, version);
	}
	
	private void validateSubsumptionRequest(SubsumptionRequest request) {
		validateSubsumptionRequest(null, request);
		
	}
	
	private void validateSubsumptionRequest(String codeSystemId, SubsumptionRequest request) {
		validateSubsumptionRequest(codeSystemId, request.getCodeA(), request.getCodeB(), request.getSystem(), request.getVersion(), request.getCodingA(), request.getCodingB());
		
	}

	private void validateSubsumptionRequest(String codeSystemId, String codeA, String codeB, String system, String version) {
		validateSubsumptionRequest(codeSystemId, codeA, codeB, system, version, null, null);
	}
	
	private void validateSubsumptionRequest(String codeSystemId, String codeA, String codeB, String system, String version, Coding codingA, Coding codingB) {
		
		//check the systems
		if (StringUtils.isEmpty(system) && StringUtils.isEmpty(codeSystemId)) {
			throw new BadRequestException("Parameter 'system' is not specified for subsumption testing.", "SubsumptionRequest.system");
		}
		
		if (!StringUtils.isEmpty(system) && !StringUtils.isEmpty(codeSystemId)) {
			if (!codeSystemId.equals(system)) {
				throw new BadRequestException(String.format("Parameter 'system: %s' and path parameter 'codeSystem: %s' are not the same.", system, codeSystemId), "SubsumptionRequest.system");
			}
		}
		
		//all empty
		if (StringUtils.isEmpty(codeA) && StringUtils.isEmpty(codeA) && codingA == null && codingB == null) {
			throw new BadRequestException("No codes or Codings are provided for subsumption testing.", "SubsumptionRequest");
		}
		
		//No codes
		if (StringUtils.isEmpty(codeA) && StringUtils.isEmpty(codeA)) {
			if (codingA == null || codingB == null) {
				throw new BadRequestException("No Codings are provided for subsumption testing.", "SubsumptionRequest.Coding");
			}
		}
		
		//No codings
		if (codingA == null && codingB == null) {
			if (StringUtils.isEmpty(codeA) || StringUtils.isEmpty(codeB)) {
				throw new BadRequestException("No codes are provided for subsumption testing.", "SubsumptionRequest.code");
			}
		}
		
		//Codes are there
		if (!StringUtils.isEmpty(codeA) && !StringUtils.isEmpty(codeA)) {
			if (codingA != null || codingB != null) {
				throw new BadRequestException("Provide either codes or Codings.", "SubsumptionRequest");
			}
		}
		
		//Coding are there
		if (codingA != null && codingB != null) {
			if (!StringUtils.isEmpty(codeA) || !StringUtils.isEmpty(codeA)) {
				throw new BadRequestException("Provide either codes or Codings.", "SubsumptionRequest");
			}
		}
	}
	
}
