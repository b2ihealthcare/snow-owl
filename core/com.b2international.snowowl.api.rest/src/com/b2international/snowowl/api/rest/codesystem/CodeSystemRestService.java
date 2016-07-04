/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.api.codesystem.ICodeSystemService;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystem;
import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystem;
import com.b2international.snowowl.api.rest.AbstractRestService;
import com.b2international.snowowl.api.rest.domain.RestApiError;
import com.b2international.snowowl.api.rest.util.Responses;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("Code System Metadata")
@RestController
@RequestMapping(
		value = "/codesystems",
		produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class CodeSystemRestService extends AbstractRestService {

	@Autowired
	private ICodeSystemService delegate;
	@Resource
	private IEventBus bus;

	@ApiOperation(
			value="Retrieve all code systems",
			notes="Returns a list containing generic information about registered code systems.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(method=RequestMethod.GET)
	public CollectionResource<ICodeSystem> getCodeSystems() {
		return CollectionResource.of(delegate.getCodeSystems());
	}

	@ApiOperation(
			value="Retrieve code system by short name or OID",
			notes="Returns generic information about a single code system with the specified short name or OID.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system not found", response = RestApiError.class)
	})
	@RequestMapping(value="{shortNameOrOid}", method=RequestMethod.GET)
	public ICodeSystem getCodeSystemByShortNameOrOid(
			@ApiParam(value="The code system identifier (short name or OID)")
			@PathVariable(value="shortNameOrOid") final String shortNameOrOId) {
		return delegate.getCodeSystemByShortNameOrOid(shortNameOrOId);
	}
	
	@ApiOperation(
			value="Create a code system",
			notes="Create a new Code System with the given parameters")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created", response = Void.class),
		@ApiResponse(code = 400, message = "Code System already exists in the system", response = RestApiError.class)
	})
	@RequestMapping(method = RequestMethod.POST, consumes = { AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createCodeSystem(
			@RequestBody
			final CodeSystem codeSystem,
			
			final Principal principal
			) {
		ApiValidation.checkInput(codeSystem);
		
		final String userId = principal.getName();
		final String commitComment = String.format("Created new Code System %s", codeSystem.getShortName());
		
		final String shortName = new CodeSystemRequests(codeSystem.getRepositoryUuid())
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
				.build(userId, IBranchPath.MAIN_BRANCH, commitComment)
				.executeSync(bus)
				.getResultAs(String.class);
		
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
	@RequestMapping(value="/{shortNameOrOid}", 
		method = RequestMethod.PUT, 
		consumes = { AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
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
		
		new CodeSystemRequests(codeSystem.getRepositoryUuid())
				.prepareUpdateCodeSystem(shortNameOrOId)
				.setName(codeSystem.getName())
				.setBranchPath(codeSystem.getBranchPath())
				.setCitation(codeSystem.getCitation())
				.setIconPath(codeSystem.getIconPath())
				.setLanguage(codeSystem.getPrimaryLanguage())
				.setLink(codeSystem.getOrganizationLink())
				.build(principal.getName(), IBranchPath.MAIN_BRANCH, commitComment)
				.executeSync(bus);
	}

	private void validateUpdateInput(final String shortNameOrOId, final String repositoryUuid) {
		if (StringUtils.isEmpty(shortNameOrOId)) {
			throw new BadRequestException("Unique ID cannot be empty for Code System update.");
		} else if (StringUtils.isEmpty(repositoryUuid)) {
			throw new BadRequestException("Repository ID cannot be empty for Code System update.");
		}
	}

}
