/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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

import static com.b2international.snowowl.test.commons.ApiTestConstants.RESOURCES_API;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.Collections;
import java.util.List;
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
				.queryParams(filters)
				.get()
				.then();
	}
	
	public static ValidatableResponse assertResourceGet(final String resourceId, final String...expand) {
		return givenAuthenticatedRequest(RESOURCES_API)
				.queryParam("expand", expand == null ? null : List.of(expand))
				.get("/{id}", resourceId)
				.then();
	}
	
	public static ValidatableResponse assertResourceGet(final String resourceId, final long timestamp) {
		return givenAuthenticatedRequest(RESOURCES_API)
				.get("/{id}@{timestamp}", resourceId, timestamp)
				.then();
	}
}
