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

import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.*;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 1.0
 */
public class CodeSystemApiTest {

	@Test
	public void getAllCodeSystems() {
		assertCodeSystemHasAttributeValue("SNOMEDCT", "oid", "2.16.840.1.113883.6.96");
	}

	@Test
	public void searchCodeSystemByOid() {
		final String oid = "2.16.840.1.113883.6.96";
		assertCodeSystemExists(oid)
			.and().body("oid", equalTo(oid));
	}

	@Test
	public void getCodeSystemByShortName() {
		final String codeSystemId = "SNOMEDCT";
		assertCodeSystemExists(codeSystemId)
			.and().body("id", equalTo(codeSystemId));
	}
	
	@Test
	public void getCodeSystemByNonExistentOid() {
		assertCodeSystemNotExists("1.2.3.4.10000");
	}
	
	@Test
	public void createCodeSystem() {
		final String codeSystemId = "cs";
		final Map<?, ?> requestBody = newCodeSystemRequestBody(codeSystemId);
		final String lastPathSegment = assertCodeSystemCreated(requestBody);
		
		assertEquals(codeSystemId, lastPathSegment);
		assertCodeSystemExists(codeSystemId);
	}
	
	@Test
	public void createCodeSystemWithNonUniqueShortName() {
		final Map<?, ?> requestBody = newCodeSystemRequestBody("cs");
		assertCodeSystemNotCreated(requestBody);
	}
	
	@Test
	public void createCodeSystemWithMetadata() {
		final String codeSystemId = "cs6";
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(codeSystemId));
		requestBody.put("settings", Map.of(
				SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000198",
				SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("123456781000198103", "876543211000198107")));
		
		assertCodeSystemCreated(requestBody);
		assertCodeSystemExists(codeSystemId);
		assertCodeSystemHasAttributeValue(codeSystemId, "settings." + SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000198");
		assertCodeSystemHasAttributeValue(codeSystemId, "settings." + SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("123456781000198103", "876543211000198107"));
	}
	
	@Test
	public void createCodeSystemWithLocales() {
		final String codeSystemId = "cs7";
		final Json requestBody = newCodeSystemRequestBody(codeSystemId).with(Json.object("settings", Json.object("locales", ExtendedLocale.parseLocales("en-x-123456781000198103,en-x-876543211000198107"))));
		
		assertCodeSystemCreated(requestBody);
		assertCodeSystemHasAttributeValue(codeSystemId, "settings", Json.object("locales", List.of("en-x-123456781000198103", "en-x-876543211000198107")));
	}
	
	@Test
	public void createCodeSystemWithoutPath() {
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
		
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(codeSystemId));
		requestBody.remove("branchPath");
		
		assertCodeSystemCreated(requestBody);
		assertCodeSystemExists(codeSystemId);
		
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
	public void createCodeSystemWithoutPathWithExtensionOf() {
		final String parentCodeSystemId = "cs11";
		final Map<String, Object> parentRequestBody = newCodeSystemRequestBody(parentCodeSystemId);
		assertCodeSystemCreated(parentRequestBody);
		assertCodeSystemExists(parentCodeSystemId);
		
		final Map<String, Object> versionRequestBody = newCodeSystemVersionRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "20200415");
		assertCodeSystemVersionCreated(versionRequestBody);

