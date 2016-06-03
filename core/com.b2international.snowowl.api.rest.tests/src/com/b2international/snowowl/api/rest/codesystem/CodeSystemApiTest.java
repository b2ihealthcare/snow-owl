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
package com.b2international.snowowl.api.rest.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;

/**
 * @since 1.0
 */
public class CodeSystemApiTest {

	@Test
	public void getAllCodeSystems() {
		givenAuthenticatedRequest("/admin")
		.when().get("/codesystems")
		.then().assertThat().statusCode(200)
		.and().body("items.oid", hasItem("2.16.840.1.113883.6.96"));
	}

	@Test
	public void getCodeSystemByOid() {
		givenAuthenticatedRequest("/admin")
		.when().get("/codesystems/{id}", "2.16.840.1.113883.6.96")
		.then().assertThat().statusCode(200)
		.and().body("oid", equalTo("2.16.840.1.113883.6.96"));		
	}

	@Test
	public void getCodeSystemByShortName() {
		givenAuthenticatedRequest("/admin")
		.when().get("/codesystems/{id}", "SNOMEDCT")
		.then().assertThat().statusCode(200)
		.and().body("shortName", equalTo("SNOMEDCT"));		
	}
	
	private void assertCodeSystemExists(final String shortName) {
		givenAuthenticatedRequest("/admin")
		.when().get("/codesystems/{id}", shortName)
		.then().assertThat().statusCode(200)
		.and().body("shortName", equalTo(shortName));
	}

	@Test
	public void getCodeSystemByNonExistentOid() {
		givenAuthenticatedRequest("/admin")
		.when().get("/codesystems/{id}", "1.2.3.4.10000")
		.then().assertThat().statusCode(404);		
	}
	
	@Test
	public void createCodeSystem() {
		final String shortName = "cs";
		final String oid = "1";
		final Map<?, ?> requestBody = newCodeSystem(shortName, oid);
		
		final String path = givenAuthenticatedRequest("/admin")
			.with().contentType(ContentType.JSON)
			.and().body(requestBody)
			.when().post("/codesystems")
			.then().assertThat().statusCode(201)
			.and().header("Location", containsString(String.format("%s/%s", "codesystems", shortName)))
			.and().body(equalTo(""))
			.and().extract().response().getHeader("Location");
		
		assertEquals(shortName, lastPathSegment(path));
		assertCodeSystemExists(shortName);
	}
	
	@Test
	public void createCodeSystemWithNonUniqueShortName() {
		final String shortName = "cs";
		final String oid = "1";
		final Map<?, ?> requestBody = newCodeSystem(shortName, oid);
		
		givenAuthenticatedRequest("/admin")
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post("/codesystems")
				.then().assertThat().statusCode(409);
			
	}

	private Map<String, String> newCodeSystem(final String shortName, final String oid) {
		return ImmutableMap.<String, String>builder()
				.put("name", "CodeSystem")
				.put("branchPath", "MAIN")
				.put("shortName", shortName)
				.put("citation", "citation")
				.put("iconPath", "icons/snomed.png")
				.put("repositoryUuid", "snomedStore")
				.put("terminologyId", "concept")
				.put("oid", oid)
				.put("primaryLanguage", "ENG")
				.put("organizationLink", "link")
				.build();
	}
	
}
