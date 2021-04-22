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
package com.b2international.snowowl.core.rest.branch;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.Merges;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 4.1
 */
@Api(value = "Branches", description = "Branches", tags = "branches")
@RequestMapping(value="/merges", produces={AbstractRestService.JSON_MEDIA_TYPE})
public abstract class RepositoryBranchMergeRestService extends AbstractRestService {
	
	private final String repositoryId;

	public RepositoryBranchMergeRestService(String repositoryId) {
		super(Collections.emptySet());
		this.repositoryId = repositoryId;
	}
	
	@ApiOperation(
		value = "Start branch merge or rebase", 
		notes = "Signals that making changes on the source branch available on the target branch in the repository should start as soon as possible."
	)
	@ApiResponses({
		@ApiResponse(code = 202, message = "Accepted"),
		@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
		@ApiResponse(code = 404, message = "Source or Target branch was not found", response=RestApiError.class)
	})
	@PostMapping(consumes={AbstractRestService.JSON_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> createMerge(
			@RequestBody 
			MergeRestRequest restRequest, 
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		ApiValidation.checkInput(restRequest);
		
		final String sourcePath = restRequest.getSource();
		final String targetPath = restRequest.getTarget();
		
		final String jobId = RepositoryRequests.merging()
				.prepareCreate()
				.setSource(sourcePath)
				.setTarget(targetPath)
				.setUserId(author)
				.setCommitComment(restRequest.getCommitComment())
				.setSquash(restRequest.isSquash())
				.build(repositoryId)
				.runAsJob(String.format("Merge branch '%s' into '%s'", sourcePath, targetPath))
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
		
		return ResponseEntity.accepted().location(getResourceLocationURI(jobId)).build();
	}
	
	@ApiOperation(
		value = "Get merge or rebase result", 
		notes = "Retrieves the merge result of a merge request."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
		@ApiResponse(code = 404, message = "Merge request not found in queue", response=RestApiError.class)
	})
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public Promise<Merge> getMerge(
			@ApiParam(value = "Merge identifier", required = true)
			@PathVariable("id") 
			final String id) {
		return RepositoryRequests.merging()
					.prepareGet(id)
					.build(repositoryId)
					.execute(getBus());	
	}
	
	@ApiOperation(
		value = "Returns a list of merge resources", 
		notes = "Retrieves the parameters and status for queued requests that match the specified condition(s).")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class)
	})
	@GetMapping
	public Promise<Merges> searchMerge(			
			@ApiParam(value = "The source branch path to match")
			@RequestParam(value="source", required = false) 
			final String source,
			
			@ApiParam(value = "The target branch path to match")
			@RequestParam(value="target", required = false) 
			final String target,
			
			@ApiParam(value = "The current status of the request")
			@RequestParam(value="status", required = false) 
			final Merge.Status status) {
		return RepositoryRequests.merging()
				.prepareSearch()
					.filterBySource(source)
					.filterByTarget(target)
					.filterByStatus(status != null ? status.name() : null)
					.build(repositoryId)
					.execute(getBus());
	}
	
	@ApiOperation(
		value="Cancels queued merge or rebase request",
		notes="Removes the request with the given identifier from the queue. If the request is not in progress, this effectively cancels the pending operation."
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, delete successful"),
		@ApiResponse(code = 404, message = "Merge request not found in queue", response=RestApiError.class)
	})
	@DeleteMapping(value="/{id}")
	public Promise<ResponseEntity<Void>> deleteMerge(
			@ApiParam(value = "Merge identifier", required = true)
			@PathVariable("id") 
			final String id) {
		return RepositoryRequests.merging()
					.prepareDelete(id)
					.build(repositoryId)
					.execute(getBus())
					.then(success -> ResponseEntity.noContent().build());
	}
	
}
