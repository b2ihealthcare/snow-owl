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

import static com.b2international.snowowl.test.commons.ApiTestConstants.CODESYSTEMS_API;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Collections;
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
	
	public static final String TOOLING_ID = SnomedTerminologyComponentConstants.TOOLING_ID;
	
	public static ValidatableResponse assertCodeSystemSearch() {
		return assertCodeSystemSearch(Collections.emptyMap());
	}
	
	public static ValidatableResponse assertCodeSystemSearch(final Map<String, Object> filters) {
		return givenAuthenticatedRequest(CODESYSTEMS_API)
				.when().queryParams(filters).get()
				.then().assertThat();
	}
	
	public static ValidatableResponse assertCodeSystemGet(final String codeSystemId) {
		return givenAuthenticatedRequest(CODESYSTEMS_API)
			.when().get("/{id}", codeSystemId)
			.then().assertThat();
	}
	
	public static String assertCodeSystemCreated(final Map<String, Object> requestBody) {
		final String path = assertCodeSystemCreate(requestBody)
				.statusCode(201)
				.and().header("Location", containsString(String.format("%s/%s", "codesystems", requestBody.get("id"))))
				.and().body(equalTo(""))
				.and().extract().response().getHeader("Location");
		
		return lastPathSegment(path);
	}
	
	public static ValidatableResponse assertCodeSystemNotCreated(final Map<String, Object> requestBody) {
		return assertCodeSystemCreate(requestBody).statusCode(409);
	}
	
	public static ValidatableResponse assertCodeSystemCreate(final Map<String, Object> requestBody) {
		return givenAuthenticatedRequest(CODESYSTEMS_API)
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post()
				.then().assertThat();
	}
	
	public static void assertCodeSystemUpdated(final String uniqueId, final Map<String, Object> requestBody) {
		assertCodeSystemUpdatedWithStatus(uniqueId, requestBody, 204);
	}
	
	public static void assertCodeSystemNotUpdated(final String uniqueId, final Map<String, Object> requestBody) {
		assertCodeSystemUpdatedWithStatus(uniqueId, requestBody, 400);
	}
	
	public static void assertCodeSystemUpdatedWithStatus(final String uniqueId, final Map<String, Object> requestBody, final int statusCode) {
		whenUpdatingCodeSystem(uniqueId, requestBody)
			.then().assertThat().statusCode(statusCode);
	}
	
	private static Response whenUpdatingCodeSystem(final String codeSystemId, final Map<String, Object> requestBody) {
		return givenAuthenticatedRequest(CODESYSTEMS_API)
			.with().contentType(ContentType.JSON)
			.and().body(requestBody)
			.when().put("/{id}", codeSystemId);
	}
	
	public static void assertCodeSystemHasAttributeValue(final String codeSystemId, final String attributeName, final Object attributeValue) {
		givenAuthenticatedRequest(CODESYSTEMS_API)
			.when().get("/{id}", codeSystemId)
			.then().assertThat().statusCode(200)
			.and().body(attributeName, equalTo(attributeValue));
	}
	
	public static Json prepareCodeSystemCreateRequestBody(final String codeSystemId) {
		return prepareCodeSystemCreateRequestBody(codeSystemId, null /*let the system create the CodeSystem*/);
	}
	
	public static Json prepareCodeSystemCreateRequestBody(final String codeSystemId, final String branchPath) {
		return Json.object(
			"id", codeSystemId,
			"title", "Code System " + codeSystemId,
			"branchPath", branchPath,
			"description", "description",
			"toolingId", TOOLING_ID,
			"oid", codeSystemId,
			"language", "en",
			"url", SnomedTerminologyComponentConstants.SNOMED_URI_DEV + "/" + codeSystemId
		);
	}
	
	public static Json prepareVersionCreateRequestBody(final ResourceURI resourceUri, final String versionId, final String effectiveTime) {
		return Json.object(
			"resource", resourceUri.toString(),
			"version", versionId,
			"description", versionId + " description",
			"effectiveTime", effectiveTime
		);
	}

	public static ValidatableResponse assertVersionCreated(final Map<String, Object> requestBody) {
		return givenAuthenticatedRequest("/versions")
			.with().contentType(ContentType.JSON)
			.and().body(requestBody)
			.when().post()
			.then().assertThat();
	}
}
