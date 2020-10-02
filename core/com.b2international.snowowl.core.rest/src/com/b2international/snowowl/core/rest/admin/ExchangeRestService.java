/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.admin;

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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.codesystem.CodeSystemVersionRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import springfox.documentation.annotations.ApiIgnore;

/**
 * RESTful service that exposes the content provisioning capabilities of the Snow Owl server
 * to exchange content between server and authoring platform instances.
 * 
 * @since 7.0
 */
@ApiIgnore
@Tag(name = "exchange", description = "Exchange")
@Controller
@RequestMapping(
	value="/exchange/{shortName}", 
	produces={ AbstractRestService.JSON_MEDIA_TYPE }
)
public class ExchangeRestService {
	
	@Operation(
		summary = "Exports a code system version",
		description = "Exports the content of a code system version in the exchange format for syndication."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(
			value="/version/{version}",
			method=RequestMethod.GET,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<?> exportContent(
			@Parameter(description = "The code system short name") 
			@PathVariable(value = "shortName") 
			final String shortName,
			
			@Parameter(description = "Code system version") 
			@PathVariable(value="version") 
			String version) throws Exception {
		
		URL url = getClass().getClassLoader()
				.getResource("com/b2international/snowowl/api/rest/service_configuration.properties");
		
		FileSystemResource fileSystemResource = new FileSystemResource(new File(url.toURI()));
		
		final HttpHeaders httpHeaders = new HttpHeaders();
		
		String fileName = String.format("attachment; filename=\"exchange_%s_%s_%s.zip\"", shortName, version, Dates.formatByHostTimeZone(new Date(), DateFormats.COMPACT_LONG));

		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.set("Content-Disposition", fileName);
		
		return new ResponseEntity<>(fileSystemResource, httpHeaders, HttpStatus.OK);
	}
	
	@Operation(
		summary="Imports a new code system version",
		description="Imports a new code system version provided in the exchange format"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@RequestMapping(value="/version/{versionId}",
			method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> importContent(String shortName, String version, @RequestParam("file") MultipartFile file) {
		
		//TODO: Implement exchange format import
		return ResponseEntity.created(getVersionURI(shortName, version)).build();
	}
	
	private URI getVersionURI(String shortName, String version) {
		return MvcUriComponentsBuilder.fromMethodName(CodeSystemVersionRestService.class, "getCodeSystemVersionByShortNameAndVersionId", shortName, version).build().toUri();
	}
	
}
