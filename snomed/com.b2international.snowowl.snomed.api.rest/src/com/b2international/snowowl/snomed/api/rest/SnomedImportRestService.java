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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.snomed.api.rest.domain.SnomedImportStatus.getImportStatus;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.b2international.snowowl.snomed.api.ISnomedRf2ImportService;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedImportDetails;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedImportRestConfiguration;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api(value = "Imports", description="Imports", tags = { "imports" })
@RestController
@RequestMapping(value = "/imports")
public class SnomedImportRestService extends AbstractRestService {

	@Autowired
	private ISnomedRf2ImportService delegate; 
	
	public SnomedImportRestService() {
		super(Collections.emptySet());
	}
	
	@ApiOperation(
			value="Import SNOMED CT content", 
			notes="Configures processes to import RF2 based archives. The configured process will wait until the archive actually uploaded via the <em>/archive</em> endpoint. "
					+ "The actual import process will start after the file upload completed. Note: unpublished components (with no value entered in the 'effectiveTime' column) are "
					+ "only allowed in DELTA import mode.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Code system version not found"),
		@ApiResponse(code = 404, message = "Task not found", response = RestApiError.class),
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="Import parameters")
			@RequestBody 
			final SnomedImportRestConfiguration importConfiguration) {

		final UUID importId = delegate.create(importConfiguration.toConfig());
		return Responses.created(linkTo(methodOn(SnomedImportRestService.class).getImportDetails(importId)).toUri()).build();
	}

	@ApiOperation(
			value="Retrieve import run details", 
			notes="Returns the specified import run's configuration and status.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or import not found", response = RestApiError.class),
	})
	@GetMapping(value = "/{importId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public SnomedImportDetails getImportDetails(
			@ApiParam(value="The import identifier")
			@PathVariable(value="importId") 
			final UUID importId) {

		return convertToDetails(importId, delegate.getImportDetails(importId));
	}
	
	@ApiOperation(
			value="Delete import run", 
			notes="Removes a pending or finished import configuration from the server.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Code system version or import not found", response = RestApiError.class),
	})
	@DeleteMapping(value="/{importId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteImportDetails(
			@ApiParam(value="The import identifier")
			@PathVariable(value="importId") 
			final UUID importId) {
		
		delegate.deleteImportDetails(importId);
	}
	
	@ApiOperation(
			value="Upload archive file and start the import", 
			notes="Removes a pending or finished import configuration from the server.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content"),
		@ApiResponse(code = 404, message = "Code system version or import not found", response = RestApiError.class),
	})
	@PostMapping(value="/{importId}/archive", consumes = { AbstractRestService.MULTIPART_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void startImport(
			@ApiParam(value="The import identifier")
			@PathVariable(value="importId") 
			final UUID importId,
			
			@ApiParam(value="RF2 import archive")
			@RequestPart("file") 
			final MultipartFile file) {
		
		checkNotNull(file, "SNOMED CT RF2 release archive should be specified.");
		
		try (final InputStream is = file.getInputStream()) {
			delegate.startImport(importId, is);
		} catch (final IOException e) {
			throw new RuntimeException("Error while reading SNOMED CT RF2 release archive content.");
		}
	}
	
	private SnomedImportDetails convertToDetails(final UUID importId, 
			final ISnomedImportConfiguration configuration) {
		
		final SnomedImportDetails details = new SnomedImportDetails();
		details.setCompletionDate(configuration.getCompletionDate());
		details.setCreateVersions(configuration.shouldCreateVersion());
		details.setId(importId);
		details.setStartDate(configuration.getStartDate());
		details.setStatus(getImportStatus(configuration.getStatus()));
		details.setType(configuration.getRf2ReleaseType());
		details.setBranchPath(configuration.getBranchPath());
		return details;
	}
}
