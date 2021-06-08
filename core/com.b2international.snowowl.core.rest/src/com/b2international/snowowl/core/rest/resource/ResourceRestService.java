package com.b2international.snowowl.core.rest.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 8.0
 */
@Api(value = "Resources", description = "Resources", tags = { "resources" })
@RestController
@RequestMapping("/resources")
public class ResourceRestService extends AbstractRestService {

	@ApiOperation(
			value = "Retrive Resources",
			notes="Returns a collection resource containing all/filtered registered Resources."
					+ "<p>Results are by default sorted by ID."
			)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Resources.class),
		@ApiResponse(code = 400, message = "Bad Request", response = RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Resources> searchByGet(final ResourceRestSearch params) {
		return ResourceRequests
				.prepareSearch()
				.filterByIds(params.getId())
				.filterByResourceType(params.getResourceType())
				.filterByTitleExact(params.getTitleExact())
				.filterByTitle(params.getTitle())
				.filterByToolingIds(params.getToolingId())
				.setLimit(params.getLimit())
				.setExpand(params.getExpand())
				.setSearchAfter(params.getSearchAfter())
				.sortBy(extractSortFields(params.getSort()))
				.buildAsync()
				.execute(getBus());
	}
	
	@ApiOperation(
			value = "Retrieve resource by it's unique identifier",
			notes = "Returns generic information about a single resource associated to the given unique identifier.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Not Found", response = RestApiError.class)
	})
	@GetMapping(value = "/{resourceId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Resource> get(
			@ApiParam(value = "The resource identifier")
			@PathVariable(value = "resourceId") 
			final String resourceId) {
		return ResourceRequests
				.prepareGet(resourceId)
				.buildAsync()
				.execute(getBus());
	}
}
