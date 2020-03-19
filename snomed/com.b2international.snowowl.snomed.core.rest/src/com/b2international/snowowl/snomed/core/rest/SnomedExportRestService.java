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


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.domain.ExportResult;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedExportRestConfiguration;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedExportRestRun;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Strings;
import com.google.common.collect.MapMaker;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 * @deprecated - refactored and redesigned to eliminate the necessity of a memory stored object {@link SnomedExportRestRun}
 */
@Api(value = "Exports (deprecated)", description="Exports (deprecated)", tags = "exports (deprecated)")
@RestController
@RequestMapping(value="/exports")
public class SnomedExportRestService extends AbstractSnomedRestService {

	@Autowired
	private AttachmentRegistry fileRegistry;
	
	private ConcurrentMap<UUID, SnomedExportRestRun> exports = new MapMaker().makeMap();
	
	public SnomedExportRestService() {
		super(Collections.emptySet());
	}
	
	@ApiOperation(
		value="Initiate a SNOMED CT export", 
		notes="Registers the specified export configuration and returns a location header pointing to the stored export run."
	)
	@ApiResponses({
		@ApiResponse(code=201, message="Created"),
		@ApiResponse(code=404, message="Code system version and/or task not found", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> beginExport(
			@ApiParam(value = "Export configuration")
			@RequestBody
			final SnomedExportRestConfiguration configuration) throws IOException {

		validate(configuration);
		
		final SnomedExportRestRun run = new SnomedExportRestRun();
		BeanUtils.copyProperties(configuration, run);
		run.setId(UUID.randomUUID());
		
		exports.put(run.getId(), run);
		
		return ResponseEntity.created(getExportRunURI(run.getId())).build();
	}
	
	private void validate(SnomedExportRestConfiguration configuration) {

		ApiValidation.checkInput(configuration);
		
		validateExportType(configuration);
		validateTransientEffectiveTime(configuration.getTransientEffectiveTime());
		Branch branch = validateBranch(configuration);
		
		validateNamespace(configuration, branch);
	}

	private void validateNamespace(SnomedExportRestConfiguration configuration, Branch branch) {
		if (Strings.isNullOrEmpty(configuration.getNamespaceId())) {
			configuration.setNamespaceId(resolveNamespaceId(branch));
		}
	}

	private void validateExportType(SnomedExportRestConfiguration configuration) {
		if (Rf2ReleaseType.FULL.equals(configuration.getType())) {
			if (configuration.getStartEffectiveTime() != null || configuration.getEndEffectiveTime() != null) {
				throw new BadRequestException("Export date ranges can only be set if the export mode is not FULL.");
			}
		}
	}

	private Branch validateBranch(SnomedExportRestConfiguration configuration) {
		
		Branch branch = RepositoryRequests.branching()
				.prepareGet(configuration.getBranchPath())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getBus())
				.getSync();
		
		if (branch == null) {
			throw new BadRequestException("The specified branch (%s) does not exists", configuration.getBranchPath()); 
		} else if (branch.isDeleted()) {
			throw new BadRequestException("Branch '%s' has been deleted and cannot accept further modifications.", configuration.getBranchPath());
		}
		
		return branch;
	}

	private void validateTransientEffectiveTime(final String transientEffectiveTime) {
		
		if (Strings.isNullOrEmpty(transientEffectiveTime)) {
			return;
		} else if ("NOW".equals(transientEffectiveTime)) {
			return;
		}

		try {
			Dates.parse(transientEffectiveTime, DateFormats.SHORT);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("Error while parsing date")) {
				throw new BadRequestException("Transient effective time '%s' was not empty, 'NOW' or a date in the expected format.", transientEffectiveTime);
			} else {
				throw e;
			}
		}
	}

	@ApiOperation(
		value="Retrieve export run resource", 
		notes= "Returns an export run resource by identifier."
	)
	@ApiResponses({
		@ApiResponse(code=200, message="OK"),
		@ApiResponse(code=404, message="Export run not found", response = RestApiError.class)
	})
	@GetMapping(value="/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public SnomedExportRestRun getExport(
			@ApiParam(value = "Export run identifier")
			@PathVariable(value="id")
			UUID exportId) {

		final SnomedExportRestRun restRun = exports.get(exportId);
		
		if (restRun == null) {
			throw new NotFoundException("Export run", exportId.toString());
		} else {
			return restRun;
		}
	}
	
	@ApiOperation(
		value="Retrieve finished export run's archive", 
		notes="Returns the export archive from a completed export run on the given version branch."
	)
	@ApiResponses({
		@ApiResponse(code=200, message="OK"),
		@ApiResponse(code=404, message="Export run not found", response = RestApiError.class)
	})
	@GetMapping(value="/{id}/archive", produces = { AbstractRestService.OCTET_STREAM_MEDIA_TYPE })
	public @ResponseBody ResponseEntity<?> getArchive(
			@ApiParam(value = "Export run ID")
			@PathVariable(value="id")
			final UUID exportId) throws IOException {

		final SnomedExportRestRun export = getExport(exportId);
		final boolean includeUnpublished = export.isIncludeUnpublished() || isDeltaWithoutRange(export);
		
		final Rf2RefSetExportLayout refSetExportLayout = ApplicationContext.getServiceForClass(SnomedCoreConfiguration.class).getExport().getRefSetExportLayout();
		
		final ExportResult exportedFile = SnomedRequests.rf2().prepareExport()
			.setReleaseType(export.getType())
			.setExtensionOnly(export.isExtensionOnly())
			.setLocales(export.getLocales())
			.setIncludePreReleaseContent(includeUnpublished)
			.setModules(export.getModuleIds())
			.setRefSets(export.getRefsetIds())
			.setCountryNamespaceElement(export.getNamespaceId())
			// .setNamespaceFilter(namespaceFilter) is not supported on REST, yet
			.setTransientEffectiveTime(export.getTransientEffectiveTime())
			.setStartEffectiveTime(export.getStartEffectiveTime())
			.setEndEffectiveTime(export.getEndEffectiveTime())
			.setRefSetExportLayout(refSetExportLayout)
			.setReferenceBranch(export.getBranchPath())
			.build(this.repositoryId)
			.execute(getBus())
			.getSync();
		
		final File file = ((InternalAttachmentRegistry) fileRegistry).getAttachment(exportedFile.getRegistryId());
		final Resource exportZipResource = new FileSystemResource(file);
		
		final HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.set("Content-Disposition", "attachment; filename=\"snomed_export_" + Dates.formatByHostTimeZone(new Date(), DateFormats.COMPACT_LONG) + ".zip\"");

		exports.remove(exportId);
		file.deleteOnExit();
		return new ResponseEntity<>(exportZipResource, httpHeaders, HttpStatus.OK);
	}
	
	private boolean isDeltaWithoutRange(final SnomedExportRestConfiguration export) {
		return Rf2ReleaseType.DELTA.equals(export.getType())
				&& export.getStartEffectiveTime() == null
				&& export.getEndEffectiveTime() == null;
	}

	private URI getExportRunURI(UUID exportId) {
		return MvcUriComponentsBuilder.fromMethodName(SnomedExportRestService.class, "getExport", exportId).build().toUri();
	}
	
	/**
	 * Resolves the namespace to be used for the export by extracting branch metadata information.
	 * @param branch the branch used for extracting the metadata information
	 * @return the namespace extracted from the branch metadata information or INT by default.
	 */
	private String resolveNamespaceId(Branch branch) {
		
		String branchMetaShortname = getEffectiveBranchMetadataValue(branch, "shortname");
		String branchMetaDefaultNamespace = getEffectiveBranchMetadataValue(branch, "defaultNamespace");
		
		if (!Strings.isNullOrEmpty(branchMetaShortname) && !Strings.isNullOrEmpty(branchMetaDefaultNamespace)) {
			return String.format("%s%s", branchMetaShortname.toUpperCase(), branchMetaDefaultNamespace);
		}
		
		return SnomedIdentifiers.INT_NAMESPACE;
	}
	
	// FIXME This should not be here, see IHTSDO/com.b2international.snowowl.snomed.core.domain.BranchMetadataResolver
	private String getEffectiveBranchMetadataValue(Branch branch, String metadataKey) {
		final String metadataValue = branch.metadata().getString(metadataKey);
		if (metadataValue != null) {
			return metadataValue;
		} else {
			if (!Branch.MAIN_PATH.equals(branch.parentPath())) {
				final Branch parent = RepositoryRequests.branching()
					.prepareGet(branch.parentPath())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID)
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync(1, TimeUnit.MINUTES);
				return getEffectiveBranchMetadataValue(parent, metadataKey);
			}
		}
		return null;
	}
	
}
