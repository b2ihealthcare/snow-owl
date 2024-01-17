/*
 * Copyright 2020-2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.jobs.RemoteJobState;
import com.b2international.snowowl.core.request.io.ImportResponse;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.SnomedApiConfig;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRf2ImportConfiguration;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.5
 */
@Tag(name = SnomedApiConfig.IMPORT)
@RestController
@RequestMapping(value = "/{path:**}/import")
public class SnomedRf2ImportRestService extends AbstractRestService {

	@Autowired
	private AttachmentRegistry attachments;
	
	@Operation(
		summary="Import SNOMED CT content from an RF2 release", 
		description="Initiates an RF2 import process on the selected path using the given configuration. File should be uploaded as part of this request using the file multipart formdata field."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "404", description = "Not found"),
	})
	@PostMapping(consumes = { AbstractRestService.MULTIPART_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(name = "path")
			final String path,
			
			@Parameter(description = "RF2 Release Type to import from the archive", schema = @Schema(allowableValues = "full,snapshot,delta", defaultValue = "delta"))
			@RequestParam(name = "type", defaultValue = "delta")
			final String type,
			
			@Parameter(description = "To create versions for the CodeSystem relative to the given path", schema = @Schema(defaultValue = "true"))
			@RequestParam(name = "createVersions", defaultValue = "true")
			final Boolean createVersions,
			
			@Parameter(description = "Ignore missing referenced components in listed references instead of reporting them as an error.")
			@RequestParam(name = "ignoreMissingReferencesIn", required = false)
			final List<String> ignoreMissingReferencesIn,
			
			@Parameter(description = "Import all component until this specified effective time value", schema = @Schema(format = "date", pattern = "\\d{6}"))
			@RequestParam(name = "importUntil", required = false)
			final String importUntil,
			
			@Parameter(description = "Configure the number of components to be imported in each effective time slice commit batch.", schema = @Schema(defaultValue = "60000"))
			@RequestParam(name = "batchSize", required = false)
			final Integer batchSize,
			
			@Parameter(description = "Enable to run the import content integrity validations without pushing any changes", schema = @Schema(defaultValue = "false"))
			@RequestParam(name = "dryRun", defaultValue = "false")
			final Boolean dryRun,
			
			@Parameter(description = "Import file", required = true)
			@RequestPart("file") 
			final MultipartFile file,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) throws IOException {
		
		final String importJobId = SnomedRf2Requests.importJobKey(path);
		
		final UUID rf2ArchiveId = UUID.randomUUID();
		attachments.upload(rf2ArchiveId, file.getInputStream());

		String jobId = SnomedRequests.rf2().prepareImport()
			.setRf2Archive(new Attachment(rf2ArchiveId, file.getName()))
			.setReleaseType(Rf2ReleaseType.getByNameIgnoreCase(type))
			.setCreateVersions(createVersions)
			.setIgnoreMissingReferencesIn(ignoreMissingReferencesIn)
			.setImportUntil(importUntil)
			.setDryRun(dryRun)
			.setImportUntil(importUntil)
			.setBatchSize(batchSize)
			.setAuthor(author)
			.build(path)
			.runAsJobWithRestart(importJobId, String.format("Importing SNOMED CT RF2 file '%s'", file.getOriginalFilename()))
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		
		return ResponseEntity.created(getResourceLocationURI(path, jobId)).build();
	}
	
	@Operation(
		summary="Retrieve an existing import job", 
		description="Returns the specified import run's configuration and status."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not found"),
	})
	@GetMapping(value = "/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedRf2ImportConfiguration> getImport(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(name = "path")
			final String path,
			
			@Parameter(description = "The import identifier")
			@PathVariable(value="id") 
			final String id) {
		return JobRequests.prepareGet(id)
				.buildAsync()
				.execute(getBus())
				.then(this::toRf2ImportConfiguration);
	}
	
	@Operation(
		summary="Delete an existing import job", 
		description="Cancels/Deletes a pending/finished import configuration."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Delete successful"),
		@ApiResponse(responseCode = "404", description = "Not found"),
	})
	@DeleteMapping(value="/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteImport(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(name = "path")
			final String path,
			
			@Parameter(description = "The import identifier")
			@PathVariable(value="id") 
			final String id) {
		JobRequests.prepareDelete(id)
				.buildAsync()
				.execute(getBus());
	}
	
	private SnomedRf2ImportConfiguration toRf2ImportConfiguration(RemoteJobEntry job) {
		ApiError error = null;
		ImportResponse response = null;
		ObjectMapper mapper = ApplicationContext.getServiceForClass(ObjectMapper.class);
		if (RemoteJobState.FAILED == job.getState()) {
			error = job.getResultAs(mapper, ApiError.class);
		} else if (job.isSuccessful()) {
			response = job.getResultAs(mapper, ImportResponse.class);
		}
		return new SnomedRf2ImportConfiguration(IDs.sha1(job.getId()), job.getState(), error, response);
	}
	
}
