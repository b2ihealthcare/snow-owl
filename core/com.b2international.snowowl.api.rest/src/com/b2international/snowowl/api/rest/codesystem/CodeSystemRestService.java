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
package com.b2international.snowowl.api.rest.codesystem;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.api.rest.AbstractRestService;
import com.b2international.snowowl.api.rest.domain.RestApiError;
import com.b2international.snowowl.api.rest.util.Responses;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.datastore.CodeSystem;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api(value = "CodeSystem", description="Code Systems", tags = { "code-systems" })
@RestController
@RequestMapping(value = "/codesystems") 
public class CodeSystemRestService extends AbstractRestService {

	@Autowired
	private CodeSystemService codeSystemService;
	
	@ApiOperation(
			value="Retrieve all code systems",
			notes="Returns a list containing generic information about registered code systems.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public CollectionResource<CodeSystem> getCodeSystems() {
		return CollectionResource.of(codeSystemService.getCodeSystems());
	}

	@ApiOperation(
			value="Retrieve code system by short name or OID",
			notes="Returns generic information about a single code system with the specified short name or OID.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system not found", response = RestApiError.class)
	})
	@GetMapping(value = "/{shortNameOrOid}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public CodeSystem getCodeSystemByShortNameOrOid(
			@ApiParam(value="The code system identifier (short name or OID)")
			@PathVariable(value="shortNameOrOid") final String shortNameOrOId) {
		return codeSystemService.getCodeSystemById(shortNameOrOId);
	}
	
	@ApiOperation(
			value="Create a code system",
			notes="Create a new Code System with the given parameters")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created", response = Void.class),
		@ApiResponse(code = 400, message = "Code System already exists in the system", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createCodeSystem(
			@RequestBody
			final CodeSystem codeSystem,
			final Principal principal) {

		ApiValidation.checkInput(codeSystem);
		
		final String userId = principal.getName();
		final String commitComment = String.format("Created new Code System %s", codeSystem.getShortName());
		
		final String shortName = CodeSystemRequests
				.prepareNewCodeSystem()
				.setBranchPath(codeSystem.getBranchPath())
				.setCitation(codeSystem.getCitation())
				.setIconPath(codeSystem.getIconPath())
				.setLanguage(codeSystem.getPrimaryLanguage())
				.setLink(codeSystem.getOrganizationLink())
				.setName(codeSystem.getName())
				.setOid(codeSystem.getOid())
				.setRepositoryUuid(codeSystem.getRepositoryUuid())
				.setShortName(codeSystem.getShortName())
				.setTerminologyId(codeSystem.getTerminologyId())
				.setExtensionOf(codeSystem.getExtensionOf())
				.build(codeSystem.getRepositoryUuid(), IBranchPath.MAIN_BRANCH, userId, commitComment)
				.execute(bus)
				.getSync().getResultAs(String.class);
		
		return Responses
				.created(linkTo(CodeSystemRestService.class)
				.slash(shortName)
				.toUri())
				.build();
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
	public void updateCodeSystem(
			@ApiParam(value="The code system identifier (short name or OID)")
			@PathVariable(value="shortNameOrOid") 
			final String shortNameOrOId,
			
			@RequestBody
			final CodeSystem codeSystem,
			
			final Principal principal
			) {
		validateUpdateInput(shortNameOrOId, codeSystem.getRepositoryUuid());
		final String commitComment = String.format("Updated Code System %s", shortNameOrOId);
		
		CodeSystemRequests
				.prepareUpdateCodeSystem(shortNameOrOId)
				.setName(codeSystem.getName())
				.setBranchPath(codeSystem.getBranchPath())
				.setCitation(codeSystem.getCitation())
				.setIconPath(codeSystem.getIconPath())
				.setLanguage(codeSystem.getPrimaryLanguage())
				.setLink(codeSystem.getOrganizationLink())
				.build(codeSystem.getRepositoryUuid(), IBranchPath.MAIN_BRANCH, principal.getName(), commitComment)
				.execute(bus)
				.getSync();
	}

	private void validateUpdateInput(final String shortNameOrOId, final String repositoryUuid) {
		if (StringUtils.isEmpty(shortNameOrOId)) {
			throw new BadRequestException("Unique ID cannot be empty for Code System update.");
		} else if (StringUtils.isEmpty(repositoryUuid)) {
			throw new BadRequestException("Repository ID cannot be empty for Code System update.");
		}
	}
}
