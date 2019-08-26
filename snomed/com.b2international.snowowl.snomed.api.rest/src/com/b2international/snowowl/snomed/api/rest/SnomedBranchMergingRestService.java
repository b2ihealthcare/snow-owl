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
package com.b2international.snowowl.snomed.api.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeCollection;
import com.b2international.snowowl.core.merge.MergeImpl;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.snomed.api.rest.domain.MergeRestRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.fasterxml.jackson.databind.ObjectMapper;

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
@RequestMapping(value="/merges", produces={AbstractRestService.JSON_MEDIA_TYPE})
public class SnomedBranchMergingRestService extends AbstractRestService {
	
	private static final String SOURCE_KEY = "source";
	private static final String TARGET_KEY = "target";
	private static final String STATUS_KEY = "status";
	
	@Autowired
	private ObjectMapper objectMapper;

	public SnomedBranchMergingRestService() {
		super(Collections.emptySet());
	}
	
	@ApiOperation(
			value = "Start branch merge or rebase", 
			notes = "Signals that making changes on the source branch available on the target branch in the SNOMED CT repository " +
					"should start as soon as possible.")
		@ApiResponses({
			@ApiResponse(code = 202, message = "Accepted"),
			@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
			@ApiResponse(code = 404, message = "Source or Target branch was not found", response=RestApiError.class)
		})
	@RequestMapping(method = RequestMethod.POST, consumes={AbstractRestService.JSON_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
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
		
		final URI linkUri = linkTo(SnomedBranchMergingRestService.class).slash(jobId).toUri();
		return Responses.accepted(linkUri).build();
	}
	
	@ApiOperation(
			value = "Get merge or rebase job", 
			notes = "Retrieves the parameters and status for a queued request.")
		@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
			@ApiResponse(code = 404, message = "Merge request not found in job pool.", response=RestApiError.class)
		})
	@RequestMapping(method = RequestMethod.GET, value="/mergeJob/{id}")
	public DeferredResult<RemoteJobEntry> getMerge(@PathVariable("id") String id) {
		
		final RemoteJobEntry mergeJob = getMergeJob(id);
		
		if (mergeJob != null) {
			return DeferredResults.wrap(Promise.immediate(mergeJob));
		}
		
		throw new NotFoundException("Merge job", id);
	}
	
	@ApiOperation(
			value = "Get merge or rebase result", 
			notes = "Retrieves the merge result of a merge request.")
		@ApiResponses({
			@ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class),
			@ApiResponse(code = 404, message = "Merge request not found in queue", response=RestApiError.class)
		})
	@RequestMapping(method = RequestMethod.GET, value="{id}")
	public DeferredResult<Merge> getMergeResult(@PathVariable("id") String id) {
		return DeferredResults.wrap(JobRequests.prepareSearch()
			.filterById(id)
			.buildAsync()
			.execute(bus)
			.then(jobs -> {
				if (jobs.isEmpty()) {
					throw new NotFoundException("Merge job", id);
				}
				
				final RemoteJobEntry mergeJob = jobs.stream().findFirst().get();
				if (mergeJob.getResult() == null) {
					final Map<String, Object> params = mergeJob.getParameters(objectMapper);
					final String source = (String) params.get(SOURCE_KEY);
					final String target = (String) params.get(TARGET_KEY);
					
					return MergeImpl.builder(source, target).build();
				} else if(mergeJob.isSuccessful()) {
					return mergeJob.getResultAs(objectMapper, Merge.class);
				} else {
					// Job failed to start merge process
					final Map<String, Object> params = mergeJob.getParameters(objectMapper);
					final String source = (String) params.get(SOURCE_KEY);
					final String target = (String) params.get(TARGET_KEY);
					
					final Merge failedMerge = MergeImpl.builder(source, target).build().failed(mergeJob.getResultAs(objectMapper, ApiError.class));
					
					return failedMerge;
				}
			}));
		
	}
	
	@ApiOperation(
			value = "Find merge or rebase status", 
			notes = "Retrieves the parameters and status for queued requests that match the specified condition(s).")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "Bad request", response=RestApiError.class)
	})
	@RequestMapping(method = RequestMethod.GET)
	public DeferredResult<MergeCollection> searchMerge(			
			@ApiParam(value="The source branch path to match")
			@RequestParam(value="source", required = false) 
			final String source,
			
			@ApiParam(value="The target branch path to match")
			@RequestParam(value="target", required = false) 
			final String target,
			
			@ApiParam(value="The current status of the request")
			@RequestParam(value=STATUS_KEY, required = false) 
			final Merge.Status status) {
		return DeferredResults.wrap(JobRequests.prepareSearch()
			.all()
			.buildAsync()
			.execute(bus)
			.then(jobs -> {
				return new MergeCollection(
					jobs.stream()
						.filter(job -> job.getDescription().contains("merging"))
						.filter(job -> job.isDone())
						.filter(job -> {
							final Map<String, Object> jobParams = job.getParameters(objectMapper);
							return jobParams.get(SOURCE_KEY).equals(source)
									&& jobParams.get(TARGET_KEY).equals(target)
									&& jobParams.get(STATUS_KEY).equals(status);
						})
						.map(job -> job.getResultAs(objectMapper, Merge.class))
						.collect(Collectors.toList())
				);
			}));
		
	}
	
	@ApiOperation(
			value="Cancels queued merge or rebase request",
			notes="Removes the request with the given identifier from the queue. If the request is not in progress, this effectively cancels the pending operation.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content, delete successful"),
		@ApiResponse(code = 404, message = "Merge request not found in queue", response=RestApiError.class)
	})
	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	public DeferredResult<ResponseEntity<Void>> deleteMerge(@PathVariable("id") String id) {
		return DeferredResults.wrap(
				RepositoryRequests.merging()
					.prepareDelete(id)
					.build(repositoryId)
					.execute(bus),
				Responses.noContent().build());
	}
	
	public RemoteJobEntry getMergeJob(String id) {
		return JobRequests.prepareSearch()
			.all()
			.filterById(id)
			.buildAsync()
			.execute(bus)
			.getSync().stream().findFirst().orElse(null);
	}
	
}
