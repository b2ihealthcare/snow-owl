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
package com.b2international.snowowl.snomed.core.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.util.DeferredResults;
import com.b2international.snowowl.core.rest.util.Responses;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.snomed.core.rest.domain.BranchUpdateRestRequest;
import com.b2international.snowowl.snomed.core.rest.domain.CreateBranchRestRequest;
import com.google.common.collect.ImmutableList;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 4.1
 */
@Tag(name = "branches", description="Branches")
@RestController
@RequestMapping(value="/branches", produces={AbstractRestService.JSON_MEDIA_TYPE})
public class SnomedBranchingRestService extends AbstractSnomedRestService {

	public SnomedBranchingRestService() {
		super(Branch.Fields.ALL);
	}
	
	@Operation(
		summary = "Create a new branch", 
		description = "Create a new branch in the SNOMED CT repository."
	)
//	@ApiResponses({
//		@ApiResponse(code = 201, message = "Created"),
//		@ApiResponse(code = 400, message = "Bad Request", response=RestApiError.class)
//	})
	@RequestMapping(method=RequestMethod.POST, consumes={AbstractRestService.JSON_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public DeferredResult<ResponseEntity<Void>> createBranch(@RequestBody CreateBranchRestRequest request) {
		ApiValidation.checkInput(request);
		return DeferredResults.wrap(
				RepositoryRequests
					.branching()
					.prepareCreate()
					.setParent(request.getParent())
					.setName(request.getName())
					.setMetadata(request.metadata())
					.build(repositoryId)
					.execute(bus), 
				Responses.created(getBranchLocationHeader(request.path())).build());
	}
	
	@Operation(
		summary = "Retrieve all branches", 
		description = "Returns all SNOMED CT branches from the repository."
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response=CollectionResource.class)
//	})
	@RequestMapping(method=RequestMethod.GET)
	public DeferredResult<Branches> searchBranches(
			@RequestParam(value="parent", required=false)
			final String[] parents,
			
			@RequestParam(value="name", required=false)
			final String[] names,
			
			@Parameter(description = "The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false)
			final String scrollKeepAlive,
			
			@Parameter(description = "A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false)
			final String scrollId,
			
			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort,
			
			@Parameter(description="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {
		return DeferredResults.wrap(
				RepositoryRequests
					.branching()
					.prepareSearch()
					.filterByParent(parents == null ? null : ImmutableList.copyOf(parents))
					.filterByName(names == null ? null : ImmutableList.copyOf(names))
					.sortBy(extractSortFields(sort))
					.setSearchAfter(searchAfter)
					.setScrollId(scrollId)
					.setScroll(scrollKeepAlive)
					.setLimit(limit)
					.build(repositoryId)
					.execute(bus));
	}
	
	@Operation(
		summary = "Retrieve children of a single branch", 
		description = "Returns the children of a single SNOMED CT branch (both direct and transitive)."
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response=CollectionResource.class),
//		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
//	})
	@RequestMapping(value="/{path:**}/children", method=RequestMethod.GET)
	public DeferredResult<Branches> getChildren(@PathVariable("path") String branchPath) {
		return DeferredResults.wrap(
				RepositoryRequests
					.branching()
					.prepareGet(branchPath)
					.setExpand(Branch.Expand.CHILDREN + "()")
					.build(repositoryId)
					.execute(bus)
					.then(Branch::getChildren));
	}
	
	@Operation(
		summary = "Retrieve a single branch", 
		description = "Returns a SNOMED CT branch."
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
//	})
	@GetMapping("/{path:**}")
	public DeferredResult<Branch> getBranch(@PathVariable("path") String branchPath) {
		return DeferredResults.wrap(
				RepositoryRequests
					.branching()
					.prepareGet(branchPath)
					.build(repositoryId)
					.execute(bus));
	}
	
	@Operation(
		summary = "Delete a branch", 
		description = "Deletes a branch and all its children."
				+ "<p>"
				+ "Note that deleted branch are still available and will be listed in <b>GET /branches</b> but with the flag <b>deleted</b> set to <i>true</i>. "
				+ "The API will return <strong>HTTP 400</strong> responses, if clients send requests to <strong>deleted</strong> branches."
				+ "</p>"
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
//	})
	@DeleteMapping("/{path:**}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public DeferredResult<ResponseEntity<Void>> deleteBranch(@PathVariable("path") String branchPath) {
		return DeferredResults.wrap(
				RepositoryRequests
					.branching()
					.prepareDelete(branchPath)
					.build(repositoryId)
					.execute(bus), 
				Responses.noContent().build());
	}
	
	@Operation(
		summary = "Update a branch", 
		description = "Updates a branch"
				+ "<p>"
				+ "The endpoint allows clients to update any metadata properties, other properties are immutable."
				+ "</p>")
//	@ApiResponses({
//		@ApiResponse(code = 204, message = "No Content"),
//		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
//	})
	@PutMapping(value="/{path:**}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public DeferredResult<ResponseEntity<Void>> updateBranch(
			@PathVariable("path") String branchPath,
			@RequestBody BranchUpdateRestRequest request) {
		return DeferredResults.wrap(
				RepositoryRequests
					.branching()
					.prepareUpdate(branchPath)
					.setMetadata(request.getMetadata())
					.build(repositoryId)
					.execute(bus),
				Responses.noContent().build());
	}
	
	private URI getBranchLocationHeader(String branchPath) {
		return linkTo(SnomedBranchingRestService.class).slash(branchPath).toUri();
	}
	
}
