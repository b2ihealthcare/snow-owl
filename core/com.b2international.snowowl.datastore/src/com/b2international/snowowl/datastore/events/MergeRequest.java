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

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.branch.BranchMergeException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.review.BranchState;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.google.common.base.Strings;

/**
 * @since 4.1
 */
public final class MergeRequest extends BaseRequest<RepositoryContext, Branch> {

	private final String source;
	private final String target;
	private final String commitMessage;
	private final String reviewId;

	public MergeRequest(final String source, final String target, final String commitMessage, String reviewId) {
		this.source = source;
		this.target = target;
		this.commitMessage = Strings.isNullOrEmpty(commitMessage) ? defaultMessage() : commitMessage;
		this.reviewId = reviewId;
	}

	private String defaultMessage() {
		return String.format("Merge branch '%s' into '%s'", getSource(), getTarget());
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}
	
	public String getReviewId() {
		return reviewId;
	}
	
	@Override
	public Branch execute(RepositoryContext context) {
		try {
			final BranchManager branchManager = context.service(BranchManager.class);
			final ReviewManager reviewManager = context.service(ReviewManager.class);
			
			final String reviewId = getReviewId();
			final Branch source = branchManager.getBranch(getSource());
			final Branch target = branchManager.getBranch(getTarget());
			
			if (reviewId != null) {
				Review review = reviewManager.getReview(reviewId);
				BranchState sourceState = review.source();
				BranchState targetState = review.target();
				
				if (!sourceState.matches(source)) {
					throw new ConflictException("Source branch '%s' did not match with stored state on review identifier '%s'.", source.path(), reviewId);
				}
				
				if (!targetState.matches(target)) {
					throw new ConflictException("Target branch '%s' did not match with stored state on review identifier '%s'.", target.path(), reviewId);
				}
			}
			
			if (source.parent().equals(target)) {
				// merge source into target (only if target is the most recent version of source's parent)
				try {
					return target.merge(source, getCommitMessage());
				} catch (BranchMergeException e) {
					throw new ConflictException("Cannot merge source '%s' into target '%s'.", source.path(), target.path(), e);
				}
				
			} else if (target.parent().equals(source)) {
				// rebase target on source (also allow STALE targets on not the most recent source branches)
				try {
					return target.rebase(source, getCommitMessage());
				} catch (BranchMergeException e) {
					throw new ConflictException("Cannot rebase target '%s' with source '%s'.", target.path(), source.path(), e);
				}
			}
			
			throw new BadRequestException("Cannot merge source '%s' into target '%s', because there is no relation between them.", source.path(), target.path());
		} catch (NotFoundException e) {
			throw e.toBadRequestException();
		}
	}
	
	@Override
	protected Class<Branch> getReturnType() {
		return Branch.class;
	}
	
	@Override
	public String toString() {
		return String.format("{type:%s, source:%s, target:%s, commitMessage:%s, reviewId:%s}", 
				getClass().getSimpleName(),
				source,
				target,
				commitMessage,
				reviewId);
	}
}
