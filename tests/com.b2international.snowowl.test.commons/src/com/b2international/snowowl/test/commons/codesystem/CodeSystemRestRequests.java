/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
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

	public static ValidatableResponse createCodeSystem(CodeSystemURI extensionOf, String shortName) {
		return createCodeSystem(extensionOf, null, shortName);
	}
	
	private static ValidatableResponse createCodeSystem(CodeSystemURI extensionOf, IBranchPath branchPath, String shortName) {
		return createCodeSystem(createCodeSystemBody(extensionOf, branchPath, shortName));
	}

	public static ValidatableResponse createCodeSystem(Json requestBody) {
		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/codesystems")
				.then();
	}

	public static Json createCodeSystemBody(CodeSystemURI extensionOf, IBranchPath branchPath, String shortName) {
		Json requestBody = Json.object(
			"name", shortName,
			"shortName", shortName,
			"citation", "citation",
			"iconPath", "iconPath",
			"repositoryId", SnomedDatastoreActivator.REPOSITORY_UUID,
			"terminologyId", SnomedTerminologyComponentConstants.TERMINOLOGY_ID,
			"oid", "oid_" + shortName,
			"primaryLanguage", "primaryLanguage",
			"organizationLink", "organizationLink"
		);
		
		if (extensionOf != null) {
			requestBody = requestBody.with("extensionOf", extensionOf);
		} else if (branchPath != null) {
			requestBody = requestBody.with("branchPath", branchPath.getPath());
		}
		
		return requestBody;
	}

	public static ValidatableResponse getCodeSystem(String id) {
		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.get("/codesystems/{id}", id)
				.then();
	}

	public static ValidatableResponse updateCodeSystem(String id, Map<?, ?> requestBody) {
		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put("codesystems/{id}", id)
				.then();
	}
	
	public static ValidatableResponse upgrade(CodeSystemURI codeSystem, CodeSystemURI extensionOf) {
		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(Map.of(
					"extensionOf", extensionOf.toString(),
					"upgradeOf", codeSystem.toString()
				))
				.post("/upgrade")
				.then();
	}
	
	public static ValidatableResponse search(String id, String expand) {
		return givenAuthenticatedRequest(ApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(Map.of(
						"id", Set.of(id),
						"expand", expand
					))
				.post("/codesystems/search")
				.then();
	}

	private CodeSystemRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}

}
