/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;
import com.b2international.snowowl.fhir.core.model.codesystem.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.provider.ICodeSystemApiProvider;
import com.b2international.snowowl.fhir.core.search.FhirFilterParameter;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;

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
	
	@Autowired
	private ICodeSystemApiProvider.Registry codeSystemProviderRegistry;
	
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
		String uri = MvcUriComponentsBuilder.fromController(FhirCodeSystemRestService.class).build().toString();
		
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
		
		CodeSystemURI codeSystemURI = new CodeSystemURI(codeSystemId);
		ICodeSystemApiProvider codeSystemProvider = codeSystemProviderRegistry.getCodeSystemProvider(getBus(), locales, codeSystemURI);
		CodeSystem codeSystem = codeSystemProvider.getCodeSystem(codeSystemURI);
		
		return applyResponseContentFilter(codeSystem, fhirParameters.getA());
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
	
	/**
	 * HTTP Get request to validate that a coded value is in the code system.
	 * The code system is identified by its Code System ID
	 * If the operation is not called at the instance level, one of the parameters "url" or "codeSystem" must be provided. 
	 * The operation returns a result (true / false), an error message, and the recommended display for the code.
     * When invoking this operation, a client SHALL provide one (and only one) of the parameters (code+system, coding, or codeableConcept). 
     * Other parameters (including version and display) are optional.
	 * 
	 * @param codeSystemUri the code system to validate against
	 * @param code to code to validate
	 * @param version the version of the code system to validate against
	 * @param date the date for which the validation should be checked
	 * @param abstract If this parameter has a value of true, the client is stating that the validation is being performed in a context 
	 * 			where a concept designated as 'abstract' is appropriate/allowed.
	 *
	 * @return validation results as {@link OperationOutcome}
	 */
	@ApiOperation(
			response=ValueSet.class,
			value="Validate a code in a code system",
			notes="Validate that a coded value is in a code system.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{codeSystemId:**}/$validate-code", method=RequestMethod.GET)
	public Parameters.Fhir validateCode(
			@ApiParam(value="The id of the code system to validate against") @PathVariable("codeSystemId") String codeSystemId, 
			@ApiParam(value="The code to be validated") @RequestParam(value="code") final String code,
			@ApiParam(value="The version of the code system") @RequestParam(value="version") final Optional<String> version,
			@ApiParam(value="The display string of the code") @RequestParam(value="display") final Optional<String> display,
			@ApiParam(value="The date stamp of the code system to validate against") @RequestParam(value="date") final Optional<String> date,
			@ApiParam(value="The abstract status of the code") @RequestParam(value="abstract") final Optional<Boolean> isAbstract) {
		
		CodeSystemURI codeSystemUri = new CodeSystemURI(codeSystemId);
		
		ValidateCodeRequest.Builder builder = ValidateCodeRequest.builder();
		
		builder.code(code)
				.version(version.orElse(null))
				.display(display.orElse(null))
				.isAbstract(isAbstract.orElse(null));
		
		if (date.isPresent()) {
			builder.date(date.get());
		}
				
		ValidateCodeRequest validateCodeRequest = builder.build();
		
		ICodeSystemApiProvider codeSystemProvider = codeSystemProviderRegistry.getCodeSystemProvider(getBus(), locales, codeSystemUri);
		ValidateCodeResult result = codeSystemProvider.validateCode(codeSystemUri, validateCodeRequest);
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
		
		ICodeSystemApiProvider codeSystemProvider = codeSystemProviderRegistry.getCodeSystemProvider(getBus(), locales, req.getSystem());
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
		
		ICodeSystemApiProvider codeSystemProvider = codeSystemProviderRegistry.getCodeSystemProvider(getBus(), locales, req.getSystem());
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
		
		ICodeSystemApiProvider codeSystemProvider = codeSystemProviderRegistry.getCodeSystemProvider(getBus(), locales, request.getSystem());
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
		
		SubsumptionResult result = codeSystemProviderRegistry.getCodeSystemProvider(getBus(), locales, request.getSystem()).subsumes(request);
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
	
	/*
	 * Perform the actual lookup by deferring the operation to the matching code system provider.
	 */
	private LookupResult lookup(LookupRequest lookupRequest) {
		ICodeSystemApiProvider codeSystemProvider = codeSystemProviderRegistry.getCodeSystemProvider(getBus(), locales, lookupRequest.getSystem());
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
		
		//TODO: this probably incorrect as codeSystemId is an internal id vs. system that is external
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
