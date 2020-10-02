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
package com.b2international.snowowl.snomed.core.rest;

import static com.google.common.base.Preconditions.checkNotNull;

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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedImportDetails;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedImportRestConfiguration;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedImportStatus;
import com.b2international.snowowl.snomed.core.rest.services.SnomedRf2ImportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 1.0
 * @deprecated - refactored and redesigned - see {@link SnomedRf2ImportRestService} - to eliminate the necessity of a memory stored object {@link ISnomedImportConfiguration}
 */
@Tag(description="Imports (deprecated)", name = "imports (deprecated)")
@RestController
@RequestMapping(value = "/imports")
public class SnomedImportRestService extends AbstractSnomedRestService {

	@Autowired
	private SnomedRf2ImportService delegate; 
	
	public SnomedImportRestService() {
		super(Collections.emptySet());
	}
	
	@Operation(
		summary="Import SNOMED CT content", 
		description="Configures processes to import RF2 based archives. The configured process will wait until the archive actually uploaded via the <em>/archive</em> endpoint. "
				+ "The actual import process will start after the file upload completed. Note: unpublished components (with no value entered in the 'effectiveTime' column) are "
				+ "only allowed in DELTA import mode."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description="Created"),
		@ApiResponse(responseCode = "404", description="Code system version not found"),
		@ApiResponse(responseCode = "404", description="Task not found"),
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@Parameter(description = "Import parameters")
			@RequestBody 
			final SnomedImportRestConfiguration importConfiguration) {

		final UUID importId = delegate.create(importConfiguration.toConfig());
		return ResponseEntity.created(MvcUriComponentsBuilder.fromMethodName(SnomedImportRestService.class, "getImportDetails", importId).build().toUri()).build();
	}

	@Operation(
		summary = "Retrieve import run details", 
		description = "Returns the specified import run's configuration and status."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description="OK"),
		@ApiResponse(responseCode = "404", description="Code system version or import not found"),
	})
	@GetMapping(value = "/{importId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public SnomedImportDetails getImportDetails(
			@Parameter(description = "The import identifier")
			@PathVariable(value="importId") 
			final UUID importId) {

		return convertToDetails(importId, delegate.getImportDetails(importId));
	}
	
	@Operation(
		summary = "Delete import run", 
		description = "Removes a pending or finished import configuration from the server."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description="Delete successful"),
		@ApiResponse(responseCode = "404", description="Code system version or import not found"),
	})
	@DeleteMapping(value="/{importId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteImportDetails(
			@Parameter(description = "The import identifier")
			@PathVariable(value="importId") 
			final UUID importId) {
		
		delegate.deleteImportDetails(importId);
	}
	
	@Operation(
		summary = "Upload archive file and start the import", 
		description = "Removes a pending or finished import configuration from the server."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description="No content"),
		@ApiResponse(responseCode = "404", description="Code system version or import not found"),
	})
	@PostMapping(value="/{importId}/archive", consumes = { AbstractRestService.MULTIPART_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void startImport(
			@Parameter(description = "The import identifier")
			@PathVariable(value="importId") 
			final UUID importId,
			
			@Parameter(description = "RF2 import archive")
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
		details.setId(importId);
		details.setType(configuration.getRf2ReleaseType());
		details.setBranchPath(configuration.getBranchPath());
		details.setCodeSystemShortName(configuration.getCodeSystemShortName());
		details.setCreateVersions(configuration.shouldCreateVersion());
		details.setStartDate(configuration.getStartDate());
		details.setCompletionDate(configuration.getCompletionDate());
		details.setStatus(SnomedImportStatus.getImportStatus(configuration.getStatus()));
		return details;
	}
}
