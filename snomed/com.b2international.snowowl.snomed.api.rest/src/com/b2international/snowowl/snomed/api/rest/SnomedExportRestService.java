/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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

import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.server.domain.StorageRef;
import com.b2international.snowowl.snomed.api.ISnomedExportService;
import com.b2international.snowowl.snomed.api.exception.ExportRunNotFoundException;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedExportConfiguration;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedExportRestConfiguration;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedExportRestRun;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
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

		ApiValidation.checkInput(configuration);
		
		if (Rf2ReleaseType.FULL.equals(configuration.getType())) {
			if (configuration.getDeltaStartEffectiveTime() != null || configuration.getDeltaEndEffectiveTime() != null) {
				throw new BadRequestException("Export date ranges can only be set if the export mode is not FULL.");
			}
		}
		
		final String transientEffectiveTime = configuration.getTransientEffectiveTime();
		validateTransientEffectiveTime(transientEffectiveTime);

		final StorageRef exportStorageRef = new StorageRef(repositoryId, configuration.getBranchPath());
		
		// Check version and branch existence
		exportStorageRef.checkStorageExists();
		
		final UUID id = UUID.randomUUID();
		final SnomedExportRestRun run = new SnomedExportRestRun();
		
		BeanUtils.copyProperties(configuration, run);
		run.setId(id);
		exports.put(id, run);
		
		return Responses.created(getExportRunURI(id)).build();
	}
	
	private void validateTransientEffectiveTime(final String transientEffectiveTime) {
		
		if (Strings.isNullOrEmpty(transientEffectiveTime)) {
			return;
		} else if ("NOW".equals(transientEffectiveTime)) {
			return;
		}

		try {
			new SimpleDateFormat("yyyyMMdd").parse(transientEffectiveTime);
		} catch (ParseException e) {
			throw new BadRequestException("Transient effective time '%s' was not empty, 'NOW' or a date in the expected format.", transientEffectiveTime);
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
			final HttpHeaders headers) throws IOException {

		final SnomedExportRestRun export = getExport(exportId);
		final File exportZipFile = exportService.export(toExportConfiguration(export));
		final FileSystemResource exportZipResource = new FileSystemResource(exportZipFile);
		
		final HttpHeaders httpHeaders = new HttpHeaders();
		final String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.set("Content-Disposition", "attachment; filename=\"snomed_export_" + timestamp + ".zip\"");

		exports.remove(exportId);
		return new ResponseEntity<FileSystemResource>(exportZipResource, httpHeaders, HttpStatus.OK);
	}
	
	private SnomedExportConfiguration toExportConfiguration(final SnomedExportRestConfiguration configuration) {
		final SnomedExportConfiguration conf = new SnomedExportConfiguration(
				configuration.getType(), 
				configuration.getBranchPath(),
				configuration.getNamespaceId(), configuration.getModuleIds(),
				configuration.getDeltaStartEffectiveTime(), configuration.getDeltaEndEffectiveTime(),
				configuration.getTransientEffectiveTime(),
				configuration.isIncludeUnpublished());

		return conf;
	}
	
	private URI getExportRunURI(UUID exportId) {
		return linkTo(methodOn(SnomedExportRestService.class).getExport(exportId)).toUri();
	}
}
