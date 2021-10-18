/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Value Set contains codes from one or more code systems.
 *  
 * @see <a href="https://www.hl7.org/fhir/valueset.html">FHIR:ValueSet</a>
 * @since 6.4
 */
@Tag(description = "ValueSet", name = "ValueSet")
@RestController
@RequestMapping(value="/ValueSet", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirValueSetController extends AbstractFhirController {
	
	@Operation(
		summary="Retrieve all value sets",
		description="Returns a collection of the supported value sets."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping
	public Promise<Bundle> getValueSets(@ParameterObject FhirValueSetSearchParameters params) {
		return FhirRequests.valueSets().prepareSearch()
				.filterByIds(asList(params.get_id()))
				.filterByNames(asList(params.getName()))
				.filterByTitle(params.getTitle())
				.filterByLastUpdated(params.get_lastUpdated())
				.filterByUrls(Collections3.intersection(params.getUrl(), params.getSystem())) // values defined in both url and system match the same field, compute intersection to simulate ES behavior here
				.filterByVersions(params.getVersion())
				.setSearchAfter(params.get_after())
				.setCount(params.get_count())
				// XXX _summary=count may override the default _count=10 value, so order of method calls is important here
				.setSummary(params.get_summary())
				.setElements(params.get_elements())
				.sortByFields(params.get_sort())
				.buildAsync()
				.execute(getBus());
	}
	
	/**
	 * HTTP GET endpoint for retrieving a value set by its logical identifier.
	 * @param id
	 * @param selectors
	 * @return
	 */
	@Operation(
		summary="Retrieve the value set by id",
		description="Retrieves the value set specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@GetMapping(value="/{id:**}")
	public Promise<ValueSet> getValueSet(
			@Parameter(description = "The identifier of the ValueSet resource")
			@PathVariable(value = "id") 
			final String id,
	
			@ParameterObject
			final FhirResourceSelectors selectors) {
		return FhirRequests.valueSets().prepareGet(id)
				.setElements(selectors.get_elements())
				.setSummary(selectors.get_summary())
				.buildAsync()
				.execute(getBus());
	}
	
}
