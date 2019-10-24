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
package com.b2international.snowowl.snomed.core.rest;

import static com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants.SCT_API;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Set;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.merge.Merge;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 5.0
 */
public abstract class SnomedMergingRestRequests {
	
	private static final long MERGE_POLLING_INTERVAL = 2_000L;
	
	private static final Set<String> FINISH_STATES = ImmutableSet.of(
			Merge.Status.COMPLETED.name(), 
			Merge.Status.FAILED.name(), 
			Merge.Status.CONFLICTS.name());

	public static ValidatableResponse createMerge(IBranchPath source, IBranchPath target, String commitComment, String reviewId) {
		ImmutableMap.Builder<String, Object> requestBuilder = ImmutableMap.<String, Object>builder()
				.put("source", source.getPath())
				.put("target", target.getPath())
				.put("commitComment", commitComment);

		if (null != reviewId) {
			requestBuilder.put("reviewId", reviewId);
		}

		return givenAuthenticatedRequest(SCT_API)
				.contentType(ContentType.JSON)
				.body(requestBuilder.build())
				.post("/merges")
				.then();
	}

	public static ValidatableResponse waitForMergeJob(String id) {

		final long endTime = System.currentTimeMillis() + SnomedApiTestConstants.POLL_TIMEOUT;
		long currentTime;
		ValidatableResponse response = null;
		String mergeStatus = null;

		do {

			try {
				Thread.sleep(MERGE_POLLING_INTERVAL);
			} catch (InterruptedException e) {
				fail(e.toString());
			}

			response = getMerge(id).statusCode(200);
			mergeStatus = response.extract().path("status");
			currentTime = System.currentTimeMillis();

		} while (!FINISH_STATES.contains(mergeStatus) && currentTime < endTime);

		assertNotNull(response);
		return response;
	}
	
	public static ValidatableResponse getMerge(String id) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/merges/{id}", id)
				.then();
	}

	private SnomedMergingRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
