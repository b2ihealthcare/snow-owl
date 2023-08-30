/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons.rest;

import static com.b2international.snowowl.test.commons.ApiTestConstants.BUNDLE_API;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Collections;
import java.util.Map;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.domain.IComponent;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 8.0
 */
public class BundleApiAssert {
	
	public static ValidatableResponse assertBundleSearch() {
		return assertBundleSearch(Collections.emptyMap());
	}
	
	public static ValidatableResponse assertBundleSearch(final Map<String, Object> filters) {
		return givenAuthenticatedRequest(BUNDLE_API)
				.when().queryParams(filters).get()
				.then().statusCode(200).assertThat();
	}
	
	public static ValidatableResponse assertBundleGet(final String bundleId, String...expand) {
		return givenAuthenticatedRequest(BUNDLE_API)
				.queryParam("expand", expand == null ? null : String.join(",", expand))
				.when().get("/{id}", bundleId)
				.then().assertThat();
	}
	
	public static String createBundle(final Map<String, Object> requestBody) {
		final String path = assertCreate(requestBody)
				.statusCode(201)
				.and().header("Location", containsString(String.format("%s/%s", Bundle.RESOURCE_TYPE, requestBody.get("id"))))
				.and().body(equalTo(""))
				.and().extract().response().getHeader("Location");
		
		return lastPathSegment(path);
	}
	
	public static String createBundle(String bundleId) {
		return createBundle(prepareBundleCreateRequestBody(bundleId));
	}
	
	public static String createBundle(String bundleId, String parentCollectionId) {
		return createBundle(prepareBundleCreateRequestBody(bundleId)
				.with("bundleId", parentCollectionId));
	}
	
	public static ValidatableResponse assertCreate(final Map<String, Object> requestBody) {
		return givenAuthenticatedRequest(BUNDLE_API)
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post()
				.then().assertThat();
	}
	
	public static ValidatableResponse assertUpdateBundle(final String uniqueId, final Map<String, Object> requestBody) {
		return givenAuthenticatedRequest(BUNDLE_API)
			.with().contentType(ContentType.JSON)
			.and().body(requestBody)
			.when().put("/{id}", uniqueId)
			.then().assertThat();
	}
	
	public static ValidatableResponse assertUpdateBundleField(final String uniqueId, final String field, final String value) {
		assertUpdateBundle(uniqueId, Map.of(field, value)).statusCode(204);
		
		return assertBundleGet(uniqueId)
				.statusCode(200)
				.body(field, equalTo(value))
				.assertThat().and();
	}
	
	public static Json prepareBundleCreateRequestBody(final String resourceId) {
		return prepareBundleCreateRequestBody(resourceId, IComponent.ROOT_ID);
	}
	
	public static Json prepareBundleCreateRequestBody(final String resourceId, final String bundleParentId) {
		return Json.object(
			"id", resourceId,
			"title", "Bundle " + resourceId,
			"description", "description",
			"language", "en",
			"url", "https://b2ihealthcare.com/" + resourceId,
			"bundleId", bundleParentId
		);
	}

}
