/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;

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

import com.b2international.commons.exceptions.ApiValidation;
import com.b2international.snowowl.api.codesystem.ICodeSystemVersionService;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersion;
import com.b2international.snowowl.api.rest.AbstractRestService;
import com.b2international.snowowl.api.rest.codesystem.domain.VersionInput;
import com.b2international.snowowl.api.rest.domain.RestApiError;
import com.b2international.snowowl.api.rest.util.Responses;
import com.b2international.snowowl.core.domain.CollectionResource;

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
@RequestMapping(
		value = "/codesystems/{shortName}/versions",
		produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class CodeSystemVersionRestService extends AbstractRestService {
	
	@Autowired
	protected ICodeSystemVersionService codeSystemVersionService;

	@ApiOperation(
			value="Retrieve all code system versions",
			notes="Returns a list containing all released code system versions for the specified code system.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system not found", response = RestApiError.class)
	})
	@RequestMapping(method=RequestMethod.GET)
	public CollectionResource<ICodeSystemVersion> getAllCodeSystemVersionsByShortName(
			@ApiParam(value="The code system short name")
			@PathVariable(value="shortName") final String shortName) {

		return CollectionResource.of(codeSystemVersionService.getCodeSystemVersions(shortName));
	}

	@ApiOperation(
			value="Retrieve code system version by identifier",
			notes="Returns a released code system version for the specified code system with the given version identifier.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system or version not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{version}", method=RequestMethod.GET)
	public ICodeSystemVersion getCodeSystemVersionByShortNameAndVersionId(
			@ApiParam(value="The code system short name")
			@PathVariable(value="shortName") 
			final String shortName,
			
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version) {

		return codeSystemVersionService.getCodeSystemVersionById(shortName, version);
	}
	
	@ApiOperation(
			value="Create a new code system version",
			notes="Creates a new code system version in the specified terminology.  "
					+ "The version tag (represented by an empty branch) is created on the specified parent branch. "
					+ "Where applicable, effective times are set on the unpublished content as part of this operation.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Not found", response = RestApiError.class),
		@ApiResponse(code = 409, message = "Code system version conflicts with existing branch", response = RestApiError.class)
	})
	@RequestMapping(method=RequestMethod.POST, consumes = { AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(value=HttpStatus.CREATED)
	public ResponseEntity<Void> createVersion(
			@ApiParam(value="The code system short name")
			@PathVariable(value="shortName") 
			final String shortName, 
			
			@ApiParam(value="Version parameters")
			@RequestBody final VersionInput input) {
		ApiValidation.checkInput(input);
		final ICodeSystemVersion version = codeSystemVersionService.createVersion(shortName, input);
		return Responses.created(getVersionURI(shortName, version.getVersion())).build();
	}

	private URI getVersionURI(String shortName, String version) {
		return linkTo(methodOn(CodeSystemVersionRestService.class).getCodeSystemVersionByShortNameAndVersionId(shortName, version)).toUri();
	}
}