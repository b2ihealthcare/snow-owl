/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.concepts;

import org.elasticsearch.core.Set;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.11.0
 */
@Tag(description = "Concepts", name = "concepts")
@RestController
@RequestMapping(value = "/concepts", produces = { AbstractRestService.JSON_MEDIA_TYPE })
public class ConceptSearchRestService extends AbstractRestService {

	public ConceptSearchRestService() {
		super(Set.of("id", "active"));
	}
	
	@Operation(
		summary = "Search concepts",
		description = "Returns a list of concepts matching the given search criteria."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config"), 
	})
	@GetMapping
	public Promise<Concepts> searchConcepts(@ParameterObject ConceptRestSearch params) {
		return CodeSystemRequests.prepareSearchConcepts()
				.filterByActive(params.getActive())
				.filterByTerm(params.getTerm())
				.filterByCodeSystems(params.getCodeSystem())
				.filterByQuery(params.getQuery())
				.setFields(params.getField())
				.setExpand(params.getExpand())
				.setLimit(params.getLimit())
				.setSearchAfter(params.getSearchAfter())
				.sortBy(extractSortFields(params.getSort()))
				.buildAsync()
				.execute(getBus());
	}
	
	@Operation(
		summary = "Search concepts",
		description = "Returns a list of concepts matching the given search criteria."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config"), 
	})
	@PostMapping(value = "/search", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Concepts> searchConceptsByPost(@RequestBody ConceptRestSearch params) {
		return searchConcepts(params);
	}
	
}
