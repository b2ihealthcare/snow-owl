/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.rest.validation;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.google.common.collect.Sets;

import io.restassured.http.ContentType;

/**
 * @since 9.3
 */
public class ValidationApiTest {
	
	private static final String VALIDATION_API = "/snomedct/validations";
	private static final Json VALIDATION_JOB = Json.object(
			"path", "SNOMEDCT",
			"ruleIds", Sets.newHashSet("38a"),
			"unpublishedOnly", "false"
	);
	
	@Test
	public void validationPostAndGetTest() {
		String locationHeader = givenAuthenticatedRequest(VALIDATION_API)
			.with().contentType(ContentType.JSON)
			.and().body(VALIDATION_JOB)
			.when().post()
			.then()
			.assertThat()
			.statusCode(201)
			.extract().header("location");
		
		String jobId = locationHeader.substring(locationHeader.length() - 40);
		
		waitDone(jobId);
		
		givenAuthenticatedRequest(VALIDATION_API + "/validations/" + jobId + "/issues")
			.when().get()
			.then()
			.assertThat()
			.statusCode(200);
	}
	
	private void waitDone(final String jobId) {
		String state = null;
		
		do {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			state = givenAuthenticatedRequest(VALIDATION_API + "/" + jobId)
					.when().get()
					.then()
					.extract().path("state");
		} while (state.equals("SCHEDULED") || state.equals("RUNNING"));
	}
}
