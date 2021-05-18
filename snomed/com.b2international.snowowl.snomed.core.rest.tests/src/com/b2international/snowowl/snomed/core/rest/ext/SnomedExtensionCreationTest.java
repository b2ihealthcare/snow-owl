/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.ext;

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;

/**
 * @since 5.0
 */
public class SnomedExtensionCreationTest extends AbstractSnomedApiTest {

	@Test
	public void createExtensionVersion01() {
		String conceptId = createNewConcept(branchPath);

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("released", equalTo(false));

		String codeSystemId = "SNOMEDCT-CV1";
		createCodeSystem(branchPath, codeSystemId).statusCode(201);

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("released", equalTo(false));

		String versionId = "v1";
		LocalDate effectiveTime = LocalDate.now();
		createVersion(codeSystemId, versionId, effectiveTime).statusCode(201);

		assertThat(getVersion(codeSystemId, versionId).getEffectiveTime()).isEqualTo(effectiveTime);

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveTime.format(DateTimeFormatter.BASIC_ISO_DATE)));

		IBranchPath versionPath = BranchPathUtils.createPath(branchPath, versionId);

		getComponent(versionPath, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveTime.format(DateTimeFormatter.BASIC_ISO_DATE)));
	}

	@Test
	public void createExtensionVersion02() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		branching.createBranch(a).statusCode(201);

		String conceptId = createNewConcept(a);

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);

		String codeSystemId = "SNOMEDCT-CV2";
		createCodeSystem(a, codeSystemId).statusCode(201);

		getComponent(a, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("released", equalTo(false));

		String versionId = "v1";
		LocalDate effectiveTime = LocalDate.now();
		createVersion(codeSystemId, versionId, effectiveTime).statusCode(201);

		assertThat(getVersion(codeSystemId, versionId).getEffectiveTime()).isEqualTo(effectiveTime);

		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptId).statusCode(404);
		getComponent(a, SnomedComponentType.CONCEPT, conceptId).statusCode(200)
			.body("released", equalTo(true))
			.body("effectiveTime", equalTo(effectiveTime.format(DateTimeFormatter.BASIC_ISO_DATE)));
	}

}
