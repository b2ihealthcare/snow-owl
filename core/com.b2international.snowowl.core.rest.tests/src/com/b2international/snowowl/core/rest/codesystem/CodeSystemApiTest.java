/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemCreated;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemExists;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemHasAttributeValue;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemNotCreated;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemNotExists;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemNotUpdated;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemUpdated;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.assertCodeSystemUpdatedWithStatus;
import static com.b2international.snowowl.core.rest.CodeSystemApiAssert.newCodeSystemRequestBody;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

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
				"namespace", "1000198",
				"moduleIds", List.of("1234567891000198103", "9876543211000198107")));
		
		assertCodeSystemCreated(requestBody);
		assertCodeSystemExists(shortName);
		assertCodeSystemHasAttributeValue(shortName, "additionalProperties.namespace", "1000198");
		assertCodeSystemHasAttributeValue(shortName, "additionalProperties.moduleIds", List.of("1234567891000198103", "9876543211000198107"));
	}
	
	@Test
	public void updateCodeSystem() {
		final String shortName = "cs2";
		final Map<String, String> requestBody = newCodeSystemRequestBody(shortName);
		assertCodeSystemCreated(requestBody);
		
		final ImmutableMap<String, String> updateRequestBody = ImmutableMap.<String, String>builder()
				.put("name", "updated name")
				.put("repositoryUuid", "snomedStore")
				.build();
		
		assertCodeSystemUpdated(shortName, updateRequestBody);
		assertCodeSystemHasAttributeValue(shortName, "name", "updated name");
	}
	
	@Test
	public void updateCodeSystemWithMetadata() {
		final String shortName = "cs5";
		final Map<String, Object> requestBody = newHashMap(newCodeSystemRequestBody(shortName));
		requestBody.put("additionalProperties", Map.of(
			"namespace", "1000198",
			"moduleIds", List.of("1234567891000198103", "9876543211000198107"),
			"locked", true));
		
		assertCodeSystemCreated(requestBody);
		
		final Map<String, Object> updatedProperties = newHashMap();
		updatedProperties.put("namespace", "1000197");
		updatedProperties.put("locked", null);
		
		final Map<String, Object> updateRequestBody = Map.of(
			"repositoryUuid", "snomedStore",
			"additionalProperties", updatedProperties);
		
		assertCodeSystemUpdated(shortName, updateRequestBody);
		assertCodeSystemHasAttributeValue(shortName, "additionalProperties.namespace", "1000197");
		assertCodeSystemHasAttributeValue(shortName, "additionalProperties.moduleIds", List.of("1234567891000198103", "9876543211000198107"));
		assertCodeSystemExists(shortName).and().body("additionalProperties", not(hasKey("locked")));
	}
	
	@Test
	public void updateCodeSystemWithInvalidBranchPath() {
		final String shortName = "cs3";
		final Map<String, String> requestBody = newCodeSystemRequestBody(shortName);
		
		assertCodeSystemCreated(requestBody);
		
		final ImmutableMap<String, String> updateRequestBody = ImmutableMap.<String, String>builder()
				.put("branchPath", "non-existent-branch-path")
				.put("repositoryUuid", "snomedStore")
				.build();
		
		assertCodeSystemUpdatedWithStatus(shortName, updateRequestBody, 404);
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
