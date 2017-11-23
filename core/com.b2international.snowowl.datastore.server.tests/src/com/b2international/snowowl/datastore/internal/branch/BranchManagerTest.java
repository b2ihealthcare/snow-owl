/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.MetadataImpl;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branch.BranchState;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.branch.BranchMergeException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.internal.branch.BranchDocument;
import com.b2international.snowowl.datastore.internal.branch.BranchImpl;
import com.b2international.snowowl.datastore.internal.branch.BranchManagerImpl;
import com.b2international.snowowl.datastore.internal.branch.InternalBranch;
import com.b2international.snowowl.datastore.internal.branch.MainBranchImpl;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;

/**
 * @since 4.1
 */
public class BranchManagerTest {

	private class BranchManagerImplTest extends BranchManagerImpl {

		private BranchManagerImplTest(Index branchStore, long mainBranchTimestamp) {
			super(branchStore);
			initBranchStore(new MainBranchImpl(mainBranchTimestamp));
		}

		@Override
		protected InternalBranch applyChangeSet(InternalBranch from, InternalBranch to, boolean dryRun, boolean isRebase, String commitMessage) {
			if (!dryRun && from.headTimestamp() > from.baseTimestamp()) {
				return handleCommit(to, clock.getTimestamp());
			} else {
				return to;
			}
		}

		@Override
		protected InternalBranch doReopen(InternalBranch parent, String name, Metadata metadata) {
			final BranchImpl branch = new BranchImpl(name, parent.path(), clock.getTimestamp(), metadata);
			return commit(create(branch));
		}
	}

	private AtomicLongTimestampAuthority clock;
	private BranchManagerImpl manager;
	private InternalBranch main;
	private InternalBranch a;
	private Index store;
	private ServiceProvider context;

	@Before
	public void givenBranchManager() {
		clock = new AtomicLongTimestampAuthority();
		store = Indexes.createIndex(UUID.randomUUID().toString(), JsonSupport.getDefaultObjectMapper(), new Mappings(BranchDocument.class));
		store.admin().create();
		manager = new BranchManagerImplTest(store, clock.getTimestamp());
		
		main = (InternalBranch) manager.getMainBranch();
		a = (InternalBranch) main.createChild("a");
		
		context = mock(ServiceProvider.class);
		final RepositoryContextProvider repositoryContextProvider = mock(RepositoryContextProvider.class);
		final RepositoryContext repositoryContext = mock(RepositoryContext.class);
		
		final IDatastoreOperationLockManager lockManager = mock(IDatastoreOperationLockManager.class);
		final ReviewManager reviewManager = mock(ReviewManager.class);
		
		when(repositoryContext.service(IDatastoreOperationLockManager.class)).thenReturn(lockManager);
		when(repositoryContext.service(ReviewManager.class)).thenReturn(reviewManager);
		when(repositoryContext.service(BranchManager.class)).thenReturn(manager);
		
		when(repositoryContextProvider.get("")).thenReturn(repositoryContext);
		when(context.service(RepositoryContextProvider.class)).thenReturn(repositoryContextProvider);
	}
	
