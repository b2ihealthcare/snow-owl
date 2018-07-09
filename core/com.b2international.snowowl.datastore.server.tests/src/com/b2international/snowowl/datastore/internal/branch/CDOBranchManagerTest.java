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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.b2international.index.Index;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.RepositoryContextProvider;
import com.b2international.snowowl.core.repository.InternalRepository;
import com.b2international.snowowl.datastore.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.review.ReviewManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.util.Providers;

/**
 * @since 4.1
 */
@RunWith(MockitoJUnitRunner.class)
public class CDOBranchManagerTest extends BaseRevisionIndexTest {

	private CDOTimeProvider clock;
	private MockInternalCDOBranchManager cdoBranchManager;
	
	private InternalRepository repository;
	private ServiceProvider context;
	
	@Override
	protected BaseRevisionBranching createBranchingSupport(Index rawIndex, ObjectMapper mapper) {
		clock = new AtomicLongTimestampAuthority();
		cdoBranchManager = new MockInternalCDOBranchManager(clock);
		cdoBranchManager.initMainBranch(false, clock.getTimeStamp());

		repository = mock(InternalRepository.class, RETURNS_MOCKS);
		final ICDOConflictProcessor conflictProcessor = mock(ICDOConflictProcessor.class, RETURNS_DEFAULTS);
		final InternalCDOBranch mainBranch = cdoBranchManager.getMainBranch();
		
		when(repository.getCdoBranchManager()).thenReturn(cdoBranchManager);
		when(repository.getCdoMainBranch()).thenReturn(mainBranch);
		when(repository.getConflictProcessor()).thenReturn(conflictProcessor);

		when(repository.provider(Mockito.eq(Index.class))).thenReturn(Providers.of(rawIndex()));
		
		context = mock(ServiceProvider.class);
		final RepositoryContextProvider repositoryContextProvider = mock(RepositoryContextProvider.class);
		final RepositoryContext repositoryContext = mock(RepositoryContext.class);
		
		final IDatastoreOperationLockManager lockManager = mock(IDatastoreOperationLockManager.class);
		final ReviewManager reviewManager = mock(ReviewManager.class);
		
		when(repositoryContext.service(IDatastoreOperationLockManager.class)).thenReturn(lockManager);
		when(repositoryContext.service(ReviewManager.class)).thenReturn(reviewManager);
		
		when(repositoryContextProvider.get(repository.id())).thenReturn(repositoryContext);
		when(context.service(RepositoryContextProvider.class)).thenReturn(repositoryContextProvider);
		
		return new CDOBranchManagerImpl(repository, mapper);
	}
	
	@Override
	protected long currentTime() {
		return clock.getTimeStamp();
	}
	
	@Override
	protected CDOBranchManagerImpl branching() {
		return (CDOBranchManagerImpl) super.branching();
	}
	
	@Test
	public void createBranchesWithCDOBranching() throws Exception {
		final String branchA = createBranch(MAIN, "a");
		final String branchB = createBranch(branchA, "b");
		final CDOBranch cdoBranchA = branching().getCDOBranch(getBranch(branchA));
		assertEquals(branchA, cdoBranchA.getPathName());
		final CDOBranch cdoBranchB = branching().getCDOBranch(getBranch(branchB));
		assertEquals(branchB, cdoBranchB.getPathName());
	}
	
	@Test
	public void whenRebasingChildBranchInForwardState_ThenManagerShouldReopenAssociatedCDOBranch() throws Exception {
		final String branchA = createBranch(MAIN, "a");
		final CDOBranch cdoBranchA = branching().getCDOBranch(getBranch(branchA));

		// commit and rebase
		branching().handleCommit(MAIN, clock.getTimeStamp());
		branching().rebase(branchA, MAIN, "Rebase", () -> {});
		
		final CDOBranch rebasedCdoBranchA = branching().getCDOBranch(getBranch(branchA));
		assertNotEquals(rebasedCdoBranchA.getID(), cdoBranchA.getID());
	}
	
}
