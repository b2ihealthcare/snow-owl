/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * A set of assert methods related to manipulation of components through the REST API.
 *
 * @since 2.0
 */
public abstract class SnomedComponentRestRequests {

	private static final Joiner COMMA_JOINER = Joiner.on(",");
	private static final String JSON_UTF8 = ContentType.JSON.withCharset(Charsets.UTF_8);

	public static ValidatableResponse createComponent(IBranchPath branchPath, SnomedComponentType type, Map<?, ?> requestBody) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(JSON_UTF8)
				.body(requestBody)
				.post("/{path}/{componentType}", branchPath.getPath(), type.toLowerCasePlural())
				.then();
	}
	
	public static ValidatableResponse searchComponent(IBranchPath branchPath, SnomedComponentType type, Map<String, Object> params) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(JSON_UTF8)
				.queryParams(params)
				.get("/{path}/{componentType}", branchPath.getPath(), type.toLowerCasePlural())
				.then();
	}

	public static ValidatableResponse getComponent(IBranchPath branchPath, SnomedComponentType type, String id, String... expand) {
		assertNotNull(id);

		final String url;
		if (expand.length > 0) {
			url = "/{path}/{componentType}/{id}?expand=" + COMMA_JOINER.join(expand);
		} else {
			url = "/{path}/{componentType}/{id}";
		}

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get(url, branchPath.getPath(), type.toLowerCasePlural(), id)
				.then();
	}

	public static ValidatableResponse updateComponent(IBranchPath branchPath, SnomedComponentType type, String id, Map<?, ?> requestBody) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(JSON_UTF8)
				.body(requestBody)
				.post("/{path}/{componentType}/{id}/updates", branchPath.getPath(), type.toLowerCasePlural(), id)
				.then();
	}

	public static ValidatableResponse deleteComponent(IBranchPath branchPath, SnomedComponentType componentType, String id, boolean force) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.queryParam("force", force)
				.delete("/{path}/{componentType}/{id}", branchPath.getPath(), componentType.toLowerCasePlural(), id)
				.then();
	}

	private SnomedComponentRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
