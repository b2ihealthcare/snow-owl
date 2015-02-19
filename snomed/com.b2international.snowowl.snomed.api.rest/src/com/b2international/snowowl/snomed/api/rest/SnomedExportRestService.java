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
package com.b2international.snowowl.snomed.api.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.api.codesystem.ICodeSystemVersionService;
import com.b2international.snowowl.snomed.api.ISnomedExportService;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedExportConfiguration;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedExportRestConfiguration;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedExportRestRun;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.google.common.collect.MapMaker;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@RestController
@RequestMapping(
		value="/{version}/exports", produces = { AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
@Api("SNOMED CT Export")
public class SnomedExportRestService extends AbstractSnomedRestService {

	@Autowired
	private ISnomedExportService delegate;
	
	@Autowired
	private ICodeSystemVersionService codeSystemVersions;
	
	private ConcurrentMap<UUID, SnomedExportRestRun> exports = new MapMaker().makeMap();
	
	@ApiOperation(
			value="Initiates a SNOMED CT export", 
			notes="Returns a location header pointing to the state of the export on the given version branch.")
	@ApiResponses({
		@ApiResponse(code=201, message="Created"),
		@ApiResponse(code=404, message="Code system version not found")
	})
	@RequestMapping(method=RequestMethod.POST, consumes = { AbstractRestService.V1_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> beginExport(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			final String version,
			
			@ApiParam(value="Export configuration")
			@RequestBody
			final SnomedExportRestConfiguration configuration) throws IOException {
		if (!"MAIN".equals(version)) {
			codeSystemVersions.getCodeSystemVersionById("SNOMEDCT", version);
		}
		final SnomedExportRestRun run = new SnomedExportRestRun();
		BeanUtils.copyProperties(configuration, run);
		final UUID id = UUID.randomUUID();
		run.setId(id);
		exports.put(id, run);
		return Responses.created(getExportRunURI(version, id)).build();
	}
	
	@ApiOperation(
			value="Retrieve export run resource", 
			notes="Returns a export run resource from the given version branch.")
	@ApiResponses({
		@ApiResponse(code=200, message="OK"),
		@ApiResponse(code=404, message="Code system version not found")
	})
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public SnomedExportRestRun getExport(
			@ApiParam(value="The code system version")
			@PathVariable(value="version") 
			String version,
			
			@ApiParam(value="Export run ID")
			@PathVariable(value="id")
			UUID exportId) {
		if (!"MAIN".equals(version)) {
			codeSystemVersions.getCodeSystemVersionById("SNOMEDCT", version);
		}
		return exports.get(exportId);
	}
	
	@ApiOperation(
			value="Retrieve finished export run's archive", 
			notes="Returns the export archive from a completed export run on the given version branch.")
	@ApiResponses({
		@ApiResponse(code=200, message="OK"),
		@ApiResponse(code=404, message="Code system version not found")
	})
	@RequestMapping(value="/{id}/archive", method=RequestMethod.GET, produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
	public void getArchive(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,
			
			@ApiParam(value="Export run ID")
			@PathVariable(value="id")
			final UUID exportId,
			
			final HttpServletResponse response) throws IOException {
		if (!"MAIN".equals(version)) {
			codeSystemVersions.getCodeSystemVersionById("SNOMEDCT", version);
		}
		final SnomedExportRestRun export = getExport(version, exportId);
		final File exportZipFile = delegate.export(toExportConfiguration(version, export));
		final FileSystemResource exportZipResource = new FileSystemResource(exportZipFile);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		final String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
		response.setHeader(com.google.common.net.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"snomed_export_" + timestamp + ".zip\"");

		try (final InputStream in = exportZipResource.getInputStream()) {
			StreamUtils.copy(in, response.getOutputStream());
		}

		response.getOutputStream().flush();
	}
		
	
	private SnomedExportConfiguration toExportConfiguration(final String version, final SnomedExportRestConfiguration configuration) {
		final SnomedExportConfiguration conf = new SnomedExportConfiguration(configuration.getType(), version, configuration.getNamespaceId(), configuration.getModuleIds());
		conf.setDeltaExportStartEffectiveTime(configuration.getDeltaStartEffectiveTime());
		conf.setDeltaExportEndEffectiveTime(configuration.getDeltaEndEffectiveTime());
		return conf;
	}
	
	private URI getExportRunURI(String version, UUID exportId) {
		return linkTo(methodOn(SnomedExportRestService.class).getExport(version, exportId)).toUri();
	}
	
}
