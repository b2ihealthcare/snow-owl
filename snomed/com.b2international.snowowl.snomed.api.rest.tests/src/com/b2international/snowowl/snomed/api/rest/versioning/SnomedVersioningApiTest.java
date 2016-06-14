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
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedVersioningApiAssert.assertVersionGetStatus;
import static com.b2international.snowowl.snomed.api.rest.SnomedVersioningApiAssert.assertVersionPostStatus;
import static com.b2international.snowowl.snomed.api.rest.SnomedVersioningApiAssert.whenCreatingVersion;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;

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
		assertVersionPostStatus("sct-v2", "20150130", 201);
		assertVersionGetStatus("sct-v2", 200);
	}

	@Test
	public void createVersionWithSameNameAsBranch() {
		givenBranchWithPath(testBranchPath);
		assertVersionPostStatus(testBranchPath.lastSegment(), "20150202", 409);
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
		createCodeSystem(branchPath.getPath(), shortName);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(false));
		
		whenCreatingVersion("v1", "20150130", shortName)
			.then().assertThat().statusCode(201);
		
		assertVersionGetStatus("v1", 200, shortName);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", "MAIN", conceptId)
			.then().body("released", equalTo(false))
			.and().body("effectiveTime", equalTo(null));
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(true))
			.and().body("effectiveTime", equalTo("20150130"));
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
		createCodeSystem(branchPath.getPath(), shortName);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(false));
		
		whenCreatingVersion("v1", "20150130", shortName)
			.then().assertThat().statusCode(201);
		
		assertVersionGetStatus("v1", 200, shortName);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", "MAIN", conceptId)
			.then().statusCode(404);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(true))
			.and().body("effectiveTime", equalTo("20150130"));
	}
	
}
