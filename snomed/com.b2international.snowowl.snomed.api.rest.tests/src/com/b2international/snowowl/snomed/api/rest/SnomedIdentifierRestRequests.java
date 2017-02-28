/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 5.0
 */
public abstract class SnomedIdentifierRestRequests {

	public static ValidatableResponse generateSctId(SnomedComponentType type, String namespaceId) {
		final ImmutableMap.Builder<String, String> requestBuilder = ImmutableMap.builder();
		requestBuilder.put("type", type.toString());

		if (namespaceId != null) {
			requestBuilder.put("namespace", namespaceId);
		}

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(ContentType.JSON)
				.body(requestBuilder.build())
				.post("/ids")
				.then();
	}

	private SnomedIdentifierRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
