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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.collections.Procedure;
import com.b2international.snowowl.core.exceptions.ApiValidation;
import com.b2international.snowowl.datastore.server.events.ConceptChangesReply;
import com.b2international.snowowl.datastore.server.events.DeleteReviewEvent;
import com.b2international.snowowl.datastore.server.events.ReadConceptChangesEvent;
import com.b2international.snowowl.datastore.server.events.ReadReviewEvent;
import com.b2international.snowowl.datastore.server.events.ReviewReply;
import com.b2international.snowowl.datastore.server.review.ConceptChanges;
import com.b2international.snowowl.datastore.server.review.Review;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.rest.domain.CreateReviewRequest;
import com.b2international.snowowl.snomed.api.rest.domain.RestApiError;
import com.b2international.snowowl.snomed.api.rest.util.Responses;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Provides REST endpoints for computing Reviewerences between branches.
 * 
 * @since 4.2
 */
@Api("Branches")
@RestController
@RequestMapping(value="/reviews", produces={AbstractRestService.SO_MEDIA_TYPE, AbstractRestService.APPLICATION_JSON_VALUE})
public class SnomedBranchReviewController extends AbstractRestService {

	@Autowired
	private IEventBus bus;

	@ApiOperation(
			value = "Create new review", 
			notes = "Creates a new terminology review for the SNOMED CT repository.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created"),
		@ApiResponse(code = 400, message = "Bad Request", response=RestApiError.class)
	})
	@RequestMapping(method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public DeferredResult<ResponseEntity<Void>> createReview(@RequestBody final CreateReviewRequest request) {
		ApiValidation.checkInput(request);
		final DeferredResult<ResponseEntity<Void>> result = new DeferredResult<>();
		final ControllerLinkBuilder linkTo = linkTo(SnomedBranchReviewController.class);
		request.toEvent(repositoryId)
			.send(bus, ReviewReply.class)
			.then(new Procedure<ReviewReply>() { @Override protected void doApply(final ReviewReply reply) {
				result.setResult(Responses.created(getLocationHeader(linkTo, reply)).build());
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(final Throwable t) {
				result.setErrorResult(t);
			}});
		return result;
	}

	@ApiOperation(
			value = "Retrieve single review", 
			notes = "Retrieves an existing terminology review with the specified identifier, if it exists.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Review not found", response=RestApiError.class),
	})
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public DeferredResult<Review> getReview(@PathVariable("id") final String reviewId) {
		final DeferredResult<Review> result = new DeferredResult<>();
		new ReadReviewEvent(repositoryId, reviewId)
			.send(bus, ReviewReply.class)
			.then(new Procedure<ReviewReply>() { @Override protected void doApply(final ReviewReply reply) {
				result.setResult(reply.getReview());
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(final Throwable t) {
				result.setErrorResult(t);
			}});
		return result;
	}

	@ApiOperation(
			value = "Retrieve change set for review", 
			notes = "Retrieves the set of created, changed and detached concepts for an existing review with the specified identifier, if it exists.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Review not found or changes are not yet available", response=RestApiError.class),
	})
	@RequestMapping(value="/{id}/concept-changes", method=RequestMethod.GET)
	public DeferredResult<ConceptChanges> getConceptChanges(@PathVariable("id") final String reviewId) {
		final DeferredResult<ConceptChanges> result = new DeferredResult<>();
		new ReadConceptChangesEvent(repositoryId, reviewId)
			.send(bus, ConceptChangesReply.class)
			.then(new Procedure<ConceptChangesReply>() { @Override protected void doApply(final ConceptChangesReply reply) {
				result.setResult(reply.getConceptChanges());
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(final Throwable t) {
				result.setErrorResult(t);
			}});
		return result;
	}

	@ApiOperation(
			value = "Delete single review", 
			notes = "Deletes a review run along with its computed change set, if any of them exist.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Review not found", response=RestApiError.class),
	})
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public DeferredResult<ResponseEntity<Void>> deleteReview(@PathVariable("id") final String ReviewId) {
		final DeferredResult<ResponseEntity<Void>> result = new DeferredResult<>();
		new DeleteReviewEvent(repositoryId, ReviewId)
			.send(bus, ReviewReply.class)
			.then(new Procedure<ReviewReply>() { @Override protected void doApply(final ReviewReply reply) {
				result.setResult(Responses.noContent().build());
			}})
			.fail(new Procedure<Throwable>() { @Override protected void doApply(final Throwable t) {
				result.setErrorResult(t);
			}});
		return result;
	}

	private URI getLocationHeader(ControllerLinkBuilder linkBuilder, final ReviewReply reply) {
		return linkBuilder.slash(reply.getReview().id()).toUri();
	}
}
