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
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.ImmutableMap;

/**
 * @since 1.0
 */
public class CodeSystemApiTest {

	@Test
	public void getAllCodeSystems() {
		assertCodeSystemHasAttributeValue("SNOMEDCT", "oid", "2.16.840.1.113883.6.96");
	}

	@Test
	public void getCodeSystemByOid() {
		final String oid = "2.16.840.1.113883.6.96";
		assertCodeSystemExists(oid)
			.and().body("oid", equalTo(oid));
	}

	@Test
	public void getCodeSystemByShortName() {
		final String shortName = "SNOMEDCT";
		assertCodeSystemExists(shortName)
			.and().body("shortName", equalTo(shortName));
	}
	
	@Test
	public void getCodeSystemByNonExistentOid() {
		assertCodeSystemNotExists("1.2.3.4.10000");
	}
	
	@Test
	public void createCodeSystem() {
		final String shortName = "cs";
		final Map<?, ?> requestBody = newCodeSystemRequestBody(shortName);
		final String lastPathSegment = assertCodeSystemCreated(requestBody);
		
		assertEquals(shortName, lastPathSegment);
		assertCodeSystemExists(shortName);
	}
	
	@Test
	public void createCodeSystemWithNonUniqueShortName() {
		final Map<?, ?> requestBody = newCodeSystemRequestBody("cs");
		assertCodeSystemNotCreated(requestBody);
	}
	
	@Test
	public void createCodeSystemWithMetadata() {
		final String shortName = "cs6";
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(shortName));
		requestBody.put("additionalProperties", Map.of(
				SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000198",
				SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("123456781000198103", "876543211000198107")));
		
		assertCodeSystemCreated(requestBody);
		assertCodeSystemExists(shortName);
		assertCodeSystemHasAttributeValue(shortName, "additionalProperties." + SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000198");
		assertCodeSystemHasAttributeValue(shortName, "additionalProperties." + SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("123456781000198103", "876543211000198107"));
	}
	
	@Test
	public void createCodeSystemWithLocales() {
		final String shortName = "cs7";
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(shortName));
		requestBody.put("locales", ExtendedLocale.parseLocales("en-x-123456781000198103,en-x-876543211000198107"));
		
