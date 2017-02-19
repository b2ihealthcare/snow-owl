/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 4.5
 */
public class SnomedBrowserRestRequests {

	public static ValidatableResponse createBrowserConcept(final IBranchPath conceptPath, final Map<?, ?> requestBody) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post("/browser/{path}/concepts", conceptPath.getPath())
				.then();
	}

	public static ValidatableResponse updateBrowserConcept(final IBranchPath branchPath, final String conceptId, final Map<?, ?> requestBody) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().put("/browser/{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
				.then();
	}

	public static ValidatableResponse getBrowserConcept(final IBranchPath branchPath, final String conceptId) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.with().contentType(ContentType.JSON)
				.when().get("/browser/{path}/concepts/{conceptId}", branchPath.getPath(), conceptId)
				.then();
	}

}
