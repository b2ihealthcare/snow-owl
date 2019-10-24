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

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.snomed.core.rest.domain.BranchUpdateRestRequest;
import com.b2international.snowowl.snomed.core.rest.domain.CreateBranchRestRequest;
import com.google.common.collect.ImmutableList;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 4.1
 */
@Api(value = "Branches", description = "Branches", tags = "branches")
@RestController
@RequestMapping(value="/branches", produces={AbstractRestService.JSON_MEDIA_TYPE})
public class SnomedBranchingRestService extends AbstractSnomedRestService {

	public SnomedBranchingRestService() {
		super(Branch.Fields.ALL);
	}
	
	@ApiOperation(
		value = "Create a new branch", 
		notes = "Create a new branch in the SNOMED CT repository."
	)
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 400, message = "Bad Request", response=RestApiError.class)
	})
	@RequestMapping(method=RequestMethod.POST, consumes={AbstractRestService.JSON_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public Promise<ResponseEntity<Void>> createBranch(@RequestBody CreateBranchRestRequest request) {
		ApiValidation.checkInput(request);
		final URI location = getBranchLocationHeader(request.path());
		return RepositoryRequests
					.branching()
					.prepareCreate()
					.setParent(request.getParent())
					.setName(request.getName())
					.setMetadata(request.metadata())
					.build(repositoryId)
					.execute(getBus())
					.then(success -> ResponseEntity.created(location).build()); 
	}
	
	@ApiOperation(
		value = "Retrieve all branches", 
		notes = "Returns all SNOMED CT branches from the repository."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response=Branches.class)
	})
	@RequestMapping(method=RequestMethod.GET)
	public Promise<Branches> searchBranches(
			@RequestParam(value="parent", required=false)
			final String[] parents,
			
			@RequestParam(value="name", required=false)
			final String[] names,
			
			@ApiParam(value = "The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false)
			final String scrollKeepAlive,
			
			@ApiParam(value = "A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false)
			final String scrollId,
			
			@ApiParam(value = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@ApiParam(value = "Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort,
			
			@ApiParam(value = "The maximum number of items to return", defaultValue = "50")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {
		return RepositoryRequests
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
					.execute(getBus());
	}
	
	@ApiOperation(
		value = "Retrieve children of a single branch", 
		notes = "Returns the children of a single SNOMED CT branch (both direct and transitive)."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response=Branches.class),
		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
	})
	@RequestMapping(value="/{path:**}/children", method=RequestMethod.GET)
	public Promise<Branches> getChildren(@PathVariable("path") String branchPath) {
		return RepositoryRequests
					.branching()
					.prepareGet(branchPath)
					.setExpand(Branch.Expand.CHILDREN + "()")
					.build(repositoryId)
					.execute(getBus())
					.then(Branch::getChildren);
	}
	
	@ApiOperation(
		value = "Retrieve a single branch", 
		notes = "Returns a SNOMED CT branch."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
	})
	@GetMapping("/{path:**}")
	public Promise<Branch> getBranch(@PathVariable("path") String branchPath) {
		return RepositoryRequests
					.branching()
					.prepareGet(branchPath)
					.build(repositoryId)
					.execute(getBus());
	}
	
	@ApiOperation(
		value = "Delete a branch", 
		notes = "Deletes a branch and all its children."
				+ "<p>"
				+ "Note that deleted branch are still available and will be listed in <b>GET /branches</b> but with the flag <b>deleted</b> set to <i>true</i>. "
				+ "The API will return <strong>HTTP 400</strong> responses, if clients send requests to <strong>deleted</strong> branches."
				+ "</p>"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
	})
	@DeleteMapping("/{path:**}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Promise<ResponseEntity<Void>> deleteBranch(@PathVariable("path") String branchPath) {
		return RepositoryRequests
					.branching()
					.prepareDelete(branchPath)
					.build(repositoryId)
					.execute(getBus())
					.then(success -> ResponseEntity.noContent().build());
	}
	
	@ApiOperation(
		value = "Update a branch", 
		notes = "Updates a branch"
				+ "<p>"
				+ "The endpoint allows clients to update any metadata properties, other properties are immutable."
				+ "</p>")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No Content"),
		@ApiResponse(code = 404, message = "Not Found", response=RestApiError.class),
	})
	@PutMapping(value="/{path:**}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Promise<ResponseEntity<Void>> updateBranch(
			@PathVariable("path") String branchPath,
			@RequestBody BranchUpdateRestRequest request) {
		return RepositoryRequests
					.branching()
					.prepareUpdate(branchPath)
					.setMetadata(request.getMetadata())
					.build(repositoryId)
					.execute(getBus())
					.then(success -> ResponseEntity.noContent().build());
	}
	
	private URI getBranchLocationHeader(String branchPath) {
		return MvcUriComponentsBuilder.fromController(SnomedBranchingRestService.class).pathSegment(branchPath).build().toUri();
	}
	
}
