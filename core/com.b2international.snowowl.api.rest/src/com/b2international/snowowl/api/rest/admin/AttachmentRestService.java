/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.api.rest.AbstractRestService;
import com.b2international.snowowl.api.rest.domain.RestApiError;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 7.0
 */
@Api(value = "Attachments", description="attachments", tags = { "attachments" })
@Controller
@RequestMapping(value = "/attachments")
public class AttachmentRestService {
	
	@Autowired
	private AttachmentRegistry attachmentRegistry;
	
	@ApiOperation(value = "Upload attachment to the registry.") 
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content"),
		@ApiResponse(code = 400, message = "AttachmentId couldn't be converted to UUID"),
		@ApiResponse(code = 404, message = "The attachment's id is malformed or is not of the correct type", response = RestApiError.class),
	})
	@PostMapping(value="/{attachmentId}", consumes = { AbstractRestService.MULTIPART_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void uploadAttachment(
			@ApiParam(value="The attachment ID")
			@PathVariable(value="attachmentId") 
			final String attachmentId,
			
			@ApiParam(value="Attachment file")
			@RequestPart("file") 
			final MultipartFile file) {
		
		try {
			attachmentRegistry.upload(convertAttachmentId(attachmentId), file.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException("Error while reading the content of the provided attachment");
		}
	}
	
	@ApiOperation(value="Retrieve attachment from the registry.") 
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "AttachmentId couldn't be converted to UUID"),
		@ApiResponse(code = 404, message = "Attachment was not found in the registry", response = RestApiError.class)
	})
	@GetMapping(value="/{attachmentId}", produces = { AbstractRestService.OCTET_STREAM_MEDIA_TYPE })
	public @ResponseBody ResponseEntity<?> getAttachment(
			@ApiParam(value="Attachment ID")
			@PathVariable(value="attachmentId")
			final String attachmentId,
			
			@RequestHeader
			final HttpHeaders headers) {
		
		final File file = ((InternalAttachmentRegistry) attachmentRegistry).getAttachment(convertAttachmentId(attachmentId));
		final Resource exportZipResource = new FileSystemResource(file);
		
		final HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		final String attachmentFileName = String.format("\"attachment_%s_%s.zip\"", attachmentId, Dates.formatByHostTimeZone(new Date(), DateFormats.COMPACT_LONG)); 
		httpHeaders.set("Content-Disposition", "attachment; filename=" + attachmentFileName);
		file.deleteOnExit();
		return new ResponseEntity<>(exportZipResource, httpHeaders, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Delete attachment from the registry.", notes = "Removes an attachment from the server if it exists.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete was successfull"),
		@ApiResponse(code = 400, message = "AttachmentId couldn't be converted to UUID"),
		@ApiResponse(code = 404, message = "Attachment with the given id was not found"),
	})
	@DeleteMapping(value="/{attachmentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAttachment(
			@ApiParam(value = "Attachment ID")
			@PathVariable(value = "attachmentId")
			final String attachmentId) {
		final UUID convertedId = convertAttachmentId(attachmentId);
		attachmentRegistry.delete(convertedId);
	}
	
	private UUID convertAttachmentId(String attachmentId) {
		try {
			return UUID.fromString(attachmentId);
		} catch (IllegalArgumentException e) {
			// convert IllegalArugmentException to BadRequestException
			throw new BadRequestException("Couldn't convert " + attachmentId + " to UUID");
		}
	}
	
}