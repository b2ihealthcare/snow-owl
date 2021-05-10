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
package com.b2international.snowowl.core.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

/**
 * @since 4.7
 */
public abstract class CodeSystemApiAssert {
	
	public static final String TOOLING_ID = SnomedTerminologyComponentConstants.TERMINOLOGY_ID;

	public static ValidatableResponse assertCodeSystemExists(final String uniqueId) {
		return assertGetCodeSystem(uniqueId, 200);
	}
	
	public static ValidatableResponse assertCodeSystemNotExists(final String uniqueId) {
		return assertGetCodeSystem(uniqueId, 404);
	}
	
	public static ValidatableResponse assertGetCodeSystem(final String uniqueId, final int statusCode) {
		assertNotNull(uniqueId);
		
		return givenAuthenticatedRequest("/codesystems")
			.when().get("/{id}", uniqueId)
			.then().assertThat().statusCode(statusCode);
	}
	
	public static String assertCodeSystemCreated(final Map<?, ?> requestBody) {
		final String path = assertCodeSystemCreatedWithStatus(requestBody, 201)
			.and().header("Location", containsString(String.format("%s/%s", "codesystems", requestBody.get("id"))))
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
		return givenAuthenticatedRequest("/codesystems")
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post();
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
	
	private static Response whenUpdatingCodeSystem(final String codeSystemId, final Map<?, ?> requestBody) {
		return givenAuthenticatedRequest("/codesystems")
			.with().contentType(ContentType.JSON)
			.and().body(requestBody)
			.when().put("/{id}", codeSystemId);
	}
	
	public static void assertCodeSystemHasAttributeValue(final String codeSystemId, final String attributeName, final Object attributeValue) {
		givenAuthenticatedRequest("/codesystems")
			.when().get("/{id}", codeSystemId)
			.then().assertThat().statusCode(200)
			.and().body(attributeName, equalTo(attributeValue));
	}
	
	public static Json newCodeSystemRequestBody(final String codeSystemId) {
		return newCodeSystemRequestBody(codeSystemId, "MAIN");
	}
	
	public static Json newCodeSystemRequestBody(final String codeSystemId, final String branchPath) {
		return Json.object(
			"id", codeSystemId,
			"title", "CodeSystem",
			"branchPath", branchPath,
			"description", "description",
			"toolingId", TOOLING_ID,
			"oid", codeSystemId,
			"language", "en",
			"url", "link"
		);
	}
	
	public static Json newCodeSystemVersionRequestBody(final ResourceURI resourceUri, final String versionId, final String effectiveTime) {
		return Json.object(
			"resource", resourceUri.toString(),
			"version", versionId,
			"description", versionId + " description",
			"effectiveTime", effectiveTime
		);
	}

	public static ValidatableResponse assertCodeSystemVersionCreated(final Map<?, ?> requestBody) {
		return givenAuthenticatedRequest("/versions")
			.with().contentType(ContentType.JSON)
			.and().body(requestBody)
			.when().post()
			.then().assertThat().statusCode(201);
	}
}