		assertCodeSystemCreated(requestBody);
		assertCodeSystemExists(shortName);
		assertCodeSystemHasAttributeValue(shortName, "locales", List.of("en-x-123456781000198103", "en-x-876543211000198107"));
	}
	
	@Test
	public void createCodeSystemWithoutPath() {
		final String shortName = "cs10";
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, shortName);

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
		
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(shortName));
		requestBody.remove("branchPath");
		
		assertCodeSystemCreated(requestBody);
		assertCodeSystemExists(shortName);
		
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
		final String parentName = "cs11";
		final Map<String, String> parentRequestBody = newCodeSystemRequestBody(parentName);
		assertCodeSystemCreated(parentRequestBody);
		assertCodeSystemExists(parentName);
		
		final Map<String, String> versionRequestBody = newCodeSystemVersionRequestBody("v1", "20200415");
		assertCodeSystemVersionCreated(parentName, versionRequestBody);

		final String shortName = "cs12";
		final Map<String, String> requestBody = newHashMap(newCodeSystemRequestBody(shortName));
		requestBody.remove("branchPath");
		requestBody.put("extensionOf", "cs11/v1");
		
		assertCodeSystemCreated(requestBody);
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, "v1", shortName);
		
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
		final String shortName = "cs2";
		final Map<String, String> requestBody = newCodeSystemRequestBody(shortName);
		assertCodeSystemCreated(requestBody);
		
		final ImmutableMap<String, String> updateRequestBody = ImmutableMap.<String, String>builder()
				.put("name", "updated name")
				.put("repositoryId", "snomedStore")
				.build();
		
		assertCodeSystemUpdated(shortName, updateRequestBody);
		assertCodeSystemHasAttributeValue(shortName, "name", "updated name");
	}
	
	@Test
	public void updateCodeSystemWithMetadata() {
		final String shortName = "cs5";
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(shortName));
		requestBody.put("additionalProperties", Map.of(
			SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000198",
			SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("1234567891000198103", "9876543211000198107"),
			"locked", true));
		
		assertCodeSystemCreated(requestBody);
		
		final Map<String, Object> updatedProperties = newHashMap();
		updatedProperties.put(SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000197");
		updatedProperties.put("locked", null);
		
		final Map<String, Object> updateRequestBody = Map.of(
			"repositoryId", "snomedStore",
			"additionalProperties", updatedProperties);
		
		assertCodeSystemUpdated(shortName, updateRequestBody);
		assertCodeSystemHasAttributeValue(shortName, "additionalProperties." + SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY, "1000197");
		assertCodeSystemHasAttributeValue(shortName, "additionalProperties." + SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, List.of("1234567891000198103", "9876543211000198107"));
		assertCodeSystemExists(shortName).and().body("additionalProperties", not(hasKey("locked")));
	}
	
	@Test
	public void updateCodeSystemWithLocales() {
		final String shortName = "cs9";
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(shortName));
		requestBody.put("locales", ExtendedLocale.parseLocales("en-x-123456781000198103,en-x-876543211000198107"));
		
		assertCodeSystemCreated(requestBody);
		
		final Map<String, Object> updateRequestBody = Map.of(
			"repositoryId", "snomedStore",
			"locales", List.of("en-us", "en-gb"));
		
		assertCodeSystemUpdated(shortName, updateRequestBody);
		assertCodeSystemHasAttributeValue(shortName, "locales", List.of("en-us", "en-gb"));
	}
	
	@Test
	public void updateCodeSystemWithInvalidBranchPath() {
		final String shortName = "cs3";
		final Map<String, String> requestBody = newCodeSystemRequestBody(shortName);
		
		assertCodeSystemCreated(requestBody);
		
		final ImmutableMap<String, String> updateRequestBody = ImmutableMap.<String, String>builder()
				.put("branchPath", "non-existent-branch-path")
				.put("repositoryId", "snomedStore")
				.build();
		
		assertCodeSystemUpdatedWithStatus(shortName, updateRequestBody, 404);
	}
	
	@Test
	public void updateCodeSystemWithExtensionOf() {
		final String parentName = "cs13";
		final Map<String, String> parentRequestBody = newCodeSystemRequestBody(parentName);
		assertCodeSystemCreated(parentRequestBody);
		assertCodeSystemExists(parentName);
		
		final Map<String, String> v3RequestBody = newCodeSystemVersionRequestBody("v3", "20200416");
		assertCodeSystemVersionCreated(parentName, v3RequestBody);
		final Map<String, String> v4RequestBody = newCodeSystemVersionRequestBody("v4", "20200417");
		assertCodeSystemVersionCreated(parentName, v4RequestBody);

		final String shortName = "cs14";
		final Map<String, String> requestBody = newHashMap(newCodeSystemRequestBody(shortName));
		requestBody.remove("branchPath");
		requestBody.put("extensionOf", "cs13/v3");
		
		assertCodeSystemCreated(requestBody);
		
		final Map<String, Object> updateRequestBody = Map.of(
				"repositoryId", "snomedStore",
				"extensionOf", "cs13/v4");
			
		assertCodeSystemUpdated(shortName, updateRequestBody);
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, "v4", shortName);
		assertCodeSystemHasAttributeValue(shortName, "extensionOf", "cs13/v4");
		assertCodeSystemHasAttributeValue(shortName, "branchPath", expectedBranchPath);
	}
	
	@Test
	public void noUpdateCodeSystem() {
		final String shortName = "cs4";
		final Map<String, String> requestBody = newCodeSystemRequestBody(shortName);
		assertCodeSystemCreated(requestBody);
		
		final ImmutableMap<String, String> updateRequestBody = ImmutableMap.<String, String>builder()
				.put("name", "updated name")
				.build();
		
		assertCodeSystemNotUpdated(shortName, updateRequestBody);
	}
	
}
