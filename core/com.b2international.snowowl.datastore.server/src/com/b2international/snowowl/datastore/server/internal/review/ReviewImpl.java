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

import java.util.Objects;

import com.b2international.snowowl.datastore.server.branch.Branch;
import com.b2international.snowowl.datastore.server.review.BranchState;
import com.b2international.snowowl.datastore.server.review.Review;
import com.b2international.snowowl.datastore.server.review.ReviewStatus;

/**
 * @since 5.0
 */
public class ReviewImpl implements Review {

	private ReviewManagerImpl reviewManager;

	private final String id;
	private final ReviewStatus status;
	private final BranchStateImpl source;
	private final BranchStateImpl target;
	private final boolean deleted;

	public static Builder builder(final String id, final Branch source, final Branch target) {
		return new Builder(id, source, target);
	}

	public static Builder builder(final ReviewImpl review) {
		return new Builder(review);
	}

	public static class Builder {

		private final String id;
		private final BranchStateImpl source;
		private final BranchStateImpl target;
		private ReviewStatus status = ReviewStatus.PENDING;
		private boolean deleted = false;

		private Builder(final String id, final Branch source, final Branch target) {
			this(id, new BranchStateImpl(source), new BranchStateImpl(target));
		}

		private Builder(final String id, final BranchStateImpl sourceState, final BranchStateImpl targetState) {
			this.id = id;
			this.source = sourceState;
			this.target = targetState;
		}

		private Builder(final ReviewImpl review) {
			this(review.id, review.source, review.target);
			status = review.status;
			deleted = review.deleted;
		}

		public Builder status(final ReviewStatus newStatus) {
			status = newStatus;
			return this;
		}

		public Builder deleted() {
			deleted = true;
			return this;
		}

		public ReviewImpl build() {
			return new ReviewImpl(this);
		}
	}

	private ReviewImpl(final Builder builder) {
		this(builder.id, builder.source, builder.target, builder.status, builder.deleted);
	}

	private ReviewImpl(final String id, final BranchStateImpl source, final BranchStateImpl target, final ReviewStatus status, final boolean deleted) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.status = status;
		this.deleted = deleted;
	}

	void setReviewManager(final ReviewManagerImpl reviewManager) {
		this.reviewManager = reviewManager;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public ReviewStatus status() {
		return status;
	}

	@Override
	public BranchState source() {
		return source;
	}

	@Override
	public BranchState target() {
		return target;
	}

	public String sourcePath() {
		return source.path();
	}
	
	public String targetPath() {
		return target.path();
	}
	
	@Override
	public Review delete() {
		return reviewManager.deleteReview(this);
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, status);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof ReviewImpl)) { return false; }

		/* 
		 * XXX: Comparison considers both ID and status, in case event listeners try to change state on the 
		 * review concurrently.
		 */
		final ReviewImpl other = (ReviewImpl) obj;
		if (!id.equals(other.id)) {	return false; }
		if (status != other.status) { return false; }

		return true;
	}
}
