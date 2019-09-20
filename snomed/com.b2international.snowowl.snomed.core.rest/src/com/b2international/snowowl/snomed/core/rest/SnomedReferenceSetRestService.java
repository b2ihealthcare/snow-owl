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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.util.DeferredResults;
import com.b2international.snowowl.core.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.core.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRefSetRestInput;
import com.b2international.snowowl.snomed.core.rest.request.BulkRestRequest;
import com.b2international.snowowl.snomed.core.rest.request.RefSetMemberRequestResolver;
import com.b2international.snowowl.snomed.core.rest.request.RefSetRequestResolver;
import com.b2international.snowowl.snomed.core.rest.request.RequestResolver;
import com.b2international.snowowl.snomed.core.rest.request.RestRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 4.5
 */
@Tag(name = "refSets", description="RefSets")
@Controller
@RequestMapping(value = "/{path:**}/refsets")		
public class SnomedReferenceSetRestService extends AbstractSnomedRestService {

	public SnomedReferenceSetRestService() {
		super(SnomedReferenceSet.Fields.ALL);
	}
	
	@Operation(
		summary="Retrieve Reference Sets from a branch", 
		description="Returns a list with all reference sets from a branch."
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response = CollectionResource.class),
//		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
//	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })	
	public @ResponseBody DeferredResult<SnomedReferenceSets> search(
			@Parameter(description="The branch path", required = true)
			@PathVariable(value="path")
			final String branchPath,
			
			@Parameter(description="The reference set type to match")
			@PathVariable(value="refSetTypes")
			final String[] refSetTypes,
			
			@Parameter(description="The scrollKeepAlive to start a scroll using this query")
			@RequestParam(value="scrollKeepAlive", required=false) 
			final String scrollKeepAlive,
			
			@Parameter(description="A scrollId to continue scrolling a previous query")
			@RequestParam(value="scrollId", required=false) 
			final String scrollId,

			@Parameter(description="The search key to use for retrieving the next page of results")
			@RequestParam(value="searchAfter", required=false) 
			final String searchAfter,

			@Parameter(description="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit,
			
			@Parameter(description="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sortKeys) {
		
		return DeferredResults.wrap(SnomedRequests.prepareSearchRefSet()
				.filterByTypes(Collections3.toImmutableSet(refSetTypes).stream().map(SnomedRefSetType::valueOf).collect(Collectors.toSet()))
				.setScroll(scrollKeepAlive)
				.setScrollId(scrollId)
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.sortBy(extractSortFields(sortKeys))
				.build(repositoryId, branchPath)
				.execute(bus));
	}
	
	@Operation(
		summary="Retrieve Reference Set",
		description="Returns all properties of the specified Reference set."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; members(offset:0,limit:50,expand(referencedComponent(expand(pt(),...))) &ndash; the reference set members, and any applicable nested expansions<br>"
	)
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK", response = Void.class),
//		@ApiResponse(code = 404, message = "Branch or Reference set not found", response = RestApiError.class)
//	})
	@GetMapping(value = "/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody DeferredResult<SnomedReferenceSet> get(
			@Parameter(description="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@Parameter(description="The Reference set identifier")
			@PathVariable(value="id")
			final String referenceSetId,
			
			@Parameter(description="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,

			@Parameter(description="Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(acceptLanguage);
		
		return DeferredResults.wrap(SnomedRequests
				.prepareGetReferenceSet(referenceSetId)
				.setExpand(expand)
				.setLocales(extendedLocales)
				.build(repositoryId, branchPath)
				.execute(bus));
	}
	
	@Operation(
			summary="Create a reference set",
			description="Creates a new reference set directly on a branch. Creates the corresponding identifier concept as well based on the given JSON body."
			+ "<p>Reference Set type and referenced component type properties are immutable and cannot be modified, "
			+ "thus there is no update endpoint for reference sets. "
			+ "To update the corresponding identifier concept properties, use the concept update endpoint.</p>")
//	@ApiResponses({
//		@ApiResponse(code = 201, message = "Created", response = Void.class),
//		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
//	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@Parameter(description="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@Parameter(description="Reference set parameters")
			@RequestBody 
			final ChangeRequest<SnomedRefSetRestInput> body,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final SnomedRefSetRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId(); 
		
		final String createdRefSetId = change.toRequestBuilder() 
			.build(repositoryId, branchPath, author, commitComment, defaultModuleId)
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS)
			.getResultAs(String.class);
		
		return Responses.created(getRefSetLocationURI(branchPath, createdRefSetId)).build();
	}
	
	@Operation(
		summary="Executes an action on a reference set",
		description="Executes an action specified via the request body on the reference set identified by the given identifier."
				+ "<p>Supported actions are:"
				+ "&bull; 'sync' - Executes sync action on all members of this query reference set, see sync on /members/:id/actions endpoint"
				+ "</p>"
	)
//	@ApiResponses({
//		@ApiResponse(code = 204, message = "No content"),
//		@ApiResponse(code = 404, message = "Branch or reference set not found", response = RestApiError.class)
//	})
	@PostMapping(value = "/{id}/actions", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void executeAction(
			@Parameter(description="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@Parameter(description="The reference set identifier")
			@PathVariable(value="id")
			final String refSetId,
			
			@Parameter(description="Reference set action")
			@RequestBody 
			final ChangeRequest<RestRequest> body,
			
			@RequestHeader(value = X_AUTHOR)
			final String author) {
		
		final RequestResolver<TransactionContext> resolver = new RefSetRequestResolver();
		
		final RestRequest change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		change.setSource("referenceSetId", refSetId);
		
		SnomedRequests.prepareCommit()
			.setDefaultModuleId(defaultModuleId)
			.setBody(change.resolve(resolver))
			.setCommitComment(commitComment)
			.setAuthor(author)
			.build(repositoryId, branchPath)
			.execute(bus)
			.getSync();
	}
	
	@Operation(
		summary="Executes multiple requests (bulk request) on the members of a single reference set.",
		description="Execute reference set member create, update, delete requests at once on a single reference set. "
				+ "See bulk requests section for more details."
	)
//	@ApiResponses({
//		@ApiResponse(code = 204, message = "No content"),
//		@ApiResponse(code = 404, message = "Branch or reference set not found", response = RestApiError.class)
//	})
	@PutMapping(value="/{id}/members", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateMembers(
			@Parameter(description="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@Parameter(description="The reference set identifier")
			@PathVariable(value="id")
			final String refSetId,
			
			@Parameter(description="The reference set member changes")
			@RequestBody
			final ChangeRequest<BulkRestRequest> request,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final RequestResolver<TransactionContext> resolver = new RefSetMemberRequestResolver();
		
		final BulkRestRequest bulkRequest = request.getChange();
		final String commitComment = request.getCommitComment();
		final String defaultModuleId = request.getDefaultModuleId();
		
		// FIXME setting referenceSetId even if defined??? 
		// enforces that new members will be created in the defined refset
		for (RestRequest req : bulkRequest.getRequests()) {
			req.setSource("referenceSetId", refSetId);
		}
		
		final BulkRequestBuilder<TransactionContext> updateRequestBuilder = BulkRequest.create();
		bulkRequest.resolve(resolver).forEach(updateRequestBuilder::add);
		
		SnomedRequests.prepareCommit()
			.setDefaultModuleId(defaultModuleId)
			.setBody(updateRequestBuilder.build())
			.setAuthor(author)
			.setCommitComment(commitComment)
			.build(repositoryId, branchPath)
			.execute(bus)
			.getSync();
	}
	
	@Operation(
		summary="Delete Reference Set",
		description="Removes the reference set from the terminology store."
	)
//	@ApiResponses({
//		@ApiResponse(code = 204, message = "OK", response = Void.class),
//		@ApiResponse(code = 404, message = "Branch or Reference set not found", response = RestApiError.class)
//	})
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@Parameter(description="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@Parameter(description="The Reference set identifier")
			@PathVariable(value="id")
			final String referenceSetId,
			
			@Parameter(description="Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		SnomedRequests.prepareDeleteReferenceSet(referenceSetId, force)
				.build(repositoryId, branchPath, author, String.format("Deleted Reference Set '%s' from store.", referenceSetId))
				.execute(bus)
				.getSync();
	}
	
	private URI getRefSetLocationURI(String branchPath, String refSetId) {
		return linkTo(SnomedReferenceSetRestService.class, branchPath).slash(refSetId).toUri();
	}
}
