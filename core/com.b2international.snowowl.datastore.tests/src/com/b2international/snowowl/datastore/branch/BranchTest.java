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

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.datastore.branch.BranchImpl.BranchState;

/*
 * TODO: change all state assertions 
 */
public class BranchTest {

	private AtomicLongTimestampAuthority clock = new AtomicLongTimestampAuthority();
	private MainBranch main;
	private Branch branchA;
	private Branch branchB;
	private Branch newBranchA;
	
	@Before
	public void before() {
		main = new MainBranch(currentTimestamp());
		main.setTimestampAuthority(clock);
		branchA = createBranch(main, "a");
	}
	
	private long currentTimestamp() {
		return clock.getTimestamp();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createWithNameShouldNotContainSeparator() {
		createBranch(main, "a/s");
	}

	@Test(expected=IllegalArgumentException.class)
	public void createWithNameShouldNotBeEmpty() {
		createBranch(main, "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void createWithNameShouldNotHaveInvalidCharacters() {
		createBranch(main, "?b");
	}

	@Test(expected=IllegalArgumentException.class)
	public void createWithNameShouldNotHaveLongerThan50Char() {
		createBranch(main, "123456789012345678901234567890123456789012345678901");
	}

	@Test
	public void createWith50CharName() {
		createBranch(main, "12345678901234567890123456789012345678901234567890");
	}

	@Test
	public void createWithMainChildPath() {
		Branch branch = createBranch(main, "p1");
		assertEquals("Branch path should be 'MAIN/p1'.", "MAIN/p1", branch.path());
	}

	@Test
	public void createWithSamePathAndBaseTimestampShouldBeEqual() {
		Branch branch1 = createBranch(main, "p1");
		Branch branch2 = new BranchImpl(main, "p1", branch1.baseTimestamp());
		assertTrue("Branches 'MAIN/p1' and 'MAIN/p1' should be equal.", branch1.equals(branch2));
	}

	@Test
	public void createWithDifferentPathsButSameBasetimestampsShouldNotBeEqual() {
		Branch branch1 = createBranch(main, "p1");
		Branch branch2 = createBranch(main, "p2");
		assertFalse("Branches 'MAIN/p1' and 'MAIN/p2' should not be equal.", branch1.equals(branch2));
	}
	
	@Test
	public void createWithSamePathButDifferentBasetimestampShouldNotBeEqual() throws Exception {
		Branch branch1 = createBranch(main, "p1");
		Branch branch2 = createBranch(main, "p1");
		assertFalse("Branches 'MAIN/p1' and 'MAIN/p1' should not be equal.", branch1.equals(branch2));
	}
	
	@Test
	public void createWithDifferentPathAndBaseTimestampShouldNotBeEqual() throws Exception {
		Branch branch1 = createBranch(main, "p1");
		Branch branch2 = createBranch(main, "p2");
		assertFalse("Branches 'MAIN/p1' and 'MAIN/p2' should not be equal.", branch1.equals(branch2));
	}
	
	@Test
	public void createWithPathName() {
		Branch branch2 = createBranch(branchA, "b");
		assertEquals("Name of branch 'MAIN/a' should be 'a'.", "a", branchA.name());
		assertEquals("Name of branch 'MAIN/a/b' should be 'b'.", "b", branch2.name());
	}

	@Test
	public void createWithPathParent() throws Exception {
		Branch branch2 = createBranch(branchA, "b");
		assertEquals("Parent of branch 'MAIN/a/b' should be branch 'MAIN/a'.", branchA, branch2.parent());
		assertEquals("Parent of branch 'MAIN/a' should be branch 'MAIN'.", main, branchA.parent());
	}

	@Test(expected=IllegalArgumentException.class)
	public void branchBeforeParentHead() throws Exception {
		new BranchImpl(main, "a", main.baseTimestamp() - 1);
	}

	@Test
	public void testForwardState() throws Exception {
		assertEquals("Branch 'MAIN/a' should be in UP_TO_DATE state initially.", BranchState.UP_TO_DATE, branchA.state());
		commit(branchA);
		assertEquals("Branch 'MAIN/a' should be in FORWARD state after committing changes to it.", BranchState.FORWARD, branchA.state());
	}

	@Test
	public void testBehindState() throws Exception {
		assertEquals("Branch 'MAIN/a' should be in UP_TO_DATE state initially.", BranchState.UP_TO_DATE, branchA.state());
		commit(main);
		assertEquals("Branch 'MAIN/a' should be in BEHIND state after committing changes to the parent.", BranchState.BEHIND, branchA.state());
	}

	@Test
	public void testDivergedState() throws Exception {
		assertEquals("Branch 'MAIN/a' should be in UP_TO_DATE state initially.", BranchState.UP_TO_DATE, branchA.state());
		commit(main);
		commit(branchA);
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
		assertState("Branch '%s' should be in %s state after merging.", branchA, BranchState.DIVERGED);
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
	
	@Test
	public void rebaseUpToDateState() throws Exception {
		clock.advance(9L);
		final Branch branchAWithTimestamp = createBranch(main, "a");
		final long expected = branchAWithTimestamp.baseTimestamp();
		branchAWithTimestamp.rebase();
		assertEquals("Rebasing UP_TO_DATE branch should do nothing", expected, branchAWithTimestamp.baseTimestamp());
	}
	
	@Test
	public void rebaseForwardState() throws Exception {
		clock.advance(9L);
		final Branch branchAWithTimestamp = createBranch(main, "a");
		final long expected = branchAWithTimestamp.baseTimestamp();
		commit(branchAWithTimestamp);
		branchAWithTimestamp.rebase();
		assertEquals("Rebasing FORWARD branch should do nothing", expected, branchAWithTimestamp.baseTimestamp());
	}
	
	@Test
	public void rebaseBehindState() throws Exception {
		testBehindState();
		final Branch newBranchA = branchA.rebase();
		assertState("Rebased branch '%s' should be in %s state after rebase.", newBranchA, BranchState.UP_TO_DATE);
		assertState("Original branch '%s' should remain in %s state after rebase", branchA, BranchState.BEHIND);
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of 'MAIN'.", newBranchA.baseTimestamp() > main.headTimestamp());
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of original 'MAIN/a' branch.", newBranchA.baseTimestamp() > branchA.headTimestamp());
	}

	@Test
	public void rebaseDivergedState() throws Exception {
		testDivergedState();
		final Branch newBranchA = branchA.rebase();
		assertState("Rebased branch '%s' should be in %s state after rebase.", newBranchA, BranchState.FORWARD);
		assertState("Original branch '%s' should remain in %s state after rebase", branchA, BranchState.DIVERGED);
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of 'MAIN'.", newBranchA.baseTimestamp() > main.headTimestamp());
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of original 'MAIN/a' branch.", newBranchA.baseTimestamp() > branchA.headTimestamp());
	}

	@Test
	public void rebaseDivergedStateWithMultipleCommits() throws Exception {
		testDivergedState();
		commit(branchA);
		final Branch newBranchA = branchA.rebase();
		assertState("Rebased branch '%s' should be in %s state after rebase.", newBranchA, BranchState.FORWARD);
		assertState("Original branch '%s' should remain in %s state after rebase", branchA, BranchState.DIVERGED);
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of 'MAIN'.", newBranchA.baseTimestamp() > main.headTimestamp());
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of original 'MAIN/a' branch.", newBranchA.baseTimestamp() > branchA.headTimestamp());
	}
	
	@Test
	public void rebaseDivergedWithBehindChild() throws Exception {
		commit(main);
		branchB = createBranch(branchA, "b");
		commit(branchA);
		assertState("Branch '%s' should be in %s state after committing changes both to the parent and the branch itself.", branchA, BranchState.DIVERGED);
		assertState("Child branch '%s' should be in %s state before rebase", branchB, BranchState.BEHIND);
		newBranchA = branchA.rebase();
		assertState("Rebased branch '%s' should be in %s state after rebase.", newBranchA, BranchState.FORWARD);
		assertState("Original branch '%s' should remain in %s state after rebase", branchA, BranchState.DIVERGED);
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of 'MAIN'.", newBranchA.baseTimestamp() > main.headTimestamp());
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of original 'MAIN/a' branch.", newBranchA.baseTimestamp() > branchA.headTimestamp());
		assertState("Child branch '%s' should be in %s state compared to its original parent", branchB, branchA, BranchState.BEHIND);
		assertState("Child branch '%s' should be in %s state compared to the rebased parent", branchB, newBranchA, BranchState.STALE);
		assertEquals("Parent of child branch 'MAIN/a/b' should be the original 'MAIN/a'.", branchB.parent(), branchA);
	}
	
	@Test
	public void rebaseBehindWithForwardChild() throws Exception {
		commit(main);
		branchB = createBranch(branchA, "b");
		commit(branchB);
		assertState("Branch '%s' should be in %s state after committing changes both to the parent and the branch itself.", branchA, BranchState.BEHIND);
		assertState("Child branch '%s' should be in %s state before rebase", branchB, BranchState.FORWARD);
		newBranchA = branchA.rebase();
		assertState("Rebased branch '%s' should be in %s state after rebase.", newBranchA, BranchState.UP_TO_DATE);
		assertState("Original branch '%s' should remain in %s state after rebase", branchA, BranchState.BEHIND);
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of 'MAIN'.", newBranchA.baseTimestamp() > main.headTimestamp());
		assertTrue("Basetimestamp of rebased branch 'MAIN/a' should be later than headTimestamp of original 'MAIN/a' branch.", newBranchA.baseTimestamp() > branchA.headTimestamp());
		assertState("Child branch '%s' should be in %s state compared to its original parent", branchB, branchA, BranchState.FORWARD);
		assertState("Child branch '%s' should be in %s state compared to the rebased parent", branchB, newBranchA, BranchState.STALE);
		assertEquals("Parent of child branch 'MAIN/a/b' should be the original 'MAIN/a'.", branchB.parent(), branchA);
	}
	
	@Test
	public void rebaseDivergedWithTwoChildren() throws Exception {
		commit(main);
		branchB = createBranch(branchA, "b");
		commit(branchA);
		final Branch branchC = createBranch(branchA, "c");
		newBranchA = branchA.rebase();
		assertState("Rebased branch '%s' should be in %s state after rebase.", newBranchA, BranchState.FORWARD);
		assertState("Original branch '%s' should remain in %s state after rebase", branchA, BranchState.DIVERGED);
		assertState("Child branch '%s' should be in %s state compared to its original parent", branchB, branchA, BranchState.BEHIND);
		assertState("Child branch '%s' should be in %s state compared to the rebased parent", branchB, newBranchA, BranchState.STALE);
		assertState("Child branch '%s' should be in %s state compared to its original parent", branchC, branchA, BranchState.UP_TO_DATE);
		assertState("Child branch '%s' should be in %s state compared to the rebased parent", branchC, newBranchA, BranchState.STALE);
	}
	
	@Test
	public void rebaseBehindChildOnRebasedForwardParent() throws Exception {
		rebaseDivergedWithBehindChild();
		final Branch newBranchB = branchB.rebase(newBranchA);
		assertState("Child branch %s should be in %s state compared to its rebased parent", newBranchB, BranchState.UP_TO_DATE);
	}
	
	private void commit(Branch branch) {
		branch.handleCommit(currentTimestamp());		
	}
	
	private static void assertState(final String message, final Branch branch, final BranchState expectedState) {
		assertEquals(String.format(message, branch.path(), expectedState), expectedState, branch.state());
	}
	
	private static void assertState(final String message, final Branch branch, final Branch other, final BranchState expectedState) {
		assertEquals(String.format(message, branch.path(), expectedState), expectedState, branch.state(other));
	}
	
	private Branch createBranch(Branch parent, String name) {
		final BranchImpl branch = new BranchImpl(parent, name, currentTimestamp());
		branch.setTimestampAuthority(clock);
		return branch;
	}
	
}
