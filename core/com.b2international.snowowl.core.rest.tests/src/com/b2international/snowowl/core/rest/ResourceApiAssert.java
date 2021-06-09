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

import static com.b2international.snowowl.test.commons.ApiTestConstants.RESOURCES_API;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.Collections;
import java.util.Map;

import io.restassured.response.ValidatableResponse;

/**
 * @since 8.0
 */
public class ResourceApiAssert {

	public static ValidatableResponse assertResourceSearch() {
		return assertResourceSearch(Collections.emptyMap());
	}
	
	public static ValidatableResponse assertResourceSearch(final Map<String, Object> filters) {
		return givenAuthenticatedRequest(RESOURCES_API)
				.when().queryParams(filters).get()
				.then().assertThat();
	}
	
	public static ValidatableResponse assertResourceGet(final String resourceId) {
		return givenAuthenticatedRequest(RESOURCES_API)
				.when().get("/{id}", resourceId)
				.then().assertThat();
	}
}
