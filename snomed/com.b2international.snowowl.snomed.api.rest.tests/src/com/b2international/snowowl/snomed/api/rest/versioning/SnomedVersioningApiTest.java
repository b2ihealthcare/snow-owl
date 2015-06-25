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
package com.b2international.snowowl.snomed.api.rest.versioning;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * @since 2.0
 */
public class SnomedVersioningApiTest extends AbstractSnomedApiTest {

	@Test
	public void getNonExistentVersion() {
		assertVersionGetStatus("nonexistent", 404);
	}
	
	@Test
	public void createVersionWithoutDescription() {
		assertVersionPostStatus("", "20150201", 400);
	}
	
	@Test
	public void createVersionWithNonLatestEffectiveDate() {
		assertVersionPostStatus("sct-v1", "20150101", 400);
	}
	
	@Test
	public void createVersion() {
		assertVersionPostStatus("sct-v2", "20150201", 201);
		assertVersionGetStatus("sct-v2", 200);
	}
	
	@Test
	public void createVersionWithSameNameAsBranch() {
		assertBranchCanBeCreated("MAIN", "sct-v3");
		assertVersionPostStatus("sct-v3", "20150202", 409);
	}

	private void assertVersionGetStatus(String version, int status) {
		givenAuthenticatedRequest(ADMIN_API)
		.when()
			.get("/codesystems/SNOMEDCT/versions/{id}", version)
		.then()
		.assertThat()
			.statusCode(status);
	}

	private void assertVersionPostStatus(String version, String effectiveDate, int status) {
		whenCreatingVersion(givenAuthenticatedRequest(ADMIN_API), version, effectiveDate)
		.then()
		.assertThat()
			.statusCode(status);
	}
	
	private Response whenCreatingVersion(RequestSpecification request, String version, String effectiveDate) {
		Map<?, ?> requestBody = ImmutableMap.of(
			"version", version,
			"description", version,
			"effectiveDate", effectiveDate
		);

		return request
		.and()
			.contentType(ContentType.JSON)
		.and()
			.body(requestBody)
		.when()
			.post("/codesystems/SNOMEDCT/versions");
	}
}
