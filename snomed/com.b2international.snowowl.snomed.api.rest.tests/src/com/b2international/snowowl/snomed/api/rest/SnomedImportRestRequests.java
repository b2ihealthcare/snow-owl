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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.google.common.collect.ImmutableSet;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
public abstract class SnomedImportRestRequests {

	private static Set<String> FINISH_STATES = ImmutableSet.of(ImportStatus.COMPLETED.name(), ImportStatus.FAILED.name());

	public static ValidatableResponse uploadImportFile(String importId, Class<?> testClass, String importFile) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.multiPart(PlatformUtil.toAbsolutePath(testClass, importFile).toFile())
				.post("/imports/{id}/archive", importId)
				.then();
	}

	public static ValidatableResponse waitForImportJob(String id) {

		long endTime = System.currentTimeMillis() + SnomedApiTestConstants.POLL_TIMEOUT;
		long currentTime;
		ValidatableResponse response = null;
		String mergeStatus = null;

		do {

			try {
				Thread.sleep(SnomedApiTestConstants.POLL_INTERVAL);
			} catch (InterruptedException e) {
				fail(e.toString());
			}

			response = getImport(id).statusCode(200);
			mergeStatus = response.extract().path("status");
			currentTime = System.currentTimeMillis();

		} while (!FINISH_STATES.contains(mergeStatus) && currentTime < endTime);

		assertNotNull(response);
		return response;
	}

	public static ValidatableResponse getImport(String importId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/imports/{id}", importId)
				.then();
	}

	public static ValidatableResponse createImport(Map<?, ?> importConfiguration) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(ContentType.JSON)
				.body(importConfiguration)
				.post("/imports")
				.then();
	}

	public static ValidatableResponse deleteImport(String importId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.delete("/imports/{id}", importId)
				.then();
	}

	private SnomedImportRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
