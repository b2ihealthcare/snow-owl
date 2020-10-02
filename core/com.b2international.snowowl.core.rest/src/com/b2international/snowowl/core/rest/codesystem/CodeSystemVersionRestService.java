/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.rest.AbstractRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 1.0
 */
@Tag(description="CodeSystems", name = "codesystems")
@RestController
@RequestMapping(value = "/codesystems/{shortName}/versions")
public class CodeSystemVersionRestService extends AbstractRestService {
	
	@Autowired
	protected CodeSystemVersionService codeSystemVersionService;

	@Operation(
		summary="Retrieve all Code System versions",
		description="Returns a list containing all released Code System versions for the specified Code System."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK"),
		@ApiResponse(responseCode = "404", description="Code System not found")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public CollectionResource<CodeSystemVersion> getAllCodeSystemVersionsByShortName(
			@Parameter(description="The Code System short name")
			@PathVariable(value="shortName") final String shortName) {

		return CollectionResource.of(codeSystemVersionService.getCodeSystemVersions(shortName));
	}

	@Operation(
		summary="Retrieve Code System version by identifier",
		description="Returns a released Code System version for the specified Code System with the given version identifier."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK"),
		@ApiResponse(responseCode = "404", description="Code System or version not found")
	})
	@GetMapping(value = "/{version}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public CodeSystemVersion getCodeSystemVersionByShortNameAndVersionId(
			@Parameter(description="The Code System short name")
			@PathVariable(value="shortName") 
			final String shortName,
			
			@Parameter(description="The Code System version")
			@PathVariable(value="version") 
			final String version) {

		return codeSystemVersionService.getCodeSystemVersionById(shortName, version);
	}
	
	@Operation(
			summary="Create a new Code System version",
			description="Creates a new Code System version in the specified terminology.  "
					+ "The version tag (represented by an empty branch) is created on the specified parent branch. "
					+ "Where applicable, effective times are set on the unpublished content as part of this operation.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description="Created"),
		@ApiResponse(responseCode = "404", description="Not found"),
		@ApiResponse(responseCode = "409", description="Code System version conflicts with existing branch")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<Void> createVersion(
			@Parameter(description="The Code System short name")
			@PathVariable(value="shortName") 
			final String shortName, 
			
			@Parameter(description="Version parameters")
			@RequestBody final VersionInput input) {
		ApiValidation.checkInput(input);
		final CodeSystemVersion version = codeSystemVersionService.createVersion(shortName, input);
		return ResponseEntity.created(getVersionURI(shortName, version.getVersion())).build();
	}

	private URI getVersionURI(String shortName, String version) {
		return MvcUriComponentsBuilder.fromMethodName(CodeSystemVersionRestService.class, "getCodeSystemVersionByShortNameAndVersionId", shortName, version).build().toUri();
	}
}
