/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * A set of assert methods related to the branching support in the REST API.
 * 
 * @since 2.0
 */
public abstract class SnomedBranchingApiAssert {

	private static Response whenCreatingBranch(final IBranchPath branchPath, final Map<?, ?> metadata) {
		final Map<?, ?> requestBody = ImmutableMap.<String, Object> builder()
				.put("parent", branchPath.getParentPath())
				.put("name", branchPath.lastSegment())
				.put("metadata", metadata).build();

		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.and().contentType(ContentType.JSON)
				.and().body(requestBody)
				.when().post("/branches");
	}

	private static ValidatableResponse assertBranchCreatedWithStatus(final IBranchPath branchPath, final Map<?, ?> metadata, final int statusCode) {
		return whenCreatingBranch(branchPath, metadata)
				.then().assertThat().statusCode(statusCode);
	}

	/**
	 * Asserts that a branch with the given branch path can be created.
	 * <p>
	 * Note that parent branches will not be created recursively.
	 * 
	 * @param branchPath the branch path to test
	 */
	public static void givenBranchWithPath(final IBranchPath branchPath) {
		givenBranchWithPathAndMetadata(branchPath, ImmutableMap.of());
	}

	/**
	 * Asserts that a branch with the given branch path and metadata can be created.
	 * <p>
	 * Note that parent branches will not be created recursively.
	 * 
	 * @param branchPath the branch path to test
	 * @param metadata the metadata to register with the new branch
	 */
	public static void givenBranchWithPathAndMetadata(final IBranchPath branchPath, final Map<?, ?> metadata) {
		assertBranchCreatedWithStatus(branchPath, metadata, 201)
		.and().header("Location", endsWith(String.format("/branches/%s", branchPath.getPath())));
	}

	/**
	 * Asserts that a branch creation with the given path triggers a 400 response.
	 * 
	 * @param branchPath the branch path to test
	 */
	public static void assertBranchNotCreated(final IBranchPath branchPath) {
		assertBranchNotCreated(branchPath, ImmutableMap.of());
	}

	/**
	 * Asserts that a branch creation with the given path and metadata triggers a 400 response.
	 * 
	 * @param branchPath the branch path to test
	 * @param metadata the associated branch metadata
	 */
	public static void assertBranchNotCreated(final IBranchPath branchPath, final Map<?, ?> metadata) {
		assertBranchCreatedWithStatus(branchPath, metadata, 400)
		.and().body("status", equalTo(400));
	}

	/**
	 * Asserts that a branch creation with the given path and metadata triggers a 409 response.
	 * 
	 * @param branchPath the branch path to test
	 */
	public static void assertBranchCreationConflicts(final IBranchPath branchPath) {
		assertBranchCreatedWithStatus(branchPath, ImmutableMap.of(), 409);
	}

	/**
	 * Asserts that a branch can be successfully marked as deleted.
	 * 
	 * @param branchPath the branch path to test
	 */
	public static void whenDeletingBranchWithPath(final IBranchPath branchPath) {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
		.when().delete("/branches/{path}", branchPath.getPath())
		.then().assertThat().statusCode(204);
	}

	/**
	 * Asserts that a branch exists, but is marked as deleted.
	 * 
	 * @param branchPath the branch path to test
	 */
	public static void assertBranchReportedAsDeleted(final IBranchPath branchPath) {
		assertBranchExists(branchPath).and().body("deleted", equalTo(true));
	}

	/**
	 * Asserts that a branch triggers the specified response code when read.
	 * 
	 * @param branchPath the branch path to test
	 * @param statusCode the expected status code
	 */
	public static ValidatableResponse assertBranchReadWithStatus(final IBranchPath branchPath, final int statusCode) {
		return givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
				.when().get("/branches/{path}", branchPath.getPath())
				.then().assertThat().statusCode(statusCode);
	}

	/**
	 * Asserts that the specified branch exists.
	 * 
	 * @param branchPath the branch path to test
	 */
	public static ValidatableResponse assertBranchExists(final IBranchPath branchPath) {
		return assertBranchReadWithStatus(branchPath, 200);
	}

	/**
	 * Asserts that the specified branch does not exist.
	 * 
	 * @param branchPath the branch path to test
	 */
	public static ValidatableResponse assertBranchNotExists(final IBranchPath branchPath) {
		return assertBranchReadWithStatus(branchPath, 404)
				.and().body("status", equalTo(404));
	}

	private static void assertItemsContains(final Response response, final String name) {
		response
		.then().assertThat().statusCode(200)
		.and().body("items.name", hasItem(name));
	}

	/**
	 * Asserts that the list of all branches returned contains the given child name.
	 * 
	 * @param branchPath the branch path to test
	 */
	public static void assertBranchesContainsName(final IBranchPath branchPath) {
		assertItemsContains(
				givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API).when().get("/branches"), 
				branchPath.lastSegment());
	}

	/**
	 * Asserts that the list of child branches returned for the specified path's parent contains the last segment of the path.
	 * 
	 * @param branchPath the branch path to test
	 */
	public static void assertBranchChildrenContainsName(final IBranchPath branchPath) {
		assertItemsContains(
				givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API).when().get("/branches/{path}/children", branchPath.getParentPath()),
				branchPath.lastSegment());
	}

	// --------------------------------------------------------
	// Rebase and merge assertions
	// --------------------------------------------------------

	// XXX: the merge service figures out what to do by inspecting the relationship between source and target
	public static Response whenMergingOrRebasingBranches(final IBranchPath source, final IBranchPath target, final String commitComment) {
		return whenMergingOrRebasingBranches(source, target, commitComment, null);
	}

	public static Response whenMergingOrRebasingBranches(final IBranchPath source, final IBranchPath target, final String commitComment, final String reviewId) {
		final ImmutableMap.Builder<String, Object> requestBuilder = ImmutableMap.<String, Object>builder()
				.put("source", source.getPath())
				.put("target", target.getPath())
				.put("commitComment", commitComment);
				
		if (null != reviewId) {
			requestBuilder.put("reviewId", reviewId);
		}
		
		return givenAuthenticatedRequest(SCT_API)
				.with().contentType(ContentType.JSON)
				.and().body(requestBuilder.build())
				.when().post("/merges");
	}

	public static void assertBranchCanBeMerged(final IBranchPath branchPath, final String commitComment) {
		whenMergingOrRebasingBranches(branchPath, branchPath.getParent(), commitComment)
		.then()
			.statusCode(204);
	}

	public static void assertBranchCanBeRebased(final IBranchPath branchPath, final String commitComment) {
		whenMergingOrRebasingBranches(branchPath.getParent(), branchPath, commitComment)
		.then().
			statusCode(204);
	}

	public static void assertBranchConflicts(final IBranchPath source, final IBranchPath target, final String commitComment) {
		whenMergingOrRebasingBranches(source, target, commitComment)
		.then()
			.statusCode(409);
	}
	
	private SnomedBranchingApiAssert() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
