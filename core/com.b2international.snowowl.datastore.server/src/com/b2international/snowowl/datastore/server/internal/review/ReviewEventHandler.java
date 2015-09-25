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
package com.b2international.snowowl.datastore.server.internal.review;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.events.util.ApiEventHandler;
import com.b2international.snowowl.core.events.util.Handler;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.server.events.*;
import com.b2international.snowowl.datastore.server.review.ConceptChanges;
import com.b2international.snowowl.datastore.server.review.Review;
import com.b2international.snowowl.datastore.server.review.ReviewManager;

/**
 * @since 4.2
 */
public class ReviewEventHandler extends ApiEventHandler {

	private final BranchManager branchManager;
	private final ReviewManager reviewManager;

	public ReviewEventHandler(final BranchManager branchManager, final ReviewManager reviewManager) {
		this.branchManager = checkNotNull(branchManager, "branchManager");
		this.reviewManager = checkNotNull(reviewManager, "reviewManager");
	}

	@Handler
	protected ReviewReply handle(final CreateReviewEvent event) {
		try {

			final Branch source = branchManager.getBranch(event.getSourcePath());
			final Branch target = branchManager.getBranch(event.getTargetPath());
			return new ReviewReply(reviewManager.createReview(source, target));

		} catch (final NotFoundException e) {
			// Non-existent branches are reported as Bad Requests for reviews
			throw e.toBadRequestException();
		}
	}

	@Handler
	protected ReviewReply handle(final ReadReviewEvent event) {
		return new ReviewReply(getReview(event));
	}

	@Handler
	protected ConceptChangesReply handle(final ReadConceptChangesEvent event) {
		return new ConceptChangesReply(getConceptChanges(event));
	}

	@Handler
	protected ReviewReply handle(final DeleteReviewEvent event) {
		return new ReviewReply(getReview(event).delete());
	}

	private Review getReview(final ReviewEvent event) {
		return reviewManager.getReview(event.getReviewId());
	}

	private ConceptChanges getConceptChanges(final ReviewEvent event) {
		return reviewManager.getConceptChanges(event.getReviewId());
	}
}
