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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedMemberRestUpdate;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRefSetMemberRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedReferenceSetMemberRestSearch;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedResourceRequest;
import com.b2international.snowowl.snomed.core.rest.request.RefSetMemberRequestResolver;
import com.b2international.snowowl.snomed.core.rest.request.RequestResolver;
import com.b2international.snowowl.snomed.core.rest.request.RestRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 4.5
 */
@Api(value = "members", description="Members", tags = "members")
@Controller
@RequestMapping(value="/{path:**}/members")
public class SnomedReferenceSetMemberRestService extends AbstractSnomedRestService {
	
	public SnomedReferenceSetMemberRestService() {
		super(SnomedReferenceSetMember.Fields.ALL);
	}
	
	@ApiOperation(
		value="Retrieve reference set members from a branch", 
		notes="Returns a list with all reference set members from a branch."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; referencedComponent(expand(pt(),...)) &ndash; the referenced component, and any applicable nested expansions<br>")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = SnomedReferenceSetMembers.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })	
	public @ResponseBody Promise<SnomedReferenceSetMembers> searchByGet(
			@ApiParam(value = "The branch path", required = true)
			@PathVariable(value="path")
			final String branchPath,

			final SnomedReferenceSetMemberRestSearch params,
			
			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {

		final List<ExtendedLocale> extendedLocales = getExtendedLocales(acceptLanguage);
		
		final SnomedRefSetMemberSearchRequestBuilder req = SnomedRequests.prepareSearchMember()
				.setLimit(params.getLimit())
				.setSearchAfter(params.getSearchAfter())
				.filterByIds(params.getId())
				.filterByActive(params.getActive())
				.filterByModule(params.getModule())
				.filterByEffectiveTime(params.getEffectiveTime())
				.filterByRefSet(params.getReferenceSet())
				.filterByReferencedComponent(params.getReferencedComponentId())
				.setExpand(params.getExpand())
				.setLocales(extendedLocales)
				.sortBy(extractSortFields(params.getSort()));
		
		Options propFilters = params.toPropsFilter();
		if (!propFilters.isEmpty()) {
			req.filterByProps(propFilters);
		}
		
		return req.build(repositoryId, branchPath).execute(getBus());
	}
	
	@ApiOperation(
		value="Retrieve reference set members from a branch", 
		notes="Returns a list with all reference set members from a branch."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; referencedComponent(expand(pt(),...)) &ndash; the referenced component, and any applicable nested expansions<br>"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = SnomedReferenceSetMembers.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping("/search")
	public @ResponseBody Promise<SnomedReferenceSetMembers> searchByPost(
			@ApiParam(value = "The branch path", required = true)
			@PathVariable(value="path")
			final String branch,

			@RequestBody(required = false)
			final SnomedReferenceSetMemberRestSearch params,
			
			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		return searchByGet(branch, params, acceptLanguage);
	}
	
	@ApiOperation(
		value="Retrieve a reference set member",
		notes="Returns all properties of the specified reference set member."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; referencedComponent(expand(pt(),...)) &ndash; the referenced component, and any applicable nested expansions<br>"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch or reference set member not found", response = RestApiError.class)
	})
	@GetMapping(value = "/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<SnomedReferenceSetMember> get(
			@ApiParam(value = "The branch path")
			@PathVariable(value="path")
			final String branchPath,

			@ApiParam(value = "The reference set member identifier")
			@PathVariable(value="id")
			final String memberId,
			
			@ApiParam(value = "Expansion parameters")
			@RequestParam(value="expand", required=false)
			final String expand,

			@ApiParam(value = "Accepted language tags, in order of preference")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		return SnomedRequests
				.prepareGetMember(memberId)
				.setExpand(expand)
				.setLocales(getExtendedLocales(acceptLanguage))
				.build(repositoryId, branchPath)
				.execute(getBus());
	}
	
	@ApiOperation(
		value="Create a reference set member",
		notes="Creates a new reference set member directly on a branch. "
				+ "On top of the basic member properties you can include other properties relevant for specific reference set member types."
				+ "For example: query type reference set members support _query_ and _refsetDescription_ properties. "
				+ "_Query_ defines the ESCG query of the new member, while the _refsetDescription_ will be used as the description of the new target simple type reference set."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = Void.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	public ResponseEntity<Void> create(
			@ApiParam(value = "The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value = "Reference set member parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedRefSetMemberRestInput> body,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final SnomedRefSetMemberRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		final String id = change.getId();
		// Check for correct UUID format in id input
		UUID.fromString(id);
		
		final String createdRefSetMemberId = change.toRequestBuilder()
				.build(repositoryId, branchPath, author, commitComment, defaultModuleId)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		return ResponseEntity.created(getResourceLocationURI(branchPath, createdRefSetMemberId)).build();
	}
	
	@ApiOperation(
		value="Delete Reference Set Member",
		notes="Permanently removes the specified unreleased Reference Set Member.<p>If the member "
				+ "has already been released, it can not be removed and a <code>409</code> "
				+ "status will be returned."
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Delete successful"),
		@ApiResponse(code = 404, message = "Branch or member not found", response = RestApiError.class),
		@ApiResponse(code = 409, message = "Member cannot be deleted", response = RestApiError.class)
	})
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@ApiParam(value = "The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value = "The reference set member identifier")
			@PathVariable(value="id")
			final String memberId,
			
			@ApiParam(value = "Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		SnomedRequests.prepareDeleteMember(memberId)
			.force(force)
			.build(repositoryId, branchPath, author, String.format("Deleted reference set member '%s' from store.", memberId))
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	@ApiOperation(
		value="Update Reference Set Member",
		notes="Updates properties of the specified Reference Set Member."
				+ "The following properties are allowed to change (other properties will be simply ignored):"
				+ "- activity status flag (active)"
				+ "- module Concept (moduleId)"
				+ "- query field of query type reference set members"
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Update successful"),
		@ApiResponse(code = 404, message = "Branch or member not found", response = RestApiError.class)
	})
	@PutMapping(value = "/{id}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@ApiParam(value = "The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value = "The reference set member identifier")
			@PathVariable(value="id")
			final String memberId,
			
			@ApiParam(value = "Updated Reference Set parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedMemberRestUpdate> body,
			
			@ApiParam(value = "Force update flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final SnomedMemberRestUpdate update = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		SnomedRequests.prepareUpdateMember()
			.setMemberId(memberId)
			.setSource(update.getSource())
			.force(force)
			.build(repositoryId, branchPath, author, commitComment, defaultModuleId)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	@ApiOperation(
		value="Executes an action",
		notes="Executes an action specified via the request body on a reference set member."
				+ "<p>Supported actions are:"
				+ "&bull; 'sync' - Executes sync action on a query type member"
				+ "&bull; 'create|update|delete' - allowed (mainly resolved and used in bulk requests), but in case of single member action use the dedicated endpoints instead"
				+ "</p>"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "Action execution successful"),
		@ApiResponse(code = 204, message = "No content"),
		@ApiResponse(code = 404, message = "Branch or member not found", response = RestApiError.class)
	})
	@PostMapping(value = "/{id}/actions", 
		consumes = { AbstractRestService.JSON_MEDIA_TYPE }, 
		produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Object executeAction(
			@ApiParam(value = "The branch path")
			@PathVariable(value="path")
			final String branchPath,
			
			@ApiParam(value = "The reference set member identifier")
			@PathVariable(value="id")
			final String memberId,
			
			@ApiParam(value = "Reference set member action")
			@RequestBody 
			final SnomedResourceRequest<RestRequest> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final RequestResolver<TransactionContext> resolver = new RefSetMemberRequestResolver();
		
		final RestRequest change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		change.setSource("memberId", memberId);
		
		return SnomedRequests.prepareCommit()
				.setDefaultModuleId(defaultModuleId)
				.setAuthor(author)
				.setBody(change.resolve(resolver))
				.setCommitComment(commitComment)
				.build(repositoryId, branchPath)
				.execute(getBus())
				.getSync();
	}
	
}
