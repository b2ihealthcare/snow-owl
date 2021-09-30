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
package com.b2international.snowowl.core.rest.resource;

import java.util.List;
import java.util.Map;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortScript;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.domain.ResourceSelectors;
import com.google.common.collect.Lists;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "Resources", name = "resources")
@RestController
@RequestMapping(value = "/resources", produces = { AbstractRestService.JSON_MEDIA_TYPE })
public class ResourceRestService extends AbstractRestService {
	
	public ResourceRestService() {
		super(Resource.Fields.ALL);
	}

	@Operation(
		summary = "Retrive Resources", 
		description = "Returns a collection resource containing all/filtered registered Resources."
			+ "<p>Results are by default sorted by ID.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@GetMapping
	public Promise<Resources> searchByGet(@ParameterObject final ResourceRestSearch params) {
		
		return ResourceRequests.prepareSearch()
			.filterByIds(params.getId())
			.filterByResourceTypes(params.getResourceType())
			.filterByTitleExact(params.getTitleExact())
			.filterByTitle(params.getTitle())
			.filterByToolingIds(params.getToolingId())
			.filterByBundleIds(params.getBundleId())
			.filterByBundleAncestorIds(params.getBundleAncestorId())
			.filterByStatus(params.getStatus())
			.setLimit(params.getLimit())
			.setExpand(params.getExpand())
			.setFields(params.getField())
			.setSearchAfter(params.getSearchAfter())
			.sortBy(extractSortFields(params.getSort()))
			.buildAsync()
			.execute(getBus());
	}
	
	@Operation(
		summary = "Retrive Resources", 
		description = "Returns a collection resource containing all/filtered registered Resources."
			+ "<p>Results are by default sorted by ID.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@PostMapping(value = "/search", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Resources> searchByPost(@RequestBody final ResourceRestSearch params) {
		return searchByGet(params);
	}

	@Operation(
		summary = "Retrieve resource by it's unique identifier", 
		description = "Returns generic information about a single resource associated to the given unique identifier."
	)
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"), 
		@ApiResponse(responseCode = "404", description = "Not Found") 
	})
	@GetMapping("/{resourceId}")
	public Promise<Resource> get(
			@Parameter(description = "The resource identifier") 
			@PathVariable(value = "resourceId") 
			final String resourceId,
			
			@ParameterObject
			final ResourceSelectors selectors) {
		return ResourceRequests
				.prepareGet(resourceId)
				.setExpand(selectors.getExpand())
				.setFields(selectors.getField())
				.buildAsync()
				.execute(getBus());
	}

}
