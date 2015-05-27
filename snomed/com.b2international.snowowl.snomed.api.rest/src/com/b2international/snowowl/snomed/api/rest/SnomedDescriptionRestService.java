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
@Api("Descriptions")
@RestController
@RequestMapping(
		produces={ AbstractRestService.SO_MEDIA_TYPE })
public class SnomedDescriptionRestService extends AbstractSnomedRestService {

	@Autowired
	protected ISnomedDescriptionService delegate;
	
	@ApiOperation(
			value="Create Description", 
			notes="Creates a new Description directly on a version.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(
			value="/{path:**}/descriptions", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="Description parameters")
			@RequestBody 
			final ChangeRequest<SnomedDescriptionRestInput> body,
			
			final Principal principal) {
		
		final ISnomedDescription createdDescription = doCreate(branchPath, body, principal);
		return Responses.created(getDescriptionLocation(branchPath, createdDescription)).build();
	}

	@ApiOperation(
			value="Retrieve Description properties", 
			notes="Returns all properties of the specified Description, including acceptability values by language reference set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Description not found")
	})
	@RequestMapping(value="/{path:**}/descriptions/{descriptionId}", method=RequestMethod.GET)
	public ISnomedDescription read(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The Description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId) {
		final IComponentRef conceptRef = createComponentRef(branchPath, descriptionId);
		return delegate.read(conceptRef);
	}


	@ApiOperation(
			value="Update Description",
			notes="Updates properties of the specified Description, also managing language reference set membership.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or Description not found")
	})
	@RequestMapping(
			value="/{path:**}/descriptions/{descriptionId}/updates", 
			method=RequestMethod.POST, 
			consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(			
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The Description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			@ApiParam(value="Update Description parameters")
			@RequestBody 
			final ChangeRequest<SnomedDescriptionRestUpdate> body,
			
			final Principal principal) {

		final IComponentRef conceptRef = createComponentRef(branchPath, descriptionId);
		final ISnomedDescriptionUpdate update = body.getChange().toComponentUpdate();
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();

		delegate.update(conceptRef, update, userId, commitComment);
	}

	@ApiOperation(
			value="Delete Description",
			notes="Permanently removes the specified unreleased Description and related components.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Branch or Description not found")
	})
	@RequestMapping(value="/{path:**}/descriptions/{descriptionId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(			
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The Description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			final Principal principal) {

		final IComponentRef descriptionRef = createComponentRef(branchPath, descriptionId);
		final String userId = principal.getName();
		delegate.delete(descriptionRef, userId, String.format("Deleted Description '%s' from store.", descriptionId));
	}
	
	private ISnomedDescription doCreate(final String branchPath, final ChangeRequest<SnomedDescriptionRestInput> body, final Principal principal) {
		final ISnomedDescriptionInput input = body.getChange().toComponentInput(branchPath);
		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		return delegate.create(input, userId, commitComment);
	}
	
	private URI getDescriptionLocation(final String branchPath, final ISnomedDescription createdDescription) {
		return linkTo(SnomedDescriptionRestService.class).slash(branchPath).slash("descriptions").slash(createdDescription.getId()).toUri();
	}
	
}