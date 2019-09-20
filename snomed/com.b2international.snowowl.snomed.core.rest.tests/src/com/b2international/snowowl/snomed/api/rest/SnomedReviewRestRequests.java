/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.review.ReviewStatus;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 5.0
 */
public abstract class SnomedReviewRestRequests {

	private static final Set<String> FINISH_STATES = ImmutableSet.of(
			ReviewStatus.CURRENT.name(), 
			ReviewStatus.FAILED.name(), 
			ReviewStatus.STALE.name());

	public static ValidatableResponse createReview(IBranchPath source, IBranchPath target) {
		Map<?, ?> requestBody = ImmutableMap.builder()
				.put("source", source.getPath())
				.put("target", target.getPath())
				.build();

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/reviews")
				.then();
	}

	public static String getReviewJobId(ValidatableResponse response) {
		return lastPathSegment(response.statusCode(201)
				.extract()
				.header("Location"));
	}

	public static ValidatableResponse getReview(String reviewId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/reviews/{id}", reviewId)
				.then();
	}

	public static ValidatableResponse deleteReview(String reviewId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.delete("/reviews/{id}", reviewId)
				.then();
	}

	public static ValidatableResponse getConceptChanges(String reviewId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/reviews/{id}/concept-changes", reviewId)
				.then();
	}

	public static ValidatableResponse waitForReviewJob(String reviewId) {

		long endTime = System.currentTimeMillis() + SnomedApiTestConstants.POLL_TIMEOUT;
		long currentTime;
		ValidatableResponse response = null;
		String reviewStatus = null;

		do {

			try {
				Thread.sleep(SnomedApiTestConstants.POLL_INTERVAL);
			} catch (InterruptedException e) {
				fail(e.toString());
			}

			response = getReview(reviewId).statusCode(200);
			reviewStatus = response.extract().path("status");
			currentTime = System.currentTimeMillis();

		} while (!FINISH_STATES.contains(reviewStatus) && currentTime < endTime);

		assertNotNull(response);
		return response;
	}

	private SnomedReviewRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
