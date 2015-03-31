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

import java.net.URI;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.snomed.api.ISnomedDescriptionService;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescriptionInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescriptionUpdate;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestUpdate;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("SNOMED CT Descriptions")
@RestController
@RequestMapping(
		value="/{version}",
		produces={ AbstractRestService.V1_MEDIA_TYPE })
public class SnomedDescriptionRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedDescriptionService delegate;
	
	@ApiOperation(
			value="Create description", 
			notes="Creates a new description directly on a version.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Code system version not found")
	})
	@RequestMapping(
			value="/descriptions", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,
			
			@ApiParam(value="Description parameters")
			@RequestBody 
			final ChangeRequest<SnomedDescriptionRestInput> body,
			
			final Principal principal) {
		
		final ISnomedDescription createdDescription = doCreate(version, null, body, principal);
		return Responses.created(getDescriptionLocation(version, createdDescription)).build();
	}

	@ApiOperation(
			value="Create description on task", 
			notes="Creates a new description on a task branch.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Code system version or task not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/descriptions", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,
			
			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,
			
			@ApiParam(value="Description parameters")
			@RequestBody 
			final ChangeRequest<SnomedDescriptionRestInput> body,
			
			final Principal principal) {

		final ISnomedDescription createdDescription = doCreate(version, taskId, body, principal);
		return Responses.created(getDescriptionOnTaskLocation(version, taskId, createdDescription)).build();
	}

	@ApiOperation(
			value="Retrieve description properties", 
			notes="Returns all properties of the specified description, including acceptability values by language reference set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or description not found")
	})
	@RequestMapping(value="/descriptions/{descriptionId}", method=RequestMethod.GET)
	public ISnomedDescription read(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,
			
			@ApiParam(value="The description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId) {

		return readOnTask(version, null, descriptionId);
	}

	@ApiOperation(
			value="Retrieve description properties on task", 
			notes="Returns all properties of the specified description on a task branch, including acceptability values by language reference set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version, task or description not found")
	})
	@RequestMapping(value="/tasks/{taskId}/descriptions/{descriptionId}", method=RequestMethod.GET)
	public ISnomedDescription readOnTask(
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,
			
			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,
			
			@ApiParam(value="The description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId) {

		final IComponentRef conceptRef = createComponentRef(version, taskId, descriptionId);
		return delegate.read(conceptRef);
	}

	@ApiOperation(
			value="Update description",
			notes="Updates properties of the specified description, also managing language reference set membership.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Code system version or description not found")
	})
	@RequestMapping(
			value="/descriptions/{descriptionId}/updates", 
			method=RequestMethod.POST, 
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(			
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,
			
			@ApiParam(value="The description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			@ApiParam(value="Update description parameters")
			@RequestBody 
			final ChangeRequest<SnomedDescriptionRestUpdate> body,
			
			final Principal principal) {

		updateOnTask(version, null, descriptionId, body, principal);
	}

	@ApiOperation(
			value="Update description on task",
			notes="Updates properties of the specified description on a task branch, also managing language reference set membership.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Code system version, task or description not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/descriptions/{descriptionId}/updates", 
			method=RequestMethod.POST, 
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateOnTask(			
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,
			
			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,
			
			@ApiParam(value="The description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			@ApiParam(value="Update description parameters")
			@RequestBody 
			final ChangeRequest<SnomedDescriptionRestUpdate> body,
			
			final Principal principal) {

		final IComponentRef conceptRef = createComponentRef(version, taskId, descriptionId);
		final ISnomedDescriptionUpdate update = body.getChange().toComponentUpdate();
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();

		delegate.update(conceptRef, update, userId, commitComment);
	}

	@ApiOperation(
			value="Delete description",
			notes="Permanently removes the specified unreleased description and related components.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Code system version or description not found")
	})
	@RequestMapping(value="/descriptions/{descriptionId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(			
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,
			
			@ApiParam(value="The description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			final Principal principal) {

		deleteOnTask(version, null, descriptionId, principal);
	}

	@ApiOperation(
			value="Delete description on task",
			notes="Permanently removes the specified unreleased description and related components from a task branch.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Code system version, task or description not found")
	})
	@RequestMapping(value="/tasks/{taskId}/descriptions/{descriptionId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOnTask(			
			@ApiParam(value="The code system version")
			@PathVariable(value="version")
			final String version,
			
			@ApiParam(value="The task")
			@PathVariable(value="taskId")
			final String taskId,
			
			@ApiParam(value="The description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			final Principal principal) {

		final IComponentRef descriptionRef = createComponentRef(version, taskId, descriptionId);
		final String userId = principal.getName();
		delegate.delete(descriptionRef, userId, "Deleted description from store.");
	}
	
	private ISnomedDescription doCreate(final String version, final String taskId, final ChangeRequest<SnomedDescriptionRestInput> body, final Principal principal) {
		final ISnomedDescriptionInput input = body.getChange().toComponentInput(version, taskId);
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		return delegate.create(input, userId, commitComment);
	}
	
	private URI getDescriptionLocation(final String version, final ISnomedDescription createdDescription) {
		return linkTo(methodOn(SnomedDescriptionRestService.class).read(version, createdDescription.getId())).toUri();
	}
	
	private URI getDescriptionOnTaskLocation(String version, String taskId, ISnomedDescription createdDescription) {
		return linkTo(methodOn(SnomedDescriptionRestService.class).readOnTask(version, taskId, createdDescription.getId())).toUri();
	}
	
}