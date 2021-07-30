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
package com.b2international.snowowl.core.rest.codesystem;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.common.Strings;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.domain.ResourceRequest;
import com.b2international.snowowl.core.rest.resource.ResourceRestSearch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 1.0
 */
@Tag(description = "Resources", name = "resources")
@RestController
@RequestMapping("/codesystems")
public class CodeSystemRestService extends AbstractRestService {

	@Operation(
		summary="Retrieve CodeSystems", 
		description="Returns a collection resource containing all/filtered registered CodeSystems."
			+ "<p>Results are by default sorted by ID."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; availableUpgrades() &ndash; a list of possible code system resource URIs that can be used as an 'extensionOf' property"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CodeSystems> searchByGet(@ParameterObject final ResourceRestSearch params) {
		return CodeSystemRequests.prepareSearchCodeSystem()
				.filterByIds(params.getId())
				.filterByOids(params.getOid())
				.filterByTitle(params.getTitle())
				.filterByTitleExact(params.getTitleExact())
				.filterByToolingIds(params.getToolingId())
				.filterByBundleIds(params.getBundleId())
				.filterByStatus(params.getStatus())
				.setLimit(params.getLimit())
				.setExpand(params.getExpand())
				.setFields(params.getField())
				.setSearchAfter(params.getSearchAfter())
				.sortBy(extractSortFields(params.getSort()))
				.buildAsync()
				.execute(getBus());
	}
	
	@Operation(
		summary="Retrieve Code Systems", 
		description="Returns a collection resource containing all/filtered registered Code Systems."
		    + "<p>Results are always sorted by repositoryUuid first, sort keys only apply per repository."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; availableUpgrades() &ndash; a list of possible code system URIs that can be used as an 'extensionOf' property"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config"),
		@ApiResponse(responseCode = "404", description = "Branch not found")
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<CodeSystems> searchByPost(
			@RequestBody(required = false)
			final ResourceRestSearch params) {
		return searchByGet(params);
	}

	@Operation(
		summary="Retrieve code system by its unique identifier",
		description="Returns generic information about a single code system associated to the given unique identifier."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@GetMapping(value = "/{codeSystemId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CodeSystem> get(
			@Parameter(description="The code system identifier")
			@PathVariable(value="codeSystemId") final String codeSystemId) {
		return CodeSystemRequests.prepareGetCodeSystem(codeSystemId)
				.buildAsync()
				.execute(getBus());
	}
	
	@Operation(
		summary="Create a code system",
		description="Create a new Code System with the given parameters"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "409", description = "CodeSystem already exists in the system")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@RequestBody
			final ResourceRequest<CodeSystemCreateRestInput> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		
		final String commitComment = Strings.isNullOrEmpty(body.getCommitComment()) ? String.format("Created new Code System %s", body.getChange().getId()) : body.getCommitComment();
		final String codeSystemId = body.getChange().toCodeSystemCreateRequest()
				.build(author, commitComment)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		return ResponseEntity.created(getResourceLocationURI(codeSystemId)).build();
	}
	
	@Operation(
		summary = "Update a code system",
		description = "Update a Code System with the given parameters"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content"),
		@ApiResponse(responseCode = "400", description = "Bad Request")
	})
	@PutMapping(value = "/{codeSystemId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@Parameter(description = "The code system identifier")
			@PathVariable(value="codeSystemId") 
			final String codeSystemId,
			
			@RequestBody
			final ResourceRequest<CodeSystemUpdateRestInput> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		final String commitComment = Strings.isNullOrEmpty(body.getCommitComment()) ? String.format("Updated Code System %s", codeSystemId) : body.getCommitComment();
		body.getChange().toCodeSystemUpdateRequest(codeSystemId)
				.build(author, commitComment)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	@Operation(
			summary="Delete a CodeSystem",
			description="Deletes a CodeSystem permanently from the server")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "409", description = "CodeSystem cannot be deleted")
	})
	@DeleteMapping(value = "/{codeSystemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@Parameter(description = "The code system identifier")
			@PathVariable(value="codeSystemId") 
			final String codeSystemId,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		ResourceRequests.prepareDelete(codeSystemId)
			.build(author, "Deleted ".concat(codeSystemId))
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
}
