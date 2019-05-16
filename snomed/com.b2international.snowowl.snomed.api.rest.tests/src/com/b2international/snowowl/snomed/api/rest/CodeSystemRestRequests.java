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

import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.collect.ImmutableMap;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

/**
 * @since 4.7
 */
public abstract class CodeSystemRestRequests {

	public static ValidatableResponse createCodeSystem(IBranchPath branchPath, String shortName) {
		Map<?,?> requestBody = ImmutableMap.builder()
				.put("name", shortName)
				.put("branchPath", branchPath.getPath())
				.put("shortName", shortName)
				.put("citation", "citation")
				.put("iconPath", "iconPath")
				.put("repositoryUuid", SnomedDatastoreActivator.REPOSITORY_UUID)
				.put("terminologyId", SnomedTerminologyComponentConstants.TERMINOLOGY_ID)
				.put("oid", "oid_" + shortName)
				.put("primaryLanguage", "primaryLanguage")
				.put("organizationLink", "organizationLink")
				.build();

		return givenAuthenticatedRequest(SnomedApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/codesystems")
				.then();
	}

	public static ValidatableResponse getCodeSystem(String id) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.ADMIN_API)
				.get("/codesystems/{id}", id)
				.then();
	}

	public static ValidatableResponse updateCodeSystem(String id, Map<?, ?> requestBody) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.ADMIN_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put("codesystems/{id}", id)
				.then();
	}

	private CodeSystemRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
