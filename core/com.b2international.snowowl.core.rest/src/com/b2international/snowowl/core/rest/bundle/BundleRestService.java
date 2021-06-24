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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.bundle.Bundles;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;

import io.swagger.annotations.*;

/**
 * @since 8.0
 */
@Api(value = "Resources", tags = { "resources" })
@RestController
@RequestMapping("/bundles")
public class BundleRestService extends AbstractRestService {
	
	public BundleRestService() {
		super(ResourceDocument.Fields.SORT_FIELDS);
	}
	
	@ApiOperation(
		value="Retrieve bundles", 
		notes="Returns a collection resource containing all/filtered registered bundles."
			+ "<p>Results are by default sorted by ID."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; resources() &ndash; this list of resources this bundle contains"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CodeSystems.class),
		@ApiResponse(code = 400, message = "Bad Request", response = RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundles> searchByGet(final BundleRestSearch params) {
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
	
	@ApiOperation(
		value="Retrieve bundles", 
		notes="Returns a collection resource containing all/filtered registered bundles."
			+ "<p>Results are by default sorted by ID."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; resources() &ndash; this list of resources this bundle contains"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CodeSystems.class),
		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundles> searchByPost(final BundleRestSearch params) {
		return searchByGet(params);
	}
	
	@ApiOperation(
			value="Retrieve budnle by its unique identifier",
			notes="Returns generic information about a single bundle associated to the given unique identifier.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Not found", response = RestApiError.class)
	})
	@GetMapping(value = "/{bundleId}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<Bundle> get(
			@ApiParam(value="The bundle identifier")
			@PathVariable(value="bundleId") 
			final String bundleId,
			@RequestParam(value = "expand")
			@ApiParam(value="expand", required = false) 
			String expand) {
		return ResourceRequests.bundles().prepareGet(bundleId)
				.setExpand(expand)
				.buildAsync()
				.execute(getBus());
	}
	
	@ApiOperation(
		value="Create a bundle",
		notes="Create a new bundle with the given parameters"
	)
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created", response = Void.class),
		@ApiResponse(code = 400, message = "Invalid input arguments", response = RestApiError.class),
		@ApiResponse(code = 409, message = "Bundle already exists in the system", response = RestApiError.class)
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
	
	@ApiOperation(
			value="Update a bundle",
			notes="Update a bundle with the given parameters")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content", response = Void.class),
		@ApiResponse(code = 400, message = "Bad Request", response = RestApiError.class)
	})
	@PutMapping(value = "/{bundleId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@ApiParam(value="The bundle identifier")
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
	
	@ApiOperation(
			value="Delete a bundle",
			notes="Delete a bundle with the given parameters")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Deletion successful", response = Void.class),
		@ApiResponse(code = 409, message = "Bundle cannot be deleted", response = RestApiError.class)
	})
	@DeleteMapping(value = "/{bundleId}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@ApiParam(value="The bundle identifier")
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
