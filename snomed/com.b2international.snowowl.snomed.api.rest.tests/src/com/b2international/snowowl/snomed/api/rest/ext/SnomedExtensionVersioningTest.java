/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getNextAvailableEffectiveDateAsString;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getVersion;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_B2I_SHORT_NAME;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.BranchBase;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;

/**
 * @since 4.7
 */
@BranchBase(value = SnomedApiTestConstants.EXTENSION_PATH, isolateTests = false)
public class SnomedExtensionVersioningTest extends AbstractSnomedApiTest {

	@Test
	public void createVersionWithoutChanges() {
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMED_B2I_SHORT_NAME);
		String versionId = "v1";

		createVersion(SNOMED_B2I_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		getVersion(SNOMED_B2I_SHORT_NAME, versionId).statusCode(200).body("effectiveDate", equalTo(effectiveDate));
	}

	@Test
	public void createVersionWithoutVersionId() {
		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMED_B2I_SHORT_NAME);
		String versionId = "";

		createVersion(SNOMED_B2I_SHORT_NAME, versionId, effectiveDate).statusCode(400);
	}

	@Test
	public void createRegularVersion() {
		String conceptId = createNewConcept(branchPath);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200).body("released", equalTo(false));

		String effectiveDate = getNextAvailableEffectiveDateAsString(SNOMED_B2I_SHORT_NAME);
		String versionId = "v2";

		createVersion(SNOMED_B2I_SHORT_NAME, versionId, effectiveDate).statusCode(201);
		getVersion(SNOMED_B2I_SHORT_NAME, versionId).statusCode(200).body("effectiveDate", equalTo(effectiveDate));

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate));
	}

}
