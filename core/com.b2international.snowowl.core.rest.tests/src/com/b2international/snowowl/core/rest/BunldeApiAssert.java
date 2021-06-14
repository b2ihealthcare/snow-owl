/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.test.commons.ApiTestConstants.BUNDLE_API;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Collections;
import java.util.Map;

import com.b2international.commons.json.Json;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 8.0
 */
public class BunldeApiAssert {
	
	public static ValidatableResponse assertBundleSearch() {
		return assertBundleSearch(Collections.emptyMap());
	}
	
	public static ValidatableResponse assertBundleSearch(final Map<String, Object> filters) {
		return givenAuthenticatedRequest(BUNDLE_API)
				.when().queryParams(filters).get()
				.then().assertThat();
	}
	
	public static ValidatableResponse assertBundleGet(final String resourceId) {
		return givenAuthenticatedRequest(BUNDLE_API)
				.when().get("/{id}", resourceId)
				.then().assertThat();
	}
	
	public static String assertBundleCreated(final Map<String, Object> requestBody) {
		final String path = assertCreate(requestBody)
				.statusCode(201)
				.and().header("Location", containsString(String.format("%s/%s", "bundles", requestBody.get("id"))))
				.and().body(equalTo(""))
				.and().extract().response().getHeader("Location");
		
		return lastPathSegment(path);
	}
	
	public static ValidatableResponse assertCreate(final Map<String, Object> requestBody) {
		return givenAuthenticatedRequest(BUNDLE_API)
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post()
				.then().assertThat();
	}
	
	public static Json prepareCreateRequestBody(final String resourceId) {
		return prepareCreateRequestBody(resourceId, "-1");
	}
	
	public static Json prepareCreateRequestBody(final String resourceId, final String bundleId) {
		return Json.object(
			"id", resourceId,
			"title", "Bundle " + resourceId,
			"description", "description",
			"language", "en",
			"url", "link",
			"bundleId", bundleId
		);
	}
}
