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
package com.b2international.snowowl.datastore.server.events;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An abstract superclass for terminology review events which include the affected review's identifier.
 * 
 * @since 4.2
 */
public abstract class ReviewEvent extends BaseReviewEvent {

	private final String reviewId;

	public ReviewEvent(final String repositoryId, final String reviewId) {
		super(repositoryId);
		this.reviewId = checkNotNull(reviewId, "reviewId");
	}

	public final String getReviewId() {
		return reviewId;
	}
}
