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
package com.b2international.snowowl.snomed.api.rest.io;

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
public class SnomedImportApiTest extends AbstractSnomedImportApiTest {

	private void assertImportConfigurationCreationFails(final Map<?, ?> importConfiguration) {
		whenCreatingImportConfiguration(importConfiguration)
		.then().assertThat().statusCode(400)
		.and().body("status", equalTo(400));
	}

	private ValidatableResponse assertImportConfigurationStatus(final String importId, final int expectedStatus) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.when().get("/imports/{id}", importId)
				.then().assertThat().statusCode(expectedStatus);
	}

	private String assertImportConfigurationExistsAfterCreation() {
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", "MAIN")
				.put("languageRefSetId", Concepts.REFSET_LANGUAGE_TYPE_UK)
				.put("createVersions", false)
				.build();

		final String importId = assertImportConfigurationCanBeCreated(importConfiguration);
		assertImportConfigurationStatus(importId, 200).and().body("status", equalTo("WAITING_FOR_FILE"));
		return importId;
	}

	@Test
	public void createImportConfiguration() {
		assertImportConfigurationExistsAfterCreation();
	}

	@Test
	public void deleteImportConfiguration() {
		final String importId = assertImportConfigurationExistsAfterCreation();

		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when().delete("/imports/{id}", importId)
		.then().assertThat().statusCode(204);

		assertImportConfigurationStatus(importId, 404);
	}

	@Test
	public void noVersionsAllowedOnBranch() {
		givenBranchWithPath(testBranchPath);

		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", testBranchPath.getPath())
				.put("languageRefSetId", Concepts.REFSET_LANGUAGE_TYPE_UK)
				.put("createVersions", true)
				.build();

		assertImportConfigurationCreationFails(importConfiguration);
	}
}
