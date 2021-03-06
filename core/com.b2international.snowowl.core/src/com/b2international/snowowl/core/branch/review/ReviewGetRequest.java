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
package com.b2international.snowowl.core.branch.review;

import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;

/**
 * Sent when a user requests to read the details of a terminology review with the specified identifier.
 * 
 * @since 4.2
 */
public final class ReviewGetRequest extends ReviewRequest<Review> implements RepositoryAccessControl {

	public ReviewGetRequest(final String reviewId) {
		super(reviewId);
	}
	
	@Override
	public Review execute(RepositoryContext context) {
		return context.service(ReviewManager.class).getReview(getReviewId());
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}
	
}
