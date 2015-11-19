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
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.events.DeleteReviewRequest;
import com.b2international.snowowl.datastore.events.ReadConceptChangesRequest;
import com.b2international.snowowl.datastore.events.ReadReviewRequest;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;

/**
 * @since 4.5
 */
public final class Reviews {

	private String repositoryId;

	Reviews(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public ReviewCreateRequestBuilder prepareCreate() {
		return new ReviewCreateRequestBuilder(repositoryId);
	}

	public Request<ServiceProvider, Review> prepareGet(String reviewId) {
		return RepositoryRequests.wrap(repositoryId, new ReadReviewRequest(reviewId));
	}

	public Request<ServiceProvider, ConceptChanges> prepareGetConceptChanges(String reviewId) {
		return RepositoryRequests.wrap(repositoryId, new ReadConceptChangesRequest(reviewId));
	}

	public Request<ServiceProvider, Review> prepareDelete(String reviewId) {
		return RepositoryRequests.wrap(repositoryId, new DeleteReviewRequest(reviewId));
	}
	
}
