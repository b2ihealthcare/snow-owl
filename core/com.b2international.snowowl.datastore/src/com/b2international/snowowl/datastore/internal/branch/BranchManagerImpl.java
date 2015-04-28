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

import java.util.Collection;

import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.Branch.BranchState;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.branch.TimestampProvider;
import com.b2international.snowowl.datastore.store.Store;
import com.google.common.collect.ImmutableList;


/**
 * @since 4.1
 */
public class BranchManagerImpl implements BranchManager {

	private final TimestampProvider clock;
	private Store<BranchImpl> branchStore;
	
	public BranchManagerImpl(Store<BranchImpl> branchStore, long mainBranchTimestamp) {
		this(branchStore, mainBranchTimestamp, null);
	}
	
	/*package*/ BranchManagerImpl(Store<BranchImpl> branchStore, long mainBranchTimestamp, TimestampProvider clock) {
		this.branchStore = branchStore;
		this.clock = clock;
		initMainBranch(mainBranchTimestamp);
	}
	
	private void initMainBranch(long mainBranchTimestamp) {
		final MainBranchImpl main = new MainBranchImpl(mainBranchTimestamp);
		registerBranch(main);
	}

	private void registerBranch(final BranchImpl branch) {
		branch.setBranchManager(this);
		branchStore.put(branch.path(), branch);
	}
	
	BranchImpl createBranch(BranchImpl parent, String name) {
		if (parent.isDeleted()) {
			throw new BadRequestException("Cannot create '%s' child branch under deleted '%s' parent.", name, parent.path());
		}
		final String path = parent.path().concat(Branch.SEPARATOR).concat(name);
		if (getBranchFromStore(path) != null) {
			throw new AlreadyExistsException(Branch.class.getSimpleName(), path);
		}
		
		return reopen(parent, name);
	}

	protected BranchImpl reopen(BranchImpl parent, String name) {
		return reopen(parent, name, clock.getTimestamp());
	}
	
	protected BranchImpl reopen(BranchImpl parent, String name, long baseTimestamp) {
		final BranchImpl child = new BranchImpl(name, parent.path(), baseTimestamp);
		registerBranch(child);
		return child;
	}

	@Override
	public Branch getMainBranch() {
		return getBranch(MainBranchImpl.MAIN_PATH);
	}

	@Override
	public Branch getBranch(String path) {
		final Branch branch = getBranchFromStore(path);
		if (branch == null) {
			throw new NotFoundException(Branch.class.getSimpleName(), path);
		}
		return branch;
	}

	private Branch getBranchFromStore(String path) {
		final BranchImpl branch = branchStore.get(path);
		if (branch != null) {
			branch.setBranchManager(this);
		}
		return branch;
	}

	@Override
	public Collection<? extends Branch> getBranches() {
		return ImmutableList.copyOf(branchStore.values());
	}

	BranchImpl merge(BranchImpl target, BranchImpl source, String commitMessage) {
		// Changes from source will appear on target as a single commit
		return applyChangeSet(target, source, clock.getTimestamp(), commitMessage);
	}

	Branch rebase(BranchImpl source, BranchImpl target, String commitMessage) {
		BranchImpl rebasedSource = reopen((BranchImpl) source.parent(), source.name());
		
		if (source.state() == BranchState.DIVERGED) {
			return applyChangeSet(rebasedSource, source, clock.getTimestamp(), commitMessage);
		} else {
			return rebasedSource;
		}
	}

	BranchImpl applyChangeSet(BranchImpl target, BranchImpl source, long timestamp, String commitMessage) {
		return handleCommit(target, timestamp);
	}

	BranchImpl delete(BranchImpl branchImpl) {
		final BranchImpl deleted = branchImpl.withDeleted();
		if (branchStore.replace(branchImpl.path(), branchImpl, deleted)) {
			postDelete(deleted);
		}
		return deleted;
	}
	
	protected void postDelete(BranchImpl branch) {
	}

	BranchImpl handleCommit(BranchImpl branch, long timestamp) {
		BranchImpl branchAfterCommit = branch.withHeadTimestamp(timestamp);
		registerBranch(branchAfterCommit);
		return branchAfterCommit;
	}
}
