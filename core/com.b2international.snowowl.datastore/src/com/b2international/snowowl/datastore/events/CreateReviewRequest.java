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
package com.b2international.snowowl.datastore.events;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.identity.domain.Permission;

/**
 * An event encapsulating a request to review differences between the specified source and target branch, identified by
 * their corresponding branch paths.
 * 
 * @since 4.2
 */
public final class CreateReviewRequest implements Request<RepositoryContext, Review>, RepositoryAccessControl {

	private final String sourcePath;
	private final String targetPath;

	public CreateReviewRequest(final String sourcePath, final String targetPath) {
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
	}
	
	@Override
	public Review execute(RepositoryContext context) {
		try {
			final ReviewManager reviewManager = context.service(ReviewManager.class);
			
			final Branch source = RepositoryRequests.branching().prepareGet(sourcePath).build().execute(context);
			final Branch target = RepositoryRequests.branching().prepareGet(targetPath).build().execute(context);
			
			return reviewManager.createReview(source, target);
		} catch (final NotFoundException e) {
			// Non-existent branches are reported as Bad Requests for reviews
			throw e.toBadRequestException();
		}
	}
	
	@Override
	public String getOperation() {
		return Permission.BROWSE;
	}
	
}
