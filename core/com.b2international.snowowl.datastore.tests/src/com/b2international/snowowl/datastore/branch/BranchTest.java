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
package com.b2international.snowowl.datastore.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.snowowl.datastore.branch.Branch.BranchState;

public class BranchTest {

	private Branch main = new MainBranch();
	private Branch mainWithTimestamp = new MainBranch(5L);
	private Branch branchA = new Branch(main, "a", 1L);
	
	@Test(expected=IllegalArgumentException.class)
	public void createWithNameShouldNotContainSeparator() {
		new Branch(main, "a/s", 1L);
	}

	@Test(expected=IllegalArgumentException.class)
	public void createWithNameShouldNotBeEmpty() {
		new Branch(main, "", 1L);
	}

	@Test(expected=IllegalArgumentException.class)
	public void createWithNameShouldNotHaveInvalidCharacters() {
		new Branch(main, "?b", 1L);
	}

	@Test(expected=IllegalArgumentException.class)
	public void createWithNameShouldNotHaveLongerThan50Char() {
		new Branch(main, "123456789012345678901234567890123456789012345678901", 1L);
	}

	@Test
	public void createWith50CharName() {
		new Branch(main, "12345678901234567890123456789012345678901234567890", 1L);
	}

	@Test
	public void createWithMainChildPath() {
		Branch branch = new Branch(main, "p1", 1L);
		assertEquals("Branch path should be 'MAIN/p1'.", "MAIN/p1", branch.path());
	}

	@Test
	public void createWithSamePathShouldBeEqual() {
		Branch branch1 = new Branch(main, "p1", 1L);
		Branch branch2 = new Branch(main, "p1", 1L);
		assertTrue("Branches 'MAIN/p1' and 'MAIN/p1' should be equal.", branch1.equals(branch2));
	}

	@Test
	public void createWithDifferentPathsShouldNotBeEqual() {
		Branch branch1 = new Branch(main, "p1", 1L);
		Branch branch2 = new Branch(main, "p2", 1L);
		assertFalse("Branches 'MAIN/p1' and 'MAIN/p2' should not be equal.", branch1.equals(branch2));
	}

	@Test
	public void createWithPathName() {
		Branch branch2 = new Branch(branchA, "b", 2L);
		assertEquals("Name of branch 'MAIN/a' should be 'a'.", "a", branchA.name());
		assertEquals("Name of branch 'MAIN/a/b' should be 'b'.", "b", branch2.name());
	}

	@Test
	public void createWithPathParent() throws Exception {
		Branch branch2 = new Branch(branchA, "b", 2L);
		assertEquals("Parent of branch 'MAIN/a/b' should be branch 'MAIN/a'.", branchA, branch2.parent());
		assertEquals("Parent of branch 'MAIN/a' should be branch 'MAIN'.", main, branchA.parent());
	}

	@Test(expected=IllegalArgumentException.class)
	public void branchBeforeParentHead() throws Exception {
		new Branch(mainWithTimestamp, "a", 4L);
	}

	@Test
	public void testForwardState() throws Exception {
		assertEquals("Branch 'MAIN/a' should be in UP_TO_DATE state initially.", BranchState.UP_TO_DATE, branchA.state());
		branchA.handleCommit(5L);
		assertEquals("Branch 'MAIN/a' should be in FORWARD state after committing changes to it.", BranchState.FORWARD, branchA.state());
	}

	@Test
	public void testBehindState() throws Exception {
		assertEquals("Branch 'MAIN/a' should be in UP_TO_DATE state initially.", BranchState.UP_TO_DATE, branchA.state());
		main.handleCommit(5L);
		assertEquals("Branch 'MAIN/a' should be in BEHIND state after committing changes to the parent.", BranchState.BEHIND, branchA.state());
	}

	@Test
	public void testDivergedState() throws Exception {
		assertEquals("Branch 'MAIN/a' should be in UP_TO_DATE state initially.", BranchState.UP_TO_DATE, branchA.state());
		main.handleCommit(5L);
		branchA.handleCommit(6L);
		assertEquals("Branch 'MAIN/a' should be in DIVERGED state after committing changes both to the parent and the branch itself.", BranchState.DIVERGED, branchA.state());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void mergeSelf() throws Exception {
		main.merge(main);
	}
	
	@Test
	public void mergeForward() throws Exception {
		testForwardState();
		main.merge(branchA);
		assertEquals("Branch 'MAIN/a' should be in UP_TO_DATE state after merging.", BranchState.DIVERGED, branchA.state());
	}

	@Test(expected=BranchMergeException.class)
	public void mergeBehind() throws Exception {
		testBehindState();
		main.merge(branchA);
	}

	@Test(expected=BranchMergeException.class)
	public void mergeDiverged() throws Exception {
		testDivergedState();
		main.merge(branchA);
	}

	@Test(expected=BranchMergeException.class)
	public void mergeUpToDate() throws Exception {
		main.merge(branchA);
	}
}
