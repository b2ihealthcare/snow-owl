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
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="https://www.hl7.org/fhir/valueset-operations.html">FHIR:ValueSet:Operations</a>
 * @since 8.0
 */
@Tag(description = "ValueSet", name = "ValueSet")
@RestController
@RequestMapping(value="/ValueSet", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirValueSetExpandOperationController extends AbstractFhirController {

	@Operation(
		summary="Expand a value set",
		description="Expand a value set specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "404", description = "Not Found")
	})
	@GetMapping(value="/{id:**}/$expand")
	public Promise<ValueSet> expand(
			@Parameter(description = "The logical id of the value set to expand") 
			@PathVariable(value = "id", required = true) 
			final String id,
			
			@Parameter(description = "Textual filter value to use") 
			@RequestParam(value = "filter", required = false)
			final String filter,
			
			@Parameter(description = "Return only active codes or not (default: return both)") 
			@RequestParam(value = "activeOnly", required = false)
			final Boolean activeOnly,
			
			@Parameter(description = "Specify the display language for the returned codes") 
			@RequestParam(value = "displayLanguage", required = false)
			final String displayLanguage,
			
			@Parameter(description = "Controls whether concept designations are to be included or excluded in value set expansions") 
			@RequestParam(value = "includeDesignations", required = false)
			final Boolean includeDesignations,
			
			@Parameter(description = "The number of codes to return in a page") 
			@RequestParam(value = "count", required = false, defaultValue = "10")
			final Integer count,
			
			@Parameter(description = "Specify the search after value to return the next page") 
			@RequestParam(value = "after", required = false)
			final String after) {
		return FhirRequests.valueSets().prepareExpand()
				.setRequest(ExpandValueSetRequest.builder()
						.url(id)
						.filter(filter)
						.after(after)
						.activeOnly(activeOnly)
						.count(count)
						.displayLanguage(displayLanguage == null ? null : new Code(displayLanguage))
						.build())
				.buildAsync()
				.execute(getBus());
	}
	
	@Operation(
		summary="Expand a value set",
		description="Expand a value set specified by its url."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@GetMapping(value="/$expand")
	public Promise<ValueSet> expandByURL(
			@Parameter(description = "Canonical URL of the value set") 
			@RequestParam(value = "url", required = true) 
			final String url,

			@Parameter(description = "Textual filter value to use") 
			@RequestParam(value = "filter", required = false)
			final String filter,
			
			@Parameter(description = "Return only active codes or not (default: return both)") 
			@RequestParam(value = "activeOnly", required = false)
			final Boolean activeOnly,
			
			@Parameter(description = "Specify the display language for the returned codes") 
			@RequestParam(value = "displayLanguage", required = false)
			final String displayLanguage,
			
			@Parameter(description = "Controls whether concept designations are to be included or excluded in value set expansions") 
			@RequestParam(value = "includeDesignations", required = false)
			final Boolean includeDesignations,
			
			@Parameter(description = "The number of codes to return in a page") 
			@RequestParam(value = "count", required = false, defaultValue = "10")
			final Integer count,
			
			@Parameter(description = "Specify the search after value to return the next page") 
			@RequestParam(value = "after", required = false)
			final String after) {
		return FhirRequests.valueSets().prepareExpand()
				.setRequest(ExpandValueSetRequest.builder()
						.url(url)
						.filter(filter)
						.after(after)
						.activeOnly(activeOnly)
						.count(count)
						.displayLanguage(displayLanguage == null ? null : new Code(displayLanguage))
						.includeDesignations(includeDesignations)
						.build())
				.buildAsync()
				.execute(getBus());
	}
	
	@Operation(
		summary="Expand a value set",
		description="Expand a value set specified by a request body."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@PostMapping(value="/$expand", consumes = AbstractFhirController.APPLICATION_FHIR_JSON)
	public Promise<ValueSet> expandByPost(
			@Parameter(description = "The expansion request parameters")
			@RequestBody 
			Parameters.Fhir body) {
		
		final ExpandValueSetRequest request = toRequest(body, ExpandValueSetRequest.class);
		
		if (request.getUrl() == null && request.getValueSet() == null) {
			throw new BadRequestException("Both URL and ValueSet parameters are null.", "ExpandValueSetRequest");
		}

		if (request.getUrl() == null || request.getUrl().getUriValue() == null) {
			throw new BadRequestException("Expand request URL is not defined.", "ExpandValueSetRequest");
		}
		
		if (request.getUrl() != null && 
				request.getValueSet() != null && 
				request.getUrl().getUriValue() != null &&
				request.getValueSet().getUrl().getUriValue() != null &&
				!request.getUrl().getUriValue().equals(request.getValueSet().getUrl().getUriValue())) {
			throw new BadRequestException("URL and ValueSet.URL parameters are different.", "ExpandValueSetRequest");
		}

		return FhirRequests.valueSets().prepareExpand()
				.setRequest(request)
				.buildAsync()
				.execute(getBus());
	}
	
}
