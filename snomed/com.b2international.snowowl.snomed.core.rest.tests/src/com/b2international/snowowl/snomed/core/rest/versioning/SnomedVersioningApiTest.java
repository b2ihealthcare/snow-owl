/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.versioning;

import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.assertGetVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getNextAvailableEffectiveDate;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.getVersion;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Map;

import org.elasticsearch.core.List;
import org.junit.Test;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.google.common.collect.ImmutableMap;

import io.restassured.http.ContentType;

/**
 * @since 2.0
 */
public class SnomedVersioningApiTest extends AbstractSnomedApiTest {

	private static final String INT_CODESYSTEM = SnomedContentRule.SNOMEDCT.getResourceId();
	
	@Test
	public void getNonExistentVersion() {
		assertGetVersion(INT_CODESYSTEM, "nonexistent-version-id").statusCode(404);
	}
	
	@Test
	public void createVersion_IncorrectVersionId_Percent() {
		createVersion(INT_CODESYSTEM, "%my-new-version%", getNextAvailableEffectiveDate(INT_CODESYSTEM))
			.statusCode(400)
			.body("message", containsString("contains invalid characters"));
	}

	@Test
	public void createVersionWithoutDescription() {
		createVersion(INT_CODESYSTEM, "", getNextAvailableEffectiveDate(INT_CODESYSTEM)).statusCode(400);
	}

	@Test
	public void createVersionWithNonLatestEffectiveDate() {
		createVersion(INT_CODESYSTEM, "not-latest-effective-time", LocalDate.parse("2002-01-01")).statusCode(400);
	}

	@Test
	public void createRegularVersion() {
		createVersion(INT_CODESYSTEM, "regular-version", getNextAvailableEffectiveDate(INT_CODESYSTEM)).statusCode(201);
		assertGetVersion(INT_CODESYSTEM, "regular-version").statusCode(200);
	}
	
	@Test
	public void createRegularVersionWithAuthor() {
		String author = "info@b2international.com";
		Map<String, String> headers = Map.of(AbstractRestService.X_AUTHOR, "info@b2international.com");
		createVersion(INT_CODESYSTEM, "version-with-author", getNextAvailableEffectiveDate(INT_CODESYSTEM), headers).statusCode(201);
		assertGetVersion(INT_CODESYSTEM, "version-with-author").statusCode(200);
		String commitComment = "Version 'codesystems/SNOMEDCT' as of 'version-with-author'";
		CommitInfos commits = givenAuthenticatedRequest("/resources/commits")
			.contentType(ContentType.JSON)
			.params(ImmutableMap.of("comment", commitComment))
			.get()
			.as(CommitInfos.class);
		assertEquals(author, commits.first().get().getAuthor());
	}

	@Test
	public void createVersionWithSameNameAsBranch() {
		createVersion(INT_CODESYSTEM, "SnomedVersioningApiTest", getNextAvailableEffectiveDate(INT_CODESYSTEM)).statusCode(409);
	}
	
	@Test
	public void forceCreateVersionWithDifferentVersionId() throws Exception {
		final String versionName = "forceCreateVersionWithDifferentVersionId";
		LocalDate versionEffectiveDate = getNextAvailableEffectiveDate(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, versionName, versionEffectiveDate).statusCode(201);
		createVersion(INT_CODESYSTEM, versionName + "-force", versionEffectiveDate, true).statusCode(400);
	}
	
	@Test
	public void forceCreateVersionWithDifferentEffectiveDate() throws Exception {
		final String versionName = "forceCreateVersionWithDifferentEffectiveDate";
		LocalDate versionEffectiveDate = getNextAvailableEffectiveDate(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, versionName, versionEffectiveDate).statusCode(201);
		
		Branch branchToVerify = RepositoryRequests.branching()
				.prepareSearch()
				.filterByParent("MAIN")
				.filterByName(List.of(versionName))
				.setLimit(1)
				.build(SnomedTerminologyComponentConstants.TOOLING_ID)
				.execute(getBus())
				.getSync()
				.first()
				.get();
		
		LocalDate nextEffectiveDate = getNextAvailableEffectiveDate(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, versionName, nextEffectiveDate, true).statusCode(201);
		
		Branch branchAfterForceRecreate = RepositoryRequests.branching()
				.prepareSearch()
				.filterByParent("MAIN")
				.filterByName(List.of(versionName))
				.build(SnomedTerminologyComponentConstants.TOOLING_ID)
				.execute(getBus())
				.getSync()
				.first()
				.get();
		
		// successful force recreation will recreate the branch with new data, should not keep the branch timestamps
		assertThat(branchAfterForceRecreate.baseTimestamp()).isGreaterThan(branchToVerify.baseTimestamp());
		assertThat(branchAfterForceRecreate.headTimestamp()).isNotEqualTo(branchToVerify.headTimestamp());
	}
	
