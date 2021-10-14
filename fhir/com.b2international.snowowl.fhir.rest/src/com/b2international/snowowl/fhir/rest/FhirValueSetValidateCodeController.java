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

import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "ValueSet", name = "ValueSet")
@RestController
@RequestMapping(value="/ValueSet", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirValueSetValidateCodeController extends AbstractFhirController {

	
	/**
	 * HTTP Get request to validate that a coded value is in the set of codes allowed by a value set.
	 * The value set is identified by its Value Set ID
	 * @param id the logical ID of the valueSet
	 * @param code code to validate
	 * @param system the code system of the code to validate
	 * @param systemVersion the optional version of the code to validate
	 *
	 * @return validation results as {@link OperationOutcome}
	 */
	@Operation(
		summary="Validate a code in a value set",
		description="Validate that a coded value is in the set of codes allowed by a value set."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@GetMapping(value="/{id:**}/$validate-code")
	public Promise<Parameters.Fhir> validateCode(
			@Parameter(description = "The id of the value set to validate against") @PathVariable("id") String id, 
			@Parameter(description = "The code to to be validated") @RequestParam(value="code") final String code,
			@Parameter(description = "The system uri of the code to be validated") @RequestParam(value="system") final String system,
			@Parameter(description = "The code system version of the code to be validated") @RequestParam(value="version", required=false) final String systemVersion) {
		
		ValidateCodeRequest request = ValidateCodeRequest.builder()
				.url(id)
				.code(code)
				.system(system)
				.systemVersion(systemVersion)
				.build();
		
		return FhirRequests.valueSets().prepareValidateCode()
			.setRequest(request)
			.buildAsync()
			.execute(getBus())
			.then(this::toResponse);
	}
	
	/**
	 * HTTP Get request to validate that a coded value is in the set of codes allowed by a value set.
	 * The value set is identified by its canonical URL (SNOMED CT for example)
	 
	 * @param url the canonical URL of the value set to validate the code against
	 * @param code code to validate
	 * @param system the code system of the code to validate
	 * @param systemVersion the optional version of the code to validate
	 * @return validation results as {@link OperationOutcome}
	 */
	@Operation(
		summary="Validate a code in a value set defined by its URL",
		description="Validate that a coded value is in the set of codes allowed by a value set."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@RequestMapping(value="/$validate-code", method=RequestMethod.GET)
	public Promise<Parameters.Fhir> validateCodeByURL(
			@Parameter(description = "Canonical URL of the value set") @RequestParam(value="url") final String url,
			@Parameter(description = "The code to to be validated") @RequestParam(value="code") final String code,
			@Parameter(description = "The system uri of the code to be validated") @RequestParam(value="system") final String system,
			@Parameter(description = "The code system version of the code to be validated") @RequestParam(value="version", required=false) final String systemVersion) {
		
		ValidateCodeRequest request = ValidateCodeRequest.builder()
			.url(url)
			.code(code)
			.system(system)
			.systemVersion(systemVersion)
			.build();
		
		return FhirRequests.valueSets().prepareValidateCode()
				.setRequest(request)
				.buildAsync()
				.execute(getBus())
				.then(this::toResponse);
	}
	
}
