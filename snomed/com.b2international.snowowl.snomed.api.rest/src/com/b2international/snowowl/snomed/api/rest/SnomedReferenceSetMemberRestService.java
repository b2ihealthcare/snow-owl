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

import org.springframework.beans.factory.annotation.Autowired;
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

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.rest.domain.ChangeRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedMemberRestUpdate;
import com.b2international.snowowl.snomed.api.rest.domain.SnomedRefSetMemberRestInput;
import com.b2international.snowowl.snomed.api.rest.request.RefSetMemberRequestResolver;
import com.b2international.snowowl.snomed.api.rest.request.RequestResolver;
import com.b2international.snowowl.snomed.api.rest.request.RestRequest;
import com.b2international.snowowl.snomed.api.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @since 4.5
 */
@Api("Reference Set Members")
@Controller
@RequestMapping(produces={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class SnomedReferenceSetMemberRestService extends AbstractSnomedRestService {

	@Autowired
	private IEventBus bus;
	
	@ApiOperation(
			value="Retrieve reference set members from a branch", 
			notes="Returns a list with all reference set members from a branch.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = PageableCollectionResource.class),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(value="/{path:**}/members", method=RequestMethod.GET)	
	public @ResponseBody DeferredResult<SnomedReferenceSetMembers> search(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The starting offset in the list")
			@RequestParam(value="offset", defaultValue="0", required=false) 
			final int offset,

			@ApiParam(value="The maximum number of items to return")
			@RequestParam(value="limit", defaultValue="50", required=false) 
			final int limit) {
		return DeferredResults.wrap(SnomedRequests.prepareMemberSearch().setLimit(limit).setOffset(offset).build(branchPath).execute(bus));
	}
	
	@ApiOperation(
			value="Retrieve a reference set member",
			notes="Returns all properties of the specified reference set member.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or reference set member not found")
	})
	@RequestMapping(value="/{path:**}/members/{id}", method=RequestMethod.GET)
	public @ResponseBody DeferredResult<SnomedReferenceSetMember> get(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value="The reference set member identifier")
			@PathVariable(value="id")
			final String memberId,
			
			@ApiParam(value="Expansion parameters")
			@RequestParam(value="expand", required=false)
			final List<String> expand) {
		return DeferredResults.wrap(SnomedRequests
				.prepareGetMember()
				.setComponentId(memberId)
				.setExpand(expand)
				.build(branchPath)
				.execute(bus));
	}
	
	@ApiOperation(
			value="Create a reference set member",
			notes="Creates a new reference set member directly on a branch. "
					+ "On top of the basic member properties you can include other properties relevant for specific reference set member types."
					+ "For example, for query type reference set member we support _query_ and _refsetDescription_. "
					+ "The _query_ parameter defines the ESCG query property of the new member, while the _refsetDescription_ used for the description of the new simple type reference set.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch not found")
	})
	@RequestMapping(value="/{path:**}/members", method=RequestMethod.POST)
	public ResponseEntity<Void> create(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="Reference set member parameters")
			@RequestBody 
			final ChangeRequest<SnomedRefSetMemberRestInput> body,

			final Principal principal) {
		
		final SnomedRefSetMemberRestInput change = body.getChange();
		final Request<TransactionContext, String> req = SnomedRequests
				.prepareNewMember()
				.setModuleId(change.getModuleId())
				.setReferencedComponentId(change.getReferencedComponentId())
				.setReferenceSetId(change.getReferenceSetId())
				.setProperties(change.getProperties())
				.build();
		
		final String createdRefSetMemberId = 
				SnomedRequests
					.prepareCommit(principal.getName(), branchPath)
					.setBody(req)
					.setCommitComment(body.getCommitComment())
					.build()
					.executeSync(bus, 120L * 1000L)
					.getResultAs(String.class);
		
		return Responses.created(getRefSetMemberLocationURI(branchPath, createdRefSetMemberId)).build();
	}
	
	@ApiOperation(
			value="Delete Reference Set Member",
			notes="Permanently removes the specified unreleased Reference Set Member.<p>If the member "
					+ "has already been released, it can not be removed and a <code>409</code> "
					+ "status will be returned.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Branch or member not found"),
		@ApiResponse(code = 409, message = "Member cannot be deleted", response = RestApiError.class)
	})
	@RequestMapping(value="/{path:**}/members/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The reference set member identifier")
			@PathVariable(value="id")
			final String memberId,
			
			final Principal principal) {
		
		SnomedRequests
			.prepareCommit(principal.getName(), branchPath)
			.setBody(SnomedRequests.prepareDeleteMember(memberId))
			.setCommitComment(String.format("Deleted reference set member '%s' from store.", memberId))
			.build()
			.executeSync(bus, 120L * 1000L);
	}
	
	@ApiOperation(
			value="Update Reference Set Member",
			notes="Updates properties of the specified Reference Set Member."
					+ "The following properties are allowed to change:"
					+ "- activity status flag (active)")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or member not found")
	})
	@RequestMapping(value="/{path:**}/members/{id}", method=RequestMethod.PUT, consumes={ AbstractRestService.SO_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The reference set member identifier")
			@PathVariable(value="id")
			final String memberId,
			
			@ApiParam(value="Updated Reference Set parameters")
			@RequestBody 
			final ChangeRequest<SnomedMemberRestUpdate> body,
			
			final Principal principal) {
		
		final String userId = principal.getName();
		final SnomedMemberRestUpdate update = body.getChange();
		
		SnomedRequests
			.prepareMemberUpdate()
			.setMemberId(memberId)
			.setSource(update.getSource())
			.commit(userId, branchPath, body.getCommitComment())
			.executeSync(bus, 120L * 1000L);
	}
	
	@ApiOperation(
			value="Executes an action",
			notes="TODO write documentation in repo's doc folder")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Action execution successful"),
		@ApiResponse(code = 204, message = "No content"),
		@ApiResponse(code = 404, message = "Branch or member not found")
	})
	@RequestMapping(value="/{path:**}/members/{id}/actions", method=RequestMethod.POST)
	public @ResponseBody Object executeAction(
			@ApiParam(value="The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value="The reference set member identifier")
			@PathVariable(value="id")
			final String memberId,
			
			@ApiParam(value="Reference set member action")
			@RequestBody 
			final ChangeRequest<RestRequest> body,
			
			final Principal principal) {
		final RequestResolver<TransactionContext> resolver = new RefSetMemberRequestResolver();
		final RestRequest change = body.getChange();
		change.setSource("memberId", memberId);
		return SnomedRequests
				.prepareCommit(principal.getName(), branchPath)
				.setBody(body.getChange().resolve(resolver))
				.setCommitComment(body.getCommitComment())
				.build()
				.executeSync(bus);
	}
	
	private URI getRefSetMemberLocationURI(String branchPath, String memberId) {
		return linkTo(SnomedReferenceSetMemberRestService.class).slash(branchPath).slash("members").slash(memberId).toUri();
	}
	
}
