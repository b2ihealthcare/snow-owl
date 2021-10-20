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
package com.b2international.snowowl.core.rest.suggest;

import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.common.Strings;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Suggestions;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.core.rest.AbstractRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "Suggest", name = "suggest")
@RestController
@RequestMapping(value = "/suggest", produces = { AbstractRestService.JSON_MEDIA_TYPE })
public class SuggestRestService extends AbstractRestService {
	
	private static final SortField SORT_BY = SearchIndexResourceRequest.SCORE;
	
	@Operation(
		summary = "Concept suggestion", 
		description = "Returns an actual concept of the specified code system based on the source term.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@GetMapping
	public Promise<Suggestions> getSuggest(
		@ParameterObject
		final SuggestRestParameters params,
		
		@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
		@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
		final String acceptLanguage) {
		
		return CodeSystemRequests.prepareSuggestConcepts()
				.setLimit(params.getLimit())
				.setLocales(Strings.isNullOrEmpty(params.getAcceptLanguage()) ? acceptLanguage : params.getAcceptLanguage())
				.setPreferredDisplay(params.getPreferredDisplay())
				.setMinOccurrenceCount(params.getMinOccurrenceCount())
				.filterByTerm(params.getTerm())
				.filterByQuery(params.getQuery())
				.filterByExclusion(params.getMustNotQuery())
				.sortBy(SORT_BY)
				.build(params.getCodeSystemPath())
				.execute(getBus());
	}
	
	@Operation(
		summary = "Concept suggestion", 
		description = "Returns an actual concept of the specified code system based on the source term.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Suggestions> postSuggest(
		@RequestBody
		final SuggestRestParameters body,
		
		@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
		@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
		final String acceptLanguage) {
		return getSuggest(body, acceptLanguage);
	}
	
	@Operation(
		summary = "Bulk concept suggestion", 
		description = "Perform a bulk suggestion request.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@PostMapping(value = "/bulk", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<List<Object>> postBulkSuggest(
		@RequestBody
		final List<SuggestRestParameters> body,
		
		@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
		@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
		final String acceptLanguage) {
		
		final List<Promise<Suggestions>> promises = body.stream()
				.map(params -> getSuggest(params, acceptLanguage))
				.collect(Collectors.toList());
	
		return Promise.all(promises);
	}
}
