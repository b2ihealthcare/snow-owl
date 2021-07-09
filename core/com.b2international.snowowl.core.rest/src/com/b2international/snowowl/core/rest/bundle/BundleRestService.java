/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.bundle;

import java.util.concurrent.TimeUnit;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.bundle.Bundles;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.0
 */
@Tag(description = "Resources", name = "resources")
@RestController
@RequestMapping("/bundles")
public class BundleRestService extends AbstractRestService {
	
	public BundleRestService() {
		super(ResourceDocument.Fields.SORT_FIELDS);
	}
	
	@Operation(
		summary="Retrieve bundles", 
		description="Returns a collection resource containing all/filtered registered bundles."
			+ "<p>Results are by default sorted by ID."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; resources() &ndash; this list of resources this bundle contains"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request")
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundles> searchByGet(@ParameterObject final BundleRestSearch params) {
		return ResourceRequests.bundles().prepareSearch()
				.filterByIds(params.getId())
				.filterByTitle(params.getTitle())
				.setLimit(params.getLimit())
				.setExpand(params.getExpand())
				.setSearchAfter(params.getSearchAfter())
				.sortBy(extractSortFields(params.getSort()))
				.buildAsync()
				.execute(getBus());
	}
	
	@Operation(
		summary="Retrieve bundles", 
		description="Returns a collection resource containing all/filtered registered bundles."
			+ "<p>Results are by default sorted by ID."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; resources() &ndash; this list of resources this bundle contains"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Invalid search config"),
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundles> searchByPost(final BundleRestSearch params) {
		return searchByGet(params);
	}
	
	@Operation(
		summary="Retrieve bundle by its unique identifier",
		description="Returns generic information about a single bundle associated to the given unique identifier."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Not found")
	})
	@GetMapping(value = "/{bundleId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundle> get(
			@Parameter(description="The bundle identifier")
			@PathVariable(value="bundleId", required = true) 
			final String bundleId,
			
			@Parameter(description="expand") 
			@RequestParam(value = "expand", required = false)
			String expand) {
		return ResourceRequests.bundles().prepareGet(bundleId)
				.setExpand(expand)
				.buildAsync()
				.execute(getBus());
	}
	
	@Operation(
		summary="Create a bundle",
		description="Create a new bundle with the given parameters"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "400", description = "Invalid input arguments"),
		@ApiResponse(responseCode = "409", description = "Bundle already exists in the system")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@RequestBody
			final BundleCreateRestInput params,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		final String commitComment = String.format("Created new bundle %s", params.getTitle());
		final String codeSystemId = ResourceRequests.bundles().prepareCreate()
				.setId(params.getId())
				.setBundleId(params.getBundleId())
				.setUrl(params.getUrl())
				.setTitle(params.getTitle())
				.setLanguage(params.getLanguage())
				.setDescription(params.getDescription())
				.setStatus(params.getStatus())
				.setCopyright(params.getCopyright())
				.setOwner(params.getOwner())
				.setContact(params.getContact())
				.setUsage(params.getUsage())
				.setPurpose(params.getPurpose())
				.commit() 
				.setAuthor(author)
				.setCommitComment(commitComment)
				.buildAsync()
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		return ResponseEntity.created(getResourceLocationURI(codeSystemId)).build();
	}
	
	@Operation(
			summary = "Update a bundle",
			description="Update a bundle with the given parameters")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "No content"),
		@ApiResponse(responseCode = "400", description = "Bad Request")
	})
	@PutMapping(value = "/{bundleId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@Parameter(description = "The bundle identifier")
			@PathVariable(value="bundleId") 
			final String bundleId,
			
			@RequestBody
			final BundleUpdateRestinput params,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		final String commitComment = String.format("Update bundle %s", bundleId);
		ResourceRequests.bundles().prepareUpdate(bundleId)
				.setUrl(params.getUrl())
				.setTitle(params.getTitle())
				.setLanguage(params.getLanguage())
				.setDescription(params.getDescription())
				.setStatus(params.getStatus())
				.setCopyright(params.getCopyright())
				.setOwner(params.getOwner())
				.setContact(params.getContact())
				.setUsage(params.getUsage())
				.setPurpose(params.getPurpose())
				.setBundleId(params.getBundleId())
				.commit()
				.setAuthor(author)
				.setCommitComment(commitComment)
				.buildAsync()
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	@Operation(
			summary = "Delete a bundle",
			description = "Delete a bundle with the given parameters")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Deletion successful"),
		@ApiResponse(responseCode = "409", description = "Bundle cannot be deleted")
	})
	@DeleteMapping(value = "/{bundleId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@Parameter(description = "The bundle identifier")
			@PathVariable(value="bundleId") 
			final String bundleId,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		
		ResourceRequests.bundles().prepareDelete(bundleId)
				.commit()
				.setAuthor(author)
				.setCommitComment(String.format("Delete bundle %s", bundleId))
				.buildAsync()
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
}
