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

import java.util.Optional;

import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.codesystem.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.annotations.*;

/**
 * @since 8.0
 */
@Api(value = "CodeSystem", description="FHIR CodeSystem Resource", tags = { "CodeSystem" })
@RestController
@RequestMapping(value="/CodeSystem", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirCodeSystemValidateCodeOperationController extends AbstractFhirController {

	/**
	 * HTTP Get request to validate that a coded value is in the code system specified by the URI parameter
	 * The code system is identified by its Code System ID within the path
	 * If the operation is not called at the instance level, one of the parameters "url" or "codeSystem" must be provided.
	 * The operation returns a result (true / false), an error message, and the recommended display for the code.
     * When invoking this operation, a client SHALL provide one (and only one) of the parameters (code+system, coding, or codeableConcept). 
     * Other parameters (including version and display) are optional.
	 * 
	 * @param url the code system to validate against
	 * @param code to code to validate
	 * @param version the version of the code system to validate against
	 * @param date the date for which the validation should be checked
	 * @param isAbstract If this parameter has a value of true, the client is stating that the validation is being performed in a context 
	 * 			where a concept designated as 'abstract' is appropriate/allowed.
	 *
	 * @return validation results as {@link OperationOutcome}
	 */
	@ApiOperation(
			value="Validate a code in a code system",
			notes="Validate that a coded value is in a code system.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@GetMapping("/$validate-code")
	public Promise<Parameters.Fhir> validateCodeByUrl(
			@ApiParam(value="The uri of the code system to validate against") @RequestParam("url") String url, 
			@ApiParam(value="The code to be validated") @RequestParam(value="code") final String code,
			@ApiParam(value="The version of the code system") @RequestParam(value="version") final Optional<String> version,
			@ApiParam(value="The display string of the code") @RequestParam(value="display") final Optional<String> display,
			@ApiParam(value="The date stamp of the code system to validate against") @RequestParam(value="date") final Optional<String> date,
			@ApiParam(value="The abstract status of the code") @RequestParam(value="abstract") final Optional<Boolean> isAbstract) {
		
		ValidateCodeRequest.Builder builder = ValidateCodeRequest.builder()
			.url(url)
			.code(code)
			.version(version.orElse(null))
			.display(display.orElse(null))
			.isAbstract(isAbstract.orElse(null));
		
		if (date.isPresent()) {
			builder.date(date.get());
		}
				
		//Convert to FHIR parameters and delegate to the POST call
		Json json = new Parameters.Json(builder.build());
		Fhir fhir = new Parameters.Fhir(json.parameters());
		
		return validateCode(fhir);
	}
	
	/**
	 * POST-based $validate-code end-point.
	 * All parameters are in the request body
	 * @param in - FHIR parameters
	 * @return out - FHIR parameters
	 */
	@ApiOperation(value="Validate a code in a code system", notes="Validate that a coded value is in a code system.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@PostMapping(value="/$validate-code", consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Parameters.Fhir> validateCode(@ApiParam(name = "body", value = "The validate-code request parameters") @RequestBody Parameters.Fhir in) {
		
		final ValidateCodeRequest request = toRequest(in, ValidateCodeRequest.class);
		
		request.validate();
		
		return FhirRequests.codeSystems().prepareValidateCode()
				.setRequest(request)
				.buildAsync()
				.execute(getBus())
				.then(this::toResponse);
	}

	
	/**
	 * HTTP Get request to validate that a coded value is in the code system specified by the ID param in the path.
	 * The code system is identified by its Code System ID within the path - 'instance' level call
	 * If the operation is not called at the instance level, one of the parameters "url" or "codeSystem" must be provided.
	 * The operation returns a result (true / false), an error message, and the recommended display for the code.
     * When invoking this operation, a client SHALL provide one (and only one) of the parameters (code+system, coding, or codeableConcept). 
     * Other parameters (including version and display) are optional.
	 * 
	 * @param codeSystemId the code system to validate against
	 * @param code to code to validate
	 * @param version the version of the code system to validate against
	 * @param date the date for which the validation should be checked
	 * @param isAbstract If this parameter has a value of true, the client is stating that the validation is being performed in a context 
	 * 			where a concept designated as 'abstract' is appropriate/allowed.
	 *
	 * @return validation results as {@link OperationOutcome}
	 */
	@ApiOperation(
			value="Validate a code in a code system",
			notes="Validate that a coded value is in a code system.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Code system not found", response = OperationOutcome.class)
	})
	@GetMapping("/{codeSystemId:**}/$validate-code")
	public Promise<Parameters.Fhir> validateCode(
			@ApiParam(value="The id of the code system to validate against") @PathVariable("codeSystemId") String codeSystemId, 
			@ApiParam(value="The code to be validated") @RequestParam(value="code") final String code,
			@ApiParam(value="The version of the code system") @RequestParam(value="version") final Optional<String> version,
			@ApiParam(value="The display string of the code") @RequestParam(value="display") final Optional<String> display,
			@ApiParam(value="The date stamp of the code system to validate against") @RequestParam(value="date") final Optional<String> date,
			@ApiParam(value="The abstract status of the code") @RequestParam(value="abstract") final Optional<Boolean> isAbstract) {
		
		ValidateCodeRequest.Builder builder = ValidateCodeRequest.builder()		
			.code(code)
			.version(version.orElse(null))
			.display(display.orElse(null))
			.isAbstract(isAbstract.orElse(null));
		
		if (date.isPresent()) {
			builder.date(date.get());
		}
				
		//Convert to FHIR parameters and delegate to the POST call
		Json json = new Parameters.Json(builder.build());
		Fhir fhir = new Parameters.Fhir(json.parameters());
		
		return validateCode(codeSystemId, fhir);
	}
	
	/**
	 * POST-based $validate-code end-point.
	 * All parameters are in the request body, except the codeSystemId
	 * @param in - FHIR parameters
	 * @return out - FHIR parameters
	 */
	@ApiOperation(value="Validate a code in a code system", notes="Validate that a coded value is in a code system.")
	@ApiResponses({
		@ApiResponse(code = HTTP_OK, message = "OK"),
		@ApiResponse(code = HTTP_NOT_FOUND, message = "Not found", response = OperationOutcome.class),
		@ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = OperationOutcome.class)
	})
	@PostMapping(value="/{codeSystemId:**}/$validate-code", consumes = AbstractFhirResourceController.APPLICATION_FHIR_JSON)
	public Promise<Parameters.Fhir> validateCode(
			@ApiParam(value="The id of the code system to validate against") @PathVariable("codeSystemId") String codeSystemId, 
			@ApiParam(name = "body", value = "The validate-code request parameters")
			@RequestBody Parameters.Fhir in) {
		
		// TODO set codesystem in validate request
		ResourceURI codeSystemUri = com.b2international.snowowl.core.codesystem.CodeSystem.uri(codeSystemId);
		
		final ValidateCodeRequest request = toRequest(in, ValidateCodeRequest.class);

		//Validate for parameters that are not allowed on the instance level
		if (request.getUrl() != null) {
			throw new BadRequestException("Parameter 'url' cannot be specified when the code system ID is set.", "ValidateCodeRequest.url");
		}
		
		if (request.getCoding() != null) {
			throw new BadRequestException("Parameter 'coding' cannot be specified when the code system ID is set.", "ValidateCodeRequest.coding");
		}
		
		if (request.getCodeableConcept() != null) {
			throw new BadRequestException("Parameter 'codeableConcept' cannot be specified when the code system ID is set.", "ValidateCodeRequest.codeableConcept");
		}
		
		if (request.getCodeSystem() != null) {
			throw new BadRequestException("Validation against external code systems is not supported", "ValidateCodeRequest.codeSystem");
		}

		return FhirRequests.codeSystems().prepareValidateCode()
				.setRequest(request)
				.buildAsync()
				.execute(getBus())
				.then(this::toResponse);
	}
	
}
