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
package com.b2international.snowowl.test.commons.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import java.util.Map;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.ApiTestConstants;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 4.7
 */
public abstract class CodeSystemRestRequests {
	
	public static ValidatableResponse createCodeSystem(IBranchPath branchPath, String shortName) {
		return createCodeSystem(null, branchPath, shortName);
	}

	public static ValidatableResponse createCodeSystem(ResourceURI extensionOf, String shortName) {
		return createCodeSystem(extensionOf, null, shortName);
	}
	
	private static ValidatableResponse createCodeSystem(ResourceURI extensionOf, IBranchPath branchPath, String shortName) {
		Json requestBody = Json.object(
			"id", shortName,
			"title", shortName,
			"url", "organizationLink",
			"description", "citation",
			"toolingId", SnomedTerminologyComponentConstants.TOOLING_ID,
			"oid", "oid_" + shortName,
			"language", "primaryLanguage"
		);
				
		
		if (extensionOf != null) {
			requestBody = requestBody.with("extensionOf", extensionOf);
		} else if (branchPath != null) {
			requestBody = requestBody.with("branchPath", branchPath.getPath());
		}
		
		return givenAuthenticatedRequest(ApiTestConstants.CODESYSTEMS_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post()
				.then();
	}

	public static ValidatableResponse getCodeSystem(String id) {
		return givenAuthenticatedRequest(ApiTestConstants.CODESYSTEMS_API)
				.get("/{id}", id)
				.then();
	}

	public static ValidatableResponse updateCodeSystem(String id, Map<?, ?> requestBody) {
		return givenAuthenticatedRequest(ApiTestConstants.CODESYSTEMS_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put("/{id}", id)
				.then();
	}
	
	public static ValidatableResponse upgrade(ResourceURI codeSystem, ResourceURI extensionOf) {
		return givenAuthenticatedRequest(ApiTestConstants.CODESYSTEMS_API)
				.contentType(ContentType.JSON)
				.body(Map.of(
					"extensionOf", extensionOf.toString(),
					"upgradeOf", codeSystem.getResourceId().toString()
				))
				.post("/upgrade")
				.then();
	}

	private CodeSystemRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}

}
