/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.exceptions.NotFoundException;

/**
 * @since 4.2
 */
public interface ReviewManager extends IDisposableService {

	/**
	 * Creates a new terminology review object with the specified source and target branches.
	 * <p>
	 * {@code source} and {@code target} branches must be directly related; either {@code source} must be the parent branch of {@code target}, or vice
	 * versa.
	 * <p>
	 * The initial review status is {@link ReviewStatus#PENDING} while the concept change set is being computed, then it transitions to
	 * {@link ReviewStatus#CURRENT} if no commits have happened on either {@code source} or {@code target} in the meantime. Should this happen at any
	 * time after creating the review, it becomes {@link ReviewStatus#STALE}.
	 * 
	 * @param source
	 *            the source branch to review
	 * @param target
	 *            the target branch to review
	 * 
	 * @see Branch#merge(Branch, String)
	 * @return the created {@link Review} object
	 */
	Review createReview(Branch source, Branch target);

	/**
	 * Retrieves a single review by its unique identifier.
	 * 
	 * @param id
	 *            the review identifier to look for
	 * @return the associated terminology review object
	 * @throws NotFoundException
	 *             if no review exists for the identifier
	 */
	Review getReview(String id);

	/**
	 * Retrieves computed concept changes for a review.
	 * 
	 * @param id
	 *            the review identifier to look for
	 * @return the concept changes associated with the terminology review object
	 * @throws NotFoundException
	 *             if no change set exists currently for the review
	 */
	ConceptChanges getConceptChanges(String id);

	/**
	 * Delets a review by its ID.
	 * 
	 * @param reviewId
	 * @return
	 */
	void delete(String reviewId);
}
