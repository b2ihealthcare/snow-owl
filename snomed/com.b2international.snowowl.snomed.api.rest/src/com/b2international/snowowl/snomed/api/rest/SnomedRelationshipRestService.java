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
@Api("Relationships")
@RestController
@RequestMapping(
		produces={ AbstractRestService.SO_MEDIA_TYPE })
public class SnomedRelationshipRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedRelationshipService delegate;

	@ApiOperation(
			value="Create Relationship", 
			notes="Creates a new Relationship directly on a version branch.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(
			value="/{path:**}/relationships", 
			method=RequestMethod.POST, 
			consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="The branch path")
			@PathVariable("path") 
			final String branchPath,
			
			@ApiParam(value="Relationship parameters")
			@RequestBody 
			final ChangeRequest<SnomedRelationshipRestInput> body,
			
			final Principal principal) {

		final ISnomedRelationship createdRelationship = doCreate(branchPath, body, principal);
		return Responses.created(getRelationshipLocation(branchPath, createdRelationship)).build();
	}

	@ApiOperation(
			value="Retrieve Relationship properties", 
			notes="Returns all properties of the specified Relationship, including the associated refinability value.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Relationship not found")
	})
	@RequestMapping(value="/{path:**}/relationships/{relationshipId}", method=RequestMethod.GET)
	public ISnomedRelationship read(
			@ApiParam(value="The branch path")
			@PathVariable("path") 
			final String branchPath,
			
			@ApiParam(value="The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId) {

		return delegate.read(createComponentRef(branchPath, relationshipId));
	}

	@ApiOperation(
			value="Update Relationship",
			notes="Updates properties of the specified Relationship.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or Relationship not found")
	})
	@RequestMapping(
			value="/{path:**}/relationships/{relationshipId}/updates", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@ApiParam(value="The branch path")
			@PathVariable("path") 
			final String branchPath,
			
			@ApiParam(value="The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,
			
			@ApiParam(value="Update Relationship parameters")
			@RequestBody 
			final ChangeRequest<SnomedRelationshipRestUpdate> body,
			
			final Principal principal) {

		final IComponentRef relationshipRef = createComponentRef(branchPath, relationshipId);
		final ISnomedRelationshipUpdate update = body.getChange().toComponentUpdate();
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();

		delegate.update(relationshipRef, update, userId, commitComment);
	}

	@ApiOperation(
			value="Delete Relationship",
			notes="Permanently removes the specified unreleased Relationship and related components.<p>If the Relationship "
					+ "has already been released, it can not be removed and a <code>409</code> "
					+ "status will be returned.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Branch or Relationship not found"),
		@ApiResponse(code = 409, message = "Relationship cannot be deleted", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/relationships/{relationshipId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@ApiParam(value="The branch path")
			@PathVariable("path") 
			final String branchPath,
			
			@ApiParam(value="The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,
			
			final Principal principal) {

		final IComponentRef relationshipRef = createComponentRef(branchPath, relationshipId);
		final String userId = principal.getName();
		delegate.delete(relationshipRef, userId, String.format("Deleted Relationship '%s' from store.", relationshipId));
	}

	private ISnomedRelationship doCreate(final String branchPath, final ChangeRequest<SnomedRelationshipRestInput> body, final Principal principal) {
		final ISnomedRelationshipInput input = body.getChange().toComponentInput(branchPath);
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		return delegate.create(input, userId, commitComment);
	}
	
	private URI getRelationshipLocation(final String branchPath, final ISnomedRelationship createdRelationship) {
		return linkTo(SnomedRelationshipRestService.class).slash(branchPath).slash("relationships").slash(createdRelationship.getId()).toUri();
	}

}