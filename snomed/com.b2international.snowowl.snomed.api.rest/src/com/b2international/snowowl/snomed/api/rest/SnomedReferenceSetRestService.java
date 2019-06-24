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
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRefSetRestInput;
import com.b2international.snowowl.snomed.api.rest.request.BulkRestRequest;
import com.b2international.snowowl.snomed.api.rest.request.RefSetMemberRequestResolver;
import com.b2international.snowowl.snomed.api.rest.request.RefSetRequestResolver;
import com.b2international.snowowl.snomed.api.rest.request.RequestResolver;
import com.b2international.snowowl.snomed.api.rest.request.RestRequest;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 4.5
 */
@Api(value = "RefSets", description="RefSets", tags = { "refsets" })
@Controller
@RequestMapping(produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class SnomedReferenceSetRestService extends AbstractRestService {

	public SnomedReferenceSetRestService() {
		super(SnomedReferenceSet.Fields.ALL);
	}
	
	@ApiOperation(
			value="Retrieve Reference Sets from a branch", 
			notes="Returns a list with all reference sets from a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CollectionResource.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@GetMapping(value="/{path:**}/refsets")	
	public @ResponseBody DeferredResult<SnomedReferenceSets> search(
			@ApiParam(value="The branch path", required = true)
			@PathVariable(value="path")
			final String branchPath,
			
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
			
			@ApiParam(value="Sort keys")
			@RequestParam(value="sort", required=false)
			final List<String> sortKeys) {
		
		return DeferredResults.wrap(SnomedRequests.prepareSearchRefSet()
				.setScroll(scrollKeepAlive)
				.setScrollId(scrollId)
				.setSearchAfter(searchAfter)
				.setLimit(limit)
				.sortBy(extractSortFields(sortKeys))
				.build(repositoryId, branchPath)
				.execute(bus));
	}
	
	@ApiOperation(
			value="Retrieve Reference Set",
			notes="Returns all properties of the specified Reference set."
					+ "<p>The following properties can be expanded:"
					+ "<p>"
					+ "&bull; members(offset:0,limit:50,expand(referencedComponent(expand(pt(),...))) &ndash; the reference set members, and any applicable nested expansions<br>")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or Reference set not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/refsets/{id}", method=RequestMethod.GET)
	public @ResponseBody DeferredResult<SnomedReferenceSet> get(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Reference set identifier")
			@PathVariable(value="id")
			final String referenceSetId,
			
			@ApiParam(value="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,

			@ApiParam(value="Accepted language tags, in order of preference")
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
	
	@ApiOperation(
			value="Create a reference set",
			notes="Creates a new reference set directly on a branch. Creates the corresponding identifier concept as well based on the given JSON body."
			+ "<p>Reference Set type and referenced component type properties are immutable and cannot be modified, "
			+ "thus there is no update endpoint for reference sets. "
			+ "To update the corresponding identifier concept properties, use the concept update endpoint.</p>")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created", response = Void.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/refsets", method=RequestMethod.POST, consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="Reference set parameters")
			@RequestBody 
			final ChangeRequest<SnomedRefSetRestInput> body,

			final Principal principal) {
		
		final String userId = principal.getName();
		
		final SnomedRefSetRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId(); 
		
		final String createdRefSetId = change.toRequestBuilder() 
			.build(repositoryId, branchPath, userId, commitComment, defaultModuleId)
			.execute(bus)
			.getSync(COMMIT_TIMEOUT, TimeUnit.MILLISECONDS)
			.getResultAs(String.class);
		
		return Responses.created(getRefSetLocationURI(branchPath, createdRefSetId)).build();
	}
	
	@ApiOperation(
			value="Executes an action on a reference set",
			notes="Executes an action specified via the request body on the reference set identified by the given identifier."
					+ "<p>Supported actions are:"
					+ "&bull; 'sync' - Executes sync action on all members of this query reference set, see sync on /members/:id/actions endpoint"
					+ "</p>")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content"),
		@ApiResponse(code = 404, message = "Branch or reference set not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/refsets/{id}/actions", method=RequestMethod.POST, consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void executeAction(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The reference set identifier")
			@PathVariable(value="id")
			final String refSetId,
			
			@ApiParam(value="Reference set action")
			@RequestBody 
			final ChangeRequest<RestRequest> body,
			
			final Principal principal) {
		
		final String userId = principal.getName();
		final RequestResolver<TransactionContext> resolver = new RefSetRequestResolver();
		
		final RestRequest change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		change.setSource("referenceSetId", refSetId);
		
		SnomedRequests.prepareCommit()
			.setDefaultModuleId(defaultModuleId)
			.setBody(change.resolve(resolver))
			.setCommitComment(commitComment)
			.setUserId(userId)
			.build(repositoryId, branchPath)
			.execute(bus)
			.getSync();
	}
	
	@ApiOperation(
			value="Executes multiple requests (bulk request) on the members of a single reference set.",
			notes="Execute reference set member create, update, delete requests at once on a single reference set. "
					+ "See bulk requests section for more details.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content"),
		@ApiResponse(code = 404, message = "Branch or reference set not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/refsets/{id}/members", method=RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateMembers(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The reference set identifier")
			@PathVariable(value="id")
			final String refSetId,
			
			@ApiParam(value="The reference set member changes")
			@RequestBody
			final ChangeRequest<BulkRestRequest> request,
			
			final Principal principal) {
		
		final String userId = principal.getName();
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
			.setUserId(userId)
			.setCommitComment(commitComment)
			.build(repositoryId, branchPath)
			.execute(bus)
			.getSync();
	}
	
	@ApiOperation(
			value="Delete Reference Set",
			notes="Removes the reference set from the terminology store.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or Reference set not found", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/refsets/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The Reference set identifier")
			@PathVariable(value="id")
			final String referenceSetId,
			
			@ApiParam(value="Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,

			final Principal principal) {
		
		SnomedRequests.prepareDeleteReferenceSet(referenceSetId)
				.force(force)
				.build(repositoryId, branchPath, principal.getName(), String.format("Deleted Reference Set '%s' from store.", referenceSetId))
				.execute(bus)
				.getSync();
	}
	
	private URI getRefSetLocationURI(String branchPath, String refSetId) {
		return linkTo(SnomedReferenceSetRestService.class).slash(branchPath).slash("refsets").slash(refSetId).toUri();
	}
	
}
