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
package com.b2international.snowowl.datastore.internal.branch;

import static com.b2international.snowowl.datastore.internal.branch.BranchAssertions.assertLaterBase;
import static com.b2international.snowowl.datastore.internal.branch.BranchAssertions.assertState;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchMergeException;
import com.b2international.snowowl.datastore.internal.branch.BranchImpl.BranchState;

/**
 * @since 4.1
 */
public class BranchManagerTest {

	private AtomicLongTimestampAuthority clock;
	private BranchManagerImpl manager;
	private BranchImpl main;
	private BranchImpl a;

	@Before
	public void givenBranchManager() {
		clock = new AtomicLongTimestampAuthority();
		manager = new BranchManagerImpl(clock.getTimestamp(), clock);
		main = (BranchImpl) manager.getMainBranch();
		a = (BranchImpl) main.createChild("a");
	}
	
	@Test
	public void whenGettingMainBranch_ThenItShouldBeReturned() throws Exception {
		assertNotNull(main);
	}
	
	@Test(expected = NotFoundException.class)
	public void whenGettingNonExistingBranch_ThenThrowNotFoundException() throws Exception {
		manager.getBranch("MAIN/nonexistent");
	}
	
	@Test
	public void whenCreatingBranch_ThenItShouldBeReturnedViaGet() throws Exception {
		assertEquals(a, manager.getBranch("MAIN/a"));
	}
	
	@Test(expected = AlreadyExistsException.class)
	public void whenCreatingAlreadyExistingBranch_ThenThrowException() throws Exception {
		main.createChild("a");
		main.createChild("a");
	}
	
	@Test
	public void whenCreatingDeepBranchHierarchy_ThenEachSegmentShouldBeCreatedAndStoredInBranchManager() throws Exception {
		final Branch abcd = a.createChild("b").createChild("c").createChild("d");
		assertEquals("MAIN/a/b/c/d", abcd.path());
		final Branch abc = abcd.parent();
		final Branch ab = abc.parent();
		final Branch a = ab.parent();
		final Branch main = a.parent();
		assertEquals(manager.getBranch("MAIN/a/b/c"), abc);
		assertEquals(manager.getBranch("MAIN/a/b"), ab);
		assertEquals(manager.getBranch("MAIN/a"), a);
		assertEquals(this.main, main);
	}
	
	@Test
	public void whenCreatingThreeBranches_ThenManagerShouldReturnAllOfThemInGetAll() throws Exception {
		final Branch b = main.createChild("b");
		final Branch c = main.createChild("c");
		final Collection<Branch> branches = manager.getBranches();
		assertThat(branches).containsOnly(main, a, b, c);
	}
	
	@Test
	public void whenDeletingBranch_ThenManagerShouldStillReturnIt() throws Exception {
		a.delete();
		assertTrue(manager.getBranch("MAIN/a").isDeleted());
	}
	