	@Test
	public void forceCreateOlderVersionShouldFail() throws Exception {
		final String olderVersionName = "OldVersion";
		final String newerVersionName = "NewVersion";
		LocalDate oldVersionEffectiveDate = getNextAvailableEffectiveDate(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, olderVersionName, oldVersionEffectiveDate).statusCode(201);
		LocalDate nextEffectiveDate = getNextAvailableEffectiveDate(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, newerVersionName, nextEffectiveDate).statusCode(201);
		
		// get both version branch states for assertion
		Branches branchesToVerify = RepositoryRequests.branching()
			.prepareSearch()
			.filterByParent("MAIN")
			.filterByName(List.of(olderVersionName, newerVersionName))
			.setLimit(2)
			.build(SnomedTerminologyComponentConstants.TOOLING_ID)
			.execute(getBus())
			.getSync();
		
		assertThat(branchesToVerify.getTotal()).isEqualTo(2);
		
		// force creating an older version should fail
		createVersion(INT_CODESYSTEM, olderVersionName, oldVersionEffectiveDate, true)
			.statusCode(400)
			.body("message", containsString("Force creating the latest version requires the same versionId ('NewVersion') to be used."));
		
		Branches branchesAfterForceRecreateAttempt = RepositoryRequests.branching()
				.prepareSearch()
				.filterByParent("MAIN")
				.filterByName(List.of(olderVersionName, newerVersionName))
				.setLimit(2)
				.build(SnomedTerminologyComponentConstants.TOOLING_ID)
				.execute(getBus())
				.getSync();
		
		// after force recreate attempt, there should be 2 version branches still and with the same attributes (not deleted)
		assertThat(branchesAfterForceRecreateAttempt)
			.usingRecursiveFieldByFieldElementComparator()
			.containsAll(branchesToVerify.getItems());
	}
	
	@Test
	public void forceCreateVersionShouldUpdateEffectiveTime() {
		final String versionName = "forceCreateVersionShouldUpdateEffectiveTime";
		ResourceURI codeSystemVersionURI = SnomedContentRule.SNOMEDCT.withPath(versionName);
		
		LocalDate versionEffectiveDate = getNextAvailableEffectiveDate(INT_CODESYSTEM);
		createVersion(INT_CODESYSTEM, versionName, versionEffectiveDate).statusCode(201);
		String conceptId = createConcept(SnomedContentRule.SNOMEDCT, SnomedRestFixtures.childUnderRootWithDefaults());
		
		SnomedConcept afterFailedVersioning = getConcept(SnomedContentRule.SNOMEDCT, conceptId);
		assertEquals(null, afterFailedVersioning.getEffectiveTime());
		
		//Should succeed to recreate version with the force flag set
		createVersion(INT_CODESYSTEM, versionName, versionEffectiveDate, true).statusCode(201);
		SnomedConcept afterForceVersioning = getConcept(codeSystemVersionURI, conceptId);
		assertEquals(versionEffectiveDate, afterForceVersioning.getEffectiveTime());
	}
	
	@Test
	public void createVersionShouldPreserveDocumentPRoperties() {
		final String versionName = "versionToTestDocumentPreservation";
		ResourceURI codeSystemURI = SnomedContentRule.SNOMEDCT;
		ResourceURI codeSystemVersionURI = SnomedContentRule.SNOMEDCT.withPath(versionName);
		
		String conceptId = createConcept(codeSystemURI, SnomedRestFixtures.childUnderRootWithDefaults());
		createMember(codeSystemURI, Map.of(
				"active", true,
				"moduleId", Concepts.MODULE_SCT_CORE,
				"refsetId", Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR,
				"referencedComponentId", conceptId,
				"valueId", Concepts.PENDING_MOVE
			));
		
		SnomedConcepts conceptBeforeVersioning = searchConcepts(codeSystemURI, Map.of(
				"activeMemberOf", REFSET_CONCEPT_INACTIVITY_INDICATOR,
				"id", conceptId), 1);
		assertThat(conceptBeforeVersioning.getTotal()).isEqualTo(1);
		
		createVersion(INT_CODESYSTEM, versionName, getNextAvailableEffectiveDate(INT_CODESYSTEM)).statusCode(201);
		getVersion(INT_CODESYSTEM, versionName);
		
		SnomedConcepts conceptAfterVersioning = searchConcepts(codeSystemVersionURI, Map.of(
				"activeMemberOf", REFSET_CONCEPT_INACTIVITY_INDICATOR,
				"id", conceptId), 1);
		assertThat(conceptAfterVersioning.getTotal()).isEqualTo(1);
	}
		
}
