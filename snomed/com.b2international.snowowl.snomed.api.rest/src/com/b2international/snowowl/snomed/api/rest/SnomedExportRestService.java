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
package com.b2international.snowowl.snomed.api.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.datastore.internal.file.InternalFileRegistry;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.snomed.api.ISnomedExportService;
import com.b2international.snowowl.snomed.api.exception.ExportRunNotFoundException;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedExportRestConfiguration;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedExportRestRun;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.Rf2ExportResult;
import com.b2international.snowowl.snomed.core.domain.Rf2RefSetExportLayout;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Strings;
import com.google.common.collect.MapMaker;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("Exports")
@RestController
@RequestMapping(
		value="/exports", produces = { AbstractRestService.SO_MEDIA_TYPE })
public class SnomedExportRestService extends AbstractSnomedRestService {

	@Autowired
	private ISnomedExportService exportService;
	
	@Autowired
	private FileRegistry fileRegistry;
	
	private ConcurrentMap<UUID, SnomedExportRestRun> exports = new MapMaker().makeMap();
	
	@ApiOperation(
			value="Initiate a SNOMED CT export", 
			notes="Registers the specified export configuration and returns a location header pointing to the stored export run.")
	@ApiResponses({
		@ApiResponse(code=201, message="Created"),
		@ApiResponse(code=404, message="Code system version and/or task not found", response = RestApiError.class)
	})
	@RequestMapping(method=RequestMethod.POST, consumes = { AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> beginExport(
			@ApiParam(value="Export configuration")
			@RequestBody
			final SnomedExportRestConfiguration configuration) throws IOException {

		validate(configuration);
		
		final SnomedExportRestRun run = new SnomedExportRestRun();
		BeanUtils.copyProperties(configuration, run);
		run.setId(UUID.randomUUID());
		
		exports.put(run.getId(), run);
		
		return Responses.created(getExportRunURI(run.getId())).build();
	}
	
	private void validate(SnomedExportRestConfiguration configuration) {

		ApiValidation.checkInput(configuration);
		
		validateExportType(configuration);
		validateTransientEffectiveTime(configuration.getTransientEffectiveTime());
		Branch branch = validateBranch(configuration);
		
		validateNamespace(configuration, branch);
		validateCodeSystemShortName(configuration);
	}

	private void validateCodeSystemShortName(SnomedExportRestConfiguration configuration) {
		if (!Strings.isNullOrEmpty(configuration.getCodeSystemShortName())) {
			
			int hitSize = CodeSystemRequests.prepareSearchCodeSystem()
				.one()
				.filterById(configuration.getCodeSystemShortName())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(bus)
				.getSync().getTotal();
			
			if (hitSize == 0) {
				throw new BadRequestException("Unknown code system with short name: %s", configuration.getCodeSystemShortName());
			}
			
		} else {
			throw new BadRequestException("Code system short name must be set for configuring the export properly.");
		}
	}

	private void validateNamespace(SnomedExportRestConfiguration configuration, Branch branch) {
		if (Strings.isNullOrEmpty(configuration.getNamespaceId())) {
			configuration.setNamespaceId(exportService.resolveNamespaceId(branch));
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
				.execute(bus)
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
			notes="Returns an export run resource by identifier.")
	@ApiResponses({
		@ApiResponse(code=200, message="OK"),
		@ApiResponse(code=404, message="Export run not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public SnomedExportRestRun getExport(
			@ApiParam(value="Export run identifier")
			@PathVariable(value="id")
			UUID exportId) {

		final SnomedExportRestRun restRun = exports.get(exportId);
		
		if (restRun == null) {
			throw new ExportRunNotFoundException(exportId.toString());
		} else {
			return restRun;
		}
	}
	
	@ApiOperation(
			value="Retrieve finished export run's archive", 
			notes="Returns the export archive from a completed export run on the given version branch.")
	@ApiResponses({
		@ApiResponse(code=200, message="OK"),
		@ApiResponse(code=404, message="Export run not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{id}/archive", method=RequestMethod.GET, produces = { AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public @ResponseBody ResponseEntity<?> getArchive(
			@ApiParam(value="Export run ID")
			@PathVariable(value="id")
			final UUID exportId,
			
			@RequestHeader
			final HttpHeaders headers,
			
			final Principal principal) throws IOException {

		final SnomedExportRestRun export = getExport(exportId);
		final boolean includeUnpublished = export.isIncludeUnpublished() || isDeltaWithoutRange(export);
		
		Rf2RefSetExportLayout refSetExportLayout = ApplicationContext.getServiceForClass(SnomedCoreConfiguration.class).getExport().getRefSetExportLayout();
		
		final Rf2ExportResult exportedFile = SnomedRequests.rf2().prepareExport()
			.setUserId(principal.getName())
			.setReleaseType(export.getType())
			.setCodeSystem(export.getCodeSystemShortName())
			.setExtensionOnly(export.isExtensionOnly())
			.setIncludePreReleaseContent(includeUnpublished)
			.setModules(export.getModuleIds())
			.setCountryNamespaceElement(export.getNamespaceId())
			// .setNamespaceFilter(namespaceFilter) is not supported on REST, yet
			.setTransientEffectiveTime(export.getTransientEffectiveTime())
			.setStartEffectiveTime(export.getStartEffectiveTime())
			.setEndEffectiveTime(export.getEndEffectiveTime())
			.setRefSetExportLayout(refSetExportLayout)
			.setReferenceBranch(export.getBranchPath())
			.build(this.repositoryId)
			.execute(bus)
			.getSync();
		
		final File file = ((InternalFileRegistry) fileRegistry).getFile(exportedFile.getRegistryId());
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
		return linkTo(methodOn(SnomedExportRestService.class).getExport(exportId)).toUri();
	}
}
