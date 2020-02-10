/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.events.CreateReviewRequest;
import com.b2international.snowowl.datastore.review.Review;

/**
 * @since 4.5
 */
public final class ReviewCreateRequestBuilder extends BaseRequestBuilder<ReviewCreateRequestBuilder, RepositoryContext, Review> implements RepositoryRequestBuilder<Review> {
	
	private String sourceBranch;
	private String targetBranch;

	ReviewCreateRequestBuilder() {}
	
	public ReviewCreateRequestBuilder setSource(String source) {
		this.sourceBranch = source;
		return this;
	}
	
	public ReviewCreateRequestBuilder setTarget(String target) {
		this.targetBranch = target;
		return this;
	}

	@Override
	protected Request<RepositoryContext, Review> doBuild() {
		return new CreateReviewRequest(sourceBranch, targetBranch);
	}

}
