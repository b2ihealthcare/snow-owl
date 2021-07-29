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

import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.domain.ResourceSelectors;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedRefSetRestInput;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedReferenceSetRestSearch;
import com.b2international.snowowl.snomed.core.rest.domain.SnomedResourceRequest;
import com.b2international.snowowl.snomed.core.rest.request.*;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Sets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 4.5
 */
@Tag(description="RefSets", name = "refSets")
@Controller
@RequestMapping(value = "/{path:**}/refsets")		
public class SnomedReferenceSetRestService extends AbstractRestService {

	public SnomedReferenceSetRestService() {
		super(SnomedReferenceSet.Fields.ALL);
	}
	
	@Operation(
		summary="Retrieve Reference Sets from a path", 
		description="Returns a list with all/filtered Reference Sets from a path."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; members() &ndash; members currently available in the reference set<br>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config"),
		@ApiResponse(responseCode = "404", description = "Branch not found")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })	
	public @ResponseBody Promise<SnomedReferenceSets> searchByGet(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,
			
			@ParameterObject
			final SnomedReferenceSetRestSearch params,
			
			@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		List<Sort> sorts = extractSortFields(params.getSort());
		
		return SnomedRequests.prepareSearchRefSet()
				.filterByIds(params.getId())
				.filterByTypes(getRefSetTypes(params.getRefSetTypes()))
				.setLimit(params.getLimit())
				.setExpand(params.getExpand())
				.setFields(params.getField())
				.setSearchAfter(params.getSearchAfter())
				.setLocales(acceptLanguage)
				.sortBy(sorts)
				.build(path)
				.execute(getBus());
	}
	
	@Operation(
		summary="Retrieve Reference Sets from a path", 
		description="Returns a list with all/filtered Reference Sets from a path."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; members() &ndash; members currently available in the reference set<br>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config"),
		@ApiResponse(responseCode = "404", description = "Branch not found")
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<SnomedReferenceSets> searchByPost(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,

			@RequestBody(required = false)
			final SnomedReferenceSetRestSearch body,
			
			@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		return searchByGet(path, body, acceptLanguage);
	}
	
	private Collection<SnomedRefSetType> getRefSetTypes(String[] refSetTypes) {
		if (refSetTypes == null) {
			return null;
		}
		
		final Set<String> unresolvedRefSetTypes = Sets.newTreeSet();
		final Set<SnomedRefSetType> resolvedRefSetTypes = newHashSetWithExpectedSize(refSetTypes.length);
		
		for (String refSetTypeString : refSetTypes) {
			final SnomedRefSetType refSetType = SnomedRefSetType.get(refSetTypeString.toUpperCase());
			if (refSetType != null) {
				resolvedRefSetTypes.add(refSetType);
			} else {
				unresolvedRefSetTypes.add(refSetTypeString);
			}
		}
		
		if (!unresolvedRefSetTypes.isEmpty()) {
			throw new BadRequestException("Unknown reference set types: '%s'", unresolvedRefSetTypes)
				.withDeveloperMessage("Available reference set types are: " + SnomedRefSetType.VALUES);
		}
		
		return resolvedRefSetTypes;
	}

	@Operation(
		summary="Retrieve Reference Set",
		description="Returns all properties of the specified Reference set."
				+ "<p>The following properties can be expanded:"
				+ "<p>"
				+ "&bull; members(offset:0,limit:50,expand(referencedComponent(expand(pt(),...))) &ndash; the reference set members, and any applicable nested expansions<br>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Branch or Reference set not found")
	})
	@GetMapping(value = "/{id}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<SnomedReferenceSet> get(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,

			@Parameter(description = "The Reference set identifier")
			@PathVariable(value="id")
			final String refsetId,
			
			@ParameterObject
			final ResourceSelectors selectors,

			@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
			@RequestHeader(value="Accept-Language", defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {

		return SnomedRequests
				.prepareGetReferenceSet(refsetId)
				.setExpand(selectors.getExpand())
				.setFields(selectors.getField())
				.setLocales(acceptLanguage)
				.build(path)
				.execute(getBus());
	}
	
	@Operation(
		summary="Create a reference set",
		description="Creates a new reference set directly on a path. Creates the corresponding identifier concept as well based on the given JSON body."
			+ "<p>Reference Set type and referenced component type properties are immutable and cannot be modified, "
			+ "thus there is no update endpoint for reference sets. "
			+ "To update the corresponding identifier concept properties, use the concept update endpoint.</p>")
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
			
			@Parameter(description = "Reference set parameters")
			@RequestBody 
			final SnomedResourceRequest<SnomedRefSetRestInput> body,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final SnomedRefSetRestInput change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId(); 
		
		final String createdRefSetId = change.toRequestBuilder()
				.commit()
				.setDefaultModuleId(defaultModuleId)
				.setAuthor(author)
				.setCommitComment(commitComment)
				.build(path)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		return ResponseEntity.created(getResourceLocationURI(path, createdRefSetId)).build();
	}
	
	@Operation(
		summary="Executes an action on a reference set",
		description="Executes an action specified via the request body on the reference set identified by the given identifier."
				+ "<p>Supported actions are:"
				+ "&bull; 'sync' - Executes sync action on all members of this query reference set, see sync on /members/:id/actions endpoint"
				+ "</p>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content"),
		@ApiResponse(responseCode = "404", description = "Branch or reference set not found")
	})
	@PostMapping(value = "/{id}/actions", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void executeAction(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,
			
			@Parameter(description = "The reference set identifier")
			@PathVariable(value="id")
			final String refsetId,
			
			@Parameter(description = "Reference set action")
			@RequestBody 
			final SnomedResourceRequest<RestRequest> body,
			
			@RequestHeader(value = X_AUTHOR)
			final String author) {
		
		final RequestResolver<TransactionContext> resolver = new RefSetRequestResolver();
		
		final RestRequest change = body.getChange();
		final String commitComment = body.getCommitComment();
		final String defaultModuleId = body.getDefaultModuleId();
		
		change.setSource(SnomedRf2Headers.FIELD_REFSET_ID, refsetId);
		
		SnomedRequests.prepareCommit()
			.setDefaultModuleId(defaultModuleId)
			.setBody(change.resolve(resolver))
			.setCommitComment(commitComment)
			.setAuthor(author)
			.build(path)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	@Operation(
		summary="Executes multiple requests (bulk request) on the members of a single reference set.",
		description="Execute reference set member create, update, delete requests at once on a single reference set. "
				+ "See bulk requests section for more details."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content"),
		@ApiResponse(responseCode = "404", description = "Branch or reference set not found")
	})
	@PutMapping(value="/{id}/members", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateMembers(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,
			
			@Parameter(description = "The reference set identifier")
			@PathVariable(value="id")
			final String refsetId,
			
			@Parameter(description = "The reference set member changes")
			@RequestBody
			final SnomedResourceRequest<BulkRestRequest> request,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		final RequestResolver<TransactionContext> resolver = new RefSetMemberRequestResolver();
		
		final BulkRestRequest bulkRequest = request.getChange();
		final String commitComment = request.getCommitComment();
		final String defaultModuleId = request.getDefaultModuleId();
		
		// FIXME setting refsetId even if defined??? 
		// enforces that new members will be created in the defined refset
		for (RestRequest req : bulkRequest.getRequests()) {
			req.setSource(SnomedRf2Headers.FIELD_REFSET_ID, refsetId);
		}
		
		final BulkRequestBuilder<TransactionContext> updateRequestBuilder = BulkRequest.create();
		bulkRequest.resolve(resolver).forEach(updateRequestBuilder::add);
		
		SnomedRequests.prepareCommit()
			.setDefaultModuleId(defaultModuleId)
			.setBody(updateRequestBuilder.build())
			.setAuthor(author)
			.setCommitComment(commitComment)
			.build(path)
			.execute(getBus())
			.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	@Operation(
		summary="Delete Reference Set",
		description="Removes the reference set from the terminology store."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Branch or Reference set not found")
	})
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(value="path")
			final String path,

			@Parameter(description = "The Reference set identifier")
			@PathVariable(value="id")
			final String refsetId,
			
			@Parameter(description = "Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,

			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		SnomedRequests.prepareDeleteReferenceSet(refsetId, force)
				.commit()
				.setAuthor(author)
				.setCommitComment(String.format("Deleted Reference Set '%s' from store.", refsetId))
				.build(path)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
}
