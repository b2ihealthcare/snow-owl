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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestUpdate;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
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
		
		final String commitComment = body.getCommitComment();
		final SnomedDescriptionCreateRequestBuilder req = body.getChange().toComponentInput();
		
		final ISnomedDescription createdDescription = 
				SnomedRequests
					.prepareCommit(principal.getName(), branchPath)
					.setCommitComment(commitComment)
					.setBody(req)
					.build()
					.executeSync(bus, 120L * 1000L)
					.getResultAs(ISnomedDescription.class);
		
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
	public DeferredResult<ISnomedDescription> read(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The Description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId) {
		return DeferredResults.wrap(
				SnomedRequests
					.prepareGetDescription()
					.setId(descriptionId)
					.build(branchPath)
					.execute(bus));
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

		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		final SnomedDescriptionRestUpdate update = body.getChange();

		SnomedRequests
			.prepareCommit(userId, branchPath)
			.setBody(
				SnomedRequests
					.prepareDescriptionUpdate(descriptionId)
					.setActive(update.isActive())
					.setModuleId(update.getModuleId())
					.setAssociationTargets(update.getAssociationTargets())
					.setInactivationIndicator(update.getInactivationIndicator())
					.setCaseSignificance(update.getCaseSignificance())
					.setAcceptability(update.getAcceptability())
					.build())
			.setCommitComment(commitComment)
			.build()
			.executeSync(bus, 120L * 1000L);
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
		
		SnomedRequests
			.prepareCommit(principal.getName(), branchPath)
			.setBody(SnomedRequests.prepareDeleteDescription(descriptionId))
			.setCommitComment(String.format("Deleted Description '%s' from store.", descriptionId))
			.build()
			.executeSync(bus, 120L * 1000L);
	}
	
	private URI getDescriptionLocation(final String branchPath, final ISnomedDescription createdDescription) {
		return linkTo(SnomedDescriptionRestService.class).slash(branchPath).slash("descriptions").slash(createdDescription.getId()).toUri();
	}
}
