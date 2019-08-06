/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.StringUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestSearch;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedDescriptionRestUpdate;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableSet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api(value = "Descriptions", description="Descriptions", tags = { "descriptions" })
@RestController
@RequestMapping(value="/{path:**}/descriptions")
public class SnomedDescriptionRestService extends AbstractRestService {

	public SnomedDescriptionRestService() {
		super(SnomedDescription.Fields.ALL);
	}
	
	@ApiOperation(
			value="Retrieve Descriptions from a branch", 
			notes="Returns all Descriptions from a branch that match the specified query parameters.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
		@ApiResponse(code = 400, message = "Invalid filter config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public DeferredResult<SnomedDescriptions> searchByGet(
			@ApiParam(value="The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			final SnomedDescriptionRestSearch params,

			@ApiParam(value="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		final List<ExtendedLocale> extendedLocales = getExtendedLocales(acceptLanguage);
		List<Sort> sorts = extractSortFields(params.getSort(), branch, extendedLocales);
		
		if (sorts.isEmpty()) {
			final SortField sortField = StringUtils.isEmpty(params.getTerm()) 
					? SearchIndexResourceRequest.DOC_ID 
					: SearchIndexResourceRequest.SCORE;
			sorts = Collections.singletonList(sortField);
		}
		
		final SnomedDescriptionSearchRequestBuilder req = SnomedRequests
			.prepareSearchDescription()
			.filterByIds(params.getId())
			.filterByEffectiveTime(params.getEffectiveTime())
			.filterByActive(params.getActive())
			.filterByModule(params.getModule())
			.filterByConcept(params.getConcept())
			.filterByLanguageCodes(params.getLanguageCode() == null ? null : ImmutableSet.copyOf(params.getLanguageCode()))
			.filterByType(params.getType())
			.filterByTerm(params.getTerm())
			.filterByCaseSignificance(params.getCaseSignificance())
			.filterBySemanticTags(params.getSemanticTag() == null ? null : ImmutableSet.copyOf(params.getSemanticTag()))
			.filterByNamespace(params.getNamespace());

		if (params.getAcceptableIn() == null && params.getPreferredIn() == null && params.getLanguageRefSet() == null) {
			if (params.getAcceptability() != null) {
				if (Acceptability.ACCEPTABLE == params.getAcceptability()) {
					req.filterByAcceptableIn(extendedLocales);
				} else if (Acceptability.PREFERRED == params.getAcceptability()) {
					req.filterByPreferredIn(extendedLocales);
				}
			} else {
				req.filterByLanguageRefSets(extendedLocales);
			}
		} else {
			if (params.getLanguageRefSet() != null) {
				req.filterByLanguageRefSets(Arrays.asList(params.getLanguageRefSet()));
			}
			if (params.getAcceptableIn() != null) {
				req.filterByAcceptableIn(Arrays.asList(params.getAcceptableIn()));
			}
			if (params.getPreferredIn() != null) {
				req.filterByPreferredIn(Arrays.asList(params.getPreferredIn()));
			}
		}
		
		return DeferredResults.wrap(
				req
					.setLocales(extendedLocales)
					.setLimit(params.getLimit())
					.setScroll(params.getScrollKeepAlive())
					.setScrollId(params.getScrollId())
					.setSearchAfter(params.getSearchAfter())
					.setExpand(params.getExpand())
					.sortBy(sorts)
					.build(repositoryId, branch)
					.execute(bus));
	}
	
	@ApiOperation(
			value="Retrieve Descriptions from a branch", 
			notes="Returns all Descriptions from a branch that match the specified query parameters.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
		@ApiResponse(code = 400, message = "Invalid filter config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping(value="/{path:**}/descriptions/search")
	public DeferredResult<SnomedDescriptions> searchByPost(
			@ApiParam(value="The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			@RequestBody(required = false)
			final SnomedDescriptionRestSearch body,
			
			@ApiParam(value="Accepted language tags, in order of preference")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		return searchByGet(branch, body, acceptLanguage);
	}

	@ApiOperation(
			value="Create Description", 
			notes="Creates a new Description directly on a version.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="The branch path", required = true)
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="Description parameters")
			@RequestBody 
			final ChangeRequest<SnomedDescriptionRestInput> body,
			
			final Principal principal) {
		
		final String userId = principal.getName();
		
		final SnomedDescriptionRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
			
		final String createdDescriptionId = change.toRequestBuilder()
			.build(repositoryId, branchPath, userId, commitComment, defaultModuleId)
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
	@GetMapping(value = "/{descriptionId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
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
	@PostMapping(value = "/{descriptionId}/updates", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
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
		final String defaultModuleId = body.getDefaultModuleId();
		body.getChange()
			.toRequestBuilder(descriptionId)
			.build(repositoryId, branchPath, userId, commitComment, defaultModuleId)
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
	@DeleteMapping(value="/{descriptionId}")
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
		return linkTo(SnomedDescriptionRestService.class, branchPath).slash(descriptionId).toUri();
	}
}
