/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.branches;

import static com.b2international.snowowl.core.branch.BranchPathUtils.createPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.google.common.collect.ImmutableMap;

/**
 * @since 2.0
 */
public class SnomedBranchingApiTest extends AbstractSnomedApiTest {

	@Test
	public void readNonExistentBranch() {
		branching.getBranch(createPath("MAIN/x/y/z")).statusCode(404);
	}

	@Test
	public void createBranchWithNonexistentParent() {
		branching.createBranch(createPath("MAIN/x/y/z")).statusCode(400);
	}

	@Test
	public void updateMetadata() throws Exception {
		branching.updateBranch(branchPath, ImmutableMap.of("key", "value"));
		branching.getBranch(branchPath).body("metadata.key", equalTo("value"));
	}

	@Test
	public void updateMainMetadata() throws Exception {
		// XXX: modifies MAIN branch, may affect other tests
		branching.updateBranch(BranchPathUtils.createMainPath(), ImmutableMap.of("key", "value"));
		branching.getBranch(BranchPathUtils.createMainPath()).body("metadata.key", equalTo("value"));
	}

	@Test
	public void createChildBranch() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		branching.createBranch(a).statusCode(201);
		branching.getBranch(a).statusCode(200);
	}

	@Test
	public void createChildBranchWithMetadata() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		branching.createBranch(a, ImmutableMap.of("key", "value")).statusCode(201);
		branching.getBranch(a).statusCode(200).body("metadata.key", equalTo("value"));
	}

	@Test
	public void createBranchTwice() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		branching.createBranch(a).statusCode(201);
		branching.createBranch(a).statusCode(409);
	}

	@Test
	public void deleteChildBranch() {
		IBranchPath a = BranchPathUtils.createPath(branchPath, "a");
		branching.createBranch(a).statusCode(201);
		branching.deleteBranch(a).statusCode(204);
		branching.getBranch(a).statusCode(200).body("deleted", equalTo(true));
	}

	@Test
	public void deleteBranchRecursively() {
		IBranchPath a = createPath(branchPath, "a");
		IBranchPath b = createPath(a, "b");

		branching.createBranchRecursively(b);
		branching.getBranch(b).statusCode(200);
		branching.getBranch(a).statusCode(200);

		branching.deleteBranch(a).statusCode(204);
		branching.getBranch(a).statusCode(200).body("deleted", equalTo(true));
		branching.getBranch(b).statusCode(200).body("deleted", equalTo(true));
	}

	@Test
	public void createBranchOnDeletedBranch() {
		IBranchPath a = createPath(branchPath, "a");
		IBranchPath b = createPath(a, "b");

		branching.createBranch(a).statusCode(201);
		branching.deleteBranch(a).statusCode(204);

		branching.createBranch(b).statusCode(400);
	}

	@Test
	public void getChildren() {
		IBranchPath a = createPath(branchPath, "a");
		IBranchPath b = createPath(a, "b");

		branching.createBranchRecursively(b);

		branching.getAllBranches().statusCode(200)
				.body("items.name", hasItem(a.lastSegment()))
				.body("items.name", hasItem(b.lastSegment()));

		branching.getBranchChildren(branchPath).statusCode(200)
				.body("items.name", hasItem(a.lastSegment()))
				.body("items.name", hasItem(b.lastSegment()));
	}

}
