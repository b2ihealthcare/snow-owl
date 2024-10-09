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

import com.b2international.fhir.r5.operations.CodeSystemValidateCodeParameters;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 9.0.0
 */
@Tag(description = "CodeSystem", name = FhirApiConfig.CODESYSTEM)
@RestController
@RequestMapping(value = "/CodeSystem")
public class FhirCodeSystemValidateCodeController extends AbstractFhirController {

	/**
	 * <code><b>GET /CodeSystem/$validate-code</b></code>
	 * <p>
	 * The code system is identified by its Code System ID within the path. If the
	 * operation is not called at the instance level, one of the parameters "url" or
	 * "codeSystem" must be provided. The operation returns a result (true / false),
	 * an error message, and the recommended display for the code. When invoking
	 * this operation, a client SHALL provide one (and only one) of the parameters
	 * (code+system, coding, or codeableConcept). Other parameters (including
	 * version and display) are optional.
	 * 
	 * @param url           the code system to validate against
	 * @param code          to code to validate
	 * @param version       the version of the code system to validate against
	 * @param display       the display string of the code
	 * @param date          the date for which the validation should be checked
	 * @param isAbstract    If this parameter has a value of true, the client is
	 *                      stating that the validation is being performed in a context
	 *                      where a concept designated as 'abstract' is
	 *                      appropriate/allowed.
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Validate a code in a code system",
		description = "Validate that a coded value is in a code system."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Code system not found")
	@GetMapping(value = "/$validate-code", produces = {
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
	public Promise<ResponseEntity<byte[]>> validateCodeType(
			
		@Parameter(description = "The uri of the code system to validate against") 
		@RequestParam(value = "url") 
		final String url,
		
		@Parameter(description = "The code to be validated") 
		@RequestParam(value = "code") 
		final String code,
		
		@Parameter(description = "The version of the code system") 
		@RequestParam(value = "version") 
		final Optional<String> version,
		
		@Parameter(description = "The display string of the code") 
		@RequestParam(value = "display") 
		final Optional<String> display,
		
		@Parameter(description = "The date stamp of the code system to validate against") 
		@RequestParam(value = "date") 
		final Optional<String> date,
		
		@Parameter(description = "The abstract status of the code") 
		@RequestParam(value = "abstract") 
		final Optional<Boolean> isAbstract,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", array = @ArraySchema(schema = @Schema(allowableValues = {
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
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
		
	) {
		
		var parameters = new CodeSystemValidateCodeParameters()
			.setUrl(url)
			.setCode(code);
		
		version.ifPresent(parameters::setVersion);
		display.ifPresent(parameters::setDisplay);
		isAbstract.ifPresent(parameters::setAbstract);
		date.ifPresent(parameters::setDate);
		
		return validateCode(parameters, accept, _format, _pretty);
	}
	
	/**
	 * <code><b>POST /CodeSystem/$validate-code</b></code>
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 * @param contentType
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Validate a code in a code system", 
		description = "Validate that a coded value is in a code system."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/$validate-code", 
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
	public Promise<ResponseEntity<byte[]>> validateCode(
			
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
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
			
	) {
		final var fhirParameters = toFhirParameters(requestBody, contentType);
		final var request = new CodeSystemValidateCodeParameters(fhirParameters);
		return validateCode(request, accept, _format, _pretty);
	}

	
	/**
	 * <code><b>GET /CodeSystem/{id}/$validate-code</b></code>
	 * <p>
	 * The code system is identified by its Code System ID within the path -
	 * 'instance' level call. If the operation is not called at the instance level,
	 * one of the parameters "url" or "codeSystem" must be provided. The operation
	 * returns a result (true / false), an error message, and the recommended
	 * display for the code. When invoking this operation, a client SHALL provide
	 * one (and only one) of the parameters (code+system, coding, or
	 * codeableConcept). Other parameters (including version and display) are
	 * optional.
	 * 
	 * @param codeSystemId the code system to validate against
	 * @param code         to code to validate
	 * @param version      the version of the code system to validate against
	 * @param display      the display string of the code
	 * @param date         the date for which the validation should be checked
	 * @param isAbstract   If this parameter has a value of true, the client is
	 *                     stating that the validation is being performed in a
	 *                     context where a concept designated as 'abstract' is
	 *                     appropriate/allowed.
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Validate a code in a code system",
		description = "Validate that a coded value is in a code system."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Code system not found")
	})
	@GetMapping(value = "/{id:**}/$validate-code", produces = {
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
	public Promise<ResponseEntity<byte[]>> validateCodeInstance(
			
		@Parameter(description = "The id of the code system to validate against") 
		@PathVariable(value = "id") 
		String codeSystemId, 
		
		@Parameter(description = "The code to be validated") 
		@RequestParam(value = "code") 
		final String code,
		
		@Parameter(description = "The version of the code system") 
		@RequestParam(value = "version") 
		final Optional<String> version,
		
		@Parameter(description = "The display string of the code") 
		@RequestParam(value = "display") 
		final Optional<String> display,
		
		@Parameter(description = "The date stamp of the code system to validate against") 
		@RequestParam(value = "date") 
		final Optional<String> date,
		
		@Parameter(description = "The abstract status of the code") 
		@RequestParam(value = "abstract") 
		final Optional<Boolean> isAbstract,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", array = @ArraySchema(schema = @Schema(allowableValues = {
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
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
	
	) {
		
		var parameters = new CodeSystemValidateCodeParameters()
			// XXX: Inject code system ID as a URI into the request
			.setUrl(codeSystemId)
			.setCode(code);
		
		version.ifPresent(parameters::setVersion);
		display.ifPresent(parameters::setDisplay);
		isAbstract.ifPresent(parameters::setAbstract);
		date.ifPresent(parameters::setDate);
		
		return validateCode(parameters, accept, _format, _pretty);
	}
	
	/**
	 * <code><b>POST /CodeSystem/{id}/$validate-code</b></code>
	 * <p>
	 * All parameters are in the request body, except the id.
	 * 
	 * @param codeSystemId
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 * @param contentType
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Validate a code in a code system", 
		description = "Validate that a coded value is in a code system."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/{id:**}/$validate-code", 
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
	public Promise<ResponseEntity<byte[]>> validateCode(

		@Parameter(description = "The id of the code system to validate against") 
		@PathVariable(value = "id") 
		String codeSystemId, 

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
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
		
	) {
		
		final var fhirParameters = toFhirParameters(requestBody, contentType);
		final CodeSystemValidateCodeParameters parameters = new CodeSystemValidateCodeParameters(fhirParameters);
		
		// Validate parameters that are not allowed on the instance level
		if (parameters.getUrl() != null) {
			throw new BadRequestException("Parameter 'url' cannot be specified when the code system ID is set.", "ValidateCodeRequest.url");
		}
		
		if (parameters.getCoding() != null) {
			throw new BadRequestException("Parameter 'coding' cannot be specified when the code system ID is set.", "ValidateCodeRequest.coding");
		}
		
		if (parameters.getCodeableConcept() != null) {
			throw new BadRequestException("Parameter 'codeableConcept' cannot be specified when the code system ID is set.", "ValidateCodeRequest.codeableConcept");
		}
		
		if (parameters.getCodeSystem() != null) {
			throw new BadRequestException("Validation against external code systems is not supported", "ValidateCodeRequest.codeSystem");
		}
		
		// Before execution set the URI to match the path variable
		parameters.setUrl(codeSystemId);
		
		return validateCode(parameters, accept, _format, _pretty);
	}

	private Promise<ResponseEntity<byte[]>> validateCode(
		final CodeSystemValidateCodeParameters parameters, 
		final String accept, 
		final String _format, 
		final Boolean _pretty
	) {
		return FhirRequests.codeSystems().prepareValidateCode()
			.setParameters(parameters)
			.buildAsync()
			.execute(getBus())
			.then(result -> {
				return toResponseEntity(result.getParameters(), accept, _format, _pretty);
			});
	}
}
