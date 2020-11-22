/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.job;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.jobs.RemoteJobs;
import com.b2international.snowowl.core.rest.AbstractRestService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 7.1
 */
@Api(value = "Jobs", description="Jobs", tags = { "jobs" })
@RestController
@RequestMapping(value = "/jobs")
public class JobRestService extends AbstractRestService {

	public JobRestService() {
		super(RemoteJobEntry.Fields.SORT_FIELDS);
	}
	
	@ApiOperation(
		value="Returns a list of asynchronous jobs",
		notes="Retrieve currently available asynchronously running/completed jobs."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<RemoteJobs> searchJobs(
			@ApiParam(value = "The Job identifier(s) to match")
			@RequestParam(value = "id", required = false) 
			final Set<String> ids,
			
			@ApiParam(value = "The usernames to match")
			@RequestParam(value = "user", required = false) 
			final String[] users,

			@ApiParam(value="The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false) 
			final String searchAfter,
			
			@ApiParam(value="The maximum number of items to return", defaultValue = "50")
			@RequestParam(value="limit", defaultValue="50", required=false)   
			final int limit,
			
			@ApiParam(value="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort) {
		return JobRequests.prepareSearch()
				.filterByIds(ids)
				.filterByUsers(users == null ? null : Collections3.toImmutableSet(users))
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.sortBy(extractSortFields(sort))
				.buildAsync()
				.execute(getBus());
	}
	
	@ApiOperation(
		value="Returns a single asynchronous job",
		notes="Retrieve a single asynchronously running/completed job by its unique identifier."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<RemoteJobEntry> getJob(
			@ApiParam(value = "Job identifier", required = true)
			@PathVariable(value = "id", required = true) 
			final String id) {
		return JobRequests.prepareGet(id)
				.buildAsync()
				.execute(getBus());
	}
	
}
