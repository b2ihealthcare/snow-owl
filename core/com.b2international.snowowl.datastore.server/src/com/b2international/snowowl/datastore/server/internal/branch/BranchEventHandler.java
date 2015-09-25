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
package com.b2international.snowowl.datastore.server.internal.branch;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import com.b2international.snowowl.core.events.util.ApiEventHandler;
import com.b2international.snowowl.core.events.util.Handler;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.branch.BranchMergeException;
import com.b2international.snowowl.datastore.server.events.BranchEvent;
import com.b2international.snowowl.datastore.server.events.BranchReply;
import com.b2international.snowowl.datastore.server.events.BranchesReply;
import com.b2international.snowowl.datastore.server.events.CreateBranchEvent;
import com.b2international.snowowl.datastore.server.events.DeleteBranchEvent;
import com.b2international.snowowl.datastore.server.events.MergeEvent;
import com.b2international.snowowl.datastore.server.events.ReadAllBranchEvent;
import com.b2international.snowowl.datastore.server.events.ReadBranchChildrenEvent;
import com.b2international.snowowl.datastore.server.events.ReadBranchEvent;
import com.b2international.snowowl.datastore.server.events.ReopenBranchEvent;
import com.b2international.snowowl.datastore.server.review.BranchState;
import com.b2international.snowowl.datastore.server.review.Review;
import com.b2international.snowowl.datastore.server.review.ReviewManager;

/**
 * @since 4.1
 */
public class BranchEventHandler extends ApiEventHandler {

	private BranchManager branchManager;
	private ReviewManager reviewManager;
	
	public BranchEventHandler(BranchManager branchManager, ReviewManager reviewManager) {
		this.branchManager = checkNotNull(branchManager, "branchManager");
		this.reviewManager = checkNotNull(reviewManager, "reviewManager");
	}
	
	@Handler
	protected BranchesReply handle(ReadAllBranchEvent event) {
		return new BranchesReply(newHashSet(branchManager.getBranches()));
	}
	
	@Handler
	protected BranchesReply handle(ReadBranchChildrenEvent event) {
		final Branch branch = getBranch(event);
		return new BranchesReply(newHashSet(branch.children()));
	}

	@Handler
	protected BranchReply handle(DeleteBranchEvent event) {
		return new BranchReply(getBranch(event).delete());
	}

	@Handler
	protected BranchReply handle(ReadBranchEvent event) {
		return new BranchReply(getBranch(event));
	}

	@Handler
	protected BranchReply handle(CreateBranchEvent event) {
		try {
			final Branch parent = branchManager.getBranch(event.getParent());
			final Branch child = parent.createChild(event.getName(), event.getMetadata());
			return new BranchReply(child);
		} catch (NotFoundException e) {
			// if parent not found, convert it to BadRequestException
			throw e.toBadRequestException();
		}
	}
	
	@Handler
	protected BranchReply handle(ReopenBranchEvent event) {
		try {
			final Branch branch = getBranch(event);
			return new BranchReply(branch.reopen());
		} catch (NotFoundException e) {
			// if parent not found, convert it to BadRequestException
			throw e.toBadRequestException();
		}
	}
	
	@Handler
	protected BranchReply handle(MergeEvent event) {
		try {
			final String reviewId = event.getReviewId();
			final Branch source = branchManager.getBranch(event.getSource());
			final Branch target = branchManager.getBranch(event.getTarget());
			
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
				// merge into target
				try {
					final Branch merged = target.merge(source, event.getCommitMessage());
					return new BranchReply(merged);
				} catch (BranchMergeException e) {
					throw new ConflictException("Cannot merge source '%s' into target '%s'.", source.path(), target.path(), e);
				}
				
			} else if (target.parent().equals(source)) {
				// rebase onto target
				try {
					final Branch rebased = target.rebase(source, event.getCommitMessage());
					return new BranchReply(rebased);
				} catch (BranchMergeException e) {
					throw new ConflictException("Cannot rebase target '%s' with source '%s'.", target.path(), source.path(), e);
				}
			}
			
			throw new BadRequestException("Cannot merge source '%s' into target '%s', because there is no relation between them.", source.path(), target.path());
		} catch (NotFoundException e) {
			throw e.toBadRequestException();
		}
	}
	
	private Branch getBranch(BranchEvent event) {
		return branchManager.getBranch(event.getBranchPath());
	}
}
