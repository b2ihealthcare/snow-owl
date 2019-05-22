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
package com.b2international.snowowl.datastore.review;

import java.util.Date;
import java.util.Set;

import com.b2international.index.Doc;
import com.b2international.snowowl.core.branch.Branch;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a terminology review comparing changes on branches.
 *
 * @since 4.2
 */
@Doc(type="review")
public final class Review {

	public static final class Fields {
		public static final String ID = "id";
		public static final String LAST_UPDATED = "lastUpdated";
		public static final String STATUS = "status";
		public static final Set<String> ALL = ImmutableSet.of(ID, LAST_UPDATED, STATUS);
	}
	
	private final String id;
	private final String lastUpdated;
	private final ReviewStatus status;
	private final BranchState source;
	private final BranchState target;

	public static Builder builder(final String id, final Branch source, final Branch target) {
		return new Builder(id, source, target);
	}

	public static Builder builder(final Review review) {
		return new Builder(review);
	}

	public static class Builder {

		private static String getCurrentTimeISO8601() {
			return ISO8601Utils.format(new Date());
		}

		private final String id;
		private final BranchState source;
		private final BranchState target;
		private ReviewStatus status = ReviewStatus.PENDING;
		private String lastUpdated = getCurrentTimeISO8601();

		private Builder(final String id, final Branch source, final Branch target) {
			this(id, new BranchState(source), new BranchState(target));
		}

		private Builder(final String id, final BranchState sourceState, final BranchState targetState) {
			this.id = id;
			this.source = sourceState;
			this.target = targetState;
		}

		private Builder(final Review review) {
			this(review.id, review.source, review.target);
			status = review.status;
			lastUpdated = review.lastUpdated;
		}

		public Builder status(final ReviewStatus newStatus) {
			status = newStatus;
			return this;
		}

		public Builder refreshLastUpdated() {
			return lastUpdated(getCurrentTimeISO8601());
		}
		
		public Builder lastUpdated(final String newLastUpdated) {
			lastUpdated = newLastUpdated;
			return this;
		}

		public Review build() {
			return new Review(this);
		}
	}

	private Review(final Builder builder) {
		this(builder.id, builder.source, builder.target, builder.status, builder.lastUpdated);
	}

	private Review(final String id, final BranchState source, final BranchState target, final ReviewStatus status, final String lastUpdated) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.status = status;
		this.lastUpdated = lastUpdated;
	}

	/**
	 * Returns the unique identifier of this review.
	 */
	public String id() {
		return id;
	}

	/**
	 * Returns the current status of this review.
	 */
	public ReviewStatus status() {
		return status;
	}

	/**
	 * Returns the branch used as the comparison source in the state it was when collection started.
	 * <p>
	 * Note that a separate retrieve request for the same branch may display different values, if it has been changed in
	 * the meantime.
	 */
	public BranchState source() {
		return source;
	}

	/**
	 * Returns the branch used as the comparison target in the state it was when collection started.
	 * <p>
	 * Note that a separate retrieve request for the same branch may display different values, if it has been changed in
	 * the meantime.
	 */
	public BranchState target() {
		return target;
	}

	/**
	 * Returns the last update time in ISO8601 format. Update time is registered at creation and whenever the review's
	 * state changes.
	 * 
	 * @return the time of last update
	 */
	public String lastUpdated() {
		return lastUpdated;
	}

	@Override
	public int hashCode() {
		return 31 + id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof Review)) { return false; }

		final Review other = (Review) obj;
		return id.equals(other.id);
	}
}
