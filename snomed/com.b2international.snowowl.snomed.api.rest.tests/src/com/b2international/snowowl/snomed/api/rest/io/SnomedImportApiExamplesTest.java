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

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.assertBranchCreated;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.joinPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.jayway.restassured.response.Response;

/**
 * @since 2.0
 */
public class SnomedImportApiExamplesTest extends AbstractSnomedImportApiTest {
	
	private static final Set<String> FINISH_STATES = ImmutableSet.of("COMPLETED", "FAILED");
	private static final long POLL_INTERVAL = TimeUnit.SECONDS.toMillis(1L);
	private static final long POLL_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);
	
	private void assertImportFileCanBeUploaded(final String importId, final String importFile) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.with()
			.multiPart(new File(PlatformUtil.toAbsolutePath(getClass(), importFile)))
		.when()
			.post("/imports/{id}/archive", importId)
		.then()
		.assertThat()
			.statusCode(204);
	}

	private void assertImportCompletes(final String importId) {
		
		final long endTime = System.currentTimeMillis() + POLL_TIMEOUT;

		Response pollResponse;
		long currentTime;
		
		do {
			
			try {
				Thread.sleep(POLL_INTERVAL);
			} catch (final InterruptedException e) {
				fail(e.toString());
			}
			
			pollResponse = givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.when()
				.get("/imports/{id}", importId);
			
			pollResponse
			.then()
			.assertThat()
				.statusCode(200);
			
			currentTime = System.currentTimeMillis();
			
		} while (!FINISH_STATES.contains(pollResponse.getBody().path("status")) && currentTime < endTime);
		
		pollResponse
		.then()
		.assertThat()
			.body("status", equalTo("COMPLETED"));
	}

	private void assertImportFileCanBeImported(final String importFile) {
		final Map<?, ?> importConfiguration = ImmutableMap.of(
			"type", Rf2ReleaseType.DELTA.name(),
			"branchPath", joinPath("MAIN", branchName),
			"languageRefSetId", Concepts.REFSET_LANGUAGE_TYPE_UK,
			"createVersions", false
		);
		
		final String importId = assertImportConfigurationCanBeCreated(importConfiguration);
		assertImportFileCanBeUploaded(importId, importFile);
		assertImportCompletes(importId);
	}

	@Test
	public void importNewConcept() {
		assertBranchCreated(branchPath);
		assertConceptNotExists("63961392103", "MAIN", branchName);
		
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150131_new_concept.zip");
		
		assertConceptExists("63961392103", "MAIN", branchName);
	}

	@Test
	public void importNewDescription() {
		assertBranchCreated(branchPath);
		assertDescriptionNotExists("11320138110", "MAIN", branchName);
		
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150131_new_concept.zip");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150201_new_description.zip");
		
		assertDescriptionExists("11320138110", "MAIN", branchName);
	}
	
	@Test
	public void importNewRelationship() {
		assertBranchCreated(branchPath);
		assertRelationshipNotExists("24088071128", "MAIN", branchName);

		assertImportFileCanBeImported("SnomedCT_Release_INT_20150131_new_concept.zip");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150202_new_relationship.zip");
		
		assertRelationshipExists("24088071128", "MAIN", branchName);
	}

	@Test
	public void importNewPreferredTerm() {
		assertBranchCreated(branchPath);

		assertImportFileCanBeImported("SnomedCT_Release_INT_20150131_new_concept.zip");
		assertPreferredTermEquals("63961392103", "13809498114", "MAIN", branchName);
		
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150201_new_description.zip");
		assertPreferredTermEquals("63961392103", "13809498114", "MAIN", branchName);
		
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150203_change_pt.zip");
		assertPreferredTermEquals("63961392103", "11320138110", "MAIN", branchName);
	}
	
	@Test
	public void importConceptInactivation() {
		assertBranchCreated(branchPath);
		
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150131_new_concept.zip");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150201_new_description.zip");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150202_new_relationship.zip");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150203_change_pt.zip");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150204_inactivate_concept.zip");
		
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when()
			.get("/{path}/concepts/{conceptId}", joinPath("MAIN", branchName), "63961392103")
		.then()
		.assertThat()
			.statusCode(200)
		.and()
			.body("active", equalTo(false));
	}
}
