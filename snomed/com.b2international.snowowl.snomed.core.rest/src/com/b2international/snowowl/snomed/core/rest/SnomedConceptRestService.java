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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedConceptRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedConceptRestSearch;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedConceptRestUpdate;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedResourceRequest;
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
@Api(value = "Concepts", description="Concepts", tags = "concepts")
@Controller
@RequestMapping(value = "/{path:**}/concepts")
public class SnomedConceptRestService extends AbstractSnomedRestService {

	public SnomedConceptRestService() {
		super(ImmutableSet.<String>builder()
				.addAll(SnomedConcept.Fields.ALL)
				.add("term") // special term based sort for concepts
				.build());
	}
	
	@ApiOperation(
		value="Retrieve Concepts from a branch", 
		notes="Returns a list with all/filtered Concepts from a branch."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; pt() &ndash; the description representing the concept's preferred term in the given locale<br>"
				+ "&bull; fsn() &ndash; the description representing the concept's fully specified name in the given locale<br>"
				+ "&bull; descriptions() &ndash; the list of descriptions for the concept<br>"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = SnomedConcepts.class),
		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<SnomedConcepts> searchByGet(
			@ApiParam(value = "The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			final SnomedConceptRestSearch params,
			
			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		List<Sort> sorts = extractSortFields(params.getSort());
		
		if (sorts.isEmpty() && !StringUtils.isEmpty(params.getTerm())) {
			sorts = Collections.singletonList(SearchIndexResourceRequest.SCORE);
		}
		
		return SnomedRequests
					.prepareSearchConcept()
					.setLimit(params.getLimit())
					.setSearchAfter(params.getSearchAfter())
					.filterByIds(params.getId())
					.filterByEffectiveTime(params.getEffectiveTime())
					.filterByActive(params.getActive())
					.filterByModule(params.getModule())
					.filterByDefinitionStatus(params.getDefinitionStatus())
					.filterByNamespace(params.getNamespace())
					.filterByParents(params.getParent() == null ? null : ImmutableSet.copyOf(params.getParent()))
					.filterByAncestors(params.getAncestor() == null ? null : ImmutableSet.copyOf(params.getAncestor()))
					.filterByStatedParents(params.getStatedParent() == null ? null : ImmutableSet.copyOf(params.getStatedParent()))
					.filterByStatedAncestors(params.getStatedAncestor() == null ? null : ImmutableSet.copyOf(params.getStatedAncestor()))
					.filterByEcl(params.getEcl())
					.filterByStatedEcl(params.getStatedEcl())
					.filterByQuery(params.getQuery())
					.filterByTerm(params.getTerm())
					.filterByDescriptionLanguageRefSet(acceptLanguage)
					.filterByDescriptionType(params.getDescriptionType())
					.filterByDescriptionSemanticTags(params.getSemanticTag() == null ? null : ImmutableSet.copyOf(params.getSemanticTag()))
					.withDoi(params.getDoi())
					.setExpand(params.getExpand())
					.setLocales(acceptLanguage)
					.sortBy(sorts)
					.build(repositoryId, branch)
					.execute(getBus());
	}
	
	@ApiOperation(
		value="Retrieve Concepts from a branch", 
		notes="Returns a list with all/filtered Concepts from a branch."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; pt() &ndash; the description representing the concept's preferred term in the given locale<br>"
				+ "&bull; fsn() &ndash; the description representing the concept's fully specified name in the given locale<br>"
				+ "&bull; descriptions() &ndash; the list of descriptions for the concept<br>"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = SnomedConcepts.class),
		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<SnomedConcepts> searchByPost(
			@ApiParam(value = "The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			@RequestBody(required = false)
			final SnomedConceptRestSearch body,
			
			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		return searchByGet(branch, body, acceptLanguage);
	}

	@ApiOperation(
		value="Retrieve Concept properties",
		notes="Returns all properties of the specified Concept, including a summary of inactivation indicator and association members."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; pt() &ndash; the description representing the concept's preferred term in the given locale<br>"
				+ "&bull; fsn() &ndash; the description representing the concept's fully specified name in the given locale<br>"
				+ "&bull; descriptions() &ndash; the list of descriptions for the concept<br>"
				+ "&bull; ancestors(offset:0,limit:50,direct:true,expand(pt(),...)) &ndash; the list of concept ancestors (parameter 'direct' is required)<br>"
				+ "&bull; descendants(offset:0,limit:50,direct:true,expand(pt(),...)) &ndash; the list of concept descendants (parameter 'direct' is required)<br>"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@GetMapping(value = "/{conceptId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<SnomedConcept> read(
			@ApiParam(value = "The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value = "The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@ApiParam(value = "Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,
			
			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		return SnomedRequests
					.prepareGetConcept(conceptId)
					.setExpand(expand)
					.setLocales(acceptLanguage)
					.build(repositoryId, branchPath)
					.execute(getBus());
	}

	@ApiOperation(
		value="Create Concept", 
		notes="Creates a new Concept directly on a branch."
	)
	@ApiResponses({
		@ApiResponse(code = 201, message = "Concept created on task"),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value = "The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value = "Concept parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedConceptRestInput> body,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final SnomedConceptRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		final String createdConceptId = change.toRequestBuilder()
			.build(repositoryId, branchPath, author, commitComment, defaultModuleId)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
			.getResultAs(String.class);
		
		
		return ResponseEntity.created(getResourceLocationURI(branchPath, createdConceptId)).build();
	}

	@ApiOperation(
		value="Update Concept",
		notes="Updates properties of the specified Concept, also managing inactivation indicator and association reference set "
				+ "membership in case of inactivation."
				+ "<p>The following properties are allowed to change:"
				+ "<p>"
				+ "&bull; module identifier<br>"
				+ "&bull; subclass definition status<br>"
				+ "&bull; definition status<br>"
				+ "&bull; associated Concepts<br>"
				+ ""
				+ "<p>The following properties, when changed, will trigger inactivation:"
				+ "<p>"
				+ "&bull; inactivation indicator<br>"
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@PostMapping(value = "/{conceptId}/updates", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(			
			@ApiParam(value = "The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value = "The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@ApiParam(value = "Updated Concept parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedConceptRestUpdate> body,
			
			@ApiParam(value = "Force update flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		final SnomedConceptRestUpdate change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		change.toRequestBuilder(conceptId)
			.force(force)
			.build(repositoryId, branchPath, author, commitComment, defaultModuleId)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}

	@ApiOperation(
		value="Delete Concept",
		notes="Permanently removes the specified unreleased Concept and related components.<p>If any participating "
				+ "component has already been released the Concept can not be removed and a <code>409</code> "
				+ "status will be returned."
				+ "<p>The force flag enables the deletion of a released Concept. "
				+ "Deleting published components is against the RF2 history policy so"
				+ " this should only be used to remove a new component from a release before the release is published.</p>"
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Deletion successful"),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class),
		@ApiResponse(code = 409, message = "Cannot be deleted if released", response = RestApiError.class)
	})
	@DeleteMapping(value = "/{conceptId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(			
			@ApiParam(value = "The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value = "The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value = "Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		SnomedRequests
			.prepareDeleteConcept(conceptId)
			.force(force)
			.build(repositoryId, branchPath, author, String.format("Deleted Concept '%s' from store.", conceptId))
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
}
