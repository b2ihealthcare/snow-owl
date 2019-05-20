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

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedConceptRestInput;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedConceptRestUpdate;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 1.0
 */
@Api(value = "Concepts", description="Concepts", tags = { "concepts" })
@Controller
@RequestMapping(produces={ AbstractRestService.SO_MEDIA_TYPE })
public class SnomedConceptRestService extends AbstractSnomedRestService {

	@ApiOperation(
			value="Retrieve Concepts from a branch", 
			notes="Returns a list with all/filtered Concepts from a branch."
					+ "<p>The following properties can be expanded:"
					+ "<p>"
					+ "&bull; pt() &ndash; the description representing the concept's preferred term in the given locale<br>"
					+ "&bull; fsn() &ndash; the description representing the concept's fully specified name in the given locale<br>"
					+ "&bull; descriptions() &ndash; the list of descriptions for the concept<br>")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
		@ApiResponse(code = 400, message = "Invalid filter config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/concepts", method=RequestMethod.GET)
	public @ResponseBody DeferredResult<SnomedConcepts> search(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branch,

			@ApiParam(value="The concept status to match")
			@RequestParam(value="active", required=false) 
			final Boolean activeFilter,
			
			@ApiParam(value="The concept module identifier to match")
			@RequestParam(value="module", required=false) 
			final String moduleFilter,
			
			@ApiParam(value="The namespace to match")
			@RequestParam(value="namespace", required=false) 
			final String namespaceFilter,
			
			@ApiParam(value="The effective time to match (yyyyMMdd, exact matches only)")
			@RequestParam(value="effectiveTime", required=false) 
			final String effectiveTimeFilter,
			
			@ApiParam(value="The definition status to match")
			@RequestParam(value="definitionStatus", required=false) 
			final String definitionStatusFilter,
			
			@ApiParam(value="The description term to match")
			@RequestParam(value="term", required=false) 
			final String termFilter,

			@ApiParam(value="The ECL expression to match on the inferred form")
			@RequestParam(value="ecl", required=false) 
			final String eclFilter,
			
			@ApiParam(value="The ECL expression to match on the stated form")
			@RequestParam(value="statedEcl", required=false) 
			final String statedEclFilter,
			
			@ApiParam(value="The SNOMED CT Query expression to match (inferred form only)")
			@RequestParam(value="query", required=false) 
			final String queryFilter,
			
			@ApiParam(value="Description semantic tag(s) to match")
			@RequestParam(value="semanticTag", required=false)
			final String[] semanticTags,
			
			@ApiParam(value="Description type ECL expression to match")
			@RequestParam(value="descriptionType", required=false) 
			final String descriptionTypeFilter,
			
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
			
			@ApiParam(value="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sortKey,
			
			@ApiParam(value="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		final List<ExtendedLocale> extendedLocales = getExtendedLocales(acceptLanguage);

		final List<Sort> sorts = newArrayList();
		if (!CompareUtils.isEmpty(sortKey)) {
			final Map<Order, String> sortValuesByOrders = extractSortKeys(sortKey);
			final List<String> languageRefSetIds = extendedLocales.stream().map(ExtendedLocale::getLanguageRefSetId).collect(Collectors.toList());
			final List<Sort> mappedSorts = mapToSorts(branch, languageRefSetIds, sortValuesByOrders);
			sorts.addAll(mappedSorts);
		}

		final SortField sortField = StringUtils.isEmpty(termFilter) 
				? SearchIndexResourceRequest.DOC_ID 
				: SearchIndexResourceRequest.SCORE;

		sorts.add(sortField);
		
		return DeferredResults.wrap(
				SnomedRequests
					.prepareSearchConcept()
					.setLimit(limit)
					.setScroll(scrollKeepAlive)
					.setScrollId(scrollId)
					.setSearchAfter(searchAfter)
					.filterByActive(activeFilter)
					.filterByModule(moduleFilter)
					.filterByEffectiveTime(effectiveTimeFilter)
					.filterByDefinitionStatus(definitionStatusFilter)
					.filterByNamespace(namespaceFilter)
					.filterByEcl(eclFilter)
					.filterByStatedEcl(statedEclFilter)
					.filterByQuery(queryFilter)
					.filterByTerm(termFilter)
					.filterByDescriptionLanguageRefSet(extendedLocales)
					.filterByDescriptionType(descriptionTypeFilter)
					.filterByDescriptionSemanticTags(semanticTags == null ? null : ImmutableSet.copyOf(semanticTags))
					.setExpand(expand)
					.setLocales(extendedLocales)
					.sortBy(sorts)
					.build(repositoryId, branch)
					.execute(bus));
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
					+ "&bull; descendants(offset:0,limit:50,direct:true,expand(pt(),...)) &ndash; the list of concept descendants (parameter 'direct' is required)<br>")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/concepts/{conceptId}", method=RequestMethod.GET)
	public @ResponseBody DeferredResult<SnomedConcept> read(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@ApiParam(value="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,
			
			@ApiParam(value="Accepted language tags, in order of preference")
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

	@ApiOperation(
			value="Create Concept", 
			notes="Creates a new Concept directly on a branch.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Concept created on task"),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@RequestMapping(
			value="/{path:**}/concepts", 
			method=RequestMethod.POST, 
			consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="Concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestInput> body,

			final Principal principal) {
		
		final String userId = principal.getName();
		
		final SnomedConceptRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		
		final String createdConceptId = change
			.toRequestBuilder()
			.build(repositoryId, branchPath, userId, commitComment)
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS)
			.getResultAs(String.class);
		
		
		return Responses.created(getConceptLocationURI(branchPath, createdConceptId)).build();
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
					+ "&bull; inactivation indicator<br>")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class)
	})
	@RequestMapping(
			value="/{path:**}/concepts/{conceptId}/updates", 
			method=RequestMethod.POST,
			consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(			
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,
			
			@ApiParam(value="Updated Concept parameters")
			@RequestBody 
			final ChangeRequest<SnomedConceptRestUpdate> body,

			final Principal principal) {

		final String userId = principal.getName();
		final String commitComment = body.getCommitComment();
		body.getChange().toRequestBuilder(conceptId)
			.build(repositoryId, branchPath, userId, commitComment)
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@ApiOperation(
			value="Delete Concept",
			notes="Permanently removes the specified unreleased Concept and related components.<p>If any participating "
					+ "component has already been released the Concept can not be removed and a <code>409</code> "
					+ "status will be returned."
					+ "<p>The force flag enables the deletion of a released Concept. "
					+ "Deleting published components is against the RF2 history policy so"
					+ " this should only be used to remove a new component from a release before the release is published.</p>")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Deletion successful"),
		@ApiResponse(code = 404, message = "Branch or Concept not found", response = RestApiError.class),
		@ApiResponse(code = 409, message = "Cannot be deleted if released", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/concepts/{conceptId}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(			
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Concept identifier")
			@PathVariable(value="conceptId")
			final String conceptId,

			@ApiParam(value="Force deletion flag")
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
	
	private Map<Order, String> extractSortKeys(List<String> sortKey) {
		final Map<Order, String> result = Maps.newHashMap();
		final String sortKeyPattern = "[a-zA-Z]+[:]((\\basc\\b)|(\\bdesc\\b))";
		for (String key : sortKey) {
			if (key.matches(sortKeyPattern)) {
				final String[] splittedKey = key.split(":");
				final String field = splittedKey[0];
				final String sortDirection = splittedKey[1];
				final Order order = Order.valueOf(sortDirection.toUpperCase());
				result.put(order, field);
			}
		}
		return result;
	}

	private List<Sort> mapToSorts(String branchPath, List<String> languageRefsetIds, Map<Order, String> fieldsByOrders) {
		final List<Sort> sorts = newArrayList();
		for (Entry<Order, String> sortEntries : fieldsByOrders.entrySet()) {
			final Order order = sortEntries.getKey();
			final String sortValue = sortEntries.getValue();
			if (sortValue.equals(SnomedRf2Headers.FIELD_TERM)) {
				sorts.add(toTermSort(branchPath, languageRefsetIds, order));
			} else {
				for (String conceptField : SnomedConceptDocument.Fields.FIELDS) {
					if (sortValue.equals(conceptField)) {
						sorts.add(SearchResourceRequest.SortField.of(conceptField, Order.ASC == order));
					}
				}
			}
		}
		return sorts;
	}
	
	private Sort toTermSort(String branchPath, List<String> languageRefsetIds, Order order) {
		final Set<String> synonymIds = SnomedRequests.prepareGetSynonyms()
			.setFields("id")
			.build(repositoryId, branchPath)
			.execute(bus)
			.getSync()
			.getItems()
			.stream()
			.map(IComponent::getId)
			.collect(Collectors.toSet());
		
		final String script = "termSort";
		final Map<String, Object> args = ImmutableMap.of("locales", languageRefsetIds, "synonymIds", synonymIds);
		return SearchResourceRequest.SortScript.of(script, args, Order.ASC == order);
	}

	private URI getConceptLocationURI(String branchPath, String conceptId) {
		return linkTo(SnomedConceptRestService.class).slash(branchPath).slash("concepts").slash(conceptId).toUri();
	}
}
