/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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

import org.hl7.fhir.r5.model.CodeType;
import org.hl7.fhir.r5.model.Parameters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.rest.FhirApiConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 9.4.0
 */
@Tag(description="CapabilityStatement", name = FhirApiConfig.CAPABILITY_STATEMENT)
@RestController
public class FhirVersionsOperationController extends AbstractFhirController {

	@Operation(
		summary = "Return the versions supported by this server",
		description = "Returns all FHIR version values supported by this server."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@GetMapping(value = "/$versions", produces = {
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
	public ResponseEntity<byte[]> getVersions(
			
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
		
		var versions = new Parameters();
		
		FhirMediaType.SUPPORTED_FHIR_VERSIONS.forEach(version -> {
			versions.addParameter("version", new CodeType(version.toCode()));
		});
		
		versions.addParameter("default", new CodeType(FhirMediaType.DEFAULT_FHIR_VERSION.toCode()));
		
		return toResponseEntity(versions, accept, _format, _pretty);
	}
	
}