	@Test
	public void whenCreatingChildrenWithInvalidPath_ThenItShouldThrowException() throws Exception {
		for (String name : newArrayList("/", "/a", "a/", "a/b")) {
			try {
				main.createChild(name);
				fail("IllegalArgumentException should be thrown when creating child branch " + name);
			} catch (IllegalArgumentException e) {
				// success
			}
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void mergeSelf() throws Exception {
		main.merge(main);
	}
	
	@Test
	public void mergeForward() throws Exception {
		final BranchImpl newBranchA = commit(a);
		BranchImpl newMain = (BranchImpl) main.merge(newBranchA);
		assertTrue(newMain.headTimestamp() > main.headTimestamp());
		assertState(manager.getBranch(a.path()), BranchState.DIVERGED);
	}

	private long currentTimestamp() {
		return clock.getTimestamp();
	}

	@Test(expected=BranchMergeException.class)
	public void mergeBehind() throws Exception {
		commit(main).merge(a);
	}

	@Test(expected=BranchMergeException.class)
	public void mergeDiverged() throws Exception {
		BranchImpl newBranchA = commit(a);
		BranchImpl newMain = commit(main);
		newMain.merge(newBranchA);
	}

	@Test(expected=BranchMergeException.class)
	public void mergeUpToDate() throws Exception {
		main.merge(a);
	}
	
	@Test
	public void rebaseUpToDateState() throws Exception {
		clock.advance(9L);
		final long expected = a.baseTimestamp();
		final Branch newBranchA = a.rebase();
		assertEquals("Rebasing UP_TO_DATE branch should do nothing", expected, newBranchA.baseTimestamp());
	}
	
	@Test
	public void rebaseForwardState() throws Exception {
		clock.advance(9L);
		final long expected = a.baseTimestamp();
		final Branch newBranchA = commit(a).rebase();
		assertEquals("Rebasing FORWARD branch should do nothing", expected, newBranchA.baseTimestamp());
	}
	
	@Test
	public void rebaseBehindState() throws Exception {
		commit(main);
		final Branch newBranchA = a.rebase();
		assertState(newBranchA, BranchState.UP_TO_DATE);
		assertState(a, BranchState.BEHIND);
		assertLaterBase(newBranchA, main);
		assertLaterBase(newBranchA, a);
	}

	@Test
	public void rebaseDivergedState() throws Exception {
		commit(main);
		final BranchImpl branchA = commit(a);
		final Branch newBranchA = branchA.rebase();
		assertState(newBranchA, BranchState.FORWARD);
		assertState(branchA, BranchState.DIVERGED);
		assertLaterBase(newBranchA, main);
		assertLaterBase(newBranchA, branchA);
	}

	@Test
	public void rebaseDivergedStateWithMultipleCommits() throws Exception {
		commit(main);
		final BranchImpl branchA = commit(a);
		final Branch rebasedBranchA = branchA.rebase();
		assertState(rebasedBranchA, BranchState.FORWARD);
		assertState(branchA, BranchState.DIVERGED);
		assertLaterBase(rebasedBranchA, main);
		assertLaterBase(rebasedBranchA, branchA);
	}
	
	@Test
	public void rebaseDivergedWithBehindChild() throws Exception {
		commit(main);
		final Branch branchB = a.createChild("b");
		final Branch branchA = commit(a);
		assertState(branchA, BranchState.DIVERGED);
		assertState(branchB, BranchState.BEHIND);
		final Branch rebasedBranchA = branchA.rebase();
		assertState(rebasedBranchA, BranchState.FORWARD);
		assertState(branchA, BranchState.DIVERGED);
		assertLaterBase(rebasedBranchA, main);
		assertLaterBase(rebasedBranchA, branchA);
		assertState(branchB, branchA, BranchState.BEHIND);
		assertState(branchB, rebasedBranchA, BranchState.STALE);
	}
	
	@Test
	public void rebaseBehindWithForwardChild() throws Exception {
		commit(main);
		final BranchImpl branchB = commit((BranchImpl) a.createChild("b"));
		assertState(a, BranchState.BEHIND);
		assertState(branchB, BranchState.FORWARD);
		final Branch rebasedBranchA = a.rebase();
		assertState(rebasedBranchA, BranchState.UP_TO_DATE);
		assertState(a, BranchState.BEHIND);
		assertLaterBase(rebasedBranchA, main);
		assertLaterBase(rebasedBranchA, a);
		assertState(branchB, a, BranchState.FORWARD);
		assertState(branchB, rebasedBranchA, BranchState.STALE);
	}
	
	@Test
	public void rebaseDivergedWithTwoChildren() throws Exception {
		commit(main);
		final Branch branchB = a.createChild("b");
		final Branch branchA = commit(a);
		final Branch branchC = branchA.createChild("c");
		final Branch rebasedBranchA = branchA.rebase();
		assertState(rebasedBranchA, BranchState.FORWARD);
		assertState(branchA, BranchState.DIVERGED);
		assertState(branchB, branchA, BranchState.BEHIND);
		assertState(branchB, rebasedBranchA, BranchState.STALE);
		assertState(branchC, branchA, BranchState.UP_TO_DATE);
		assertState(branchC, rebasedBranchA, BranchState.STALE);
	}
	
	@Test
	public void rebaseBehindChildOnRebasedForwardParent() throws Exception {
		rebaseDivergedWithBehindChild();
		final Branch newBranchB = manager.getBranch("MAIN/a/b").rebase(manager.getBranch("MAIN/a"));
		assertState(newBranchB, BranchState.UP_TO_DATE);
	}
	
	private BranchImpl commit(BranchImpl branch) {
		return manager.handleCommit(branch, currentTimestamp());
	}
	
}
