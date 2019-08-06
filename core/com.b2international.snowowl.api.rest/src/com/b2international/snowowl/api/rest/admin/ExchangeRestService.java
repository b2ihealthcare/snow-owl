/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.admin;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.b2international.snowowl.api.rest.AbstractRestService;
import com.b2international.snowowl.api.rest.codesystem.CodeSystemVersionRestService;
import com.b2international.snowowl.api.rest.domain.RestApiError;
import com.b2international.snowowl.api.rest.util.Responses;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * RESTful service that exposes the content provisioning capabilities of the Snow Owl server
 * to exchange content between server and authoring platform instances.
 * 
 * @since 7.0
 */
@Api("Exchange")
@Controller
@RequestMapping(
		value="/exchange/{shortName}", 
		produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class ExchangeRestService {
	
	@ApiOperation(
			value="Exports a code system version",
			notes="Exports the content of a code system version in the exchange format for syndication.")
	@ApiResponses({
		@ApiResponse(code=200, message="Export successful"),
		@ApiResponse(code=404, message="Code system version not found")
	})
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(
			value="/version/{version}",
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<?> exportContent(
			@ApiParam(value="The code system short name") @PathVariable(value="shortName") final String shortName,
			@ApiParam(value="Code system version") @PathVariable(value="version") String version) throws Exception {
		
		System.out.println("Code system:" + shortName);
		System.out.println("Version:" + version);
		
		URL url = getClass().getClassLoader()
				.getResource("com/b2international/snowowl/api/rest/service_configuration.properties");
		
		FileSystemResource fileSystemResource = new FileSystemResource(new File(url.toURI()));
		
		final HttpHeaders httpHeaders = new HttpHeaders();
		
		String fileName = String.format("attachment; filename=\"exchange_%s_%s_%s.zip\"", shortName, version, Dates.formatByHostTimeZone(new Date(), DateFormats.COMPACT_LONG));

		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.set("Content-Disposition", fileName);
		
		return new ResponseEntity<>(fileSystemResource, httpHeaders, HttpStatus.OK);
	}
	
	@ApiOperation(
			value="Imports a new code system version",
			notes="Imports a new code system version provided in the exchange format")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created", response = Void.class),
		@ApiResponse(code = 404, message = "Code system not found", response = RestApiError.class)
	})
	@RequestMapping(value="/version/{versionId}",
			method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> importContent(String shortName, String version, @RequestParam("file") MultipartFile file) {
		
		//TODO: Implement exchange format import
		return Responses.created(getVersionURI(shortName, version)).build();
	}
	
	private URI getVersionURI(String shortName, String version) {
		return linkTo(methodOn(CodeSystemVersionRestService.class).getCodeSystemVersionByShortNameAndVersionId(shortName, version)).toUri();
	}
	
}
