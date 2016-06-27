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

import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemApiAssert.assertCodeSystemCreated;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemApiAssert.newCodeSystemRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * @since 2.0
 */
public class SnomedVersioningApiTest extends AbstractSnomedApiTest {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	/**
	 * The context-relative base URL for the administrative controller. 
	 */
	private static String ADMIN_API = "/admin";

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
		assertVersionPostStatus("sct-v2", dateFormat.format(new Date()), 201);
		assertVersionGetStatus("sct-v2", 200);
	}

	@Test
	public void createVersionWithSameNameAsBranch() {
		final Date tomorrow = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
		givenBranchWithPath(testBranchPath);
		assertVersionPostStatus(testBranchPath.lastSegment(), dateFormat.format(tomorrow), 409);
	}

	private void assertVersionGetStatus(final String version, final int status) {
		assertVersionGetStatus(version, status, "SNOMEDCT");
	}
	
	private void assertVersionGetStatus(final String version, final int status, final String shortName) {
		givenAuthenticatedRequest(ADMIN_API)
		.when().get("/codesystems/{shortNameOrOid}/versions/{id}", shortName, version)
		.then().assertThat().statusCode(status);
	}

	private void assertVersionPostStatus(final String version, final String effectiveDate, final int status) {
		whenCreatingVersion(version, effectiveDate)
		.then().assertThat().statusCode(status);
	}

	private Response whenCreatingVersion(final String version, final String effectiveDate) {
		return whenCreatingVersion(version, effectiveDate, "SNOMEDCT");
	}
	
	private Response whenCreatingVersion(final String version, final String effectiveDate, final String shortName) {
		final Map<?, ?> requestBody = ImmutableMap.builder()
				.put("version", version)
				.put("description", version)
				.put("effectiveDate", effectiveDate)
				.build();

		return givenAuthenticatedRequest(ADMIN_API)
				.and().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post("/codesystems/{shortNameOrOid}/versions", shortName);
	}
	
	@Test
	public void createExtensionVersion01() {
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, requestBody);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", "MAIN", conceptId)
			.then().body("released", equalTo(false));
		
		final String shortName = "versionTest";
		final IBranchPath branchPath = createRandomBranchPath();
		givenBranchWithPath(branchPath);
		final Map<String, String> newCodeSystemRequestBody = newCodeSystemRequestBody(shortName, branchPath.getPath());
		assertCodeSystemCreated(newCodeSystemRequestBody);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(false));
		
		final String versionDate = dateFormat.format(new Date());
		whenCreatingVersion("v1", versionDate, shortName)
			.then().assertThat().statusCode(201);
		
		assertVersionGetStatus("v1", 200, shortName);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", "MAIN", conceptId)
			.then().body("released", equalTo(false))
			.and().body("effectiveTime", equalTo(null));
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(true))
			.and().body("effectiveTime", equalTo(versionDate));
	}
	
	@Test
	public void createExtensionVersion02() {
		final IBranchPath branchPath = createRandomBranchPath();
		givenBranchWithPath(branchPath);
		
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(branchPath, SnomedComponentType.CONCEPT, requestBody);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", "MAIN", conceptId)
			.then().statusCode(404);
		
		final String shortName = "versionTest2";
		final Map<String, String> newCodeSystemRequestBody = newCodeSystemRequestBody(shortName, branchPath.getPath());
		assertCodeSystemCreated(newCodeSystemRequestBody);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(false));
		
		final String versionDate = dateFormat.format(new Date());
		whenCreatingVersion("v1", versionDate, shortName)
			.then().assertThat().statusCode(201);
		
		assertVersionGetStatus("v1", 200, shortName);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", "MAIN", conceptId)
			.then().statusCode(404);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(true))
			.and().body("effectiveTime", equalTo(versionDate));
	}
	
}
