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
import java.util.Optional;
import java.util.Set;

import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.annotations.*;

/**
 * @since 8.0
 */
@Tag(description = "CodeSystem", description="FHIR CodeSystem Resource", tags = { "CodeSystem" })
@RestController
@RequestMapping(value="/CodeSystem", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirCodeSystemLookupOperationController extends AbstractFhirController {

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
	@Operation(
			value="Concept lookup and decomposition",
			description="Given a code/version/system, or a Coding, get additional details about the concept.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@GetMapping("/$lookup")
	public Promise<Parameters.Fhir> lookup(
		
		@Parameter(value="The code to look up") 
		@RequestParam(value="code") 
		final String code,
		
		@Parameter(value="The code system's uri") 
		@RequestParam(value="system") 
		final String system,
		
		@Parameter(value="The code system version") 
		@RequestParam(value="version") 
		final Optional<String> version,
		
		@Parameter(value="Lookup date in datetime format") 
		@RequestParam(value="date") 
		final Optional<String> date,
		
		@Parameter(value="Language code for display") 
		@RequestParam(value="displayLanguage") 
		final Optional<String> displayLanguage,
		
		//Collection binding does not work with Optional!! (Optional<Set<String>> properties does not get populated with multiple properties, only the first one is present!)
		@Parameter(value="Properties to return in the output") 
		@RequestParam(value="property", required = false) 
		Set<String> properties) {
		
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
		return lookup(builder.build());
	}
	
	/**
	 * POST-based lookup end-point. Parameters are in the request body.
	 * @param in - FHIR parameters
	 */
	@Operation(value="Concept lookup and decomposition", description="Given a code/version/system, or a Coding, get additional details about the concept.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@PostMapping(value = "/$lookup", consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Parameters.Fhir> lookup(
			@Parameter(name = "body", value = "The lookup request parameters")
			@RequestBody 
			final Parameters.Fhir in) {
		
		final LookupRequest req = toRequest(in, LookupRequest.class);
		return lookup(req);
	}
	
	private Promise<Parameters.Fhir> lookup(LookupRequest lookupRequest) {
		return FhirRequests.codeSystems().prepareLookup()
				.setRequest(lookupRequest)
				.buildAsync()
				.execute(getBus())
				.then(this::toResponse);
	}
	
}