	@After
	public void after() {
		if (store != null) {
			store.admin().delete();
		}
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
	
	@Test
	public void whenGettingBranch_ThenBranchManagerShouldBeSet() throws Exception {
		final BranchImpl branch = (BranchImpl) manager.getBranch("MAIN/a");
		assertNotNull(branch.getBranchManager());
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
	public void whenCreatingBranchWithMetadata_ThenItShouldBeStored() throws Exception {
		final Metadata metadata = new MetadataImpl();
		metadata.put("key", "value");
		final Branch b = main.createChild("b", metadata);
		assertEquals("value", b.metadata().get("key"));
	}
	
	@Test
	public void whenCreatingBranchAndChildren_ThenChildrenShouldReturnAllOfThem() throws Exception {
		final Branch b = a.createChild("b");
		final Branch c = a.createChild("c");
		assertThat(a.children()).containsOnly(b, c);
	}
	
	@Test
	public void whenCreatingBranchAndChildTree_ThenChildrenShouldReturnAllOfThem() throws Exception {
		final Branch b = a.createChild("b");
		final Branch c = b.createChild("c");
		assertThat(a.children()).containsOnly(b, c);
	}
	
	@Test
	public void whenDeletingBranch_ThenManagerShouldStillReturnIt() throws Exception {
		a.delete();
		assertTrue(manager.getBranch("MAIN/a").isDeleted());
	}
	
	@Test(expected = BadRequestException.class)
	public void whenCreatingChildUnderDeletedBranch_ThenThrowBadRequestException() throws Exception {
		a.delete().createChild("childOfDeletedA");
	}
	
	@Test
	public void whenDeletingBranch_ChildBranchesShouldBeDeletedAsWell() throws Exception {
		for (int i = 0; i < 10; i++) {
			a.createChild(""+i);
		}
		
		assertEquals(10, a.children().size());
		
		a.delete();
		
		for (int i = 0; i < 10; i++) {
			assertTrue(manager.getBranch("MAIN/a/"+i).isDeleted());
		}
	}
	
	@Test
	public void whenDeletingBranchWithChildTree_ThenChildTreeShouldBeDeleted() throws Exception {
		a.createChild("1").createChild("2");
		assertTrue(a.delete().isDeleted());
		assertTrue(manager.getBranch("MAIN/a/1").isDeleted());
		assertTrue(manager.getBranch("MAIN/a/1/2").isDeleted());
	}
	
	@Test
	public void whenCreatingChildrenWithInvalidPath_ThenItShouldThrowException() throws Exception {
		for (String name : newArrayList("/", "/a", "a/", "a/b")) {
			try {
				main.createChild(name);
				fail("BadRequestException should be thrown when creating child branch " + name);
			} catch (BadRequestException e) {
				// success
			}
		}
	}

	@Test(expected=BadRequestException.class)
	public void mergeSelf() throws Exception {
		merge(main, main);
	}

	@Test
	public void mergeForward() throws Exception {
		final InternalBranch newBranchA = commit(a);
		InternalBranch newMain = (InternalBranch) merge(main, newBranchA);
		assertTrue(newMain.headTimestamp() > main.headTimestamp());
		assertState(manager.getBranch(a.path()), BranchState.UP_TO_DATE);
	}

	private long currentTimestamp() {
		return clock.getTimestamp();
	}

	@Test(expected=BranchMergeException.class)
	public void mergeBehind() throws Exception {
		merge(commit(main), a);
	}

	@Test(expected=BranchMergeException.class)
	public void mergeDiverged() throws Exception {
		InternalBranch newBranchA = commit(a);
		InternalBranch newMain = commit(main);
		merge(newMain, newBranchA);
	}

	@Test(expected=BranchMergeException.class)
	public void mergeUpToDate() throws Exception {
		merge(main, a);
	}
	
	@Test
	public void rebaseUpToDateState() throws Exception {
		clock.advance(9L);
		final long expected = a.baseTimestamp();
		final Branch newBranchA = rebase(a);
		assertEquals("Rebasing UP_TO_DATE branch should do nothing", expected, newBranchA.baseTimestamp());
	}
	
	@Test
	public void rebaseForwardState() throws Exception {
		clock.advance(9L);
		final long expected = a.baseTimestamp();
		final Branch newBranchA = rebase(commit(a));
		assertEquals("Rebasing FORWARD branch should do nothing", expected, newBranchA.baseTimestamp());
	}
	
	@Test
	public void rebaseBehindState() throws Exception {
		commit(main);
		final Branch newBranchA = rebase(a);
		assertState(newBranchA, BranchState.UP_TO_DATE);
		assertState(a, BranchState.BEHIND);
		assertLaterBase(newBranchA, main);
		assertLaterBase(newBranchA, a);
	}

	@Test
	public void rebaseDivergedState() throws Exception {
		commit(main);
		final InternalBranch branchA = commit(a);
		final Branch newBranchA = rebase(branchA);
		assertState(newBranchA, BranchState.FORWARD);
		assertState(branchA, BranchState.DIVERGED);
		assertLaterBase(newBranchA, main);
		assertLaterBase(newBranchA, branchA);
	}

	@Test
	public void rebaseDivergedStateWithMultipleCommits() throws Exception {
		commit(main);
		final InternalBranch branchA = commit(a);
		final Branch rebasedBranchA = rebase(branchA);
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
		final Branch rebasedBranchA = rebase(branchA);
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
		final InternalBranch branchB = commit((InternalBranch) a.createChild("b"));
		assertState(a, BranchState.BEHIND);
		assertState(branchB, BranchState.FORWARD);
		final Branch rebasedBranchA = rebase(a);
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
		final Branch rebasedBranchA = rebase(branchA);
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
		final Branch newBranchB = rebase(manager.getBranch("MAIN/a/b"), manager.getBranch("MAIN/a"));
		assertState(newBranchB, BranchState.UP_TO_DATE);
	}
	
	private InternalBranch commit(InternalBranch branch) {
		return manager.handleCommit(branch, currentTimestamp());
	}

	private Branch merge(Branch target, Branch source) {
		return target.merge(source, "Message");
	}

	private Branch rebase(Branch branch) {
		return rebase(branch, branch.parent());
	}
	
	private Branch rebase(Branch branch, Branch onTopOf) {
		return branch.rebase(onTopOf, "Message");
	}
}
