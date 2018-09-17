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
package com.b2international.snowowl.snomed.api.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.http.AcceptHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRelationshipRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRelationshipRestUpdate;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
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

	@ApiOperation(
			value="Retrieve Relationships from a branch", 
			notes="Returns a list with all/filtered Relationships from a branch."
					+ "<p>The following properties can be expanded:"
					+ "<p>"
					+ "&bull; type() &ndash; the relationship's type concept<br>"
					+ "&bull; source() &ndash; the relationship's source concept<br>"
					+ "&bull; destination() &ndash; the relationship's destination concept<br>")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
		@ApiResponse(code = 400, message = "Invalid filter config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/relationships", method=RequestMethod.GET)
	public DeferredResult<SnomedRelationships> search(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branch,

			@ApiParam(value="The status to match")
			@RequestParam(value="active", required=false) 
			final Boolean activeFilter,
			
			@ApiParam(value="The module identifier to match")
			@RequestParam(value="module", required=false) 
			final String moduleFilter,
			
			@ApiParam(value="The namespace to match")
			@RequestParam(value="namespace", required=false) 
			final String namespaceFilter,
			
			@ApiParam(value="The effective time to match (yyyyMMdd, exact matches only)")
			@RequestParam(value="effectiveTime", required=false) 
			final String effectiveTimeFilter,
			
			@ApiParam(value="The source concept to match")
			@RequestParam(value="source", required=false) 
			final String sourceFilter,
			
			@ApiParam(value="The type concept to match")
			@RequestParam(value="type", required=false) 
			final String typeFilter,
			
			@ApiParam(value="The destination concept to match")
			@RequestParam(value="destination", required=false) 
			final String destinationFilter,
			
			@ApiParam(value="The characteristic type to match")
			@RequestParam(value="characteristicType", required=false) 
			final String characteristicTypeFilter,
			
			@ApiParam(value="The group to match")
			@RequestParam(value="group", required=false) 
			final Integer groupFilter,
			
			@ApiParam(value="The union group to match")
			@RequestParam(value="unionGroup", required=false) 
			final Integer unionGroupFilter,
			
			@ApiParam(value="The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false) 
			final String scrollKeepAlive,
			
			@ApiParam(value="A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false) 
			final String scrollId,

			@ApiParam(value="The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false) 
			final String searchAfter,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,
			
			@ApiParam(value="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,

			@ApiParam(value="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		final List<ExtendedLocale> extendedLocales;
		
		try {
			extendedLocales = AcceptHeader.parseExtendedLocales(new StringReader(acceptLanguage));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return DeferredResults.wrap(
				SnomedRequests
					.prepareSearchRelationship()
					.setLimit(limit)
					.setScroll(scrollKeepAlive)
					.setScrollId(scrollId)
					.setSearchAfter(searchAfter)
					.filterByActive(activeFilter)
					.filterByModule(moduleFilter)
					.filterByNamespace(namespaceFilter)
					.filterByEffectiveTime(effectiveTimeFilter)
					.filterByCharacteristicType(characteristicTypeFilter)
					.filterBySource(sourceFilter)
					.filterByType(typeFilter)
					.filterByDestination(destinationFilter)
					.filterByGroup(groupFilter)
					.filterByUnionGroup(unionGroupFilter)
					.setExpand(expand)
					.setLocales(extendedLocales)
					.build(repositoryId, branch)
					.execute(bus));
		
	}
	
	@ApiOperation(
			value="Create Relationship", 
			notes="Creates a new Relationship directly on a version branch.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
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

		final String commitComment = body.getCommitComment();
		final String createdRelationshipId = body
				.getChange()
				.toRequestBuilder()
				.build(repositoryId, branchPath, principal.getName(), commitComment)
				.execute(bus)
				.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS)
				.getResultAs(String.class);
				
		return Responses.created(getRelationshipLocation(branchPath, createdRelationshipId)).build();
	}

	@ApiOperation(
			value="Retrieve Relationship properties", 
			notes="Returns all properties of the specified Relationship, including the associated refinability value.",
			response=Void.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Relationship not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/relationships/{relationshipId}", method=RequestMethod.GET)
	public DeferredResult<SnomedRelationship> read(
			@ApiParam(value="The branch path")
			@PathVariable("path") 
			final String branchPath,
			
			@ApiParam(value="The Relationship identifier")
			@PathVariable("relationshipId") 
			final String relationshipId) {

		return DeferredResults.wrap(
				SnomedRequests.prepareGetRelationship(relationshipId)
					.build(repositoryId, branchPath)
					.execute(bus));
	}

	@ApiOperation(
			value="Update Relationship",
			notes="Updates properties of the specified Relationship.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or Relationship not found", response = RestApiError.class)
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

		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		final SnomedRelationshipRestUpdate update = body.getChange();

		SnomedRequests
			.prepareUpdateRelationship(relationshipId)
			.setActive(update.isActive())
			.setModuleId(update.getModuleId())
			.setCharacteristicType(update.getCharacteristicType())
			.setGroup(update.getGroup())
			.setUnionGroup(update.getUnionGroup())
			.setModifier(update.getModifier())
			.setTypeId(update.getTypeId())
			.setDestinationId(update.getDestinationId())
			.build(repositoryId, branchPath, userId, commitComment)
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@ApiOperation(
			value="Delete Relationship",
			notes="Permanently removes the specified unreleased Relationship and related components.<p>If the Relationship "
					+ "has already been released, it can not be removed and a <code>409</code> "
					+ "status will be returned."
					+ "<p>The force flag enables the deletion of a released Relationship. "
					+ "Deleting published components is against the RF2 history policy so"
					+ " this should only be used to remove a new component from a release before the release is published.</p>")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Branch or Relationship not found", response = RestApiError.class),
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

			@ApiParam(value="Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,
			
			final Principal principal) {

		SnomedRequests.prepareDeleteRelationship(relationshipId)
			.force(force)
			.build(repositoryId, branchPath, principal.getName(), String.format("Deleted Relationship '%s' from store.", relationshipId))
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	private URI getRelationshipLocation(final String branchPath, final String relationshipId) {
		return linkTo(SnomedRelationshipRestService.class).slash(branchPath).slash("relationships").slash(relationshipId).toUri();
	}
}
