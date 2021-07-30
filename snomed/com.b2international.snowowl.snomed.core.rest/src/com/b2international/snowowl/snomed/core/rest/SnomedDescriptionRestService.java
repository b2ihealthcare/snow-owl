/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.domain.ResourceSelectors;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedDescriptionRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedDescriptionRestSearch;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedDescriptionRestUpdate;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedResourceRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableSet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 1.0
 */
@Tag(description="Descriptions", name = "descriptions")
@RestController
@RequestMapping(value="/{path:**}/descriptions")
public class SnomedDescriptionRestService extends AbstractRestService {

	public SnomedDescriptionRestService() {
		super(SnomedDescription.Fields.ALL);
	}
	
	@Operation(
		summary="Retrieve Descriptions from a path", 
		description="Returns all Descriptions from a path that match the specified query parameters."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid filter config"),
		@ApiResponse(responseCode = "404", description = "Resource not found")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedDescriptions> searchByGet(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,

			@ParameterObject
			final SnomedDescriptionRestSearch params,

			@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		List<Sort> sorts = extractSortFields(params.getSort());
		
		if (sorts.isEmpty() && !StringUtils.isEmpty(params.getTerm())) {
			sorts = Collections.singletonList(SearchIndexResourceRequest.SCORE);
		}
		
		return SnomedRequests
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
				.filterByNamespace(params.getNamespace())
				.filterByLanguageRefSets(params.getLanguageRefSet() == null ? null : ImmutableSet.copyOf(params.getLanguageRefSet()))
				.filterByAcceptableIn(params.getAcceptableIn() == null ? null : ImmutableSet.copyOf(params.getAcceptableIn()))
				.filterByPreferredIn(params.getPreferredIn() == null ? null : ImmutableSet.copyOf(params.getPreferredIn()))
				.setLocales(acceptLanguage)
				.setLimit(params.getLimit())
				.setSearchAfter(params.getSearchAfter())
				.setExpand(params.getExpand())
				.setFields(params.getField())
				.sortBy(sorts)
				.build(path)
				.execute(getBus());
	}
	
	@Operation(
		summary="Retrieve Descriptions from a path", 
		description="Returns all Descriptions from a path that match the specified query parameters."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid filter config"),
		@ApiResponse(responseCode = "404", description = "Resource not found")
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedDescriptions> searchByPost(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,

			@RequestBody(required = false)
			final SnomedDescriptionRestSearch body,
			
			@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		return searchByGet(path, body, acceptLanguage);
	}

	@Operation(
		summary="Create Description", 
		description="Creates a new Description directly on a version."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "404", description = "Branch not found")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,
			
			@Parameter(description = "Description parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedDescriptionRestInput> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final SnomedDescriptionRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
			
		final String createdDescriptionId = change.toRequestBuilder()
				.commit()
				.setDefaultModuleId(defaultModuleId)
				.setAuthor(author)
				.setCommitComment(commitComment)
				.build(path)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		return ResponseEntity.created(getResourceLocationURI(path, createdDescriptionId)).build();
	}

	@Operation(
		summary="Retrieve Description properties", 
		description="Returns all properties of the specified Description, including acceptability values by language reference set."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Branch or Description not found")
	})
	@GetMapping(value = "/{descriptionId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<SnomedDescription> read(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,
			
			@Parameter(description = "The Description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			@ParameterObject
			final ResourceSelectors selectors) {
		
		return SnomedRequests.prepareGetDescription(descriptionId)
					.setExpand(selectors.getExpand())
					.setFields(selectors.getField())
					.build(path)
					.execute(getBus());
	}

	@Operation(
		summary="Update Description",
		description="Updates properties of the specified Description, also managing language reference set membership."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content"),
		@ApiResponse(responseCode = "404", description = "Branch or Description not found")
	})
	@PutMapping(value = "/{descriptionId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(			
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,
			
			@Parameter(description = "The Description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			@Parameter(description = "Update Description parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedDescriptionRestUpdate> body,
			
			@Parameter(description = "Force update flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		body.getChange()
			.toRequestBuilder(descriptionId)
			.force(force)
			.commit()
			.setDefaultModuleId(defaultModuleId)
			.setAuthor(author)
			.setCommitComment(commitComment)
			.build(path)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
		
	}

	@Operation(
		summary="Delete Description",
		description="Permanently removes the specified unreleased Description and related components."
				+ "<p>The force flag enables the deletion of a released Description. "
				+ "Deleting published components is against the RF2 history policy so"
				+ " this should only be used to remove a new component from a release before the release is published.</p>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Delete successful"),
		@ApiResponse(responseCode = "404", description = "Branch or Description not found")
	})
	@DeleteMapping(value="/{descriptionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(			
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,
			
			@Parameter(description = "The Description identifier")
			@PathVariable(value="descriptionId")
			final String descriptionId,
			
			@Parameter(description = "Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		SnomedRequests.prepareDeleteDescription(descriptionId)
			.force(force)
			.commit()
			.setAuthor(author)
			.setCommitComment(String.format("Deleted Description '%s' from store.", descriptionId))
			.build(path)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
}
