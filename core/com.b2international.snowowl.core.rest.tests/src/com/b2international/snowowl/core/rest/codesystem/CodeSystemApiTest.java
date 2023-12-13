/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.rest.codesystem;

import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.rest.BaseResourceApiTest;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests;
import com.b2international.snowowl.test.commons.rest.BundleApiAssert;

/**
 * @since 1.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CodeSystemApiTest extends BaseResourceApiTest {

	private static final Json SNOMED = Json.object(
		ResourceDocument.Fields.ID, "SNOMEDCT",
		ResourceDocument.Fields.TITLE, "SNOMED CT",
		ResourceDocument.Fields.URL, SnomedTerminologyComponentConstants.SNOMED_URI_SCT,
		ResourceDocument.Fields.TOOLING_ID, SnomedTerminologyComponentConstants.TOOLING_ID,
		ResourceDocument.Fields.OID, SnomedContentRule.SNOMEDCT_OID,
		ResourceDocument.Fields.BUNDLE_ID, IComponent.ROOT_ID
	);
	
	@Test
	public void codesystem01_NoCodeSystemsPresent() {
		assertCodeSystemSearch()
			.statusCode(200)
			.body("items", iterableWithSize(0));
	}

	@Test
	public void codesystem02_CreateEmptyBody() throws Exception {
		assertCodeSystemCreate(
			Json.object(
					ResourceDocument.Fields.ID, ""
			)
		).statusCode(400).body("violations", hasItem("'id' may not be empty (was '')"));
	}
	
	@Test
	public void codesystem03_CreateNoTitle() throws Exception {
		assertCodeSystemCreate(
			Json.object(
				ResourceDocument.Fields.ID, "SNOMEDCT"
			)
		).statusCode(400).body("violations", hasItem("'title' may not be empty (was 'null')"));
	}
	
	@Test
	public void codesystem04_CreateNoUrl() throws Exception {
		assertCodeSystemCreate(
			Json.object(
				ResourceDocument.Fields.ID, "SNOMEDCT",
				ResourceDocument.Fields.TITLE, "SNOMED CT"
			)
		).statusCode(400).body("violations", hasItem("'url' may not be empty (was 'null')"));
	}
	
	@Test
	public void codesystem05_CreateNoToolingId() throws Exception {
		assertCodeSystemCreate(
			Json.object(
				ResourceDocument.Fields.ID, "SNOMEDCT",
				ResourceDocument.Fields.TITLE, "SNOMED CT",
				ResourceDocument.Fields.URL, "http://snomed.info/sct"
			)
		).statusCode(400).body("violations", hasItem("'toolingId' may not be empty (was 'null')"));
	}
	
	@Test
	public void codesystem06_CreateUnknownToolingId() throws Exception {
		assertCodeSystemCreate(SNOMED.with(ResourceDocument.Fields.TOOLING_ID, "unknown"))
			.statusCode(400)
			.body("message", containsString("ToolingId 'unknown' is not supported by this server."));
	}
	
	@Test
	public void codesystem07_Create() throws Exception {
		assertCodeSystemCreate(SNOMED)
			.statusCode(201)
			.header("Location", containsString("/codesystems/SNOMEDCT"));
	}
	
	
	@Test
	public void codesystem08_CreateNonUniqueId() throws Exception {
		assertCodeSystemCreate(SNOMED)
			.statusCode(201);
		
		assertCodeSystemCreate(SNOMED)
			.statusCode(409)
			.body("message", containsString("Resource with identifier 'SNOMEDCT' already exists."));
	}
	
	@Test
	public void codesystem09_CreateNonUniqueUrl() throws Exception {
		assertCodeSystemCreate(SNOMED)
			.statusCode(201);
		
		assertCodeSystemCreate(SNOMED.with(ResourceDocument.Fields.ID, "SNOMEDCT-other"))
			.statusCode(409)
			.body("message", containsString("Resource with url 'http://snomed.info/sct' already exists."));
	}
	
	@Test
	public void codesystem10_CreateNonUniqueOid() throws Exception {
		assertCodeSystemCreate(SNOMED)
			.statusCode(201);
		
		assertCodeSystemCreate(SNOMED.with(ResourceDocument.Fields.ID, "SNOMEDCT-other").with("url", SnomedTerminologyComponentConstants.SNOMED_URI_DEV))
			.statusCode(409)
			.body("message", containsString("Resource with oid '2.16.840.1.113883.6.96' already exists."));
	}
	
	@Test
	public void codesystem11_SearchById() throws Exception {
		assertCodeSystemCreate(SNOMED)
			.statusCode(201);
		assertCodeSystemCreate(prepareCodeSystemCreateRequestBody(UUID.randomUUID().toString())).statusCode(201);
		assertCodeSystemSearch(
			Map.of(
				ResourceDocument.Fields.ID, "SNOMEDCT"
			)
		).statusCode(200).body("items", iterableWithSize(1));
	}
	
	@Test
	public void codesystem12_SearchByTitle() throws Exception {
		assertCodeSystemCreate(SNOMED)
			.statusCode(201);
		assertCodeSystemCreate(prepareCodeSystemCreateRequestBody(UUID.randomUUID().toString())).statusCode(201);
		assertCodeSystemSearch(
			Map.of(
				ResourceDocument.Fields.TITLE, "SNOMED CT"
			)
		).statusCode(200).body("items", iterableWithSize(1));
	}
	
	@Test
	public void codesystem13_SearchByOid() throws Exception {
		assertCodeSystemCreate(SNOMED)
			.statusCode(201);
		assertCodeSystemCreate(prepareCodeSystemCreateRequestBody(UUID.randomUUID().toString())).statusCode(201);
		assertCodeSystemSearch(
			Map.of(
				ResourceDocument.Fields.OID, "2.16.840.1.113883.6.96"
			)
		).statusCode(200).body("items", iterableWithSize(1));
	}
	
	// TODO add other searches here
	
	@Test
	public void codesystem14_CreateWithSettings() throws Exception {
		final String codeSystemId = "cs6";
		final Json body = prepareCodeSystemCreateRequestBody(codeSystemId)
			.with(Json.object(
				"settings", Json.object(
					SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000198",
					SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("123456781000198103", "876543211000198107"),
					CodeSystem.CommonSettings.LOCALES, List.of("en-us", "en-gb")
				)
			));

		assertCodeSystemCreate(body).statusCode(201);
		
		assertCodeSystemGet(codeSystemId)
			.statusCode(200)
			.body("settings." + SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, equalTo("1000198"))
			.body("settings." + SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, equalTo(List.of("123456781000198103", "876543211000198107")))
			.body("settings." + CodeSystem.CommonSettings.LOCALES, equalTo(List.of("en-us", "en-gb")));
	}
	
	@Test
	public void codesystem15_CreateWithDedicatedBranchPath() {
		final String codeSystemId = "cs10";
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, codeSystemId);

		try {
			
			RepositoryRequests.branching()
				.prepareGet(expectedBranchPath)
				.build(TOOLING_ID)
				.execute(Services.bus())
				.getSync();
			
			fail("Branch " + expectedBranchPath + " already exists");
			
		} catch (NotFoundException expected) {
			// Branch does not exist, continue
		}
		
		final Json requestBody = prepareCodeSystemCreateRequestBody(codeSystemId).without("branchPath");
		
		createCodeSystem(requestBody);
		assertCodeSystemGet(codeSystemId).statusCode(200);
		
		try {
			
			// Check if the branch has been created
			RepositoryRequests.branching()
				.prepareGet(expectedBranchPath)
				.build(TOOLING_ID)
				.execute(Services.bus())
				.getSync();
			
		} catch (NotFoundException e) {
			fail("Branch " + expectedBranchPath + " did not get created as part of code system creation");
		}
	}
	
	@Test
	public void codesystem17_UpdateTitle() {
		final String codeSystemId = "cs2";
		final Map<String, Object> requestBody = prepareCodeSystemCreateRequestBody(codeSystemId);
		createCodeSystem(requestBody);
		
		final Json updateRequestBody = Json.object("title", "updated name");
		
		assertCodeSystemUpdated(codeSystemId, updateRequestBody);
		assertCodeSystemHasAttributeValue(codeSystemId, "title", "updated name");
	}

	// TODO add other property update tests as well
	
	@Test
	public void codesystem18_UpdateSettings() {
		final String codeSystemId = "cs5";
		
		final Json requestBody = prepareCodeSystemCreateRequestBody(codeSystemId).with(
				"settings", Map.of(
					SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000198",
					SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("1234567891000198103", "9876543211000198107"),
					"locked", true)
				);
		assertCodeSystemCreate(requestBody).statusCode(201);
		
		final Json updateRequestBody = Json.object("settings", Json.object(
			SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000197"
		).with("locked", null));
		assertCodeSystemUpdated(codeSystemId, updateRequestBody);
		assertCodeSystemGet(codeSystemId)
			.statusCode(200)
			.and().body("settings." + SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, equalTo("1000197"))
			.and().body("settings." + SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, equalTo(List.of("1234567891000198103", "9876543211000198107")))
			.and().body("settings", not(hasKey("locked")));
	}
	
	@Test
	public void codesystem19_UpdateLocales() {
		final String codeSystemId = "cs9";
		
		createCodeSystem(Json.assign(
			prepareCodeSystemCreateRequestBody(codeSystemId), 
			Json.object("settings", Json.object(
				CodeSystem.CommonSettings.LOCALES, AcceptLanguageHeader.parseHeader("en-x-123456781000198103,en-x-876543211000198107")
			))
		));
		
		assertCodeSystemUpdated(codeSystemId, 
			Json.object("settings", Json.object(
				"locales", List.of("en-us", "en-gb")
			))
		);
		
		assertCodeSystemHasAttributeValue(codeSystemId, "settings", Json.object("locales", List.of("en-us", "en-gb")));
	}
	
	@Test
	public void codesystem20_UpdateInvalidBranchPath() {
		final String codeSystemId = "cs3";
		final Map<String, Object> requestBody = prepareCodeSystemCreateRequestBody(codeSystemId);
		
		createCodeSystem(requestBody);
		
		final Json updateRequestBody = Json.object("branchPath", "non-existent-branch-path");
		
		assertCodeSystemUpdatedWithStatus(codeSystemId, updateRequestBody, 400);
	}
	
	@Test
	public void codesystem22_Delete() throws Exception {
		final String codeSystemId = "cs22";
		createCodeSystem(prepareCodeSystemCreateRequestBody(codeSystemId));
		assertCodeSystemGet(codeSystemId).statusCode(200);
		
		assertCodeSystemDelete(codeSystemId).statusCode(204);
		assertCodeSystemGet(codeSystemId).statusCode(404);
		
		// Check if the branch has been deleted
		String branch = Branch.get(Branch.MAIN_PATH, codeSystemId);
		assertThat(RepositoryRequests.branching()
			.prepareGet(branch)
			.build(TOOLING_ID)
			.execute(Services.bus())
			.getSync()
			.isDeleted()).isTrue();
	}
	
	@Test
	public void codesystem23_DeleteVersioned() throws Exception {
		final String codeSystemId = "cs23";
		createCodeSystem(prepareCodeSystemCreateRequestBody(codeSystemId));
		assertCodeSystemGet(codeSystemId).statusCode(200);
		
		// version codesystem
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), "v1", LocalDate.now().toString());
		assertVersionCreated(versionRequestBody).statusCode(201);
		
		assertCodeSystemDelete(codeSystemId).statusCode(204);
		assertCodeSystemGet(codeSystemId).statusCode(404);
		
		String branch = Branch.get(Branch.MAIN_PATH, codeSystemId);
		assertThat(RepositoryRequests.branching()
				.prepareGet(branch)
				.build(TOOLING_ID)
				.execute(Services.bus())
				.getSync()
				.isDeleted()).isTrue();
			
		CodeSystemVersionRestRequests.assertGetVersion(codeSystemId, "v1").statusCode(404);
		
		// Check if the branch has been deleted
		String versionBranch = Branch.get(Branch.MAIN_PATH, codeSystemId, "v1");
		assertThat(RepositoryRequests.branching()
				.prepareGet(versionBranch)
				.build(TOOLING_ID)
				.execute(Services.bus())
				.getSync()
				.isDeleted()).isTrue();
	}
	
	@Test
	public void codesystem24_UpdateBundleId() {
		final String codeSystemId = "cs24";
		final Map<String, Object> requestBody = prepareCodeSystemCreateRequestBody(codeSystemId);
		createCodeSystem(requestBody);
		
		final String bundleId = IDs.base62UUID();
		BundleApiAssert.assertCreate(BundleApiAssert.prepareBundleCreateRequestBody(bundleId))
			.statusCode(201);
		
		final Json updateRequestBody = Json.object("bundleId", bundleId);
		
		assertCodeSystemUpdated(codeSystemId, updateRequestBody);
		assertCodeSystemHasAttributeValue(codeSystemId, "bundleId", bundleId);
	}

	@Test
	public void codesystem25_UpdateBundleIdNotExist() {
		final String codeSystemId = "cs25";
		final Map<String, Object> requestBody = prepareCodeSystemCreateRequestBody(codeSystemId);
		createCodeSystem(requestBody);

		final Json updateRequestBody = Json.object("bundleId", "not-existing-bundle");
		
		assertCodeSystemNotUpdated(codeSystemId, updateRequestBody);
	}
	
	@Test
	public void codesystem26_CreateVersionIncorrectEffectiveTime() {
		final String codeSystemId = "cs26";
		final Map<String, Object> requestBody = prepareCodeSystemCreateRequestBody(codeSystemId);
		createCodeSystem(requestBody);
		
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), "v1", "2020-04-15")).statusCode(201);
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), "v2", "2020-04-14")).statusCode(400);
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), "v3", "2020-04-15")).statusCode(400);
	}

	@Test
	public void codesystem27_GetWithTimestamp() {
		final String codeSystemId = "cs27";
		final Map<String, Object> requestBody = prepareCodeSystemCreateRequestBody(codeSystemId);
		createCodeSystem(requestBody);
		
		final CodeSystem createdCodeSystem = assertCodeSystemGet(codeSystemId)
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		
		assertCodeSystemUpdated(codeSystemId, Json.object("copyright", "Updated copyright"));

		final CodeSystem updatedCodeSystem = assertCodeSystemGet(codeSystemId)
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		
		// try to retrieve code system state before it was created
		assertCodeSystemGet(codeSystemId, createdCodeSystem.getCreatedAt() - 1L)
			.statusCode(404);
		
		// retrieve the state at creation time
		final CodeSystem codeSystem1 = assertCodeSystemGet(codeSystemId, createdCodeSystem.getCreatedAt())
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		
		assertEquals(createdCodeSystem.getCopyright(), codeSystem1.getCopyright());
		
		// retrieve the state at the point in time when the update happened
		final CodeSystem codeSystem2 = assertCodeSystemGet(codeSystemId, updatedCodeSystem.getUpdatedAt())
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		
		assertEquals("Updated copyright", codeSystem2.getCopyright());
		
		// look into the future a small amount as well
		final CodeSystem codeSystem3 = assertCodeSystemGet(codeSystemId, updatedCodeSystem.getUpdatedAt() + 1L)
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		
		assertEquals("Updated copyright", codeSystem3.getCopyright());
		
		// check versioned resource access
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), "v1", "2020-04-15")).statusCode(201);
		
		// update copyright after versioning to have a different latest revision
		assertCodeSystemUpdated(codeSystemId, Json.object("copyright", "Updated copyright after versioning"));

		// get the versioned state
		final CodeSystem v1CodeSystemState = assertCodeSystemGet(codeSystemId + "/v1")
				.statusCode(200)
				.extract()
				.as(CodeSystem.class);
		
		// verify that the copyright is the old version
		assertEquals("Updated copyright", v1CodeSystemState.getCopyright());
	}
	
	@Test
	public void codesystem28_SearchWithTimestamp() throws Exception {
		createCodeSystem(prepareCodeSystemCreateRequestBody("cs28_1"));
		createCodeSystem(prepareCodeSystemCreateRequestBody("cs28_2"));
		createCodeSystem(prepareCodeSystemCreateRequestBody("cs28_3"));

		final long timestamp1 = getCodeSystemCreatedAt("cs28_1");
		final long timestamp2 = getCodeSystemCreatedAt("cs28_2");
		final long timestamp3 = getCodeSystemCreatedAt("cs28_3");
		
		assertCodeSystemSearch(Map.of("timestamp", timestamp1 - 1L)).statusCode(200).body("items", empty());
		assertCodeSystemSearch(Map.of("timestamp", timestamp1)).statusCode(200).body("items.id", containsInAnyOrder("cs28_1"));
		assertCodeSystemSearch(Map.of("timestamp", timestamp2 - 1L)).statusCode(200).body("items.id", containsInAnyOrder("cs28_1"));
		assertCodeSystemSearch(Map.of("timestamp", timestamp2)).statusCode(200).body("items.id", containsInAnyOrder("cs28_1", "cs28_2"));
		assertCodeSystemSearch(Map.of("timestamp", timestamp3 - 1L)).statusCode(200).body("items.id", containsInAnyOrder("cs28_1", "cs28_2"));
		assertCodeSystemSearch(Map.of("timestamp", timestamp3)).statusCode(200).body("items.id", containsInAnyOrder("cs28_1", "cs28_2", "cs28_3"));
		assertCodeSystemSearch(Map.of("timestamp", timestamp3 + 1L)).statusCode(200).body("items.id", containsInAnyOrder("cs28_1", "cs28_2", "cs28_3"));
	}
	
	@Test
	public void codesystem29_VersionWithReservedBranchName() throws Exception {
		String codeSystemId = createCodeSystem(prepareCodeSystemCreateRequestBody("cs29_1"));
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), ResourceURI.HEAD, EffectiveTimes.today()))
			.statusCode(400)
			.body("message", containsString("Version 'HEAD' is a reserved alias or branch name."));
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), ResourceURI.LATEST, EffectiveTimes.today()))
			.statusCode(400)
			.body("message", containsString("Version 'LATEST' is a reserved alias or branch name."));
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), ResourceURI.NEXT, EffectiveTimes.today()))
			.statusCode(400)
			.body("message", containsString("Version 'NEXT' is a reserved alias or branch name."));
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), ResourceURI.HEAD.toLowerCase(), EffectiveTimes.today()))
			.statusCode(400)
			.body("message", containsString("Version 'head' is a reserved alias or branch name."));
	}
	
	@Test
	public void codesystem30_AllowMetadataUpdatesOnRetiredResources() throws Exception {
		String codeSystemId = createCodeSystem(prepareCodeSystemCreateRequestBody("cs30"));
		assertCodeSystemUpdated(codeSystemId, Map.of(
			"status", Resource.RETIRED_STATUS
		));
		assertCodeSystemGet(codeSystemId)
			.statusCode(200)
			.body("status", equalTo(Resource.RETIRED_STATUS));
		
		// update copyright and add it to a bundle
		final String bundleId = IDs.base62UUID();
		BundleApiAssert.assertCreate(BundleApiAssert.prepareBundleCreateRequestBody(bundleId))
			.statusCode(201);
		
		assertCodeSystemUpdated(codeSystemId, Map.of(
			"copyright", "MIT License",
			"bundleId", bundleId
		));
		assertCodeSystemGet(codeSystemId)
			.statusCode(200)
			.body("copyright", equalTo("MIT License"))
			.body("bundleId", equalTo(bundleId));
	}
	
	@Test
	public void codesystem31_DisallowVersioningOfRetiredResources() throws Exception {
		String codeSystemId = createCodeSystem(prepareCodeSystemCreateRequestBody("cs31"));
		assertCodeSystemUpdated(codeSystemId, Map.of(
			"status", Resource.RETIRED_STATUS
		));
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), "v1", EffectiveTimes.today()))
			.statusCode(400)
			.body("message", containsString("Resource 'Code System cs31' cannot be versioned in its current status 'retired'"));
	}
	
	@Test
	public void codesystem32_NonexistentBundleErrorShouldNotCreateUnderlyingBranchAsSideEffect() throws Exception {
		// this should report an error
		assertCodeSystemCreate(prepareCodeSystemCreateRequestBody("cs32").with("bundleId", "nonexistent")).statusCode(400);
		// corresponding tooling branch should NOT be present
		Branches branches = RepositoryRequests.branching()
				.prepareSearch()
				.one()
				.filterById("MAIN/cs32")
				.build(TOOLING_ID)
				.execute(Services.bus())
				.getSync();
		assertThat(branches).isEmpty();
	}
	
	@Test
	public void codesystem33_AllowDraftResourcesToBeQueriedWithLATEST() throws Exception {
		assertCodeSystemCreate(prepareCodeSystemCreateRequestBody("cs33").with("status", "draft")).statusCode(201);
		// this should proceed without any errors from now on, if it throws an error then that is a failure of the test
		CodeSystemRequests.prepareSearchConcepts()
			.setLimit(0)
			.filterByCodeSystemUri(CodeSystem.uri("cs33", ResourceURI.LATEST))
			.buildAsync()
			.execute(Services.bus())
			.getSync();
	}
	
	@Test
	public void codesystem34_DisallowUsingSpecialTildeCharacterInResourceIds() throws Exception {
		assertCodeSystemCreate(prepareCodeSystemCreateRequestBody("cs~34"))
			.statusCode(400)
			.body("message", equalTo("CodeSystem.id 'cs~34' uses an illegal character. Allowed characters are 'A-Za-z0-9-_'."));
	}
	
	private long getCodeSystemCreatedAt(final String id) {
		return assertCodeSystemGet(id)
			.statusCode(200)
			.extract()
			.jsonPath()
			.getLong("createdAt");
	}
}
