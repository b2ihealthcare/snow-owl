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

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.ReviewManager;

/**
 * Sent when a user requests to read change set of a terminology review with the specified identifier.
 * 
 * @since 4.2
 */
public final class ReadConceptChangesRequest extends ReviewRequest<ConceptChanges> {

	public ReadConceptChangesRequest(final String reviewId) {
		super(reviewId);
	}

	@Override
	public ConceptChanges execute(RepositoryContext context) {
		return context.service(ReviewManager.class).getConceptChanges(getReviewId());
	}

}
