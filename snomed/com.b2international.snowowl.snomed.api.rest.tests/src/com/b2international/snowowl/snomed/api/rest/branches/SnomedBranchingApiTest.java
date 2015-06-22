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
package com.b2international.snowowl.snomed.api.rest.branches;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.joinPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
public class SnomedBranchingApiTest extends AbstractSnomedApiTest {

	@Test
	public void readNonExistentBranch() {
		givenAuthenticatedRequest(API)
		.when()
			.get("/branches/MAIN/{branchName}", "nonexistent")
		.then()
		.assertThat()
			.statusCode(404)
		.and()
			.body("status", equalTo(404));
	}

	@Test
	public void createBranchWithNonexistentParent() {
		whenCreatingBranch(givenAuthenticatedRequest(API), "nonexistent", branchName)
		.then()
		.assertThat()
			.statusCode(400)
		.and()
			.body("status", equalTo(400));
	}

	private ValidatableResponse assertBranchExists(final String parent, final String name) {
		return givenAuthenticatedRequest(API)
		.when()
			.get("/branches/{parent}/{branchName}", parent, name)
		.then()
		.assertThat()
			.statusCode(200);
	}

	@Test
	public void createBranch() {
		assertBranchCanBeCreated("MAIN", branchName);
		assertBranchExists("MAIN", branchName);
	}
	
	@Test
	public void createBranchWithMetadata() {
		final String description = "Description of branch";
		final Map<?, ?> metadata = ImmutableMap.of(
			"description", description
		);
		
		assertBranchCanBeCreated("MAIN", branchName, metadata);
		assertBranchExists("MAIN", branchName)
		.and()
			.body("metadata.description", equalTo(description));
	}

	@Test
	public void createBranchTwice() {
		assertBranchCanBeCreated("MAIN", branchName);
		
		whenCreatingBranch(givenAuthenticatedRequest(API), "MAIN", branchName)
		.then()
		.assertThat()
			.statusCode(409);
	}
	
	private void assertBranchCanBeDeleted(final String parent, final String name) {
		givenAuthenticatedRequest(API)
		.when()
			.delete("/branches/{parent}/{name}", parent, name)
		.then()
		.assertThat()
			.statusCode(204);
	}

	private void assertBranchIsDeleted(final String parent, final String name) {
		assertBranchExists(parent, name)
		.and()
			.body("deleted", equalTo(true));
	}

	@Test
	public void deleteBranch() {
		assertBranchCanBeCreated("MAIN", branchName);
		assertBranchCanBeDeleted("MAIN", branchName);
		assertBranchIsDeleted("MAIN", branchName);
	}

	@Test
	public void deleteBranchRecursively() {
		assertBranchCanBeCreated("MAIN", branchName);
		assertBranchCanBeCreated(joinPath("MAIN", branchName), "child");
		
		assertBranchExists("MAIN", branchName);
		assertBranchExists(joinPath("MAIN", branchName), "child");
		
		assertBranchCanBeDeleted("MAIN", branchName);
		
		assertBranchIsDeleted("MAIN", branchName);
		assertBranchIsDeleted(joinPath("MAIN", branchName), "child");
	}
	
	@Test
	public void createBranchOnDeletedBranch() {
		assertBranchCanBeCreated("MAIN", branchName);
		assertBranchCanBeDeleted("MAIN", branchName);
		
		whenCreatingBranch(givenAuthenticatedRequest(API), joinPath("MAIN", branchName), "childOfDeletedBranch")
		.then()
		.assertThat()
			.statusCode(400)
		.and()
			.body("status", equalTo(400));
	}
	
	private void assertBranchChildrenContains(String childrenUrl, String childName) {
		givenAuthenticatedRequest(API)
		.when()
			.get(childrenUrl)
		.then()
		.assertThat()
			.statusCode(200)
		.and()
			.body("items.name", hasItem(childName));
	}

	@Test
	public void getChildren() {
		assertBranchCanBeCreated("MAIN", branchName);
		assertBranchChildrenContains("/branches", branchName);
		assertBranchChildrenContains("/branches/MAIN/children", branchName);
	}
}
