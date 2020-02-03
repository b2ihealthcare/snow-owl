/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.branch;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Provides REST endpoints for computing Reviewerences between branches.
 * 
 * @since 4.2
 */
@Api(value = "Branches", description = "Branches", tags = "branches")
@RequestMapping(value="/reviews", produces={AbstractRestService.JSON_MEDIA_TYPE})
public abstract class RepositoryBranchReviewRestService extends AbstractRestService {

	private final String repositoryId;

	public RepositoryBranchReviewRestService(String repositoryId) {
		super(Review.Fields.ALL);
		this.repositoryId = repositoryId;
	}
	
	@ApiOperation(
		value = "Create new review", 
		notes = "Creates a new terminology review for the SNOMED CT repository."
	)
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 400, message = "Bad Request", response=RestApiError.class)
	})
	@RequestMapping(method=RequestMethod.POST, consumes={AbstractRestService.JSON_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public Promise<ResponseEntity<Void>> createReview(@RequestBody final CreateReviewRequest request) {
		ApiValidation.checkInput(request);
		final UriComponentsBuilder linkTo = MvcUriComponentsBuilder.fromController(RepositoryBranchReviewRestService.class);
		return RepositoryRequests
			.reviews()
			.prepareCreate()
			.setSource(request.getSource())
			.setTarget(request.getTarget())
			.build(repositoryId)
			.execute(getBus())
			.then(review -> {
				return ResponseEntity.created(linkTo.pathSegment(review.id()).build().toUri()).build();
			});
	}

	@ApiOperation(
		value = "Retrieve single review", 
		notes = "Retrieves an existing terminology review with the specified identifier, if it exists."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Review not found", response=RestApiError.class),
	})
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public Promise<Review> getReview(@PathVariable("id") final String reviewId) {
		return RepositoryRequests
			.reviews()
			.prepareGet(reviewId)
			.build(repositoryId)
			.execute(getBus());
	}

	@ApiOperation(
		value = "Retrieve change set for review", 
		notes = "Retrieves the set of created, changed and detached concepts for an existing review with the specified identifier, if it exists."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Review not found or changes are not yet available", response=RestApiError.class),
	})
	@RequestMapping(value="/{id}/concept-changes", method=RequestMethod.GET)
	public Promise<ConceptChanges> getConceptChanges(@PathVariable("id") final String reviewId) {
		return RepositoryRequests
					.reviews()
					.prepareGetConceptChanges(reviewId)
					.build(repositoryId)
					.execute(getBus());
	}

	@ApiOperation(
		value = "Delete single review", 
		notes = "Deletes a review run along with its computed change set, if any of them exist."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Review not found", response=RestApiError.class),
	})
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Promise<ResponseEntity<Void>> deleteReview(@PathVariable("id") final String reviewId) {
		return RepositoryRequests
					.reviews()
					.prepareDelete(reviewId)
					.build(repositoryId)
					.execute(getBus())
					.then(success -> ResponseEntity.noContent().build());
	}

}
