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
package com.b2international.snowowl.datastore.events;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;

/**
 * Sent when a user requests a review to be deleted.
 * 
 * @since 4.2
 */
public final class DeleteReviewRequest extends ReviewRequest<Review> {

	public DeleteReviewRequest(final String reviewId) {
		super(reviewId);
	}
	
	@Override
	public Review execute(RepositoryContext context) {
		return context.service(ReviewManager.class).getReview(getReviewId()).delete();
	}
	
}
