/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.jobs.RemoteJobState;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRf2ImportConfiguration;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.SnomedRf2Requests;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 7.5
 */
@Api(value = "Import", description="Import", tags = "import")
@RestController
@RequestMapping(value = "/{path:**}/import")
public class SnomedRf2ImportRestService extends AbstractSnomedRestService {

	@Autowired
	private AttachmentRegistry attachments;
	
	@ApiOperation(
		value="Import SNOMED CT content", 
		notes="Configures processes to import RF2 based archives. The configured process will wait until the archive actually uploaded via the <em>/archive</em> endpoint. "
				+ "The actual import process will start after the file upload completed. Note: unpublished components (with no value entered in the 'effectiveTime' column) are "
				+ "only allowed in DELTA import mode."
	)
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 400, message = "Bad Request", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Not found", response = RestApiError.class),
	})
	@PostMapping(consumes = { AbstractRestService.MULTIPART_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value = "The branch path expression", required = true)
			@PathVariable(name = "path")
			final String branchPath,
			
			@ApiParam(value = "RF2 Release Type to import from the archive", allowableValues = "full,snapshot,delta", defaultValue = "delta")
			@RequestParam(name = "type", defaultValue = "delta")
			final String type,
			
			@ApiParam(value = "To create versions for the CodeSystem relative to the given path", defaultValue = "true")
			@RequestParam(name = "createVersions", defaultValue = "true")
			final Boolean createVersions,
			
			@ApiParam(value = "Enable to run the import content integrity validations without pushing any changes", defaultValue = "false")
			@RequestParam(name = "dryRun", defaultValue = "false")
			final Boolean dryRun,
			
			@ApiParam(value = "Import file", required = true)
			@RequestPart("file") 
			final MultipartFile file) throws IOException {
		
		final String importJobId = SnomedRf2Requests.importJobKey(branchPath);
		
		final UUID rf2ArchiveId = UUID.randomUUID();
		attachments.upload(rf2ArchiveId, file.getInputStream());

		String jobId = SnomedRequests.rf2().prepareImport()
			.setRf2ArchiveId(rf2ArchiveId)
			.setReleaseType(Rf2ReleaseType.getByNameIgnoreCase(type))
			.setCreateVersions(createVersions)
			.setDryRun(dryRun)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
			.runAsJobWithRestart(importJobId, String.format("Importing SNOMED CT RF2 file '%s'", file.getOriginalFilename()))
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES);
		
		return ResponseEntity.created(getResourceLocationURI(branchPath, jobId)).build();
	}
	
	@ApiOperation(
		value="Retrieve an existing import job", 
		notes="Returns the specified import run's configuration and status."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Not found", response = RestApiError.class),
	})
	@GetMapping(value = "/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedRf2ImportConfiguration> getImport(
			@ApiParam(value = "The import identifier")
			@PathVariable(value="id") 
			final String id) {
		return JobRequests.prepareGet(id)
				.buildAsync()
				.execute(getBus())
				.then(this::toRf2ImportConfiguration);
	}
	
	@ApiOperation(
		value="Delete an existing import job", 
		notes="Cancels/Deletes a pending/finished import configuration."
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Not found", response = RestApiError.class),
	})
	@DeleteMapping(value="/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteImport(
			@ApiParam(value = "The import identifier")
			@PathVariable(value="id") 
			final String id) {
		JobRequests.prepareDelete(id)
				.buildAsync()
				.execute(getBus());
	}
	
	private SnomedRf2ImportConfiguration toRf2ImportConfiguration(RemoteJobEntry job) {
		ApiError error = null;
		if (job.getState() == RemoteJobState.FAILED) {
			error = job.getResultAs(ApplicationContext.getServiceForClass(ObjectMapper.class), ApiError.class);
		}
		return new SnomedRf2ImportConfiguration(IDs.sha1(job.getId()), job.getState(), error);
	}
	
}
