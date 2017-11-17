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
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.StringUtils;
import com.b2international.commons.http.AcceptHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestUpdate;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Api("Descriptions")
@RestController
@RequestMapping(
		produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class SnomedDescriptionRestService extends AbstractSnomedRestService {

	@ApiOperation(
			value="Retrieve Descriptions from a branch", 
			notes="Returns all Descriptions from a branch that match the specified query parameters.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
		@ApiResponse(code = 400, message = "Invalid filter config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/descriptions", method=RequestMethod.GET)
	public @ResponseBody DeferredResult<SnomedDescriptions> search(
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
			
			@ApiParam(value="The term to match")
			@RequestParam(value="term", required=false) 
			final String termFilter,

			@ApiParam(value="The concept ECL expression to match")
			@RequestParam(value="concept", required=false) 
			final String conceptFilter,
			
			@ApiParam(value="The type ECL expression to match")
			@RequestParam(value="type", required=false) 
			final String typeFilter,
			
			@ApiParam(value="The acceptability to match. DEPRECATED! Use acceptableIn or preferredIn!")
			@RequestParam(value="acceptability", required=false) 
			final Acceptability acceptabilityFilter,
			
			@ApiParam(value="Acceptable membership to match in these language refsets")
			@RequestParam(value="acceptableIn", required=false)
			final String[] acceptableIn,
			
			@ApiParam(value="Preferred membership to match in these language refsets")
			@RequestParam(value="preferredIn", required=false)
			final String[] preferredIn,
			
			@ApiParam(value="Any membership to match in these language refsets")
			@RequestParam(value="languageRefSet", required=false)
			final String[] languageRefSet,
			
			@ApiParam(value="The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false) 
			final String scrollKeepAlive,
			
			@ApiParam(value="A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false) 
			final String scrollId,

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
		
		final SortField sortField = StringUtils.isEmpty(termFilter) 
				? SearchIndexResourceRequest.DOC_ID 
				: SearchIndexResourceRequest.SCORE;
		
		final SnomedDescriptionSearchRequestBuilder req = SnomedRequests
			.prepareSearchDescription()
			.filterByActive(activeFilter)
			.filterByModule(moduleFilter)
			.filterByNamespace(namespaceFilter)
			.filterByConcept(conceptFilter)
			.filterByTerm(termFilter)
			.filterByType(typeFilter);

		if (acceptableIn == null && preferredIn == null && languageRefSet == null) {
			if (acceptabilityFilter != null) {
				if (Acceptability.ACCEPTABLE == acceptabilityFilter) {
					req.filterByAcceptableIn(extendedLocales);
				} else if (Acceptability.PREFERRED == acceptabilityFilter) {
					req.filterByPreferredIn(extendedLocales);
				}
			} else {
				req.filterByLanguageRefSets(extendedLocales);
			}
		} else {
			if (languageRefSet != null) {
				req.filterByLanguageRefSets(Arrays.asList(languageRefSet));
			}
			if (acceptableIn != null) {
				req.filterByAcceptableIn(Arrays.asList(acceptableIn));
			}
			if (preferredIn != null) {
				req.filterByPreferredIn(Arrays.asList(preferredIn));
			}
		}
		
		return DeferredResults.wrap(
				req
					.setLocales(extendedLocales)
					.setLimit(limit)
					.setScroll(scrollKeepAlive)
					.setScrollId(scrollId)
					.setExpand(expand)
					.sortBy(sortField)
					.build(repositoryId, branch)
					.execute(bus));
	}

	@ApiOperation(
			value="Create Description", 
			notes="Creates a new Description directly on a version.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
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
		
		final String createdDescriptionId = body
			.getChange()
			.toRequestBuilder()
			.build(repositoryId, branchPath, principal.getName(), commitComment)
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS)
			.getResultAs(String.class);
		
		return Responses.created(getDescriptionLocation(branchPath, createdDescriptionId)).build();
	}

	@ApiOperation(
			value="Retrieve Description properties", 
			notes="Returns all properties of the specified Description, including acceptability values by language reference set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Branch or Description not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/descriptions/{descriptionId}", method=RequestMethod.GET)
	public DeferredResult<SnomedDescription> read(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The Description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			@ApiParam(value="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand) {
		
		return DeferredResults.wrap(
				SnomedRequests.prepareGetDescription(descriptionId)
					.setExpand(expand)
					.build(repositoryId, branchPath)
					.execute(bus));
	}


	@ApiOperation(
			value="Update Description",
			notes="Updates properties of the specified Description, also managing language reference set membership.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or Description not found", response = RestApiError.class)
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
			.prepareUpdateDescription(descriptionId)
			.setActive(update.isActive())
			.setModuleId(update.getModuleId())
			.setAssociationTargets(update.getAssociationTargets())
			.setInactivationIndicator(update.getInactivationIndicator())
			.setCaseSignificance(update.getCaseSignificance())
			.setAcceptability(update.getAcceptability())
			.setTypeId(update.getTypeId())
			.setTerm(update.getTerm())
			.setLanguageCode(update.getLanguageCode())
			.build(repositoryId, branchPath, userId, commitComment)
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS);
		
	}

	@ApiOperation(
			value="Delete Description",
			notes="Permanently removes the specified unreleased Description and related components."
					+ "<p>The force flag enables the deletion of a released Description. "
					+ "Deleting published components is against the RF2 history policy so"
					+ " this should only be used to remove a new component from a release before the release is published.</p>")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Branch or Description not found", response = RestApiError.class)
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
			
			@ApiParam(value="Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,

			final Principal principal) {
		
		SnomedRequests.prepareDeleteDescription(descriptionId)
			.force(force)
			.build(repositoryId, branchPath, principal.getName(), String.format("Deleted Description '%s' from store.", descriptionId))
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS);
	}
	
	private URI getDescriptionLocation(final String branchPath, final String descriptionId) {
		return linkTo(SnomedDescriptionRestService.class).slash(branchPath).slash("descriptions").slash(descriptionId).toUri();
	}
}
