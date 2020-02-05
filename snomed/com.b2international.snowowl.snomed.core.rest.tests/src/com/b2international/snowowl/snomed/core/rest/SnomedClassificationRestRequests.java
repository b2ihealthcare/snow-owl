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

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 5.0
 */
public abstract class SnomedClassificationRestRequests {

	private static final Set<String> CLASSIFICATION_EXIT_STATES = ImmutableSet.of(
			ClassificationStatus.COMPLETED.name(), 
			ClassificationStatus.FAILED.name(), 
			ClassificationStatus.CANCELED.name(), 
			ClassificationStatus.STALE.name());

	private static final Set<String> SAVE_EXIT_STATES = ImmutableSet.of(
			ClassificationStatus.SAVED.name(), 
			ClassificationStatus.SAVE_FAILED.name());

	public static ValidatableResponse beginClassification(IBranchPath branchPath) {
		Map<String, Object> requestBody = ImmutableMap.of(
				"reasonerId", SnomedCoreConfiguration.ELK_REASONER_ID,
				"branch", branchPath.getPath());

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/classifications")
				.then();
	}

	public static String getClassificationJobId(ValidatableResponse response) {
		return lastPathSegment(response.statusCode(201)
				.extract()
				.header("Location"));
	}

	public static ValidatableResponse getClassification(IBranchPath branchPath, String classificationId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/classifications/{id}", classificationId)
				.then();
	}

	public static ValidatableResponse getRelationshipChanges(IBranchPath branchPath, String classificationId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.queryParam("limit", 2000)
				.queryParam("expand", "relationship()")
				.get("/classifications/{id}/relationship-changes", classificationId)
				.then();
	}
	
	public static ValidatableResponse getEquivalentConceptSets(IBranchPath branchPath, String classificationId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/classifications/{id}/equivalent-concepts", classificationId)
				.then();
	}

	public static ValidatableResponse waitForClassificationJob(IBranchPath branchPath, String classificationId) {
		return waitForJob(branchPath, classificationId, CLASSIFICATION_EXIT_STATES);
	}

	public static ValidatableResponse beginClassificationSave(IBranchPath branchPath, String classificationId) {
		Map<String, Object> requestBody = ImmutableMap.<String, Object>of("status", ClassificationStatus.SAVED.toString());

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put("/classifications/{id}", classificationId)
				.then()
				.assertThat()
				.statusCode(204);
	}

	public static ValidatableResponse waitForClassificationSaveJob(IBranchPath branchPath, String classificationId) throws InterruptedException {
		return waitForJob(branchPath, classificationId, SAVE_EXIT_STATES);
	}

	private static ValidatableResponse waitForJob(IBranchPath branchPath, String classificationId, Set<String> exitStates) {
		long endTime = System.currentTimeMillis() + SnomedApiTestConstants.POLL_TIMEOUT;
		long currentTime;
		ValidatableResponse response = null;
		String classificationStatus = null;

		do {

			try {
				Thread.sleep(SnomedApiTestConstants.POLL_INTERVAL);
			} catch (InterruptedException e) {
				fail(e.toString());
			}

			response = getClassification(branchPath, classificationId).statusCode(200);
			classificationStatus = response.extract().path("status");
			currentTime = System.currentTimeMillis();

		} while (!exitStates.contains(classificationStatus) && currentTime < endTime);

		assertNotNull(response);
		return response;
	}

	private SnomedClassificationRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
