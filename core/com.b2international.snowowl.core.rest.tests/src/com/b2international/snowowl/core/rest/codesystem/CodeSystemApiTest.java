/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.TOOLING_ID;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemCreate;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemCreated;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemGet;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemHasAttributeValue;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemSearch;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemUpdated;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemUpdatedWithStatus;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertVersionCreated;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.prepareCodeSystemCreateRequestBody;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.prepareVersionCreateRequestBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 1.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CodeSystemApiTest {

	private static final Json SNOMED = Json.object(
		ResourceDocument.Fields.ID, "SNOMEDCT",
		ResourceDocument.Fields.TITLE, "SNOMED CT",
		ResourceDocument.Fields.URL, "http://snomed.info/sct",
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
			Json.object()
		).statusCode(400).body("violations", hasItem("'id' may not be empty (was 'null')"));
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
			.body("message", containsString("Resource with 'SNOMEDCT' identifier already exists."));
	}
	
	@Test
	public void codesystem09_CreateNonUniqueTitle() throws Exception {
		assertCodeSystemCreate(SNOMED)
			.statusCode(201);
		
		assertCodeSystemCreate(SNOMED.with(ResourceDocument.Fields.ID, "SNOMEDCT-other"))
			.statusCode(409)
			.body("message", containsString("Resource with 'SNOMED CT' title already exists."));
	}
	
	@Test
	public void codesystem10_CreateNonUniqueOid() throws Exception {
		assertCodeSystemCreate(SNOMED)
			.statusCode(201);
		
		assertCodeSystemCreate(SNOMED.with(ResourceDocument.Fields.ID, "SNOMEDCT-other").with("title", "SNOMED CT-other"))
			.statusCode(409)
			.body("message", containsString("Resource with '2.16.840.1.113883.6.96' oid already exists."));
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
		
		assertCodeSystemCreated(requestBody);
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
	public void codesystem16_CreateWithExtensionOf() {
		final String parentCodeSystemId = "cs11";
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		assertCodeSystemCreated(parentRequestBody);
		assertCodeSystemGet(parentCodeSystemId).statusCode(200);
		
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "2020-04-15");
		assertVersionCreated(versionRequestBody);

		final String codeSystemId = "cs12";
		
		final Json requestBody = prepareCodeSystemCreateRequestBody(codeSystemId)
				.without("branchPath")
				.with("extensionOf", CodeSystem.uri("cs11/v1"));
		
		assertCodeSystemCreated(requestBody);
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, "cs11", "v1", codeSystemId);
		
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
		assertCodeSystemCreated(requestBody);
		
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
		
		assertCodeSystemCreated(Json.assign(
			prepareCodeSystemCreateRequestBody(codeSystemId), 
			Json.object("settings", Json.object(
				CodeSystem.CommonSettings.LOCALES, ExtendedLocale.parseLocales("en-x-123456781000198103,en-x-876543211000198107")
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
		
		assertCodeSystemCreated(requestBody);
		
		final Json updateRequestBody = Json.object("branchPath", "non-existent-branch-path");
		
		assertCodeSystemUpdatedWithStatus(codeSystemId, updateRequestBody, 400);
	}
	
	@Test
	public void codesystem21_UpdateExtensionOf() {
		final String parentCodeSystemId = "cs13";
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		assertCodeSystemCreated(parentRequestBody);
		assertCodeSystemGet(parentCodeSystemId).statusCode(200);
		
		final Json v3RequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v3", "2020-04-16");
		assertVersionCreated(v3RequestBody);
		final Json v4RequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v4", "2020-04-17");
		assertVersionCreated(v4RequestBody);
		
		final String codeSystemId = "cs14";
		final Json requestBody = prepareCodeSystemCreateRequestBody(codeSystemId)
				.without("branchPath")
				.with("extensionOf", CodeSystem.uri("cs13/v3"));
		
		assertCodeSystemCreated(requestBody);
		assertCodeSystemUpdated(codeSystemId, Json.object("extensionOf", CodeSystem.uri("cs13/v4")));
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, "cs13", "v4", codeSystemId);
		assertCodeSystemHasAttributeValue(codeSystemId, "extensionOf", "codesystems/cs13/v4");
		assertCodeSystemHasAttributeValue(codeSystemId, "branchPath", expectedBranchPath);
	}
	
	@Test
	public void codesystem22_Delete() throws Exception {
		final String codeSystemId = "cs22";
		assertCodeSystemCreated(prepareCodeSystemCreateRequestBody(codeSystemId));
		assertCodeSystemGet(codeSystemId).statusCode(200);
		
		// TODO add REST API
		ResourceRequests.prepareDelete(codeSystemId)
			.build(RestExtensions.USER, "Delete " + codeSystemId)
			.execute(Services.bus())
			.getSync();
		
		assertCodeSystemGet(codeSystemId).statusCode(404);
		
		// Check if the branch has been created
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
		assertCodeSystemCreated(prepareCodeSystemCreateRequestBody(codeSystemId));
		assertCodeSystemGet(codeSystemId).statusCode(200);
		
		// version codesystem
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(codeSystemId), "v1", LocalDate.now().toString());
		assertVersionCreated(versionRequestBody);
		
		// TODO add REST API
		ResourceRequests.prepareDelete(codeSystemId)
			.build(RestExtensions.USER, "Delete " + codeSystemId)
			.execute(Services.bus())
			.getSync();
		
		assertCodeSystemGet(codeSystemId).statusCode(404);
		
		String branch = Branch.get(Branch.MAIN_PATH, codeSystemId);
		assertThat(RepositoryRequests.branching()
				.prepareGet(branch)
				.build(TOOLING_ID)
				.execute(Services.bus())
				.getSync()
				.isDeleted()).isTrue();
			
		CodeSystemVersionRestRequests.assertGetVersion(codeSystemId, "v1").statusCode(404);
		
		// Check if the branch has been created
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
		assertCodeSystemCreated(requestBody);
		
		final Json updateRequestBody = Json.object("bundleId", "updated-bundle-id");
		
		assertCodeSystemUpdated(codeSystemId, updateRequestBody);
		assertCodeSystemHasAttributeValue(codeSystemId, "bundleId", "updated-bundle-id");
	}
	
	@After
	public void cleanUp() {
		ResourceRequests
		.prepareSearch()
		.buildAsync()
		.execute(Services.bus())
		.getSync(1, TimeUnit.MINUTES)
		.forEach(resource -> {
			ResourceRequests
			.prepareDelete(resource.getId())
			.build(RestExtensions.USER, "Delete " + resource.getId())
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES); 
		});
	}
	
}
