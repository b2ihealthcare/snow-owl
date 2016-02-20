/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.MergeQueueEntry;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.snomed.api.ISnomedBranchMergingService;
import com.b2international.snowowl.snomed.api.rest.domain.MergeRestRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 4.1
 */
@Api("Branches")
@RestController
@RequestMapping(value="/merges", produces={AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
public class SnomedBranchMergingController extends AbstractRestService {

	@Autowired
	private ISnomedBranchMergingService delegate;
	
	@ApiOperation(
			value = "Enqueue branch merge or rebase", 
			notes = "Signals that making changes on the source branch available on the target branch in the SNOMED CT repository " +
					"should start as soon as possible.")
		@ApiResponses({
			@ApiResponse(code = 202, message = "Accepted"),
			@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
			@ApiResponse(code = 404, message = "Source or Target branch was not found", response=RestApiError.class)
		})
	@RequestMapping(method = RequestMethod.POST, consumes={AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> enqueue(@RequestBody MergeRestRequest restRequest) {
		ApiValidation.checkInput(restRequest);
		
		final Request<ServiceProvider, Branch> busRequest = SnomedRequests
			.branching()
			.prepareMerge()
			.setSource(restRequest.getSource())
			.setTarget(restRequest.getTarget())
			.setReviewId(restRequest.getReviewId())
			.setCommitComment(restRequest.getCommitComment())
			.build();
		
		final UUID id = delegate.enqueue(busRequest, restRequest.getSource(), restRequest.getTarget());
		final URI linkUri = linkTo(SnomedBranchMergingController.class).slash(id).toUri();
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
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	public MergeQueueEntry findEntryById(@PathVariable("id") UUID id) {
		return delegate.findEntryById(id);
	}
	
	@ApiOperation(
			value = "Find merge or rebase status", 
			notes = "Retrieves the parameters and status for queued requests that match the specified condition(s).")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class)
	})
	@RequestMapping(method = RequestMethod.GET)
	public CollectionResource<MergeQueueEntry> findEntryByProperties(			
			@ApiParam(value="The source branch path to match")
			@RequestParam(value="source", required = false) 
			final String source,
			
			@ApiParam(value="The target branch path to match")
			@RequestParam(value="target", required = false) 
			final String target,
			
			@ApiParam(value="The current status of the request")
			@RequestParam(value="status", required = false) 
			final MergeQueueEntry.Status status) {
		
		OptionsBuilder optionsBuilder = OptionsBuilder.newBuilder();
		
		if (!Strings.isNullOrEmpty(source)) { optionsBuilder.put("source", source); }
		if (!Strings.isNullOrEmpty(target)) { optionsBuilder.put("target", target); }
		if (status != null) { optionsBuilder.put("status", status); }
		
		return delegate.findEntryByProperties(optionsBuilder.build());
	}
	
	@ApiOperation(
			value="Cancels queued merge or rebase request",
			notes="Removes the request with the given identifier from the queue. If the request is not in progress, this effectively cancels the pending operation.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, delete successful"),
		@ApiResponse(code = 404, message = "Merge request not found in queue", response=RestApiError.class)
	})
	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	@ResponseStatus(value=HttpStatus.NO_CONTENT)
	public void dequeueMerge(@PathVariable("id") UUID id) {
		delegate.cancel(id);
	}
}
