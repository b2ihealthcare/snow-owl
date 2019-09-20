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
package com.b2international.snowowl.snomed.core.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.StringUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.util.DeferredResults;
import com.b2international.snowowl.core.rest.util.Responses;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedConceptRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedConceptRestSearch;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedConceptRestUpdate;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 1.0
 */
@Tag(name = "concepts", description="Concepts")
@Controller
@RequestMapping(value = "/{path:**}/concepts")
public class SnomedConceptRestService extends AbstractSnomedRestService {

	public SnomedConceptRestService() {
		super(ImmutableSet.<String>builder()
				.addAll(SnomedConcept.Fields.ALL)
				.add("term") // special term based sort for concepts
				.build());
	}
	
	@Operation(
		summary="Retrieve Concepts from a branch", 
		description="Returns a list with all/filtered Concepts from a branch."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; pt() &ndash; the description representing the concept's preferred term in the given locale<br>"
				+ "&bull; fsn() &ndash; the description representing the concept's fully specified name in the given locale<br>"
				+ "&bull; descriptions() &ndash; the list of descriptions for the concept<br>"
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
//		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
//		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
//	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody DeferredResult<SnomedConcepts> searchByGet(
			@Parameter(description="The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			final SnomedConceptRestSearch params,
			
			@Parameter(description="Accepted language tags, in order of preference")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		final List<ExtendedLocale> extendedLocales = getExtendedLocales(acceptLanguage);

		List<Sort> sorts = extractSortFields(params.getSort(), branch, extendedLocales);
		
		if (sorts.isEmpty()) {
			final SortField sortField = StringUtils.isEmpty(params.getTerm()) 
					? SearchIndexResourceRequest.DOC_ID 
					: SearchIndexResourceRequest.SCORE;
			sorts = Collections.singletonList(sortField);
		}
		
		return DeferredResults.wrap(
				SnomedRequests
					.prepareSearchConcept()
					.setLimit(params.getLimit())
					.setScroll(params.getScrollKeepAlive())
					.setScrollId(params.getScrollId())
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
					.filterByDescriptionLanguageRefSet(extendedLocales)
					.filterByDescriptionType(params.getDescriptionType())
					.filterByDescriptionSemanticTags(params.getSemanticTag() == null ? null : ImmutableSet.copyOf(params.getSemanticTag()))
					.setExpand(params.getExpand())
					.setLocales(extendedLocales)
					.sortBy(sorts)
					.build(repositoryId, branch)
					.execute(bus));
	}
	
	@Operation(
		summary="Retrieve Concepts from a branch", 
		description="Returns a list with all/filtered Concepts from a branch."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; pt() &ndash; the description representing the concept's preferred term in the given locale<br>"
				+ "&bull; fsn() &ndash; the description representing the concept's fully specified name in the given locale<br>"
				+ "&bull; descriptions() &ndash; the list of descriptions for the concept<br>"
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
//		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
//		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
//	})
	@PostMapping(value="/{path:**}/concepts/search")
	public @ResponseBody DeferredResult<SnomedConcepts> searchByPost(
			@Parameter(description="The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			@RequestBody(required = false)
			final SnomedConceptRestSearch body,
			
			@Parameter(description="Accepted language tags, in order of preference")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		return searchByGet(branch, body, acceptLanguage);
	}

	@Operation(
			summary="Retrieve Concept properties",
			description="Returns all properties of the specified Concept, including a summary of inactivation indicator and association members."
					+ "<p>The following properties can be expanded:"
					+ "<p>"
					+ "&bull; pt() &ndash; the description representing the concept's preferred term in the given locale<br>"
					+ "&bull; fsn() &ndash; the description representing the concept's fully specified name in the given locale<br>"
					+ "&bull; descriptions() &ndash; the list of descriptions for the concept<br>"
					+ "&bull; ancestors(offset:0,limit:50,direct:true,expand(pt(),...)) &ndash; the list of concept ancestors (parameter 'direct' is required)<br>"
					+ "&bull; descendants(offset:0,limit:50,direct:true,expand(pt(),...)) &ndash; the list of concept descendants (parameter 'direct' is required)<br>")
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response = Void.class),
//		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
//	})
	@GetMapping(value = "/{conceptId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody DeferredResult<SnomedConcept> read(
			@Parameter(description="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@Parameter(description="The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@Parameter(description="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,
			
			@Parameter(description="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(acceptLanguage);
		
		return DeferredResults.wrap(
				SnomedRequests
					.prepareGetConcept(conceptId)
					.setExpand(expand)
					.setLocales(extendedLocales)
					.build(repositoryId, branchPath)
					.execute(bus));
	}

	@Operation(
		summary="Create Concept", 
		description="Creates a new Concept directly on a branch."
	)
//	@ApiResponses({
//		@ApiResponse(code = 201, message = "Concept created on task"),
//		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
//	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@Parameter(description="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@Parameter(description="Concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestInput> body,

			final Principal principal) {
		
		final String userId = principal.getName();
		
		final SnomedConceptRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		final String createdConceptId = change.toRequestBuilder()
			.build(repositoryId, branchPath, userId, commitComment, defaultModuleId)
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS)
			.getResultAs(String.class);
		
		
		return Responses.created(getConceptLocationURI(branchPath, createdConceptId)).build();
	}

	@Operation(
			summary="Update Concept",
			description="Updates properties of the specified Concept, also managing inactivation indicator and association reference set "
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
					+ "&bull; inactivation indicator<br>")
//	@ApiResponses({
//		@ApiResponse(code = 204, message = "Update successful"),
//		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
//	})
	@PostMapping(value = "/{conceptId}/updates", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(			
			@Parameter(description="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@Parameter(description="The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@Parameter(description="Updated Concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestUpdate> body,

			final Principal principal) {

		final String userId = principal.getName();
		
		final SnomedConceptRestUpdate change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		change.toRequestBuilder(conceptId)
			.build(repositoryId, branchPath, userId, commitComment, defaultModuleId)
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@Operation(
		summary="Delete Concept",
		description="Permanently removes the specified unreleased Concept and related components.<p>If any participating "
				+ "component has already been released the Concept can not be removed and a <code>409</code> "
				+ "status will be returned."
				+ "<p>The force flag enables the deletion of a released Concept. "
				+ "Deleting published components is against the RF2 history policy so"
				+ " this should only be used to remove a new component from a release before the release is published.</p>"
	)
//	@ApiResponses({
//		@ApiResponse(code = 204, message = "Deletion successful"),
//		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class),
//		@ApiResponse(code = 409, message = "Cannot be deleted if released", response = RestApiError.class)
//	})
	@DeleteMapping(value = "/{conceptId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(			
			@Parameter(description="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@Parameter(description="The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@Parameter(description="Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,

			final Principal principal) {
		SnomedRequests
			.prepareDeleteConcept(conceptId)
			.force(force)
			.build(repositoryId, branchPath, principal.getName(), String.format("Deleted Concept '%s' from store.", conceptId))
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS);
	}
	
	private URI getConceptLocationURI(String branchPath, String conceptId) {
		return linkTo(SnomedConceptRestService.class, branchPath).slash(conceptId).toUri();
	}
	
	@Override
	protected Sort toSort(String field, boolean ascending, String branch, List<ExtendedLocale> extendedLocales) {
		switch (field) {
		case SnomedRf2Headers.FIELD_TERM:
			return toTermSort(field, ascending, branch, extendedLocales);
		}
		return super.toSort(field, ascending, branch, extendedLocales);
	}

	private Sort toTermSort(String field, boolean ascending, String branchPath, List<ExtendedLocale> extendedLocales) {
		final Set<String> synonymIds = SnomedRequests.prepareGetSynonyms()
			.setFields(SnomedConcept.Fields.ID)
			.build(repositoryId, branchPath)
			.execute(bus)
			.getSync()
			.getItems()
			.stream()
			.map(IComponent::getId)
			.collect(Collectors.toSet());
	
		final Map<String, Object> args = ImmutableMap.of("locales", SnomedDescriptionSearchRequestBuilder.getLanguageRefSetIds(extendedLocales), "synonymIds", synonymIds);
		return SearchResourceRequest.SortScript.of("termSort", args, ascending);
	}
	
}
