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
import java.util.concurrent.ConcurrentMap;

import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.branch.TimestampProvider;
import com.b2international.snowowl.datastore.internal.branch.BranchImpl.BranchState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;


/**
 * @since 4.1
 */
public class BranchManagerImpl implements BranchManager {

	private TimestampProvider clock;
	
	private ConcurrentMap<String, Branch> branches = new MapMaker().makeMap();

	public BranchManagerImpl(TimestampProvider clock) {
		this.clock = clock;
		initMainBranch();
	}
	
	private void initMainBranch() {
		final MainBranchImpl main = new MainBranchImpl(this, clock.getTimestamp());
		registerBranch(main);
	}

	private void registerBranch(final Branch branch) {
		branches.put(branch.path(), branch);
	}
	
	BranchImpl createBranch(BranchImpl parent, String name) {
		
		final String path = parent.path().concat(Branch.SEPARATOR).concat(name);
		if (getBranchFromStore(path) != null) {
			throw new AlreadyExistsException(Branch.class.getSimpleName(), path);
		}
		
		final BranchImpl child = new BranchImpl(this, name, parent.path(), clock.getTimestamp());
		registerBranch(child);
		return child;
	}

	@Override
	public Branch getMainBranch() {
		return getBranch(MainBranchImpl.DEFAULT_PATH);
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
		return branches.get(path);
	}

	@Override
	public Collection<Branch> getBranches() {
		return ImmutableList.copyOf(branches.values());
	}

	BranchImpl merge(BranchImpl target, BranchImpl source) {
		// Changes from source will appear on target as a single commit
		return handleCommit(target, clock.getTimestamp());
	}

	Branch rebase(BranchImpl source, BranchImpl target) {
		BranchImpl rebasedSource = createBranch((BranchImpl) source.parent(), source.name());
		
		if (source.state() == BranchState.DIVERGED) {
			rebasedSource = handleCommit(rebasedSource, clock.getTimestamp());
		}
		
		return rebasedSource;
	}

	BranchImpl delete(BranchImpl branchImpl) {
		if (branches.replace(branchImpl.path(), branchImpl, branchImpl.withDeleted())) {
			// 
		}
		
		return null;
	}
	
	BranchImpl handleCommit(BranchImpl branch, long timestamp) {
		BranchImpl branchAfterCommit = branch.withHeadTimestamp(timestamp);
		registerBranch(branchAfterCommit);
		return branchAfterCommit;
	}
}
