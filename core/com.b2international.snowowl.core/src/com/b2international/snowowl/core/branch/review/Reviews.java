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
package com.b2international.snowowl.core.branch.review;

/**
 * @since 4.5
 */
public final class Reviews {

	public Reviews() {}
	
	public ReviewCreateRequestBuilder prepareCreate() {
		return new ReviewCreateRequestBuilder();
	}

	public ReviewGetRequestBuilder prepareGet(String reviewId) {
		return new ReviewGetRequestBuilder(reviewId);
	}

	public ReviewGetConceptChangesRequestBuilder prepareGetConceptChanges(String reviewId) {
		return new ReviewGetConceptChangesRequestBuilder(reviewId);
	}

	public ReviewDeleteRequestBuilder prepareDelete(String reviewId) {
		return new ReviewDeleteRequestBuilder(reviewId);
	}
	
}
