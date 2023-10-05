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

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.converter.CodeSystemConverter_50;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
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
@Tag(description = "CodeSystem", name= FhirApiConfig.CODESYSTEM)
@RestController
@RequestMapping(value = "/CodeSystem")
public class FhirCodeSystemSubsumesOperationController extends AbstractFhirController {

	/**
	 * <code><b>GET /CodeSystem/$subsumes</b></code>
	 * 
	 * @param codeA
	 * @param codeB
	 * @param system
	 * @param version
	 * @return
	 */
	@Operation(
		summary = "Subsumption testing",
		description = "Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning)."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "CodeSystem not found")
	@GetMapping(value = "/$subsumes", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> subsumes(
			
		@Parameter(description = "The \"A\" code that is to be tested") 
		@RequestParam(value = "codeA") 
		final String codeA,
		
		@Parameter(description = "The \"B\" code that is to be tested") 
		@RequestParam(value="codeB") 
		final String codeB,
		
		@Parameter(description = "The code system's uri") 
		@RequestParam(value="system") 
		final String system,
		
		@Parameter(description = "The code system version") 
		@RequestParam(value="version", required = false) 
		final String version
		
	) {
		
		validateSubsumptionRequest(codeA, codeB, system, version);
		
		SubsumptionRequest subsumptionRequest = SubsumptionRequest.builder()
			.codeA(codeA)
			.codeB(codeB)
			.system(system)
			.version(version)
			.build();

		return subsumes(subsumptionRequest);
	}

	/**
	 * <code><b>GET /CodeSystem/{id}/$subsumes</b></code>
	 * 
	 * @param codeSystemId
	 * @param codeA
	 * @param codeB
	 * @return
	 */
	@Operation(
		summary = "Subsumption testing",
		description = "Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning)."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Code system not found")
	@GetMapping(value = "/{codeSystemId:**}/$subsumes", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> subsumes(
			
		@Parameter(description = "The id of the code system to invoke the operation on")
		@PathVariable("codeSystemId") 
		String codeSystemId,
		
		@Parameter(description = "The \"A\" code that is to be tested") 
		@RequestParam(value = "codeA") 
		final String codeA,
		
		@Parameter(description = "The \"B\" code that is to be tested") 
		@RequestParam(value = "codeB") 
		final String codeB	
		
	) {
		
		validateSubsumptionRequest(codeSystemId, codeA, codeB, null, null);
		
		SubsumptionRequest subsumptionRequest = SubsumptionRequest.builder()
			.codeA(codeA)
			.codeB(codeB)
			// XXX: We populate a resource ID in an URI here that is also potentially versioned
			.system(codeSystemId) 
			.build();
		
		return subsumes(subsumptionRequest);		
	}
	
	/**
	 * <code><b>POST /CodeSystem/$subsumes</b></code>
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 */
	@Operation(
		summary = "Subsumption testing", 
		description = "Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning)."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/$subsumes", 
		consumes = { AbstractFhirController.APPLICATION_FHIR_JSON },
		produces = { AbstractFhirController.APPLICATION_FHIR_JSON }
	)
	public Promise<ResponseEntity<byte[]>> subsumes(
			
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON, schema = @Schema(type = "object"))
		})
		final InputStream requestBody
				
	) {
		
		final SubsumptionRequest request;
		
		try {
			
			final var parameters = FHIRParser.parser(Format.JSON).parse(requestBody);
		
			if (!parameters.is(org.linuxforhealth.fhir.model.r5.resource.Parameters.class)) {
				throw new BadRequestException("Expected a complete Parameters resource as the request body, got '" 
					+ parameters.getClass().getSimpleName() + "'.");
			}
			
			final var fhirParameters = parameters.as(org.linuxforhealth.fhir.model.r5.resource.Parameters.class);
			request = CodeSystemConverter_50.INSTANCE.toSubsumptionRequest(fhirParameters);
			
		} catch (FHIRParserException e) {
			throw new BadRequestException("Failed to parse request body as a complete Parameters resource.");
		}
		
		validateSubsumptionRequest(request);
		return subsumes(request);
	}
	
	/**
	 * <code><b>POST /CodeSystem/$subsumes</b></code>
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 */