		final String codeSystemId = "cs12";
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(codeSystemId));
		requestBody.remove("branchPath");
		requestBody.put("extensionOf", CodeSystem.uri("cs11/v1"));
		
		assertCodeSystemCreated(requestBody);
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, "v1", codeSystemId);
		
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
	public void updateCodeSystem() {
		final String codeSystemId = "cs2";
		final Map<String, Object> requestBody = newCodeSystemRequestBody(codeSystemId);
		assertCodeSystemCreated(requestBody);
		
		final Json updateRequestBody = Json.object("title", "updated name");
		
		assertCodeSystemUpdated(codeSystemId, updateRequestBody);
		assertCodeSystemHasAttributeValue(codeSystemId, "title", "updated name");
	}
	
	@Test
	public void updateCodeSystemSettings() {
		final String codeSystemId = "cs5";
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(codeSystemId));
		requestBody.put("settings", Map.of(
			SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000198",
			SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("1234567891000198103", "9876543211000198107"),
			"locked", true));
		
		assertCodeSystemCreated(requestBody);
		
		final Map<String, Object> updatedProperties = newHashMap();
		updatedProperties.put(SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000197");
		updatedProperties.put("locked", null);
		
		final Map<String, Object> updateRequestBody = Map.of("settings", updatedProperties);
		
		assertCodeSystemUpdated(codeSystemId, updateRequestBody);
		assertCodeSystemHasAttributeValue(codeSystemId, "settings." + SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000197");
		assertCodeSystemHasAttributeValue(codeSystemId, "settings." + SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("1234567891000198103", "9876543211000198107"));
		assertCodeSystemExists(codeSystemId).and().body("settings", not(hasKey("locked")));
	}
	
	@Test
	public void updateCodeSystemSettingsWithLocales() {
		final String codeSystemId = "cs9";
		
		assertCodeSystemCreated(Json.assign(
			newCodeSystemRequestBody(codeSystemId), 
			Json.object("settings", Json.object(
				"locales", ExtendedLocale.parseLocales("en-x-123456781000198103,en-x-876543211000198107")
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
	public void updateCodeSystemWithInvalidBranchPath() {
		final String codeSystemId = "cs3";
		final Map<String, Object> requestBody = newCodeSystemRequestBody(codeSystemId);
		
		assertCodeSystemCreated(requestBody);
		
		final Json updateRequestBody = Json.object("branchPath", "non-existent-branch-path");
		
		assertCodeSystemUpdatedWithStatus(codeSystemId, updateRequestBody, 404);
	}
	
	@Test
	public void updateCodeSystemWithExtensionOf() {
		final String parentCodeSystemId = "cs13";
		final Map<String, Object> parentRequestBody = newCodeSystemRequestBody(parentCodeSystemId);
		assertCodeSystemCreated(parentRequestBody);
		assertCodeSystemExists(parentCodeSystemId);
		
		final Map<String, Object> v3RequestBody = newCodeSystemVersionRequestBody(CodeSystem.uri(parentCodeSystemId), "v3", "20200416");
		assertCodeSystemVersionCreated(v3RequestBody);
		final Map<String, Object> v4RequestBody = newCodeSystemVersionRequestBody(CodeSystem.uri(parentCodeSystemId), "v4", "20200417");
		assertCodeSystemVersionCreated(v4RequestBody);
		
		final String codeSystemId = "cs14";
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(codeSystemId));
		requestBody.remove("branchPath");
		requestBody.put("extensionOf", CodeSystem.uri("cs13/v3"));
		
		assertCodeSystemCreated(requestBody);
		
		final Map<String, Object> updateRequestBody = Json.object("extensionOf", CodeSystem.uri("cs13/v4"));
			
		assertCodeSystemUpdated(codeSystemId, updateRequestBody);
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, "v4", codeSystemId);
		assertCodeSystemHasAttributeValue(codeSystemId, "extensionOf", "codesystems/cs13/v4");
		assertCodeSystemHasAttributeValue(codeSystemId, "branchPath", expectedBranchPath);
	}
	
	@Test
	public void noUpdateCodeSystem() {
		final String codeSystemId = "cs4";
		final Map<String, Object> requestBody = newCodeSystemRequestBody(codeSystemId);
		assertCodeSystemCreated(requestBody);
		
		final Map<String, Object> updateRequestBody = Json.object("title", "updated name");
		
		assertCodeSystemNotUpdated(codeSystemId, updateRequestBody);
	}
	
}
