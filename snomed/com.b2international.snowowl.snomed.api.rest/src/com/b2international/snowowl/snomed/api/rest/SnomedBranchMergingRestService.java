/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeCollection;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.snomed.api.rest.domain.MergeRestRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 4.1
 */
@Api(value = "Branches", description="Branches", tags = { "branches" })
@RestController
@RequestMapping(value="/merges") 
public class SnomedBranchMergingRestService extends AbstractRestService {

	@ApiOperation(
			value = "Start branch merge or rebase", 
			notes = "Signals that making changes on the source branch available on the target branch in the SNOMED CT repository " +
					"should start as soon as possible.")
		@ApiResponses({
			@ApiResponse(code = 202, message = "Accepted"),
			@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
			@ApiResponse(code = 404, message = "Source or Target branch was not found", response=RestApiError.class)
		})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public ResponseEntity<Void> createMerge(@RequestBody MergeRestRequest restRequest, Principal principal) {
		ApiValidation.checkInput(restRequest);
		
		final Merge merge = RepositoryRequests.merging()
			.prepareCreate()
			.setSource(restRequest.getSource())
			.setTarget(restRequest.getTarget())
			.setReviewId(restRequest.getReviewId())
			.setUserId(principal.getName())
			.setCommitComment(restRequest.getCommitComment())
			.build(repositoryId)
			.execute(bus)
			.getSync();
		
		final URI linkUri = linkTo(SnomedBranchMergingRestService.class).slash(merge.getId()).toUri();
		return Responses.accepted(linkUri).build();
	}
	
	@ApiOperation(
			value = "Get merge or rebase status", 
			notes = "Retrieves the parameters and status for a queued request.")
		@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
			@ApiResponse(code = 404, message = "Merge request not found in queue", response=RestApiError.class)
		})
	@GetMapping(value="/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public DeferredResult<Merge> getMerge(@PathVariable("id") UUID id) {
		return DeferredResults.wrap(RepositoryRequests.merging().prepareGet(id).build(repositoryId).execute(bus));
	}
	
	@ApiOperation(
			value = "Find merge or rebase status", 
			notes = "Retrieves the parameters and status for queued requests that match the specified condition(s).")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public DeferredResult<MergeCollection> searchMerge(			
			@ApiParam(value="The source branch path to match")
			@RequestParam(value="source", required = false) 
			final String source,
			
			@ApiParam(value="The target branch path to match")
			@RequestParam(value="target", required = false) 
			final String target,
			
			@ApiParam(value="The current status of the request")
			@RequestParam(value="status", required = false) 
			final Merge.Status status) {
		
		 return DeferredResults.wrap(RepositoryRequests.merging()
				.prepareSearch()
				.withSource(source)
				.withTarget(target)
				.withStatus(status)
				.build(repositoryId)
				.execute(bus));
	}
	
	@ApiOperation(
			value="Cancels queued merge or rebase request",
			notes="Removes the request with the given identifier from the queue. If the request is not in progress, this effectively cancels the pending operation.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, delete successful"),
		@ApiResponse(code = 404, message = "Merge request not found in queue", response=RestApiError.class)
	})
	@DeleteMapping(value="/{id}")
	public DeferredResult<ResponseEntity<Void>> deleteMerge(@PathVariable("id") UUID id) {
		return DeferredResults.wrap(
				RepositoryRequests.merging()
					.prepareDelete(id)
					.build(repositoryId)
					.execute(bus),
				Responses.noContent().build());
	}
}
