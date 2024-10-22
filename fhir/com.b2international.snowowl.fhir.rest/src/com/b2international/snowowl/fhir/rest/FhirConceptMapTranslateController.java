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

import com.b2international.fhir.operations.OperationParametersFactory;
import com.b2international.fhir.r5.operations.ConceptMapTranslateParameters;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
		APPLICATION_FHIR_JSON_5_0_0_VALUE,
		APPLICATION_FHIR_JSON_4_3_0_VALUE,
		APPLICATION_FHIR_JSON_4_0_1_VALUE,
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_JSON_VALUE,
		TEXT_JSON_VALUE,
		
		APPLICATION_FHIR_XML_5_0_0_VALUE,
		APPLICATION_FHIR_XML_4_3_0_VALUE,
		APPLICATION_FHIR_XML_4_0_1_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		APPLICATION_XML_VALUE,
		TEXT_XML_VALUE
	})
	public Promise<ResponseEntity<byte[]>> translateType(
			
		@Parameter(description = "The code that is to be translated.") 
		@RequestParam(value = "sourceCode") 
		final String sourceCode,
		
		@Parameter(description = "The system for the code that is to be translated.") 
		@RequestParam(value = "system") 
		final String system,
		
		@Parameter(description = "The code system's version, if null latest is used.") 
		@RequestParam(value = "version") 
		final Optional<String> version,
		
		@Parameter(description = "Limits the scope of the $translate operation to source codes.") 
		@RequestParam(value = "sourceScope") 
		final Optional<String> sourceScope,
		
		@Parameter(description = "The target code that is to be translated to. If a code is provided, a system must be provided") 
		@RequestParam(value = "targetCode")
		final Optional<String> targetCode,
		
		@Parameter(description = "Identifies a target code system in which a mapping is sought. ") 
		@RequestParam(value = "targetSystem") 
		final Optional<String> targetSystem,
		
		@Parameter(description = "Limits the scope of the $translate operation to target codes.") 
		@RequestParam(value = "targetScope") 
		final Optional<String> targetScope,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		}))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty		
		
	) {
		
		var parameters = new ConceptMapTranslateParameters()
			.setSourceCode(sourceCode)
			.setSystem(system);
		
		version.ifPresent(parameters::setVersion);
		sourceScope.ifPresent(parameters::setSourceScope);
		
		targetCode.ifPresent(parameters::setTargetCode);
		targetSystem.ifPresent(parameters::setTargetSystem);
		targetScope.ifPresent(parameters::setTargetScope);
		
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
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		},
		produces = {
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		}
	)
	public Promise<ResponseEntity<byte[]>> translate(
			
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = APPLICATION_FHIR_JSON_5_0_0_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_JSON_4_3_0_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_JSON_4_0_1_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_JSON_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = TEXT_JSON_VALUE, schema = @Schema(type = "object")),

			@Content(mediaType = APPLICATION_FHIR_XML_5_0_0_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_XML_4_3_0_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_XML_4_0_1_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_XML_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_XML_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = TEXT_XML_VALUE, schema = @Schema(type = "object"))
		})
		final InputStream requestBody,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.CONTENT_TYPE)
		final String contentType,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,
		
		@Parameter(description = "Prefer header", schema = @Schema(
			allowableValues = { PREFER_HANDLING_STRICT, PREFER_HANDLING_LENIENT }, 
			defaultValue = PREFER_HANDLING_LENIENT
		))
		@RequestHeader(value = PREFER, required = false)
		final String prefer,

		@Parameter(description = "Alternative response format", schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		}))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
				
	) {
		
		final ConceptMapTranslateParameters parameters = toFhirParameters(requestBody, contentType, prefer, OperationParametersFactory.ConceptMapTranslateParametersFactory.INSTANCE);
		
		return translate(parameters, accept, _format, _pretty);
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
		APPLICATION_FHIR_JSON_5_0_0_VALUE,
		APPLICATION_FHIR_JSON_4_3_0_VALUE,
		APPLICATION_FHIR_JSON_4_0_1_VALUE,
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_JSON_VALUE,
		TEXT_JSON_VALUE,
		
		APPLICATION_FHIR_XML_5_0_0_VALUE,
		APPLICATION_FHIR_XML_4_3_0_VALUE,
		APPLICATION_FHIR_XML_4_0_1_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		APPLICATION_XML_VALUE,
		TEXT_XML_VALUE
	})
	public Promise<ResponseEntity<byte[]>> translateInstance(
			
		@Parameter(description = "The id of the Concept Map to base the translation on") 
		@PathVariable("id") 
		String conceptMapId,
		
		@Parameter(description = "The code that is to be translated.") 
		@RequestParam(value = "sourceCode") 
		final String sourceCode,
		
		@Parameter(description = "The system for the code that is to be translated.") 
		@RequestParam(value = "system") 
		final String system,
		
		@Parameter(description = "The code system's version, if null latest is used.") 
		@RequestParam(value = "version") 
		final Optional<String> version,
		
		@Parameter(description = "Limits the scope of the $translate operation to source codes.") 
		@RequestParam(value = "sourceScope") 
		final Optional<String> sourceScope,
		
		@Parameter(description = "The target code that is to be translated to. If a code is provided, a system must be provided") 
		@RequestParam(value = "targetCode")
		final Optional<String> targetCode,
		
		@Parameter(description = "Identifies a target code system in which a mapping is sought. ") 
		@RequestParam(value = "targetSystem") 
		final Optional<String> targetSystem,
		
		@Parameter(description = "Limits the scope of the $translate operation to target codes.") 
		@RequestParam(value = "targetScope") 
		final Optional<String> targetScope,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		}))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty		
		
	) {
		
		var parameters = new ConceptMapTranslateParameters()
			.setSourceCode(sourceCode)
			.setSystem(system)
			// XXX: Using a concept map ID as the URL here
			.setUrl(conceptMapId);
		
		version.ifPresent(parameters::setVersion);
		sourceScope.ifPresent(parameters::setSourceScope);
		
		targetCode.ifPresent(parameters::setTargetCode);
		targetSystem.ifPresent(parameters::setTargetSystem);
		targetScope.ifPresent(parameters::setTargetScope);
		
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
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		},
		produces = {
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		}
	)
	public Promise<ResponseEntity<byte[]>> translate(
			
		@Parameter(description = "The id of the conceptMap to base the translation on") 
		@PathVariable("conceptMapId") 
		String conceptMapId,
		
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = APPLICATION_FHIR_JSON_5_0_0_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_JSON_4_3_0_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_JSON_4_0_1_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_JSON_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = TEXT_JSON_VALUE, schema = @Schema(type = "object")),

			@Content(mediaType = APPLICATION_FHIR_XML_5_0_0_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_XML_4_3_0_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_XML_4_0_1_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_FHIR_XML_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = APPLICATION_XML_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = TEXT_XML_VALUE, schema = @Schema(type = "object"))
		})
		final InputStream requestBody,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.CONTENT_TYPE)
		final String contentType,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,
		
		@Parameter(description = "Prefer header", schema = @Schema(
			allowableValues = { PREFER_HANDLING_STRICT, PREFER_HANDLING_LENIENT }, 
			defaultValue = PREFER_HANDLING_LENIENT
		))
		@RequestHeader(value = PREFER, required = false)
		final String prefer,

		@Parameter(description = "Alternative response format", schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		}))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
		
	) {

		final ConceptMapTranslateParameters parameters = toFhirParameters(requestBody, contentType, prefer, OperationParametersFactory.ConceptMapTranslateParametersFactory.INSTANCE);
		
		// Before execution set the URI to match the path variable
		parameters.setUrl(conceptMapId);
		
		return translate(parameters, accept, _format, _pretty);
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
				return toResponseEntity(result, accept, _format, _pretty);
			});
	}
}
