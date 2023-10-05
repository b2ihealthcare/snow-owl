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
import com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.converter.ValueSetConverter_50;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="https://www.hl7.org/fhir/valueset-operation-validate-code.html">4.9.18.2 Operation $validate-code on ValueSet</a>
 * @since 8.0
 */
@Tag(description = "ValueSet", name = FhirApiConfig.VALUESET)
@RestController
@RequestMapping(value = "/ValueSet")
public class FhirValueSetValidateCodeController extends AbstractFhirController {

	/**
	 * <code><b>GET /ValueSet/$validate-code</b></code>
	 * <p>
	 * The value set is identified by its Value Set ID within the path. If the
	 * operation is not called at the instance level, one of the parameters "url" or
	 * "valueSet" must be provided. The operation returns a result (true / false),
	 * an error message, and the recommended display for the code. When invoking
	 * this operation, a client SHALL provide one (and only one) of the parameters
	 * (code+system, coding, or codeableConcept). Other parameters (including
	 * version and display) are optional.
	 * 
	 * @param url             the value set to validate against
	 * @param code            to code to validate
	 * @param valueSetVersion the version of the value set to validate against
	 * @param date            the date for which the validation should be checked
	 * @param isAbstract      If this parameter has a value of true, the client is
	 *                        stating that the validation is being performed in a
	 *                        context where a concept designated as 'abstract' is
	 *                        appropriate/allowed.
	 */
	@Operation(
		summary = "Validate a code in a value set",
		description = "Validate that a coded value is in a value set."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Value set not found")
	@GetMapping(value = "/$validate-code", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> validateCodeType(
			
		@Parameter(description = "The uri of the value set to validate against") 
		@RequestParam(value = "url") 
		String url,
		
		@Parameter(description = "The version of the value set") 
		@RequestParam(value = "valueSetVersion") 
		final Optional<String> valueSetVersion,

		@Parameter(description = "The code to be validated") 
		@RequestParam(value = "code") 
		final String code,

		@Parameter(description = "The system uri of the code to be validated") 
		@RequestParam(value="system") 
		final Optional<String> system,
		
		@Parameter(description = "The code system version of the code to be validated") 
		@RequestParam(value = "systemVersion") 
		final Optional<String> systemVersion,

		@Parameter(description = "The display string of the code") 
		@RequestParam(value = "display") 
		final Optional<String> display,
		
		@Parameter(description = "The date stamp of the value set to validate against") 
		@RequestParam(value = "date") 
		final Optional<String> date,
		
		@Parameter(description = "The abstract status of the code") 
		@RequestParam(value = "abstract") 
		final Optional<Boolean> isAbstract
		
	) {
		
		ValidateCodeRequest.Builder builder = ValidateCodeRequest.builder()
			.url(url)
			.code(code)
			.valueSetVersion(valueSetVersion.orElse(null))
			.system(system.orElse(null))
			.systemVersion(systemVersion.orElse(null))
			.display(display.orElse(null))
			.isAbstract(isAbstract.orElse(null));
		
		if (date.isPresent()) {
			builder.date(date.get());
		}
				
		return validateCode(builder.build());
	}
	
	/**
	 * <code><b>POST /ValueSet/$validate-code</b></code>
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 */
	@Operation(
		summary = "Validate a code in a value set", 
		description = "Validate that a coded value is in a value set."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/$validate-code", 
		consumes = { AbstractFhirController.APPLICATION_FHIR_JSON },
		produces = { AbstractFhirController.APPLICATION_FHIR_JSON }
	)
	public Promise<ResponseEntity<byte[]>> validateCode(
			
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON, schema = @Schema(type = "object"))
		})
		final InputStream requestBody
			
	) {
		
		final ValidateCodeRequest request;
		
		try {
			
			final var parameters = FHIRParser.parser(Format.JSON).parse(requestBody);
		
			if (!parameters.is(org.linuxforhealth.fhir.model.r5.resource.Parameters.class)) {
				throw new BadRequestException("Expected a complete Parameters resource as the request body, got '" 
					+ parameters.getClass().getSimpleName() + "'.");
			}
			
			final var fhirParameters = parameters.as(org.linuxforhealth.fhir.model.r5.resource.Parameters.class);
			request = ValueSetConverter_50.INSTANCE.toValidateCodeRequest(fhirParameters);
			
		} catch (FHIRParserException e) {
			throw new BadRequestException("Failed to parse request body as a complete Parameters resource.");
		}
		
		return validateCode(request);
	}

	
	/**
	 * <code><b>GET /ValueSet/{id}/$validate-code</b></code>
	 * <p>
	 * The value set is identified by its Value Set ID within the path - 'instance'
	 * level call. If the operation is not called at the instance level, one of the
	 * parameters "url" or "valueSet" must be provided. The operation returns a
	 * result (true / false), an error message, and the recommended display for the
	 * code. When invoking this operation, a client SHALL provide one (and only one)
	 * of the parameters (code+system, coding, or codeableConcept). Other parameters
	 * (including version and display) are optional.
	 * 
	 * @param valueSetId      the value set to validate against
	 * @param code            to code to validate
	 * @param valueSetVersion the version of the value set to validate against
	 * @param date            the date for which the validation should be checked
	 * @param isAbstract      If this parameter has a value of true, the client is
	 *                        stating that the validation is being performed in a
	 *                        context where a concept designated as 'abstract' is
	 *                        appropriate/allowed.
	 */
	@Operation(
		summary = "Validate a code in a value set",
		description = "Validate that a coded value is in a value set."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@GetMapping(value = "/{valueSetId:**}/$validate-code", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> validateCodeInstance(
			
		@Parameter(description = "The id of the value set to validate against") 
		@PathVariable(value = "valueSetId") 
		String valueSetId, 
		
		@Parameter(description = "The version of the value set") 
		@RequestParam(value = "valueSetVersion") 
		final Optional<String> valueSetVersion,
		
		@Parameter(description = "The code to be validated") 
		@RequestParam(value = "code") 
		final String code,

		@Parameter(description = "The system uri of the code to be validated") 
		@RequestParam(value="system") 
		final Optional<String> system,
		
		@Parameter(description = "The code system version of the code to be validated") 
		@RequestParam(value = "systemVersion") 
		final Optional<String> systemVersion,

		@Parameter(description = "The display string of the code") 
		@RequestParam(value = "display") 
		final Optional<String> display,
		
		@Parameter(description = "The date stamp of the value set to validate against") 
		@RequestParam(value = "date") 
		final Optional<String> date,
		
		@Parameter(description = "The abstract status of the code") 
		@RequestParam(value = "abstract") 
		final Optional<Boolean> isAbstract
	
	) {
		
		ValidateCodeRequest.Builder builder = ValidateCodeRequest.builder()
			// XXX: Inject value set ID as a URI into the request
			.url(valueSetId)
			.code(code)
			.code(code)
			.valueSetVersion(valueSetVersion.orElse(null))
			.system(system.orElse(null))
			.systemVersion(systemVersion.orElse(null))
			.display(display.orElse(null))
			.isAbstract(isAbstract.orElse(null));
		
		if (date.isPresent()) {
			builder.date(date.get());
		}
		
		return validateCode(builder.build());
	}
	
	/**
	 * <code><b>GET /ValueSet/{id}/$validate-code</b></code>
	 * <p>
	 * All parameters are in the request body, except the valueSetId.
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 */
	@Operation(
		summary = "Validate a code in a value set", 
		description = "Validate that a coded value is in a value set."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/{valueSetId:**}/$validate-code", 
		consumes = { AbstractFhirController.APPLICATION_FHIR_JSON},
		produces = { AbstractFhirController.APPLICATION_FHIR_JSON}
	)
	public Promise<ResponseEntity<byte[]>> validateCode(

		@Parameter(description = "The id of the value set to validate against") 
		@PathVariable(value = "valueSetId") 
		String valueSetId, 

		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON, schema = @Schema(type = "object"))
		})
		final InputStream requestBody
		
	) {
		
		final ValidateCodeRequest request;
		
		try {
			
			final var parameters = FHIRParser.parser(Format.JSON).parse(requestBody);
		
			if (!parameters.is(org.linuxforhealth.fhir.model.r5.resource.Parameters.class)) {
				throw new BadRequestException("Expected a complete Parameters resource as the request body, got '" 
					+ parameters.getClass().getSimpleName() + "'.");
			}
			
			final var fhirParameters = parameters.as(org.linuxforhealth.fhir.model.r5.resource.Parameters.class);
			request = ValueSetConverter_50.INSTANCE.toValidateCodeRequest(fhirParameters);
			
		} catch (FHIRParserException e) {
			throw new BadRequestException("Failed to parse request body as a complete Parameters resource.");
		}
		
		// Before execution set the URI to match the path variable
		request.setUrl(new Uri(valueSetId));
		return validateCode(request);
	}

	private Promise<ResponseEntity<byte[]>> validateCode(ValidateCodeRequest validateCodeRequest) {
		return FhirRequests.valueSets().prepareValidateCode()
			.setRequest(validateCodeRequest)
			.buildAsync()
			.execute(getBus())
			.then(soValidateCodeResult -> {
				var fhirValidateCodeResult = ValueSetConverter_50.INSTANCE.fromValidateCodeResult(soValidateCodeResult);
				
				final Format format = Format.JSON;
				final boolean prettyPrinting = true;
				final FHIRGenerator generator = FHIRGenerator.generator(format, prettyPrinting);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try {
					generator.generate(fhirValidateCodeResult, baos);
				} catch (FHIRGeneratorException e) {
					throw new BadRequestException("Failed to convert response body to a Parameters resource.");
				}

				return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
			});
	}
}
