/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
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
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRefSetCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 4.5
 */
@Api("Reference Sets")
@Controller
@RequestMapping(produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class SnomedReferenceSetRestService extends AbstractSnomedRestService {

	@ApiOperation(
			value="Retrieve Reference Sets from a branch", 
			notes="Returns a list with all reference sets from a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CollectionResource.class),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(value="/{path:**}/refsets", method=RequestMethod.GET)	
	public @ResponseBody DeferredResult<SnomedReferenceSets> search(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath) {
		return DeferredResults.wrap(SnomedRequests.prepareRefSetSearch(branchPath).execute(bus));
	}
	
	@ApiOperation(
			value="Retrieve Reference Set",
			notes="Returns all properties of the specified Reference set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or Reference set not found")
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
			final List<String> expand) {
		return DeferredResults.wrap(SnomedRequests
				.prepareGetReferenceSet()
				.setComponentId(referenceSetId)
				.setExpand(expand)
				.build(branchPath)
				.execute(bus));
	}
	
	@ApiOperation(
			value="Create a reference set",
			notes="Creates a new reference set directly on a branch. Creates the corresponding identifier concept.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(value="/{path:**}/refsets", method=RequestMethod.POST)
	public ResponseEntity<Void> create(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="Reference set parameters")
			@RequestBody 
			final ChangeRequest<SnomedRefSetRestInput> body,

			final Principal principal) {
		
		final SnomedRefSetRestInput change = body.getChange();
		
		final SnomedRefSetCreateRequestBuilder req = SnomedRequests
			.prepareNewRefSet()
			.setIdentifierConcept(change.toComponentInput())
			.setType(change.getType())
			.setReferencedComponentType(change.getReferencedComponentType());
		
		final String createdRefSetId = 
				SnomedRequests
					.prepareCommit(principal.getName(), branchPath)
					.setBody(req)
					.setCommitComment(body.getCommitComment())
					.build()
					.executeSync(bus, 120L * 1000L)
					.getResultAs(String.class);
		
		return Responses.created(getRefSetLocationURI(branchPath, createdRefSetId)).build();
	}
	
	@ApiOperation(
			value="Executes an action on a reference set",
			notes="TODO write documentation in repo's doc folder")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Action execution successful"),
		@ApiResponse(code = 204, message = "No content"),
		@ApiResponse(code = 404, message = "Branch or reference set not found")
	})
	@RequestMapping(value="/{path:**}/refsets/{id}/actions", method=RequestMethod.POST)
	public @ResponseBody Object executeAction(
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
		final RequestResolver<TransactionContext> resolver = new RefSetRequestResolver();
		
		final RestRequest change = body.getChange();
		change.setSource("referenceSetId", refSetId);
		
		return SnomedRequests
				.prepareCommit(principal.getName(), branchPath)
				.setBody(body.getChange().resolve(resolver))
				.setCommitComment(body.getCommitComment())
				.build()
				.executeSync(bus);
	}
	
	@ApiOperation(
			value="Executes multiple requests on the members of a single reference set",
			notes="TODO write documentation in repo's doc folder")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content"),
		@ApiResponse(code = 404, message = "Branch or reference set not found")
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
		
		final RequestResolver<TransactionContext> resolver = new RefSetMemberRequestResolver();
		SnomedRequests
			.prepareCommit(principal.getName(), branchPath)
			.setBody(request.getChange().resolve(resolver))
			.setCommitComment(request.getCommitComment())
			.build()
			.executeSync(bus);
		
	}
	
	private URI getRefSetLocationURI(String branchPath, String refSetId) {
		return linkTo(SnomedReferenceSetRestService.class).slash(branchPath).slash("refsets").slash(refSetId).toUri();
	}
	
}
