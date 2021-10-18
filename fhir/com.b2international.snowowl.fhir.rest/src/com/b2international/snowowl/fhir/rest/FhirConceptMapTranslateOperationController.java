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

import java.util.Optional;

import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="https://www.hl7.org/fhir/conceptmap-operations.html">ConceptMap</a>
 * @since 8.0
 */
@Tag(description = "ConceptMap", name = "ConceptMap")
@RestController
@RequestMapping(value="/ConceptMap", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class FhirConceptMapTranslateOperationController extends AbstractFhirController {

	/**
	 * HTTP Get request to translate a code that belongs to a {@link ConceptMap} specified by its ID.
	 * @param conceptMapId
	 * @return translation of the code
	 */
	@Operation(
		summary="Translate a code based on a specific Concept Map",
		description="Translate a code from one value set to another, based on the existing value set and specific concept map."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Concept map not found")
	})
	@GetMapping(value="/{conceptMapId:**}/$translate")
	public Promise<Parameters.Fhir> translate(
		@Parameter(description = "The id of the Concept Map to base the translation on") @PathVariable("conceptMapId") String conceptMapId,
		@Parameter(description = "The code to translate") @RequestParam(value="code") final String code,
		@Parameter(description = "The code system's uri") @RequestParam(value="system") final String system,
		@Parameter(description = "The code system's version") @RequestParam(value="version") final Optional<String> version,
		@Parameter(description = "The source value set") @RequestParam(value="source") final Optional<String> source,
		@Parameter(description = "Value set in which a translation is sought") @RequestParam(value="target") final Optional<String> target,
		@Parameter(description = "Target code system") @RequestParam(value="targetsystem") final Optional<String> targetSystem,
		@Parameter(description = "If true, the mapping is reversed") @RequestParam(value="reverse") final Optional<Boolean> isReverse) {
		
		//validation is triggered by builder.build()
		Builder builder = TranslateRequest.builder()
			.code(code)
			.system(system)
			.url(conceptMapId);
		
		if (version.isPresent()) {
			builder.version(version.get());
		}
		
		if(source.isPresent()) {
			builder.source(source.get());
		}
		
		if(target.isPresent()) {
			builder.target(target.get());
		}
		
		if(targetSystem.isPresent()) {
			builder.targetSystem(targetSystem.get());
		}
		
		if(isReverse.isPresent()) {
			builder.isReverse(isReverse.get());
		}
		
		return doTranslate(builder.build());
	}
	
	/**
	 * HTTP POST request to translate a code that belongs to a {@link ConceptMap} specified by its ID.
	 * @param conceptMapId
	 * @return translation of the code
	 */
	@Operation(
		summary="Translate a code based on a specific Concept Map",
		description="Translate a code from one value set to another, based on the existing value set and specific concept map."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not found"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@PostMapping(value="/{conceptMapId:**}/$translate", consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Parameters.Fhir> translate(
		@Parameter(description = "The id of the conceptMap to base the translation on") 
		@PathVariable("conceptMapId") 
		String conceptMapId,
		
		@Parameter(description = "The translate request parameters")
		@RequestBody 
		Parameters.Fhir body) {
		
		//validation is triggered by builder.build()
		final TranslateRequest request = toRequest(body, TranslateRequest.class);
		request.setUrl(conceptMapId);
		return doTranslate(request);
	}

	/**
	 * HTTP Get request to translate a code that could belongs to any {@link ConceptMap} in the system.
	 * @return translation of the code
	 */
	@Operation(
		summary="Translate a code",
		description="Translate a code from one value set to another, based on the existing value set and concept maps resources, and/or other additional knowledge available to the server."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Concept map not found")
	})
	@GetMapping(value="/$translate")
	public Promise<Parameters.Fhir> translate(
		@Parameter(description = "The code to translate") @RequestParam(value="code") final String code,
		@Parameter(description = "The code system's uri") @RequestParam(value="system") final String system,
		@Parameter(description = "The code system's version, if null latest is used") @RequestParam(value="version") final Optional<String> version,
		@Parameter(description = "The source value set") @RequestParam(value="source") final Optional<String> source,
		@Parameter(description = "Value set in which a translation is sought") @RequestParam(value="target") final Optional<String> target,
		@Parameter(description = "Target code system") @RequestParam(value="targetsystem") final Optional<String> targetSystem,
		@Parameter(description = "If true, the mapping is reversed") @RequestParam(value="reverse") final Optional<Boolean> isReverse) {
		
		//validation is triggered by builder.build()
		Builder builder = TranslateRequest.builder()
			.code(code)
			.system(system);
		
		version.ifPresent(builder::version);
			
		if(source.isPresent()) {
			builder.source(source.get());
		}
		
		if(target.isPresent()) {
			builder.target(target.get());
		}
		
		if(targetSystem.isPresent()) {
			builder.targetSystem(targetSystem.get());
		}
		
		if(isReverse.isPresent()) {
			builder.isReverse(isReverse.get());
		}
		
		return doTranslate(builder.build());
	}
	
	/**
	 * HTTP POST request to translate a code that belongs to any {@link ConceptMap} in the system.
	 * @param body - {@link TranslateRequest}}
	 * @return translation of the code
	 */
	@Operation(
		summary="Translate a code",
		description="Translate a code from one value set to another, based on the existing value set and concept map resources."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not found"),
		@ApiResponse(responseCode = "400", description = "Bad request")
	})
	@PostMapping(value="/$translate", consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Parameters.Fhir> translate(
			@Parameter(description = "The translate request parameters")
			@RequestBody 
			Parameters.Fhir body) {
		
		final TranslateRequest request = toRequest(body, TranslateRequest.class);
		return doTranslate(request);
	}
	
	private Promise<Parameters.Fhir> doTranslate(TranslateRequest request) {
		return FhirRequests.conceptMaps().prepareTranslate()
				.setRequest(request)
				.buildAsync()
				.execute(getBus())
				.then(this::toResponse);
	}
	
}
