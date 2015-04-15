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
		manager = new BranchManagerImpl(clock);
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
		manager.handleCommit(a, currentTimestamp());
		
		BranchImpl newMain = (BranchImpl) main.merge(manager.getBranch(a.path()));
		assertTrue(newMain.headTimestamp() > main.headTimestamp());
		assertState(manager.getBranch(a.path()), BranchState.DIVERGED);
	}

	private long currentTimestamp() {
		return clock.getTimestamp();
	}

	@Test(expected=BranchMergeException.class)
	public void mergeBehind() throws Exception {
		manager.handleCommit(main, currentTimestamp()).merge(a);
	}

	@Test(expected=BranchMergeException.class)
	public void mergeDiverged() throws Exception {
		BranchImpl newBranchA = manager.handleCommit(a, currentTimestamp());
		BranchImpl newMain = manager.handleCommit(main, currentTimestamp());
		newMain.merge(newBranchA);
	}

	@Test(expected=BranchMergeException.class)
	public void mergeUpToDate() throws Exception {
		main.merge(a);
	}
}
