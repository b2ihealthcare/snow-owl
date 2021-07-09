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

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.google.common.collect.ImmutableList;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 4.1
 */
@Tag(description = "Branches", name = "branches")
@RequestMapping(value="/branches", produces={AbstractRestService.JSON_MEDIA_TYPE})
public abstract class RepositoryBranchRestService extends AbstractRestService {

	private final String repositoryId;

	public RepositoryBranchRestService(String repositoryId) {
		super(Branch.Fields.ALL);
		this.repositoryId = repositoryId;
	}
	
	@Operation(
		summary = "Create a new branch", 
		description = "Create a new branch in the SNOMED CT repository."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "400", description = "Bad Request")
	})
	@RequestMapping(method=RequestMethod.POST, consumes={AbstractRestService.JSON_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public Promise<ResponseEntity<Void>> createBranch(@RequestBody CreateBranchRestRequest request) {
		ApiValidation.checkInput(request);
		final URI location = getResourceLocationURI(request.path());
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
	
	@Operation(
		summary = "Retrieve all branches", 
		description = "Returns all SNOMED CT branches from the repository."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public Promise<Branches> searchBranches(
			@RequestParam(value="parent", required=false)
			final String[] parents,
			
			@RequestParam(value="name", required=false)
			final String[] names,
			
			@Parameter(description = "The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false)
			final String searchAfter,
			
			@Parameter(description = "Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sort,
			
			@Parameter(description = "The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {
		return RepositoryRequests
					.branching()
					.prepareSearch()
					.filterByParent(parents == null ? null : ImmutableList.copyOf(parents))
					.filterByName(names == null ? null : ImmutableList.copyOf(names))
					.sortBy(extractSortFields(sort))
					.setSearchAfter(searchAfter)
					.setLimit(limit)
					.build(repositoryId)
					.execute(getBus());
	}
	
	@Operation(
		summary = "Retrieve children of a single branch", 
		description = "Returns the children of a single SNOMED CT branch (both direct and transitive)."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not Found"),
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
	
	@Operation(
		summary = "Retrieve a single branch", 
		description = "Returns a SNOMED CT branch."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not Found"),
	})
	@GetMapping("/{path:**}")
	public Promise<Branch> getBranch(@PathVariable("path") String branchPath) {
		return RepositoryRequests
					.branching()
					.prepareGet(branchPath)
					.build(repositoryId)
					.execute(getBus());
	}
	
	@Operation(
		summary = "Delete a branch", 
		description = "Deletes a branch and all its children."
				+ "<p>"
				+ "Note that deleted branch are still available and will be listed in <b>GET /branches</b> but with the flag <b>deleted</b> set to <i>true</i>. "
				+ "The API will return <strong>HTTP 400</strong> responses, if clients send requests to <strong>deleted</strong> branches."
				+ "</p>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not Found"),
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
	
	@Operation(
		summary = "Update a branch", 
		description = "Updates a branch"
				+ "<p>"
				+ "The endpoint allows clients to update any metadata properties, other properties are immutable."
				+ "</p>")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No Content"),
		@ApiResponse(responseCode = "404", description = "Not Found"),
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
	
}
