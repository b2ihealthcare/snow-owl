/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.ext;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedVersioningApiAssert.assertVersionGetStatus;
import static com.b2international.snowowl.snomed.api.rest.SnomedVersioningApiAssert.assertVersionPostStatus;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;

/**
 * @since 4.7
 */
public class SnomedExtensionVersioningTest extends ExtensionTest {
	
	@Test
	public void createVersionWithoutChangesOnB2iBranch() {
		assertB2iExtensionExistsWithDefaults();
		
		final String versionDate = getDateForNewVersion(B2I_EXT_SHORT_NAME);
		final String versionId = UUID.randomUUID().toString();
		
		assertVersionPostStatus(versionId, versionDate, B2I_EXT_SHORT_NAME, 201);
		assertVersionGetStatus(versionId, 200, B2I_EXT_SHORT_NAME);
	}
	
	@Test
	public void createVersionWithoutVersionIdOnB2iBranch() {
		assertB2iExtensionExistsWithDefaults();
		
		final String versionDate = getDateForNewVersion(B2I_EXT_SHORT_NAME);
		assertVersionPostStatus("", versionDate, B2I_EXT_SHORT_NAME, 400);
	}
	
	@Test
	public void createVersionOnB2iBranch() {
		assertB2iExtensionExistsWithDefaults();
		
		final IBranchPath branchPath = BranchPathUtils.createPath(B2I_EXT_BRANCH);
		final Map<?, ?> requestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		final String conceptId = assertComponentCreated(branchPath, SnomedComponentType.CONCEPT, requestBody);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(false));
		
		final String versionDate = getDateForNewVersion(B2I_EXT_SHORT_NAME);
		final String versionId = UUID.randomUUID().toString();
		
		assertVersionPostStatus(versionId, versionDate, B2I_EXT_SHORT_NAME, 201);
		assertVersionGetStatus(versionId, 200, B2I_EXT_SHORT_NAME);
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when().get("{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
			.then().body("released", equalTo(true));
	}

}
