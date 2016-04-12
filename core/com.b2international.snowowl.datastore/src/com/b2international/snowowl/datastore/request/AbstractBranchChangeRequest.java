/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.review.BranchState;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;

/**
 * @since 4.6
 */
public abstract class AbstractBranchChangeRequest<R> extends BaseRequest<RepositoryContext, R> {

	private final Class<R> responseClass;
	
	protected final String sourcePath;
	protected final String targetPath;
	protected final String commitMessage;
	protected final String reviewId;

	protected AbstractBranchChangeRequest(Class<R> responseClass, String sourcePath, String targetPath, String commitMessage, String reviewId) {
		this.responseClass = responseClass;
		
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		this.commitMessage = commitMessage;
		this.reviewId = reviewId;
	}

	@Override
	public R execute(RepositoryContext context) {
		
		try {
			final BranchManager branchManager = context.service(BranchManager.class);
			final Branch source = branchManager.getBranch(sourcePath);
			final Branch target = branchManager.getBranch(targetPath);
			
			if (reviewId != null) {
				final ReviewManager reviewManager = context.service(ReviewManager.class);
				final Review review = reviewManager.getReview(reviewId);
				final BranchState sourceState = review.source();
				final BranchState targetState = review.target();
				
				if (!sourceState.matches(source)) {
					throw new ConflictException("Source branch '%s' did not match with stored state on review identifier '%s'.", source.path(), reviewId);
				}
				
				if (!targetState.matches(target)) {
					throw new ConflictException("Target branch '%s' did not match with stored state on review identifier '%s'.", target.path(), reviewId);
				}
			}
			
			return execute(context, source, target);
						
		} catch (NotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	protected abstract R execute(RepositoryContext context, Branch source, Branch target);

	@Override
	protected Class<R> getReturnType() {
		return responseClass;
	}

	@Override
	public String toString() {
		return String.format("{type:%s, source:%s, target:%s, commitMessage:%s, reviewId:%s}", 
				getClass().getSimpleName(),
				sourcePath,
				targetPath,
				commitMessage,
				reviewId);
	}
}
