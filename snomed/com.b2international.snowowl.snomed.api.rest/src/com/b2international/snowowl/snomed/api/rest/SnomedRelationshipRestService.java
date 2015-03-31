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
import com.b2international.snowowl.snomed.api.ISnomedRelationshipService;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationshipInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationshipUpdate;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRelationshipRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRelationshipRestUpdate;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api("SNOMED CT Relationships")
@RestController
@RequestMapping(
		value="/{version}",
		produces={ AbstractRestService.V1_MEDIA_TYPE })
public class SnomedRelationshipRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedRelationshipService delegate;

	@ApiOperation(
			value="Create relationship", 
			notes="Creates a new relationship directly on a version branch.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Code system version not found")
	})
	@RequestMapping(
			value="/relationships", 
			method=RequestMethod.POST, 
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="The code system version")
			@PathVariable("version") 
			final String version,
			
			@ApiParam(value="Relationship parameters")
			@RequestBody 
			final ChangeRequest<SnomedRelationshipRestInput> body,
			
			final Principal principal) {

		final ISnomedRelationship createdRelationship = doCreate(version, null, body, principal);
		return Responses.created(getRelationshipLocation(version, createdRelationship)).build();
	}


	@ApiOperation(
			value="Create relationship on task", 
			notes="Creates a new relationship on a task branch.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Code system version or task not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/relationships", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> createOnTask(
			@ApiParam(value="The code system version")
			@PathVariable("version") 
			final String version,
			
			@ApiParam(value="The task")
			@PathVariable("taskId") 
			final String taskId,
			
			@ApiParam(value="Relationship parameters")
			@RequestBody 
			final ChangeRequest<SnomedRelationshipRestInput> body,
			
			final Principal principal) {

		final ISnomedRelationship createdRelationship = doCreate(version, taskId, body, principal);
		return Responses.created(getRelationshipOnTaskLocation(version, taskId, createdRelationship)).build();
	}

	@ApiOperation(
			value="Retrieve relationship properties", 
			notes="Returns all properties of the specified relationship, including the associated refinability value.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version or relationship not found")
	})
	@RequestMapping(value="/relationships/{relationshipId}", method=RequestMethod.GET)
	public ISnomedRelationship read(
			@ApiParam(value="The code system version")
			@PathVariable("version") 
			final String version,
			
			@ApiParam(value="The relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId) {

		return readOnTask(version, null, relationshipId);
	}

	@ApiOperation(
			value="Retrieve relationship properties on task", 
			notes="Returns all properties of the specified relationship on a task branch, including the associated refinability value.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system version, task or relationship not found")
	})
	@RequestMapping(value="/tasks/{taskId}/relationships/{relationshipId}", method=RequestMethod.GET)
	public ISnomedRelationship readOnTask(
			@ApiParam(value="The code system version")
			@PathVariable("version") 
			final String version,
			
			@ApiParam(value="The task")
			@PathVariable("taskId") 
			final String taskId,
			
			@ApiParam(value="The relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId) {

		final IComponentRef relationshipRef = createComponentRef(version, taskId, relationshipId);
		return delegate.read(relationshipRef);
	}

	@ApiOperation(
			value="Update relationship",
			notes="Updates properties of the specified relationship.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Code system version or relationship not found")
	})
	@RequestMapping(
			value="/relationships/{relationshipId}/updates", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@ApiParam(value="The code system version")
			@PathVariable("version") 
			final String version,
			
			@ApiParam(value="The relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,
			
			@ApiParam(value="Update relationship parameters")
			@RequestBody 
			final ChangeRequest<SnomedRelationshipRestUpdate> body,
			
			final Principal principal) {

		updateOnTask(version, null, relationshipId, body, principal);
	}

	@ApiOperation(
			value="Update relationship on task",
			notes="Updates properties of the specified relationship on a task branch.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Code system version, task or relationship not found")
	})
	@RequestMapping(
			value="/tasks/{taskId}/relationships/{relationshipId}/updates", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.V1_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateOnTask(
			@ApiParam(value="The code system version")
			@PathVariable("version") 
			final String version,
			
			@ApiParam(value="The task")
			@PathVariable("taskId") 
			final String taskId,
			
			@ApiParam(value="The relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,
			
			@ApiParam(value="Update relationship parameters")
			@RequestBody 
			final ChangeRequest<SnomedRelationshipRestUpdate> body,
			
			final Principal principal) {

		final IComponentRef relationshipRef = createComponentRef(version, taskId, relationshipId);
		final ISnomedRelationshipUpdate update = body.getChange().toComponentUpdate();
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();

		delegate.update(relationshipRef, update, userId, commitComment);
	}

	@ApiOperation(
			value="Delete relationship",
			notes="Permanently removes the specified unreleased relationship and related components.<p>If the relationship "
					+ "has already been released, it can not be removed and a <code>409</code> "
					+ "status will be returned.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Code system version or relationship not found"),
		@ApiResponse(code = 409, message = "Relationship cannot be deleted", response = RestApiError.class)
	})
	@RequestMapping(value="/relationships/{relationshipId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@ApiParam(value="The code system version")
			@PathVariable("version") 
			final String version,
			
			@ApiParam(value="The relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,
			
			final Principal principal) {

		deleteOnTask(version, null, relationshipId, principal);
	}

	@ApiOperation(
			value="Delete relationship on task",
			notes="Permanently removes the specified unreleased relationship and related components from a task branch.<p>If the relationship "
					+ "has already been released, it can not be removed and a <code>409</code> "
					+ "status will be returned.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Code system version, task or relationship not found"),
		@ApiResponse(code = 409, message = "Relationship cannot be deleted", response = RestApiError.class)
	})
	@RequestMapping(value="/tasks/{taskId}/relationships/{relationshipId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOnTask(
			@ApiParam(value="The code system version")
			@PathVariable("version") 
			final String version,
			
			@ApiParam(value="The task")
			@PathVariable("taskId") 
			final String taskId,
			
			@ApiParam(value="The relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId, 
			
			final Principal principal) {

		final IComponentRef relationshipRef = createComponentRef(version, taskId, relationshipId);
		final String userId = principal.getName();
		delegate.delete(relationshipRef, userId, "Deleted relationship from store.");
	}
	
	private ISnomedRelationship doCreate(final String version, final String taskId, final ChangeRequest<SnomedRelationshipRestInput> body, final Principal principal) {
		final ISnomedRelationshipInput input = body.getChange().toComponentInput(version, taskId);
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		return delegate.create(input, userId, commitComment);
	}
	
	private URI getRelationshipLocation(final String version, final ISnomedRelationship createdRelationship) {
		return linkTo(methodOn(SnomedRelationshipRestService.class).read(version, createdRelationship.getId())).toUri();
	}

	private URI getRelationshipOnTaskLocation(final String version, final String taskId, final ISnomedRelationship createdRelationship) {
		return linkTo(methodOn(SnomedRelationshipRestService.class).readOnTask(version, taskId, createdRelationship.getId())).toUri();
	}
}