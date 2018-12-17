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
package com.b2international.snowowl.api.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

/**
 * @since 4.7
 */
public abstract class CodeSystemApiAssert {
	
	public static ValidatableResponse assertCodeSystemExists(final String uniqueId) {
		return assertCodeSystemReadWithStatus(uniqueId, 200);
	}
	
	public static ValidatableResponse assertCodeSystemNotExists(final String uniqueId) {
		return assertCodeSystemReadWithStatus(uniqueId, 404);
	}
	
	public static ValidatableResponse assertCodeSystemReadWithStatus(final String uniqueId, final int statusCode) {
		assertNotNull(uniqueId);
		
		return givenAuthenticatedRequest("/admin")
			.when().get("/codesystems/{id}", uniqueId)
			.then().assertThat().statusCode(statusCode);
	}
	
	public static String assertCodeSystemCreated(final Map<?, ?> requestBody) {
		final String path = assertCodeSystemCreatedWithStatus(requestBody, 201)
			.and().header("Location", containsString(String.format("%s/%s", "codesystems", requestBody.get("shortName"))))
			.and().body(equalTo(""))
			.and().extract().response().getHeader("Location");
		
		return lastPathSegment(path);
	}
	
	public static ValidatableResponse assertCodeSystemNotCreated(final Map<?, ?> requestBody) {
		return assertCodeSystemCreatedWithStatus(requestBody, 409);
	}
	
	public static ValidatableResponse assertCodeSystemCreatedWithStatus(final Map<?, ?> requestBody, final int statusCode) {
		return whenCreatingCodeSystem(requestBody)
			.then().assertThat().statusCode(statusCode);
	}
	
	private static Response whenCreatingCodeSystem(final Map<?, ?> requestBody) {
		return givenAuthenticatedRequest("/admin")
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post("/codesystems");
	}
	
	public static void assertCodeSystemUpdated(final String uniqueId, final Map<?, ?> requestBody) {
		assertCodeSystemUpdatedWithStatus(uniqueId, requestBody, 204);
	}
	
	public static void assertCodeSystemNotUpdated(final String uniqueId, final Map<?, ?> requestBody) {
		assertCodeSystemUpdatedWithStatus(uniqueId, requestBody, 400);
	}
	
	public static void assertCodeSystemUpdatedWithStatus(final String uniqueId, final Map<?, ?> requestBody, final int statusCode) {
		whenUpdatingCodeSystem(uniqueId, requestBody)
			.then().assertThat().statusCode(statusCode);
	}
	
	private static Response whenUpdatingCodeSystem(final String uniqueId, final Map<?, ?> requestBody) {
		return givenAuthenticatedRequest("/admin")
			.with().contentType(ContentType.JSON)
			.and().body(requestBody)
			.when().put("codesystems/{id}", uniqueId);
	}
	
	public static void assertCodeSystemHasAttributeValue(final String uniqueId, final String attributeName, final String attributeValue) {
		givenAuthenticatedRequest("/admin")
			.when().get("/codesystems/{id}", uniqueId)
			.then().assertThat().statusCode(200)
			.and().body(attributeName, equalTo(attributeValue));
	}
	
	public static Map<String, String> newCodeSystemRequestBody(final String shortName) {
		return newCodeSystemRequestBody(shortName, "MAIN");
	}
	
	public static Map<String, String> newCodeSystemRequestBody(final String shortName, final String branchPath) {
		return ImmutableMap.<String, String>builder()
				.put("name", "CodeSystem")
				.put("branchPath", branchPath)
				.put("shortName", shortName)
				.put("citation", "citation")
				.put("iconPath", "icons/snomed.png")
				.put("repositoryUuid", "snomedStore")
				.put("terminologyId", "concept")
				.put("oid", shortName)
				.put("primaryLanguage", "ENG")
				.put("organizationLink", "link")
				.build();
	}

}