	@Operation(
		summary = "Subsumption testing", 
		description = "Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning)."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/{codeSystemId:**}/$subsumes", 
		consumes = { AbstractFhirController.APPLICATION_FHIR_JSON },
		produces = { AbstractFhirController.APPLICATION_FHIR_JSON }
	)
	public Promise<ResponseEntity<byte[]>> subsumes(

		@Parameter(description = "The id of the code system to invoke the operation on")
		@PathVariable("codeSystemId") 
		String codeSystemId,
		
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON, schema = @Schema(type = "object"))
		})
		final InputStream requestBody
		
	) {
		
		final SubsumptionRequest request;
		
		try {
			
			final var parameters = FHIRParser.parser(Format.JSON).parse(requestBody);
		
			if (!parameters.is(org.linuxforhealth.fhir.model.r5.resource.Parameters.class)) {
				throw new BadRequestException("Expected a complete Parameters resource as the request body, got '" 
					+ parameters.getClass().getSimpleName() + "'.");
			}
			
			final var fhirParameters = parameters.as(org.linuxforhealth.fhir.model.r5.resource.Parameters.class);
			request = CodeSystemConverter_50.INSTANCE.toSubsumptionRequest(fhirParameters);
			
		} catch (FHIRParserException e) {
			throw new BadRequestException("Failed to parse request body as a complete Parameters resource.");
		}		
		
		validateSubsumptionRequest(codeSystemId, request);
		
		/*
		 * TODO: Interpolate codeSystemId into "system" parameter if the subsumption
		 * works with codes, not Codings (and so no system information is available in
		 * the request at all). This is valid, as clients rightfully assume that an
		 * instance-level operation works with the CodeSystem instance mentioned in the
		 * identifier.
		 */
		return subsumes(request);
	}
	
	private Promise<ResponseEntity<byte[]>> subsumes(SubsumptionRequest req) {
		return FhirRequests.codeSystems().prepareSubsumes()
			.setRequest(req)
			.buildAsync()
			.execute(getBus())
			.then(soSubsumptionResult -> {
				var fhirSubsumptionResult = CodeSystemConverter_50.INSTANCE.fromSubsumptionResult(soSubsumptionResult);
				
				final Format format = Format.JSON;
				final boolean prettyPrinting = true;
				final FHIRGenerator generator = FHIRGenerator.generator(format, prettyPrinting);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try {
					generator.generate(fhirSubsumptionResult, baos);
				} catch (FHIRGeneratorException e) {
					throw new BadRequestException("Failed to convert response body to a Parameters resource.");
				}

				return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
			});
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
		
		// check the systems
		if (StringUtils.isEmpty(system) && StringUtils.isEmpty(codeSystemId)) {
			throw new BadRequestException("Parameter 'system' is not specified for subsumption testing.", "SubsumptionRequest.system");
		}
		
		// TODO: this probably incorrect as codeSystemId is an internal id vs. system that is external
		if (!StringUtils.isEmpty(system) && !StringUtils.isEmpty(codeSystemId) && !codeSystemId.equals(system)) {
			throw new BadRequestException(String.format("Parameter 'system: %s' and path parameter 'codeSystem: %s' are not the same.", system, codeSystemId), "SubsumptionRequest.system");
		}
		
		if (StringUtils.isEmpty(codeA) && StringUtils.isEmpty(codeB)) {
			// No codes and no codings
			if (codingA == null && codingB == null) {
				throw new BadRequestException("No codes or Codings are provided for subsumption testing.", "SubsumptionRequest");
			}

			// One coding is present, but the other is missing (both can not be missing as it was handled above)
			if (codingA == null || codingB == null) {
				throw new BadRequestException("No Codings are provided for subsumption testing.", "SubsumptionRequest.Coding");
			}
			
			// Both codings are present at this point
			
		} else {

			// Some codes were provided, but a coding is also present, ambiguous
			if (codingA != null || codingB != null) {
				throw new BadRequestException("Provide either codes or Codings.", "SubsumptionRequest");
			}

			// One code is present, but the other is missing (both can not be missing as it was handled in the outer "if" block above)
			if (StringUtils.isEmpty(codeA) || StringUtils.isEmpty(codeB)) {
				throw new BadRequestException("No codes are provided for subsumption testing.", "SubsumptionRequest.code");		
			}
			
			// Both codes are present at this point
		}

		// Check system (URL) against version 
		if (!StringUtils.isEmpty(system) && !StringUtils.isEmpty(version) && !system.endsWith("/" + version)) {
			throw new BadRequestException(String.format("Version specified in the URI [%s] does not match the version set in the request [%s]",
				system, version));
		}
	}
}
