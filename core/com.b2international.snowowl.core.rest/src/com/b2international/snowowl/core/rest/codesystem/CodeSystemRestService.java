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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;

import io.swagger.annotations.*;

/**
 * @since 1.0
 */
@Api(value = "Resources", description="Resources", tags = { "resources" })
@RestController
@RequestMapping("/codesystems")
public class CodeSystemRestService extends AbstractRestService {

	@ApiOperation(
		value="Retrieve CodeSystems", 
		notes="Returns a collection resource containing all/filtered registered CodeSystems."
			+ "<p>Results are by default sorted by ID."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; availableUpgrades() &ndash; a list of possible code system resource URIs that can be used as an 'extensionOf' property"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CodeSystems.class),
		@ApiResponse(code = 400, message = "Bad Request", response = RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CodeSystems> searchByGet(final CodeSystemRestSearch params) {
		return CodeSystemRequests.prepareSearchCodeSystem()
				.filterByIds(params.getId())
				.filterByOids(params.getOid())
				.filterByTitle(params.getTitle())
				.filterByToolingIds(params.getToolingId())
				.setLimit(params.getLimit())
				.setExpand(params.getExpand())
				.setSearchAfter(params.getSearchAfter())
				.sortBy(extractSortFields(params.getSort()))
				.buildAsync()
				.execute(getBus());
	}
	
	@ApiOperation(
		value="Retrieve Code Systems", 
		notes="Returns a collection resource containing all/filtered registered Code Systems."
		    + "<p>Results are always sorted by repositoryUuid first, sort keys only apply per repository."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; availableUpgrades() &ndash; a list of possible code system URIs that can be used as an 'extensionOf' property"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CodeSystems.class),
		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<CodeSystems> searchByPost(
			@RequestBody(required = false)
			final CodeSystemRestSearch params) {
		return searchByGet(params);
	}

	@ApiOperation(
			value="Retrieve code system by its unique identifier",
			notes="Returns generic information about a single code system associated to the given unique identifier.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Not found", response = RestApiError.class)
	})
	@GetMapping(value = "/{codeSystemId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CodeSystem> get(
			@ApiParam(value="The code system identifier")
			@PathVariable(value="codeSystemId") final String codeSystemId) {
		return CodeSystemRequests.prepareGetCodeSystem(codeSystemId)
				.buildAsync()
				.execute(getBus());
	}
	
	@ApiOperation(
		value="Create a code system",
		notes="Create a new Code System with the given parameters"
	)
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created", response = Void.class),
		@ApiResponse(code = 400, message = "CodeSystem already exists in the system", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@RequestBody
			final CodeSystemCreateRestInput params,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		final String commitComment = String.format("Created new Code System %s", params.getId());
		final String codeSystemId = CodeSystemRequests.prepareNewCodeSystem()
				.setId(params.getId())
				.setBundleId(params.getBundleId())
				.setUrl(params.getUrl())
				.setTitle(params.getTitle())
				.setLanguage(params.getLanguage())
				.setDescription(params.getDescription())
				.setStatus(params.getStatus())
				.setCopyright(params.getCopyright())
				.setOwner(params.getOwner())
				.setContact(params.getContact())
				.setUsage(params.getUsage())
				.setPurpose(params.getPurpose())
				.setOid(params.getOid())
				.setBranchPath(params.getBranchPath())
				.setToolingId(params.getToolingId())
				.setExtensionOf(params.getExtensionOf())
				.setSettings(params.getSettings())
				.commit() 
				.setAuthor(author)
				.setCommitComment(commitComment)
				.buildAsync()
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		return ResponseEntity.created(getResourceLocationURI(codeSystemId)).build();
	}
	
	@ApiOperation(
			value="Update a code system",
			notes="Update a Code System with the given parameters")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content", response = Void.class),
		@ApiResponse(code = 400, message = "Code System cannot be updated", response = RestApiError.class)
	})
	@PutMapping(value = "/{codeSystemId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@ApiParam(value="The code system identifier")
			@PathVariable(value="codeSystemId") 
			final String codeSystemId,
			
			@RequestBody
			final CodeSystemUpdateRestInput params,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		final String commitComment = String.format("Updated Code System %s", codeSystemId);
		CodeSystemRequests.prepareUpdateCodeSystem(codeSystemId)
				.setUrl(params.getUrl())
				.setTitle(params.getTitle())
				.setLanguage(params.getLanguage())
				.setDescription(params.getDescription())
				.setStatus(params.getStatus())
				.setCopyright(params.getCopyright())
				.setOwner(params.getOwner())
				.setContact(params.getContact())
				.setUsage(params.getUsage())
				.setPurpose(params.getPurpose())
				.setBundleId(params.getBundleId())
				.setOid(params.getOid())
				.setBranchPath(params.getBranchPath())
				.setExtensionOf(params.getExtensionOf())
				.setSettings(params.getSettings())
				.commit()
				.setAuthor(author)
				.setCommitComment(commitComment)
				.buildAsync()
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	@ApiOperation(
			value="Delete a CodeSystem",
			notes="Deletes a CodeSystem permanently from the server")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content", response = Void.class),
		@ApiResponse(code = 400, message = "Bad Request", response = RestApiError.class),
		@ApiResponse(code = 409, message = "CodeSystem cannot be deleted", response = RestApiError.class)
	})
	@DeleteMapping(value = "/{codeSystemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@ApiParam(value="The code system identifier")
			@PathVariable(value="codeSystemId") 
			final String codeSystemId,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		ResourceRequests.prepareDelete(codeSystemId)
			.build(author, "Delete ".concat(codeSystemId))
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
}
