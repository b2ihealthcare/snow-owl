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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.fhir.core.IFhirProvider;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest.Builder;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;

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
 */
@Api(value = "CodeSystem", description="FHIR CodeSystem Resource", tags = { "CodeSystem" })
@RestController //no need for method level @ResponseBody annotations
@RequestMapping(value="/CodeSystem", produces = { BaseFhirRestService.APPLICATION_FHIR_JSON })
public class FhirCodeSystemRestService extends BaseFhirRestService {
	
	/**
	 * CodeSystems
	 * @param _summary
	 * @param _elements
	 * @return bundle of code systems
	 */
	@ApiOperation(
			value="Retrieve all code systems",
			notes="Returns a collection of the supported code systems.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public Bundle getCodeSystems(@RequestParam(required=false) String _summary,
			@RequestParam(required=false) List<String> _elements) {
		
		validateSearchParams(_summary, _elements);
		
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
		ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(FhirCodeSystemRestService.class);
		String uri = linkBuilder.toUri().toString();
		
		Bundle.Builder builder = Bundle.builder(UUID.randomUUID().toString())
			.type(BundleType.SEARCHSET)
			.addLink(uri);
		
		int total = 0;
		for (IFhirProvider fhirProvider : IFhirProvider.Registry.getProviders()) {
			Collection<CodeSystem> codeSystems = fhirProvider.getCodeSystems();
			for (CodeSystem codeSystem : codeSystems) {
				applyResponseFilter(_summary, _elements, codeSystem);
				String resourceUrl = String.format("%s/%s", uri, codeSystem.getId().getIdValue());
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
	 * @param _summary
	 * @param _elements
	 * @return
	 */
	@ApiOperation(
			response=CodeSystem.class,
			value="Retrieve the code system by id",
			notes="Retrieves the code system specified by its logical id.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@RequestMapping(value="/{codeSystemId:**}", method=RequestMethod.GET)
	public MappingJacksonValue getCodeSystem(@PathVariable("codeSystemId") String codeSystemId, 
			@RequestParam(required=false) String _summary,
			@RequestParam(required=false) List<String> _elements) {
		
		validateSearchParams(_summary, _elements);

		Path codeSystemPath = Paths.get(codeSystemId);
		CodeSystem codeSystem = IFhirProvider.Registry
			.getFhirProvider(codeSystemPath)
			.getCodeSystem(codeSystemPath);

		return applyResponseFilter(_summary, _elements, codeSystem);
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
		@ApiParam(value="The code system version") @RequestParam(value="version", required=false) final String version,
		@ApiParam(value="Lookup date in datetime format") @RequestParam(value="date", required=false) final String date,
		@ApiParam(value="Language code for display") @RequestParam(value="displayLanguage", required=false) final String displayLanguage,
		@ApiParam(value="Properties to return in the output") @RequestParam(value="property", required=false) Set<String> properties) throws ParseException {
		
		Builder builder = LookupRequest.builder()
			.code(code)
			.system(system)
			.version(version)
			.displayLanguage(displayLanguage)
			.properties(properties);
		
		if (date != null) {
			builder.date(date);
		}
		
		//all good, now do something
		return toResponse(lookup(builder.build()));
	}
	
	/**
	 * POST-based lookup end-point.
	 * All parameters are in the request body.
	 * @param coding
	 * @param date
	 * @param displayLanguage
	 * @param properties
	 */
	@ApiOperation(value="Concept lookup and decomposition", notes="Given a code/verion/system, or a Coding, get additional details about the concept.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@RequestMapping(value="/$lookup", method=RequestMethod.POST, consumes = BaseFhirRestService.APPLICATION_FHIR_JSON)
	public Parameters.Fhir lookup(
			@ApiParam(name = "body", value = "The lookup request parameters")
			@RequestBody
			Parameters.Fhir in) {
		final LookupRequest req = toRequest(in, LookupRequest.class);

		//validate the code/system/version parameters BOTH in the request as well as possibly in the coding
		validateLookupRequest(req);
		
		//all good, now do something
		LookupResult result = lookup(req);
		
		return toResponse(result);
	}
	
	/*
	@ApiOperation(
			value="FHIR REST API Ping Test",
			notes="This is only an FHIR ping test.")
	@RequestMapping(value="/ping", method=RequestMethod.GET)
	public String ping() {
		System.out.println("FhirCodeSystemRestService.ping()");
		return "Ping!";
	}
	*/
	
	/*
	 * Perform the actual lookup by deferring the operation to the matching code system provider.
	 */
	private LookupResult lookup(LookupRequest lookupRequest) {
		return IFhirProvider.Registry.getFhirProvider(lookupRequest.getSystem()).lookup(lookupRequest);
	}

	/*
	 * Cross-field validation of the incoming parameters
	 * @param lookupRequest
	 */
	private void validateLookupRequest(LookupRequest lookupRequest) {
		if (lookupRequest.getSystem() != null && lookupRequest.getCode() == null) {
			throw new NotFoundException("Code", "");
		}
		
		if (lookupRequest.getCode()!=null && lookupRequest.getSystem() == null) {
			throw new BadRequestException("Parameter 'system' is not specified while code is present in the request.", "LookupRequest.system");
		}
		
		if (lookupRequest.getCode() !=null && lookupRequest.getCoding() !=null) {
			Coding coding = lookupRequest.getCoding();
			if (!coding.getCode().getCodeValue().equals(lookupRequest.getCode())) {
				throw new BadRequestException("Code and Coding.code are different. Probably would make sense to specify only one of them.", "LookupRequest");
			}
			
			if (!coding.getSystem().getUriValue().equals(lookupRequest.getSystem())) {
				throw new BadRequestException("System and Coding.system are different. Probably would make sense to specify only one of them.", "LookupRequest");
			}
			
			if (!Objects.equals(coding.getVersion(), lookupRequest.getVersion())) {
				throw new BadRequestException("Version and Coding.version are different. Probably would make sense to specify only one of them.", "LookupRequest");
			}
		}
	}
	
}
