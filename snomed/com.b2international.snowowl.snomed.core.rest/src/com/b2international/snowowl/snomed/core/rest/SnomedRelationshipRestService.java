/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRelationshipRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRelationshipRestSearch;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRelationshipRestUpdate;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedResourceRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api(value = "Relationships", description="Relationships", tags = "relationships")
@RestController
@RequestMapping(value = "/{path:**}/relationships")		
public class SnomedRelationshipRestService extends AbstractSnomedRestService {

	public SnomedRelationshipRestService() {
		super(SnomedRelationship.Fields.ALL);
	}
	
	@ApiOperation(
		value="Retrieve Relationships from a branch", 
		notes="Returns a list with all/filtered Relationships from a branch."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; type() &ndash; the relationship's type concept<br>"
				+ "&bull; source() &ndash; the relationship's source concept<br>"
				+ "&bull; destination() &ndash; the relationship's destination concept<br>"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = SnomedRelationships.class),
		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedRelationships> searchByGet(
			@ApiParam(value = "The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			final SnomedRelationshipRestSearch params,
			
			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		return SnomedRequests
					.prepareSearchRelationship()
					.filterByIds(params.getId())
					.filterByActive(params.getActive())
					.filterByModule(params.getModule())
					.filterByNamespace(params.getNamespace())
					.filterByEffectiveTime(params.getEffectiveTime())
					.filterByCharacteristicType(params.getCharacteristicType())
					.filterBySource(params.getSource())
					.filterByType(params.getType())
					.filterByDestination(params.getDestination())
					.filterByGroup(params.getGroup())
					.filterByUnionGroup(params.getUnionGroup())
					.setLimit(params.getLimit())
					.setSearchAfter(params.getSearchAfter())
					.setExpand(params.getExpand())
					.setLocales(acceptLanguage)
					.sortBy(extractSortFields(params.getSort()))
					.build(repositoryId, branch)
					.execute(getBus());
	}
	
	@ApiOperation(
		value="Retrieve Relationships from a branch", 
		notes="Returns a list with all/filtered Relationships from a branch."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; type() &ndash; the relationship's type concept<br>"
				+ "&bull; source() &ndash; the relationship's source concept<br>"
				+ "&bull; destination() &ndash; the relationship's destination concept<br>"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = SnomedRelationships.class),
		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedRelationships> searchByPost(
			@ApiParam(value = "The branch path", required = true)
			@PathVariable(value="path")
			final String branch,
	
			@RequestBody(required = false)
			final SnomedRelationshipRestSearch params,
		
			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		return searchByGet(branch, params, acceptLanguage);
	}
	
	@ApiOperation(
		value="Create Relationship", 
		notes="Creates a new Relationship directly on a version branch."
	)
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value = "The branch path")
			@PathVariable("path") 
			final String branchPath,
			
			@ApiParam(value = "Relationship parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedRelationshipRestInput> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		final SnomedRelationshipRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		final String createdRelationshipId = change.toRequestBuilder()
				.build(repositoryId, branchPath, author, commitComment, defaultModuleId)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
				
		return ResponseEntity.created(getResourceLocationURI(branchPath, createdRelationshipId)).build();
	}

	@ApiOperation(
		value="Retrieve Relationship properties", 
		notes="Returns all properties of the specified Relationship, including the associated refinability value."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Relationship not found", response = RestApiError.class)
	})
	@GetMapping(value = "/{relationshipId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedRelationship> read(
			@ApiParam(value = "The branch path")
			@PathVariable("path") 
			final String branchPath,
			
			@ApiParam(value = "The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId) {

		return SnomedRequests.prepareGetRelationship(relationshipId)
					.build(repositoryId, branchPath)
					.execute(getBus());
	}

	@ApiOperation(
		value="Update Relationship",
		notes="Updates properties of the specified Relationship."
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or Relationship not found", response = RestApiError.class)
	})
	@PostMapping(value = "/{relationshipId}/updates", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@ApiParam(value = "The branch path")
			@PathVariable("path") 
			final String branchPath,
			
			@ApiParam(value = "The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,
			
			@ApiParam(value = "Update Relationship parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedRelationshipRestUpdate> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		final String commitComment = body.getCommitComment();
		final SnomedRelationshipRestUpdate update = body.getChange();
		final String defaultModuleId = body.getDefaultModuleId();

		SnomedRequests
			.prepareUpdateRelationship(relationshipId)
			.setActive(update.isActive())
			.setModuleId(update.getModuleId())
			.setCharacteristicTypeId(update.getCharacteristicTypeId())
			.setGroup(update.getGroup())
			.setUnionGroup(update.getUnionGroup())
			.setModifierId(update.getModifierId())
			.setTypeId(update.getTypeId())
			.setDestinationId(update.getDestinationId())
			.build(repositoryId, branchPath, author, commitComment, defaultModuleId)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}

	@ApiOperation(
		value="Delete Relationship",
		notes="Permanently removes the specified unreleased Relationship and related components.<p>If the Relationship "
				+ "has already been released, it can not be removed and a <code>409</code> "
				+ "status will be returned."
				+ "<p>The force flag enables the deletion of a released Relationship. "
				+ "Deleting published components is against the RF2 history policy so"
				+ " this should only be used to remove a new component from a release before the release is published.</p>"
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Branch or Relationship not found", response = RestApiError.class),
		@ApiResponse(code = 409, message = "Relationship cannot be deleted", response = RestApiError.class)
	})
	@DeleteMapping(value = "/{relationshipId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@ApiParam(value = "The branch path")
			@PathVariable("path") 
			final String branchPath,
			
			@ApiParam(value = "The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId,

			@ApiParam(value = "Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		SnomedRequests.prepareDeleteRelationship(relationshipId)
			.force(force)
			.build(repositoryId, branchPath, author, String.format("Deleted Relationship '%s' from store.", relationshipId))
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}

}
