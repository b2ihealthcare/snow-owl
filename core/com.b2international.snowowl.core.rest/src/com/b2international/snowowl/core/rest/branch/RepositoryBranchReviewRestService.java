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
import com.b2international.snowowl.core.branch.review.ConceptChanges;
import com.b2international.snowowl.core.branch.review.Review;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Provides REST endpoints for computing Reviewerences between branches.
 * 
 * @since 4.2
 */
@Tag(description = "Branches", name = "branches")
@RequestMapping(value="/reviews", produces={AbstractRestService.JSON_MEDIA_TYPE})
public abstract class RepositoryBranchReviewRestService extends AbstractRestService {

	private final String repositoryId;

	public RepositoryBranchReviewRestService(String repositoryId) {
		super(Review.Fields.ALL);
		this.repositoryId = repositoryId;
	}
	
	@Operation(
		summary = "Create new review", 
		description = "Creates a new terminology review for the SNOMED CT repository."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "400", description = "Bad Request")
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

	@Operation(
		summary = "Retrieve single review", 
		description = "Retrieves an existing terminology review with the specified identifier, if it exists."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Review not found"),
	})
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public Promise<Review> getReview(@PathVariable("id") final String reviewId) {
		return RepositoryRequests
			.reviews()
			.prepareGet(reviewId)
			.build(repositoryId)
			.execute(getBus());
	}

	@Operation(
		summary = "Retrieve change set for review", 
		description = "Retrieves the set of created, changed and detached concepts for an existing review with the specified identifier, if it exists."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Review not found or changes are not yet available"),
	})
	@RequestMapping(value="/{id}/concept-changes", method=RequestMethod.GET)
	public Promise<ConceptChanges> getConceptChanges(@PathVariable("id") final String reviewId) {
		return RepositoryRequests
					.reviews()
					.prepareGetConceptChanges(reviewId)
					.build(repositoryId)
					.execute(getBus());
	}

	@Operation(
		summary = "Delete single review", 
		description = "Deletes a review run along with its computed change set, if any of them exist."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Review not found"),
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
