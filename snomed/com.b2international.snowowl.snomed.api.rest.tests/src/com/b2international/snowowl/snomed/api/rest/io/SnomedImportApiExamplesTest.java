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

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentActive;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertConceptExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertConceptNotExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertDescriptionExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertDescriptionNotExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertPreferredTermEquals;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertRelationshipExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertRelationshipNotExists;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.jayway.restassured.http.ContentType;

/**
 * @since 2.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedImportApiExamplesTest extends AbstractSnomedImportApiTest {

	private static final Set<String> FINISH_STATES = ImmutableSet.of("COMPLETED", "FAILED");

	private static final long POLL_INTERVAL = TimeUnit.SECONDS.toMillis(1L);

	private static final long POLL_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);

	@Override
	protected IBranchPath createRandomBranchPath() {
		return BranchPathUtils.createMainPath();
	}
	
	private void assertImportFileCanBeUploaded(final String importId, final String importFile) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API).with().multiPart(new File(PlatformUtil.toAbsolutePath(getClass(), importFile)))
		.when().post("/imports/{id}/archive", importId)
		.then().assertThat().statusCode(204);
	}

	private void assertImportCompletes(final String importId) {
		final long endTime = System.currentTimeMillis() + POLL_TIMEOUT;

		long currentTime;
		String currentStatus;

		do {

			try {
				Thread.sleep(POLL_INTERVAL);
			} catch (final InterruptedException e) {
				fail(e.toString());
			}

			currentStatus = givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
					.when().get("/imports/{id}", importId)
					.then().assertThat().statusCode(200)
					.and().extract().response().body().path("status");

			currentTime = System.currentTimeMillis();

		} while (!FINISH_STATES.contains(currentStatus) && currentTime < endTime);

		assertEquals("End state should be COMPLETED.", "COMPLETED", currentStatus);
	}

	private void assertImportFileCanBeImported(final String importFile) {

		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", testBranchPath.getPath())
				.put("languageRefSetId", Concepts.REFSET_LANGUAGE_TYPE_UK)
				.put("createVersions", true)
				.build();

		final String importId = assertImportConfigurationCanBeCreated(importConfiguration);
		assertImportFileCanBeUploaded(importId, importFile);
		assertImportCompletes(importId);
	}

	@Test
	public void import01NewConcept() {
		assertConceptNotExists(testBranchPath, "63961392103");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150131_new_concept.zip");
		assertConceptExists(testBranchPath, "63961392103");
		assertPreferredTermEquals(testBranchPath, "63961392103", "13809498114");
		givenAuthenticatedRequest("/admin").when().get("/codesystems/{shortName}/versions/{version}", "SNOMEDCT", "2015-01-31").then().statusCode(200);
	}

	@Test
	public void import02NewDescription() {
		assertDescriptionNotExists(testBranchPath, "11320138110");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150201_new_description.zip");
		assertDescriptionExists(testBranchPath, "11320138110");
		givenAuthenticatedRequest("/admin").when().get("/codesystems/{shortName}/versions/{version}", "SNOMEDCT", "2015-02-01").then().statusCode(200);
	}

	@Test
	public void import03NewRelationship() {
		assertRelationshipNotExists(testBranchPath, "24088071128");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150202_new_relationship.zip");
		assertRelationshipExists(testBranchPath, "24088071128");
		givenAuthenticatedRequest("/admin").when().get("/codesystems/{shortName}/versions/{version}", "SNOMEDCT", "2015-02-02").then().statusCode(200);
	}

	@Test
	public void import04NewPreferredTerm() {
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150203_change_pt.zip");
		assertPreferredTermEquals(testBranchPath, "63961392103", "11320138110");
		givenAuthenticatedRequest("/admin").when().get("/codesystems/{shortName}/versions/{version}", "SNOMEDCT", "2015-02-03").then().statusCode(200);
	}

	@Test
	public void import05ConceptInactivation() {
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150204_inactivate_concept.zip");
		assertComponentActive(testBranchPath, SnomedComponentType.CONCEPT, "63961392103", false);
	}
	
	@Test
	public void import06IndexInitBug_ImportSameNewConceptWithAdditionalDescriptionShouldNotFail() throws Exception {
		assertConceptExists(testBranchPath, "63961392103").body("active", CoreMatchers.equalTo(false));
		assertPreferredTermEquals(testBranchPath, "63961392103", "11320138110");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150131_index_init_bug.zip");
		assertConceptExists(testBranchPath, "63961392103").body("active", CoreMatchers.equalTo(false));
		assertPreferredTermEquals(testBranchPath, "63961392103", "11320138110");
	}
	
	@Test
	public void import07InvalidBranchPath() {
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.FULL.name())
				.put("branchPath", "DK")
				.put("languageRefSetId", Concepts.REFSET_LANGUAGE_TYPE_UK)
				.put("createVersions", true)
				.build();
		
		final String importId = assertImportConfigurationCanBeCreated(importConfiguration);
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API).with()
			.multiPart(new File(PlatformUtil.toAbsolutePath(getClass(), "SnomedCT_Release_INT_20150131_new_concept.zip")))
			.when().post("/imports/{id}/archive", importId)
			.then().assertThat().statusCode(400);
	}
	
	@Test
	public void import08NewExtensionConceptOnNestedBranch() {
		final IBranchPath nestedBranch = createNestedBranch(testBranchPath, "dk");
		createCodeSystem(nestedBranch.getPath());
		assertConceptNotExists(nestedBranch, "63961392103");
		
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", nestedBranch.getPath())
				.put("languageRefSetId", Concepts.REFSET_LANGUAGE_TYPE_UK)
				.put("createVersions", true)
				.build();

		final String importId = assertImportConfigurationCanBeCreated(importConfiguration);
		assertImportFileCanBeUploaded(importId, "SnomedCT_Release_INT_20150131_new_concept.zip");
		assertImportCompletes(importId);
		
		assertConceptExists(nestedBranch, "63961392103");
		assertPreferredTermEquals(nestedBranch, "63961392103", "13809498114");
		givenAuthenticatedRequest("/admin")
				.when().get("/codesystems/{shortName}/versions/{version}", "dk", "2015-01-31")
				.then().statusCode(200);
	}
	
	private void createCodeSystem(final String branchPath) {
		final ImmutableMap<String, String> extension = ImmutableMap.<String, String>builder()
				.put("baseCodeSystemOID", SnomedTerminologyComponentConstants.SNOMED_INT_OID)
				.put("releaseType", "DELTA")
				.build();
		
		final ImmutableMap<Object, Object> requestBody = ImmutableMap.builder()
				.put("name", "CodeSystem")
				.put("branchPath", branchPath)
				.put("shortName", "dk")
				.put("citation", "citation")
				.put("iconPath", "icons/snomed.png")
				.put("repositoryUuid", "snomedStore")
				.put("terminologyId", "concept")
				.put("oid", "1")
				.put("primaryLanguage", "ENG")
				.put("organizationLink", "link")
				.put("extension", extension)
				.build();
		
		givenAuthenticatedRequest("/admin")
			.with().contentType(ContentType.JSON)
			.and().body(requestBody)
			.when().post("/codesystems")
			.then().assertThat().statusCode(201);
	}
	
}
