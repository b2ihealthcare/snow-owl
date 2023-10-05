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
import java.text.ParseException;
import java.util.Optional;
import java.util.Set;

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

import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.converter.CodeSystemConverter_50;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "CodeSystem", name = FhirApiConfig.CODESYSTEM)
@RestController
@RequestMapping(value="/CodeSystem")
public class FhirCodeSystemLookupOperationController extends AbstractFhirController {

	/**
	 * <code><b>GET /CodeSystem/$lookup</b></code>
	 * <p>
	 * Given a code/system, or a Coding, get additional details about the concept,
	 * including definition, status, designations, and properties. One of the
	 * products of this operation is a full decomposition of a code from a
	 * structured terminology.
	 * 
	 * @param code
	 * @param system
	 * @param version
	 * @param date
	 * @param displayLanguage
	 * @param properties
	 * 
	 * @throws ParseException
	 */
	@Operation(
		summary = "Concept lookup and decomposition",
		description = "Given a code/version/system, or a Coding, get additional details about the concept."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Code system not found")
	@GetMapping(value = "/$lookup", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> lookup(
		
		@Parameter(description = "The code to look up") 
		@RequestParam(value = "code", required = true) 
		final String code,
		
		@Parameter(description = "The code system's URI") 
		@RequestParam(value = "system", required = true) 
		final String system,
		
		@Parameter(description = "The code system version") 
		@RequestParam(value = "version") 
		final Optional<String> version,
		
		@Parameter(description = "Lookup date in datetime format") 
		@RequestParam(value = "date") 
		final Optional<String> date,
		
		@Parameter(description = "Language code for display") 
		@RequestParam(value = "displayLanguage", defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required = false) 
		final Optional<String> displayLanguage,
		
		@Parameter(description = "Properties to return in the output") 
		@RequestParam(value = "property", required = false) 
		final Set<String> properties
		
	) {
		
		LookupRequest.Builder builder = LookupRequest.builder()
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
		
		return lookup(builder.build());
	}
	
	/**
	 * <code><b>POST /CodeSystem/$lookup</b></code>
	 * <p>
	 * Given a code/system, or a Coding, get additional details about the concept,
	 * including definition, status, designations, and properties. One of the
	 * products of this operation is a full decomposition of a code from a
	 * structured terminology.
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 */
	@Operation(
		summary = "Concept lookup and decomposition", 
		description = "Given a code/version/system, or a Coding, get additional details about the concept."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/$lookup", 
		consumes = { AbstractFhirController.APPLICATION_FHIR_JSON },
		produces = { AbstractFhirController.APPLICATION_FHIR_JSON }
	)
	public Promise<ResponseEntity<byte[]>> lookup(
			
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
				@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON, schema = @Schema(type = "object"))
			})
			final InputStream requestBody

	) {
		final LookupRequest request;
		
		try {
			
			final var parameters = FHIRParser.parser(Format.JSON).parse(requestBody);
		
			if (!parameters.is(org.linuxforhealth.fhir.model.r5.resource.Parameters.class)) {
				throw new BadRequestException("Expected a complete Parameters resource as the request body, got '" 
					+ parameters.getClass().getSimpleName() + "'.");
			}
			
			final var fhirParameters = parameters.as(org.linuxforhealth.fhir.model.r5.resource.Parameters.class);
			request = CodeSystemConverter_50.INSTANCE.toLookupRequest(fhirParameters);
			
		} catch (FHIRParserException e) {
			throw (BadRequestException) new BadRequestException("Failed to parse request body as a complete Parameters resource.")
				.initCause(e);
		}
		
		return lookup(request);
	}
	
	private Promise<ResponseEntity<byte[]>> lookup(LookupRequest lookupRequest) {
		return FhirRequests.codeSystems().prepareLookup()
			.setRequest(lookupRequest)
			.buildAsync()
			.execute(getBus())
			.then(soLookupResult -> {
				var fhirLookupResult = CodeSystemConverter_50.INSTANCE.fromLookupResult(soLookupResult);
				
				final Format format = Format.JSON;
				final boolean prettyPrinting = true;
				final FHIRGenerator generator = FHIRGenerator.generator(format, prettyPrinting);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try {
					generator.generate(fhirLookupResult, baos);
				} catch (FHIRGeneratorException e) {
					throw (BadRequestException) new BadRequestException("Failed to convert response body to a Parameters resource.")
						.initCause(e);
				}

				return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
			});
	}
}
