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
package com.b2international.snowowl.core.rest.suggest;

import java.util.List;

import org.elasticsearch.common.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.suggest.ConceptSuggestionBulkRequestBuilder;
import com.b2international.snowowl.core.request.suggest.ConceptSuggestionRequestBuilder;
import com.b2international.snowowl.core.request.suggest.Suggestions;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.CoreApiConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "Concepts", name = CoreApiConfig.CONCEPTS)
@RestController
@RequestMapping(value = "/suggest", produces = { AbstractRestService.JSON_MEDIA_TYPE })
public class SuggestRestService extends AbstractRestService {
	
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
		
		@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
		@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required=false) 
		final String acceptLanguage) {
		return prepareSuggestRequest(body, acceptLanguage)
				.buildAsync()
				.execute(getBus());
	}
	
	@Operation(
		summary = "Bulk concept suggestion", 
		description = "Perform a bulk suggestion request.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@PostMapping(value = "/bulk", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public List<Suggestions> postBulkSuggest(
		@RequestBody
		final List<SuggestRestParameters> body,
		
		@Parameter(description = "The number of suggestion requests to process in a batch (aka parallel)", example = "10")
		@RequestParam(value = "batchSize", defaultValue = "10", required = false)
		final Integer batchSize,
		
		@Parameter(description = "Timeout after a batch of suggestion request will fail and report back as timeout error (specified in seconds)", example = "120")
		@RequestParam(value = "batchTimeout", defaultValue = "120", required = false)
		final Integer batchTimeout,
		
		@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
		@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required=false) 
		final String acceptLanguage) {
	
		if (body == null) {
			throw new BadRequestException("");
		}
		
		ConceptSuggestionBulkRequestBuilder bulk = CodeSystemRequests.prepareBulkSuggestConcepts();
		
		body.stream().map(params -> prepareSuggestRequest(params, acceptLanguage)).forEach(bulk::add);
		
		return bulk
				.setBatchSize(batchSize)
				.setBatchTimeout(batchTimeout)
				.buildAsync()
				.execute(getBus())
				// XXX internal batchTimeout ensures that the request completes in time
				.getSync();
	}
	
	private ConceptSuggestionRequestBuilder prepareSuggestRequest(final SuggestRestParameters params, final String acceptLanguage) {
		return CodeSystemRequests.prepareSuggestConcepts()
				.setFrom(params.getFrom())
				.setLike(params.getLike())
				.setUnlike(params.getUnlike())
				.setSuggester(params.getSuggester())
				.setLimit(params.getLimit())
				.setLocales(Strings.isNullOrEmpty(params.getAcceptLanguage()) ? acceptLanguage : params.getAcceptLanguage())
				.setPreferredDisplay(params.getPreferredDisplay());
	}
}
