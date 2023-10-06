/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Optional;

import org.linuxforhealth.fhir.model.format.Format;
import org.linuxforhealth.fhir.model.generator.exception.FHIRGeneratorException;
import org.linuxforhealth.fhir.model.parser.exception.FHIRParserException;
import org.linuxforhealth.fhir.model.r5.generator.FHIRGenerator;
import org.linuxforhealth.fhir.model.r5.parser.FHIRParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest.Builder;
import com.b2international.snowowl.fhir.core.model.converter.ConceptMapConverter_50;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="https://hl7.org/fhir/conceptmap-operation-translate.html">4.10.16.1 Operation $translate on ConceptMap</a>
 * @since 8.0
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
	@GetMapping(value = "/$translate", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
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
		final Optional<Boolean> isReverse
		
	) {
		
		Builder builder = TranslateRequest.builder()
			.code(code)
			.system(system);
		
		version.ifPresent(builder::version);
			
		if (source.isPresent()) {
			builder.source(source.get());
		}
		
		if (target.isPresent()) {
			builder.target(target.get());
		}
		
		if (targetSystem.isPresent()) {
			builder.targetSystem(targetSystem.get());
		}
		
		if (isReverse.isPresent()) {
			builder.isReverse(isReverse.get());
		}
		
		return translate(builder.build());
	}

	/**
	 * <code><b>POST /ConceptMap/$translate</b></code>
	 * <p>
	 * Translates a code that belongs to any {@link ConceptMap} in the system.
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
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
		consumes = { AbstractFhirController.APPLICATION_FHIR_JSON },
		produces = { AbstractFhirController.APPLICATION_FHIR_JSON }
	)
	public Promise<ResponseEntity<byte[]>> translate(
			
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON, schema = @Schema(type = "object"))
		})
		final InputStream requestBody
				
	) {
		
		final TranslateRequest request;
		
		try {
			
			final var parameters = FHIRParser.parser(Format.JSON).parse(requestBody);
		
			if (!parameters.is(org.linuxforhealth.fhir.model.r5.resource.Parameters.class)) {
				throw new BadRequestException("Expected a complete Parameters resource as the request body, got '" 
					+ parameters.getClass().getSimpleName() + "'.");
			}
			
			final var fhirParameters = parameters.as(org.linuxforhealth.fhir.model.r5.resource.Parameters.class);
			request = ConceptMapConverter_50.INSTANCE.toTranslateRequest(fhirParameters);
			
		} catch (FHIRParserException e) {
			throw new BadRequestException("Failed to parse request body as a complete Parameters resource.");
		}
		
		return translate(request);
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
	 * @return
	 */
	@Operation(
		summary = "Translate a code based on a specific Concept Map",
		description = "Translate a code from one value set to another, based on the existing value set and specific concept map."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Concept map not found")
	@GetMapping(value = "/{conceptMapId:**}/$translate", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> translateInstance(
			
		@Parameter(description = "The id of the Concept Map to base the translation on") 
		@PathVariable("conceptMapId") 
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
		final Optional<Boolean> isReverse
		
	) {
		
		TranslateRequest.Builder builder = TranslateRequest.builder()
			.code(code)
			.system(system)
			// XXX: Using a concept map ID as the URL here
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
		
		return translate(builder.build());
	}
	
	/**
	 * <code><b>POST /ConceptMap/{id}/$translate</b></code>
	 * 
	 * Translates a code that belongs to a {@link ConceptMap} specified by its ID.
	 * 
	 * @param conceptMapId
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
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
		consumes = { AbstractFhirController.APPLICATION_FHIR_JSON },
		produces = { AbstractFhirController.APPLICATION_FHIR_JSON }
	)
	public Promise<ResponseEntity<byte[]>> translate(
			
		@Parameter(description = "The id of the conceptMap to base the translation on") 
		@PathVariable("conceptMapId") 
		String conceptMapId,
		
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON, schema = @Schema(type = "object"))
		})
		final InputStream requestBody
		
	) {

		final TranslateRequest request;
		
		try {
			
			final var parameters = FHIRParser.parser(Format.JSON).parse(requestBody);
		
			if (!parameters.is(org.linuxforhealth.fhir.model.r5.resource.Parameters.class)) {
				throw new BadRequestException("Expected a complete Parameters resource as the request body, got '" 
					+ parameters.getClass().getSimpleName() + "'.");
			}
			
			final var fhirParameters = parameters.as(org.linuxforhealth.fhir.model.r5.resource.Parameters.class);
			request = ConceptMapConverter_50.INSTANCE.toTranslateRequest(fhirParameters);
			
		} catch (FHIRParserException e) {
			throw new BadRequestException("Failed to parse request body as a complete Parameters resource.");
		}
		
		// Before execution set the URI to match the path variable
		request.setUrl(conceptMapId);
		return translate(request);
	}

	private Promise<ResponseEntity<byte[]>> translate(TranslateRequest translateRequest) {
		return FhirRequests.conceptMaps().prepareTranslate()
			.setRequest(translateRequest)
			.buildAsync()
			.execute(getBus())
			.then(soTranslateResult -> {
				var fhirTranslateResult = ConceptMapConverter_50.INSTANCE.fromTranslateResult(soTranslateResult);
				
				final Format format = Format.JSON;
				final boolean prettyPrinting = true;
				final FHIRGenerator generator = FHIRGenerator.generator(format, prettyPrinting);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try {
					generator.generate(fhirTranslateResult, baos);
				} catch (FHIRGeneratorException e) {
					throw new BadRequestException("Failed to convert response body to a Parameters resource.");
				}

				return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
			});
	}
	
}
