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
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;

import io.swagger.annotations.*;

/**
 * @since 1.0
 */
@Api(value = "CodeSystem", description="Code Systems", tags = { "code-systems" })
@RestController
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
	public Promise<CodeSystems> searchByPost(final CodeSystemRestSearch params) {
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
			final CodeSystem codeSystem,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		ApiValidation.checkInput(codeSystem);
		
		if (codeSystem.getUpgradeOf() != null) {
			throw new BadRequestException("'upgradeOf' property cannot be set through code system create API");
		}
		
		final String commitComment = String.format("Created new Code System %s", codeSystem.getId());
		final String codeSystemId = codeSystem.toCreateRequest()
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
	@PutMapping(value = "/{shortNameOrOid}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@ApiParam(value="The code system identifier")
			@PathVariable(value="codeSystemId") 
			final String codeSystemId,
			
			@RequestBody
			final CodeSystem codeSystem,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		final String commitComment = String.format("Updated Code System %s", codeSystemId);
		CodeSystemRequests.prepareUpdateCodeSystem(codeSystemId)
				.setUrl(codeSystem.getUrl())
				.setTitle(codeSystem.getTitle())
				.setLanguage(codeSystem.getLanguage())
				.setDescription(codeSystem.getDescription())
				.setStatus(codeSystem.getStatus())
				.setCopyright(codeSystem.getCopyright())
				.setOwner(codeSystem.getOwner())
				.setContact(codeSystem.getContact())
				.setUsage(codeSystem.getUsage())
				.setPurpose(codeSystem.getPurpose())
				.setOid(codeSystem.getOid())
				.setBranchPath(codeSystem.getBranchPath())
				.setExtensionOf(codeSystem.getExtensionOf())
				.setSettings(codeSystem.getSettings())
				.commit()
				.setAuthor(author)
				.setCommitComment(commitComment)
				.buildAsync()
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	@ApiOperation(
		value="Start a Code System dependency upgrade (EXPERIMENTAL)",
		notes="Starts the upgrade process of a Code System to a newer extensionOf Code System dependency than the current extensionOf."
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Upgrade ", response = Void.class),
		@ApiResponse(code = 400, message = "Code System cannot be upgraded", response = RestApiError.class)
	})
	@PostMapping(value = "/{codeSystemId}/upgrades", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public Promise<ResponseEntity<Void>> upgrade(
			@ApiParam(value="The code system identifier")
			@PathVariable(value="codeSystemId") 
			final String codeSystemId,
			
			@RequestBody
			final UpgradeRestInput body) {
		final CodeSystem codeSystem = CodeSystemRequests.prepareGetCodeSystem(codeSystemId)
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
			
		final UriComponentsBuilder uriBuilder = createURIBuilder();
		
		return CodeSystemRequests.prepareUpgrade(codeSystem.getResourceURI(), ResourceURI.of(codeSystem.getResourceType(), body.getExtensionOf()))
				.setResourceId(body.getCodeSystemId())
				.buildAsync()
				.execute(getBus())
				.then(upgradeCodeSystemId -> {
					return ResponseEntity.created(uriBuilder.pathSegment(upgradeCodeSystemId).build().toUri()).build();
				});
	}

}
