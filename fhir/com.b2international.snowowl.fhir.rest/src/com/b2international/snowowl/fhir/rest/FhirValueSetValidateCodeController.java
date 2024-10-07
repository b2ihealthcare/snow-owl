/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.fhir.r5.operations.ValueSetValidateCodeParameters;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.FhirApiConfig;
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
	 * @param valueSetVersion the version of the value set to validate against
	 * @param code            to code to validate
	 * @param system
	 * @param systemVersion
	 * @param display
	 * @param date            the date for which the validation should be checked
	 * @param isAbstract      If this parameter has a value of true, the client is
	 *                        stating that the validation is being performed in a
	 *                        context where a concept designated as 'abstract' is
	 *                        appropriate/allowed.
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Validate a code in a value set",
		description = "Validate that a coded value is in a value set."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Value set not found")
	@GetMapping(value = "/$validate-code", produces = {
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
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
		final Optional<Boolean> isAbstract,
		
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
		
		var parameters = new ValueSetValidateCodeParameters()
			.setUrl(url)
			.setCode(code);
		
		valueSetVersion.ifPresent(parameters::setValueSetVersion);
		system.ifPresent(parameters::setSystem);
		systemVersion.ifPresent(parameters::setSystemVersion);
		display.ifPresent(parameters::setDisplay);
		isAbstract.ifPresent(parameters::setAbstract);
		date.ifPresent(parameters::setDate);
				
		return validateCode(parameters, accept, _format, _pretty);
	}
	
	/**
	 * <code><b>POST /ValueSet/$validate-code</b></code>
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 * @param contentType
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
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
		
		final var fhirParameters = toFhirParameters(requestBody, contentType);
		final var request = new ValueSetValidateCodeParameters(fhirParameters);
		
		return validateCode(request, accept, _format, _pretty);
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
	 * @param valueSetVersion the version of the value set to validate against
	 * @param code            to code to validate
	 * @param system
	 * @param systemVersion
	 * @param display
	 * @param date            the date for which the validation should be checked
	 * @param isAbstract      If this parameter has a value of true, the client is
	 *                        stating that the validation is being performed in a
	 *                        context where a concept designated as 'abstract' is
	 *                        appropriate/allowed.
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
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
	@GetMapping(value = "/{id:**}/$validate-code", produces = {
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
	public Promise<ResponseEntity<byte[]>> validateCodeInstance(
			
		@Parameter(description = "The id of the value set to validate against") 
		@PathVariable(value = "id") 
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
		final Optional<Boolean> isAbstract,
		
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
		
		var parameters = new ValueSetValidateCodeParameters()
			// XXX: Inject value set ID as a URI into the request
			.setUrl(valueSetId)
			.setCode(code);
		
			
		valueSetVersion.ifPresent(parameters::setValueSetVersion);
		system.ifPresent(parameters::setSystem);
		systemVersion.ifPresent(parameters::setSystemVersion);
		display.ifPresent(parameters::setDisplay);
		isAbstract.ifPresent(parameters::setAbstract);
		date.ifPresent(parameters::setDate);
		
		return validateCode(parameters, accept, _format, _pretty);
	}
	
	/**
	 * <code><b>POST /ValueSet/{id}/$validate-code</b></code>
	 * <p>
	 * All parameters are in the request body, except the valueSetId.
	 * 
	 * @param valueSetId
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to FHIR parameters
	 * @param contentType
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Validate a code in a value set", 
		description = "Validate that a coded value is in a value set."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@PostMapping(
		value = "/{id:**}/$validate-code", 
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
	public Promise<ResponseEntity<byte[]>> validateCode(

		@Parameter(description = "The id of the value set to validate against") 
		@PathVariable(value = "id") 
		String valueSetId, 

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
		
		final var fhirParameters = toFhirParameters(requestBody, contentType);
		final var request = new ValueSetValidateCodeParameters(fhirParameters);
		
		// Before execution set the URI to match the path variable
		request.setUrl(valueSetId);
		
		return validateCode(request, accept, _format, _pretty);
	}

	private Promise<ResponseEntity<byte[]>> validateCode(
		final ValueSetValidateCodeParameters parameters,
		final String accept,
		final String _format,
		final Boolean _pretty
	) {
		return FhirRequests.valueSets().prepareValidateCode()
			.setParameters(parameters)
			.buildAsync()
			.execute(getBus())
			.then(result -> {
				return toResponseEntity(result.getParameters(), accept, _format, _pretty);
			});
	}
}
