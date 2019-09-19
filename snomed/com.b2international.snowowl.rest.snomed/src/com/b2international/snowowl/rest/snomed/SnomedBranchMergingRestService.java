/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.rest.snomed;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeCollection;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.rest.AbstractRestService;
import com.b2international.snowowl.rest.snomed.domain.MergeRestRequest;
import com.b2international.snowowl.rest.util.DeferredResults;
import com.b2international.snowowl.rest.util.Responses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 4.1
 */
@Tag(name = "branches", description="Branches")
@RestController
@RequestMapping(value="#{snomedApiBaseUrl}/merges", produces={AbstractRestService.JSON_MEDIA_TYPE})
public class SnomedBranchMergingRestService extends AbstractSnomedRestService {
	
	public SnomedBranchMergingRestService() {
		super(Collections.emptySet());
	}
	
	@Operation(
		summary = "Start branch merge or rebase", 
		description = "Signals that making changes on the source branch available on the target branch in the SNOMED CT repository " +
				"should start as soon as possible."
	)
//	@ApiResponses({
//		@ApiResponse(code = 202, message = "Accepted"),
//		@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
//		@ApiResponse(code = 404, message = "Source or Target branch was not found", response=RestApiError.class)
//	})
	@PostMapping(consumes={AbstractRestService.JSON_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> createMerge(@RequestBody MergeRestRequest restRequest, Principal principal) {
		ApiValidation.checkInput(restRequest);
		
		final String sourcePath = restRequest.getSource();
		final String targetPath = restRequest.getTarget();
		final String username = principal.getName();
		
		final Request<ServiceProvider, Merge> mergeRequest = RepositoryRequests.merging()
				.prepareCreate()
				.setSource(sourcePath)
				.setTarget(targetPath)
				.setReviewId(restRequest.getReviewId())
				.setUserId(username)
				.setCommitComment(restRequest.getCommitComment())
				.build(repositoryId)
				.getRequest();
		
		final String jobId = UUID.randomUUID().toString();
		
		JobRequests.prepareSchedule()
			.setId(jobId)
			.setUser(username)
			.setDescription(String.format("Merging branches %s to %s", sourcePath, targetPath))
			.setRequest(mergeRequest)
			.buildAsync()
			.execute(bus);
		
		final URI linkUri = linkTo(SnomedBranchMergingRestService.class).slash("merges").slash(jobId).toUri();
		return Responses.accepted(linkUri).build();
	}
	
	@Operation(
		summary = "Get merge or rebase result", 
		description = "Retrieves the merge result of a merge request."
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
//		@ApiResponse(code = 404, message = "Merge request not found in queue", response=RestApiError.class)
//	})
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public DeferredResult<Merge> getMerge(
			@Parameter(description = "Merge identifier", required = true)
			@PathVariable("id") 
			final String id) {
		return DeferredResults.wrap(RepositoryRequests.merging()
					.prepareGet(id)
					.build(repositoryId)
					.execute(bus));	
	}
	
	@Operation(
		summary = "Returns a list of merge resources", 
		description = "Retrieves the parameters and status for queued requests that match the specified condition(s).")
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class)
//	})
	@GetMapping
	public DeferredResult<MergeCollection> searchMerge(			
			@Parameter(description="The source branch path to match")
			@RequestParam(value="source", required = false) 
			final String source,
			
			@Parameter(description="The target branch path to match")
			@RequestParam(value="target", required = false) 
			final String target,
			
			@Parameter(description="The current status of the request")
			@RequestParam(value="status", required = false) 
			final Merge.Status status) {
		return DeferredResults.wrap(RepositoryRequests.merging()
				.prepareSearch()
					.filterBySource(source)
					.filterByTarget(target)
					.filterByStatus(status != null ? status.name() : null)
					.build(repositoryId)
					.execute(bus));
	}
	
	@Operation(
		summary="Cancels queued merge or rebase request",
		description="Removes the request with the given identifier from the queue. If the request is not in progress, this effectively cancels the pending operation.")
//	@ApiResponses({
//		@ApiResponse(code = 204, message = "No content, delete successful"),
//		@ApiResponse(code = 404, message = "Merge request not found in queue", response=RestApiError.class)
//	})
	@DeleteMapping(value="/{id}")
	public DeferredResult<ResponseEntity<Void>> deleteMerge(
			@Parameter(description = "Merge identifier", required = true)
			@PathVariable("id") 
			final String id) {
		return DeferredResults.wrap(
				RepositoryRequests.merging()
					.prepareDelete(id)
					.build(repositoryId)
					.execute(bus),
				Responses.noContent().build());
	}
	
}
