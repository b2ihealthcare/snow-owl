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

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.jobs.RemoteJobs;
import com.b2international.snowowl.core.rest.AbstractRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.1
 */
@Tag(description="Jobs", name = "jobs")
@RestController
@RequestMapping(value = "/jobs")
public class JobRestService extends AbstractRestService {

	public JobRestService() {
		super(RemoteJobEntry.Fields.SORT_FIELDS);
	}
	
	@Operation(
		summary = "Returns a list of asynchronous jobs",
		description = "Retrieve currently available asynchronously running/completed jobs."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK"),
		@ApiResponse(responseCode = "400", description="Bad Request")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<RemoteJobs> searchJobs(
			@Parameter(description = "The Job identifier(s) to match")
			@RequestParam(value = "id", required = false) 
			final Set<String> ids,
			
			@Parameter(description = "The usernames to match")
			@RequestParam(value = "user", required = false) 
			final String user,
			
			@Parameter(description="The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false) 
			final String searchAfter,
			
			@Parameter(description="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false)   
			final int limit,
			
			@Parameter(description="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort) {
		return JobRequests.prepareSearch()
				.filterByIds(ids)
				.filterByUser(user)
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.sortBy(extractSortFields(sort))
				.buildAsync()
				.execute(getBus());
	}
	
	@Operation(
		summary = "Returns a single asynchronous job",
		description="Retrieve a single asynchronously running/completed job by its unique identifier."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK"),
		@ApiResponse(responseCode = "400", description="Bad Request")
	})
	@GetMapping(value = "/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<RemoteJobEntry> getJob(
			@Parameter(description = "Job identifier", required = true)
			@PathVariable(value = "id", required = true) 
			final String id) {
		return JobRequests.prepareGet(id)
				.buildAsync()
				.execute(getBus());
	}
	
}
