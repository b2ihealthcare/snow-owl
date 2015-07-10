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

import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.*;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.google.common.collect.ImmutableMap;

/**
 * @since 2.0
 */
public class SnomedBranchingApiTest extends AbstractSnomedApiTest {

	@Test
	public void readNonExistentBranch() {
		assertBranchNotExists(createPath("MAIN/nonexistent"));
	}

	@Test
	public void createBranchWithNonexistentParent() {
		assertBranchNotCreated(createPath(testBranchPath, "a"));
	}

	@Test
	public void createBranch() {
		givenBranchWithPath(testBranchPath);
		assertBranchExists(testBranchPath);
	}

	@Test
	public void createBranchWithMetadata() {
		final String description = "Description of branch";
		final Map<?, ?> metadata = ImmutableMap.of("description", description);

		givenBranchWithPathAndMetadata(testBranchPath, metadata);
		assertBranchExists(testBranchPath)
		.and().body("metadata.description", equalTo(description));
	}

	@Test
	public void createBranchTwice() {
		givenBranchWithPath(testBranchPath);
		assertBranchCreationConflicts(testBranchPath);
	}

	@Test
	public void deleteBranch() {
		givenBranchWithPath(testBranchPath);
		whenDeletingBranchWithPath(testBranchPath);
		assertBranchReportedAsDeleted(testBranchPath);
	}

	@Test
	public void deleteBranchRecursively() {
		final IBranchPath secondBranchPath = createPath(testBranchPath, "child");

		givenBranchWithPath(testBranchPath);
		givenBranchWithPath(secondBranchPath);

		assertBranchExists(testBranchPath);
		assertBranchExists(secondBranchPath);

		whenDeletingBranchWithPath(testBranchPath);
		assertBranchReportedAsDeleted(testBranchPath);
		assertBranchReportedAsDeleted(secondBranchPath);
	}

	@Test
	public void createBranchOnDeletedBranch() {
		final IBranchPath secondBranchPath = createPath(testBranchPath, "childOfDeletedBranch");

		givenBranchWithPath(testBranchPath);
		whenDeletingBranchWithPath(testBranchPath);

		assertBranchNotCreated(secondBranchPath);
	}

	@Test
	public void getChildren() {
		givenBranchWithPath(testBranchPath);

		assertBranchesContainsName(testBranchPath);
		assertBranchChildrenContainsName(testBranchPath);
	}
}
