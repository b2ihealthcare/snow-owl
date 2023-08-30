/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.resource.collection;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.common.Strings;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.collection.TerminologyResourceCollection;
import com.b2international.snowowl.core.collection.TerminologyResourceCollectionRequests;
import com.b2international.snowowl.core.collection.TerminologyResourceCollections;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.domain.ResourceRequest;
import com.b2international.snowowl.core.rest.domain.ResourceSelectors;
import com.b2international.snowowl.core.rest.resource.ResourceUpdateRestInput;
import com.b2international.snowowl.core.rest.resource.TerminologyResourceRestSearch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 9.0
 */
@Tag(description = "Collections", name = "collections")
@RestController
@RequestMapping(value = "/collections")
public class TerminologyResourceCollectionRestService extends AbstractRestService {

	public TerminologyResourceCollectionRestService() {
		super(TerminologyResourceCollection.Fields.ALL);
	}
	
	@Operation(
		summary = "Retrieve Terminology Resource Collections", 
		description = "Returns a collection resource containing all/filtered registered Terminology Collection Resources."
			+ "<p>Default sort order of results are by ID.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<TerminologyResourceCollections> searchByGet(@ParameterObject final TerminologyResourceRestSearch params) {
		return TerminologyResourceCollectionRequests.prepareSearch()
			.filterByIds(params.getId())
			.filterByTitleExact(params.getTitleExact())
			.filterByTitle(params.getTitle())
			.filterByOids(params.getOid())
			.filterByToolingIds(params.getToolingId())
			.filterByBundleIds(params.getBundleId())
			.filterByBundleAncestorIds(params.getBundleAncestorId())
			.filterByStatus(params.getStatus())
			.filterByUrls(params.getUrl())
			.filterByOwner(params.getOwner())
			.filterBySettings(params.getSettings())
			.filterByDependency(params.getDependency())
			.setLimit(params.getLimit())
			.setExpand(params.getExpand())
			.setFields(params.getField())
			.setSearchAfter(params.getSearchAfter())
			.sortBy(extractSortFields(params.getSort()))
			.buildAsync(params.getTimestamp())
			.execute(getBus());
	}
	
	@Operation(
		summary = "Retrieve Terminology Resource Collections", 
		description = "Returns a collection resource containing all/filtered registered Terminology Collection Resources."
			+ "<p>Default sort order of results are by ID.")
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request") 
	})
	@PostMapping(value = "/search", consumes = { AbstractRestService.JSON_MEDIA_TYPE }, produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<TerminologyResourceCollections> searchByPost(@RequestBody(required = false) final TerminologyResourceRestSearch params) {
		return searchByGet(params);
	}
	
	@Operation(
		summary = "Retrieve a terminology collection resource by its unique identifier", 
		description = "Returns generic information about a single terminology collection resource identified by the given unique identifier."
	)
	@ApiResponses({ 
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"), 
		@ApiResponse(responseCode = "404", description = "Not Found") 
	})
	@GetMapping(value = "/{collectionId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<TerminologyResourceCollection> get(
		@Parameter(description = "The collection resource identifier") 
		@PathVariable(value = "collectionId") 
		final String collectionId,
		
		@Parameter(description = "The timestamp to use for historical ('as of') queries")
		final Long timestamp,
		
		@ParameterObject
		final ResourceSelectors selectors) {
		
		return TerminologyResourceCollectionRequests
			.prepareGet(collectionId)
			.setExpand(selectors.getExpand())
			.setFields(selectors.getField())
			.buildAsync(timestamp)
			.execute(getBus());
	}
	
	@Operation(
		summary="Retrieve a versioned terminology resource collection by its unique identifier",
		description="Returns generic information about a single terminology collection identified by the given unique identifier."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@GetMapping(value = "/{collectionId}/{versionId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<TerminologyResourceCollection> getVersioned(
		@Parameter(description="The resource collection identifier")
		@PathVariable(value="collectionId", required = true) 
		final String collectionId,
		
		@Parameter(description="The resource collection version")
		@PathVariable(value="versionId", required = true) 
		final String versionId,
		
		@ParameterObject
		final ResourceSelectors selectors) {
		
		return TerminologyResourceCollectionRequests.prepareGet(TerminologyResourceCollection.uri(collectionId, versionId))
			.setExpand(selectors.getExpand())
			.setFields(selectors.getField())
			.buildAsync()
			.execute(getBus());
	}
	
	@Operation(
		summary="Create a terminology resource collection",
		description="Create a new Terminology Resource Collection with the given parameters"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "409", description = "Resource already exists in the system")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
		@RequestBody
		final ResourceRequest<TerminologyResourceCollectionRestCreate> body,
		
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author) {

		
		final String commitComment = Strings.isNullOrEmpty(body.getCommitComment()) ? String.format("Created new Resource Collection %s", body.getChange().getId()) : body.getCommitComment();
		final String collectionId = body.getChange().toCreateRequest()
				.build(author, commitComment)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		return ResponseEntity.created(getResourceLocationURI(collectionId)).build();
	}
	
	@Operation(
		summary = "Update a terminology collection resource by its unique identifier", 
		description = "Updates a terminology resource collection definition in the system using its identifier and the given patch update."
	)
	@ApiResponses({ 
		@ApiResponse(responseCode = "204", description = "No Content"),
		@ApiResponse(responseCode = "400", description = "Bad Request"), 
	})
	@PutMapping(value = "/{collectionId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@Parameter(description = "The collection resource identifier") 
			@PathVariable(value = "collectionId") 
			final String collectionId,
			
			@RequestBody
			final ResourceRequest<ResourceUpdateRestInput> body,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		final String commitComment = Strings.isNullOrEmpty(body.getCommitComment()) ? String.format("Updated Terminology Collection Resource %s", collectionId) : body.getCommitComment();
		body.getChange().toUpdateRequest(collectionId)
				.build(author, commitComment)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	@Operation(
			summary="Delete a Terminology Collection Resource",
			description="Deletes a Terminology Collection Resource permanently from the server")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "409", description = "Resource cannot be deleted")
	})
	@DeleteMapping(value = "/{collectionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@Parameter(description = "The collection resource identifier")
			@PathVariable(value="collectionId") 
			final String collectionId,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		try {
			final TerminologyResourceCollection resource = TerminologyResourceCollectionRequests.prepareGet(collectionId)
					.buildAsync()
					.execute(getBus())
					.getSync(1, TimeUnit.MINUTES);
			
			TerminologyResourceCollectionRequests.prepareDelete(collectionId)
				.build(author, String.format("Deleted resource %s", resource.getTitle()))
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
		} catch(NotFoundException e) {
			// already deleted, ignore error
		}
	}
	
}
