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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.datastore.branch.BranchImpl.BranchState;

/**
 * @since 4.1
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
		assertPath(branch, "MAIN/p1");
	}
	
	@Test
	public void createWithSamePathAndBaseTimestampShouldBeEqual() {
		Branch branch1 = createBranch(main, "p1");
		Branch branch2 = new BranchImpl(main, "p1", branch1.baseTimestamp());
		assertEquals(branch1, branch2);
	}

	@Test
	public void createWithDifferentPathsButSameBasetimestampsShouldNotBeEqual() {
		Branch branch1 = createBranch(main, "p1");
		Branch branch2 = createBranch(main, "p2");
		assertNotEquals(branch1, branch2);
	}
	
	@Test
	public void createWithSamePathButDifferentBasetimestampShouldNotBeEqual() throws Exception {
		Branch branch1 = createBranch(main, "p1");
		Branch branch2 = createBranch(main, "p1");
		assertNotEquals(branch1, branch2);
	}
	
	@Test
	public void createWithDifferentPathAndBaseTimestampShouldNotBeEqual() throws Exception {
		Branch branch1 = createBranch(main, "p1");
		Branch branch2 = createBranch(main, "p2");
		assertNotEquals(branch1, branch2);
	}
	
	@Test
	public void createWithPathName() {
		Branch branch2 = createBranch(branchA, "b");
		assertEquals("a", branchA.name());
		assertEquals("b", branch2.name());
	}

	@Test
	public void createWithPathParent() throws Exception {
		Branch branch2 = createBranch(branchA, "b");
		assertEquals(branchA, branch2.parent());
		assertEquals(main, branchA.parent());
	}

	@Test(expected=IllegalArgumentException.class)
	public void branchBeforeParentHead() throws Exception {
		new BranchImpl(main, "a", main.baseTimestamp() - 1);
	}

	@Test
	public void testForwardState() throws Exception {
		assertState(branchA, BranchState.UP_TO_DATE);
		commit(branchA);
		assertState(branchA, BranchState.FORWARD);
	}

	@Test
	public void testBehindState() throws Exception {
		assertState(branchA, BranchState.UP_TO_DATE);
		commit(main);
		assertState(branchA, BranchState.BEHIND);
	}

	@Test
	public void testDivergedState() throws Exception {
		assertState(branchA, BranchState.UP_TO_DATE);
		commit(main);
		commit(branchA);
		assertState(branchA, BranchState.DIVERGED);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void mergeSelf() throws Exception {
		main.merge(main);
	}
	
	@Test
	public void mergeForward() throws Exception {
		testForwardState();
		main.merge(branchA);
		assertState(branchA, BranchState.DIVERGED);
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
		newBranchA = branchA.rebase();
		assertState(newBranchA, BranchState.UP_TO_DATE);
		assertState(branchA, BranchState.BEHIND);
		assertLaterBase(newBranchA, main);
		assertLaterBase(newBranchA, branchA);
	}

	@Test
	public void rebaseDivergedState() throws Exception {
		testDivergedState();
		newBranchA = branchA.rebase();
		assertState(newBranchA, BranchState.FORWARD);
		assertState(branchA, BranchState.DIVERGED);
		assertLaterBase(newBranchA, main);
		assertLaterBase(newBranchA, branchA);
	}

	@Test
	public void rebaseDivergedStateWithMultipleCommits() throws Exception {
		testDivergedState();
		commit(branchA);
		newBranchA = branchA.rebase();
		assertState(newBranchA, BranchState.FORWARD);
		assertState(branchA, BranchState.DIVERGED);
		assertLaterBase(newBranchA, main);
		assertLaterBase(newBranchA, branchA);
	}
	
	@Test
	public void rebaseDivergedWithBehindChild() throws Exception {
		commit(main);
		branchB = createBranch(branchA, "b");
		commit(branchA);
		assertState(branchA, BranchState.DIVERGED);
		assertState(branchB, BranchState.BEHIND);
		newBranchA = branchA.rebase();
		assertState(newBranchA, BranchState.FORWARD);
		assertState(branchA, BranchState.DIVERGED);
		assertLaterBase(newBranchA, main);
		assertLaterBase(newBranchA, branchA);
		assertState(branchB, branchA, BranchState.BEHIND);
		assertState(branchB, newBranchA, BranchState.STALE);
		assertParent(branchB, branchA);
	}
	
	@Test
	public void rebaseBehindWithForwardChild() throws Exception {
		commit(main);
		branchB = createBranch(branchA, "b");
		commit(branchB);
		assertState(branchA, BranchState.BEHIND);
		assertState(branchB, BranchState.FORWARD);
		newBranchA = branchA.rebase();
		assertState(newBranchA, BranchState.UP_TO_DATE);
		assertState(branchA, BranchState.BEHIND);
		assertLaterBase(newBranchA, main);
		assertLaterBase(newBranchA, branchA);
		assertState(branchB, branchA, BranchState.FORWARD);
		assertState(branchB, newBranchA, BranchState.STALE);
		assertParent(branchB, branchA);
	}
	
	@Test
	public void rebaseDivergedWithTwoChildren() throws Exception {
		commit(main);
		branchB = createBranch(branchA, "b");
		commit(branchA);
		final Branch branchC = createBranch(branchA, "c");
		newBranchA = branchA.rebase();
		assertState(newBranchA, BranchState.FORWARD);
		assertState(branchA, BranchState.DIVERGED);
		assertState(branchB, branchA, BranchState.BEHIND);
		assertState(branchB, newBranchA, BranchState.STALE);
		assertState(branchC, branchA, BranchState.UP_TO_DATE);
		assertState(branchC, newBranchA, BranchState.STALE);
	}
	
	@Test
	public void rebaseBehindChildOnRebasedForwardParent() throws Exception {
		rebaseDivergedWithBehindChild();
		final Branch newBranchB = branchB.rebase(newBranchA);
		assertState(newBranchB, BranchState.UP_TO_DATE);
	}
	
	private void commit(Branch branch) {
		branch.handleCommit(currentTimestamp());		
	}
	
	private Branch createBranch(Branch parent, String name) {
		final BranchImpl branch = new BranchImpl(parent, name, currentTimestamp());
		branch.setTimestampAuthority(clock);
		return branch;
	}
	
	private static void assertState(Branch branch, BranchState expectedState) {
		assertEquals(String.format("Incorrect branch state of branch '%s' ", branch.path()), expectedState, branch.state());
	}
	
	private static void assertState(final Branch branch, final Branch other, final BranchState expectedState) {
		assertEquals(String.format("Incorrect branch state of branch '%s' compared to branch '%s'", branch.path(), other.path()), expectedState, branch.state(other));
	}
	
	private static void assertPath(Branch branch, String expectedPath) {
		assertEquals("Branch path should be '%s'.", expectedPath, branch.path());
	}
	
	private static void assertLaterBase(Branch branch, Branch other) {
		assertTrue(String.format("Basetimestamp of branch '%s' should be later than headTimestamp of '%s'.", branch.path(), other.path()), branch.baseTimestamp() > other.headTimestamp());
	}
	
	private static void assertParent(Branch branch, Branch parent) {
		assertEquals(String.format("Parent of branch '%s' should be '%s'.", branch.path(), parent.path()), branch.parent(), parent);		
	}
	
}
