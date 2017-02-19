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

import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.SCT_API;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * A set of assert methods related to the branching support in the REST API.
 * 
 * @since 2.0
 */
public abstract class SnomedBranchingRestRequests {

	public static ValidatableResponse createBranch(IBranchPath branchPath) {
		return createBranch(branchPath, ImmutableMap.of());
	}

	public static ValidatableResponse createBranch(IBranchPath branchPath, Map<?, ?> metadata) {
		Map<?, ?> requestBody = ImmutableMap.<String, Object> builder()
				.put("parent", branchPath.getParentPath())
				.put("name", branchPath.lastSegment())
				.put("metadata", metadata)
				.build();

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.post("/branches")
				.then();
	}

	public static ValidatableResponse getBranch(IBranchPath branchPath) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/branches/{path}", branchPath.getPath())
				.then();
	}

	public static ValidatableResponse getAllBranches() {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/branches")
				.then();
	}

	public static ValidatableResponse getBranchChildren(IBranchPath branchPath) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.get("/branches/{path}/children", branchPath.getParentPath())
				.then();
	}

	public static void createBranchRecursively(IBranchPath branchPath) {
		createBranchRecursively(branchPath, ImmutableMap.of());
	}

	public static void createBranchRecursively(IBranchPath branchPath, Map<?, ?> metadata) {
		IBranchPath currentPath = branchPath;
		ValidatableResponse response = getBranch(currentPath);
		List<String> segmentsToCreate = newArrayList();

		// Step upwards until we find an existing branch
		while (response.extract().statusCode() == 404) {
			segmentsToCreate.add(segmentsToCreate.size(), currentPath.lastSegment());
			currentPath = currentPath.getParent();
			response = getBranch(currentPath);
		}

		// No response should return with a status outside of 404, then 200 
		response.assertThat().statusCode(200);

		// Step downwards and create all non-existing segments
		while (!segmentsToCreate.isEmpty()) {
			currentPath = BranchPathUtils.createPath(currentPath, segmentsToCreate.remove(segmentsToCreate.size() - 1));
			Map<?, ?> currentMetadata = segmentsToCreate.isEmpty() ? metadata : ImmutableMap.of();
			createBranch(currentPath, currentMetadata).assertThat().statusCode(201);
		}
	}

	public static ValidatableResponse updateBranch(IBranchPath branchPath, Map<?, ?> metadata) {
		Map<?, ?> requestBody = ImmutableMap.of("metadata", metadata);

		return givenAuthenticatedRequest(SCT_API)
				.contentType(ContentType.JSON)
				.body(requestBody)
				.put("/branches/{path}", branchPath)
				.then();
	}

	public static ValidatableResponse deleteBranch(IBranchPath branchPath) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.delete("/branches/{path}", branchPath.getPath())
				.then();
	}

	private SnomedBranchingRestRequests() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
