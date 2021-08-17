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
package com.b2international.snowowl.core.rest.suggestion;

import java.util.List;
import java.util.stream.Collectors;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Suggestions;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.core.rest.AbstractRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "Suggest", name = "suggest")
@RestController
@RequestMapping(value = "/suggest", produces = { AbstractRestService.JSON_MEDIA_TYPE })
public class SuggestionRestService extends AbstractRestService {
	
	private static final SortField SORT_BY = SearchIndexResourceRequest.SCORE;
	
	@Operation(
		summary = "Concept suggestion", 
		description = "Returns an actual concept of the specified code system based on the source term.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@GetMapping
	public Promise<Suggestions> getSuggestion(@ParameterObject final SuggestionRestParameters params) {
		return CodeSystemRequests.prepareSuggestConcepts()
				.setLimit(params.getLimit())
				.setPreferredDisplay(params.getPreferredDisplay())
				.filterByTerm(params.getTerm())
				.sortBy(SORT_BY)
				.build(params.getCodeSystemPath())
				.execute(getBus());
	}
	
	@Operation(
			summary = "Concept suggestion", 
			description = "Returns an actual concept of the specified code system based on the source term.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "201", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@PostMapping
	public Promise<Suggestions> postSuggestion(@RequestBody final SuggestionRestParameters body) {
		return CodeSystemRequests.prepareSuggestConcepts()
				.setLimit(body.getLimit())
				.setPreferredDisplay(body.getPreferredDisplay())
				.filterByTerm(body.getTerm())
				.sortBy(SORT_BY)
				.build(body.getCodeSystemPath())
				.execute(getBus());
	}
	
	@Operation(
		summary = "Bulk concept suggestion", 
		description = "Perform a bulk suggestion request.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "201", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@PostMapping(value = "/bulk")
	public Promise<List<Object>> postBulkSuggestion(@RequestBody final List<SuggestionRestParameters> body) {
		final List<Promise<Suggestions>> promises = body.stream().map(params -> {
			return CodeSystemRequests.prepareSuggestConcepts()
				.setLimit(params.getLimit())
				.setPreferredDisplay(params.getPreferredDisplay())
				.filterByTerm(params.getTerm())
				.sortBy(SORT_BY)
				.build(params.getCodeSystemPath())
				.execute(getBus());
		}).collect(Collectors.toList());
		
		return Promise.all(promises);
	}
}
