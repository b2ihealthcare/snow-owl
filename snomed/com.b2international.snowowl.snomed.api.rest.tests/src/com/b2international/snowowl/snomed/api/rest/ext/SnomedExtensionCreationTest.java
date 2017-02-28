/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getVersion;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Date;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;

/**
 * @since 5.0
 */
public class SnomedExtensionCreationTest extends AbstractSnomedApiTest {

	@Test
	public void createExtensionVersion01() {
		String conceptId = createNewConcept(branchPath);

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(false));

		String shortName = "SNOMEDCT-CV1";
		createCodeSystem(branchPath, shortName).statusCode(201);

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(false));

		String versionId = "v1";
		String effectiveDate = EffectiveTimes.format(new Date(), DateFormats.SHORT);
		createVersion(shortName, versionId, effectiveDate).statusCode(201);

		getVersion(shortName, versionId).statusCode(200)
		.body("effectiveDate", equalTo(effectiveDate));

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate));

		IBranchPath versionPath = BranchPathUtils.createPath(branchPath, versionId);

		getComponent(versionPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate));
	}

	@Test
	public void createExtensionVersion02() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		createBranch(a).statusCode(201);

		String conceptId = createNewConcept(a);

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);

		String shortName = "SNOMEDCT-CV2";
		createCodeSystem(a, shortName).statusCode(201);

		getComponent(a, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(false));

		String versionId = "v1";
		String effectiveDate = EffectiveTimes.format(new Date(), DateFormats.SHORT);
		createVersion(shortName, versionId, effectiveDate).statusCode(201);

		getVersion(shortName, versionId).statusCode(200)
		.body("effectiveDate", equalTo(effectiveDate));

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(a, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
		.body("released", equalTo(true))
		.body("effectiveTime", equalTo(effectiveDate));
	}

}
