/*
 * Copyright 2023-2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.b2international.snowowl.fhir.rest.FhirMediaType.*;

import java.io.InputStream;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.fhir.r5.operations.ConceptMapTranslateParameters;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="https://hl7.org/fhir/conceptmap-operation-translate.html">4.10.16.1 Operation $translate on ConceptMap</a>
 * @since 9.0.0
 */
@Tag(description = "ConceptMap", name = FhirApiConfig.CONCEPTMAP)
@RestController
@RequestMapping(value = "/ConceptMap")
public class FhirConceptMapTranslateController extends AbstractFhirController {

	/**
	 * <code><b>GET /ConceptMap/$translate</b></code>
	 * <p>
	 * Translates a code that could belong to any {@link ConceptMap} in the system.
	 * 
	 * @param code
	 * @param system
	 * @param version
	 * @param source
	 * @param target
	 * @param targetSystem
	 * @param isReverse
 	 * @param accept
	 * @param _format
	 * @param _pretty
	 * 
	 * @return translation of the code
	 */
	@Operation(
		summary = "Translate a code",
		description = "Translate a code from one value set to another, based on the existing value set and concept maps resources, and/or other additional knowledge available to the server."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Concept map not found")
	@GetMapping(value = "/$translate", produces = {
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
	public Promise<ResponseEntity<byte[]>> translateType(
			
		@Parameter(description = "The code to translate") 
		@RequestParam(value = "code") 
		final String code,
		
		@Parameter(description = "The code system's uri") 
		@RequestParam(value = "system") 
		final String system,
		
		@Parameter(description = "The code system's version, if null latest is used") 
		@RequestParam(value = "version") 
		final Optional<String> version,
		
		@Parameter(description = "The source value set") 
		@RequestParam(value = "source") 
		final Optional<String> source,
		
		@Parameter(description = "Value set in which a translation is sought") 
		@RequestParam(value = "target") 
		final Optional<String> target,
		
		@Parameter(description = "Target code system") 
		@RequestParam(value = "targetsystem") 
		final Optional<String> targetSystem,
		
		@Parameter(description = "If true, the mapping is reversed") 
		@RequestParam(value = "reverse") 
		final Optional<Boolean> isReverse,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", array = @ArraySchema(schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty		
		
	) {
		
		// TODO fix R4 parameters and replace them with R5 (reintroduce R4, R4B support via dedicated versioned paths)
		var parameters = new ConceptMapTranslateParameters()
			.setSourceCode(code)
			.setSystem(system);
		
//		version.ifPresent(parameters::setVersion);
//		source.ifPresent(parameters::setSource);
//		target.ifPresent(parameters::setTarget);
//		targetSystem.ifPresent(parameters::setTargetSystem);
//		isReverse.ifPresent(parameters::setIsReverse);
		
		return translate(parameters, accept, _format, _pretty);
	}

	/**
	 * <code><b>POST /ConceptMap/$translate</b></code>
	 * <p>
	 * Translates a code that belongs to any {@link ConceptMap} in the system.
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 * @param contentType
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return translation of the code
	 */
	@Operation(
		summary = "Translate a code",
		description = "Translate a code from one value set to another, based on the existing value set and concept map resources."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/$translate", 
		consumes = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		},
		produces = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		}
	)
	public Promise<ResponseEntity<byte[]>> translate(
			
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = APPLICATION_FHIR_JSON_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_XML_VALUE, schema = @Schema(type = "object"))
		})
		final InputStream requestBody,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.CONTENT_TYPE)
		final String contentType,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", array = @ArraySchema(schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
				
	) {
		
		final var parameters = toFhirParameters(requestBody, contentType);
		final var request = new ConceptMapTranslateParameters(parameters);
		
		return translate(request, accept, _format, _pretty);
	}

	/**
	 * <code><b>GET /ConceptMap/{id}/$translate</b></code>
	 * 
	 * @param conceptMapId
	 * @param code
	 * @param system
	 * @param version
	 * @param source
	 * @param target
	 * @param targetSystem
	 * @param isReverse
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Translate a code based on a specific Concept Map",
		description = "Translate a code from one value set to another, based on the existing value set and specific concept map."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Concept map not found")
	@GetMapping(value = "/{id:**}/$translate", produces = {
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
	public Promise<ResponseEntity<byte[]>> translateInstance(
			
		@Parameter(description = "The id of the Concept Map to base the translation on") 
		@PathVariable("id") 
		String conceptMapId,
		
		@Parameter(description = "The code to translate") 
		@RequestParam(value = "code") 
		final String code,
		
		@Parameter(description = "The code system's uri") 
		@RequestParam(value = "system") 
		final String system,
		
		@Parameter(description = "The code system's version") 
		@RequestParam(value = "version") 
		final Optional<String> version,
		
		@Parameter(description = "The source value set") 
		@RequestParam(value = "source") 
		final Optional<String> source,
		
		@Parameter(description = "Value set in which a translation is sought") 
		@RequestParam(value = "target") 
		final Optional<String> target,
		
		@Parameter(description = "Target code system") 
		@RequestParam(value = "targetsystem") 
		final Optional<String> targetSystem,
		
		@Parameter(description = "If true, the mapping is reversed") 
		@RequestParam(value = "reverse") 
		final Optional<Boolean> isReverse,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", array = @ArraySchema(schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty		
		
	) {
		
		var parameters = new ConceptMapTranslateParameters()
			.setSourceCode(code)
			.setSystem(system)
			// XXX: Using a concept map ID as the URL here
			.setUrl(conceptMapId);
		
//		version.ifPresent(parameters::setVersion);
//		source.ifPresent(parameters::setSource);
//		target.ifPresent(parameters::setTarget);
//		targetSystem.ifPresent(parameters::setTargetSystem);
//		isReverse.ifPresent(parameters::setIsReverse);
		
		return translate(parameters, accept, _format, _pretty);
	}
	
	/**
	 * <code><b>POST /ConceptMap/{id}/$translate</b></code>
	 * 
	 * Translates a code that belongs to a {@link ConceptMap} specified by its ID.
	 * 
	 * @param conceptMapId
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 * @param contentType
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * 
	 * @return translation of the code
	 */
	@Operation(
		summary = "Translate a code based on a specific Concept Map",
		description = "Translate a code from one value set to another, based on the existing value set and specific concept map."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/{conceptMapId:**}/$translate", 
		consumes = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		},
		produces = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		}
	)
	public Promise<ResponseEntity<byte[]>> translate(
			
		@Parameter(description = "The id of the conceptMap to base the translation on") 
		@PathVariable("conceptMapId") 
		String conceptMapId,
		
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = APPLICATION_FHIR_JSON_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_XML_VALUE, schema = @Schema(type = "object"))
		})
		final InputStream requestBody,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.CONTENT_TYPE)
		final String contentType,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", array = @ArraySchema(schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
		
	) {

		final var parameters = toFhirParameters(requestBody, contentType);
		final var request = new ConceptMapTranslateParameters(parameters);
		
		// Before execution set the URI to match the path variable
		request.setUrl(conceptMapId);
		
		return translate(request, accept, _format, _pretty);
	}

	private Promise<ResponseEntity<byte[]>> translate(
		final ConceptMapTranslateParameters parameters, 
		final String accept, 
		final String _format, 
		final Boolean _pretty
	) {
		return FhirRequests.conceptMaps().prepareTranslate()
			.setParameters(parameters)
			.buildAsync()
			.execute(getBus())
			.then(result -> {
				return toResponseEntity(result.getParameters(), accept, _format, _pretty);
			});
	}
}
