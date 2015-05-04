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

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.Branch.BranchState;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.branch.TimestampProvider;
import com.b2international.snowowl.datastore.store.Store;


/**
 * @since 4.1
 */
public class BranchManagerImpl implements BranchManager {

	private final TimestampProvider clock;
	private Store<InternalBranch> branchStore;
	
	public BranchManagerImpl(Store<InternalBranch> branchStore, long mainBranchTimestamp) {
		this(branchStore, mainBranchTimestamp, null);
	}
	
	/*package*/ BranchManagerImpl(Store<InternalBranch> branchStore, long mainBranchTimestamp, TimestampProvider clock) {
		this.branchStore = branchStore;
		this.clock = clock;
		initMainBranch(new MainBranchImpl(mainBranchTimestamp));
	}
	
	/*package*/ void initMainBranch(InternalBranch main) {
		registerBranch(main);
	}

	void registerBranch(final InternalBranch branch) {
		branch.setBranchManager(this);
		branchStore.put(branch.path(), branch);
	}
	
	InternalBranch createBranch(InternalBranch parent, String name, Metadata metadata) {
		if (parent.isDeleted()) {
			throw new BadRequestException("Cannot create '%s' child branch under deleted '%s' parent.", name, parent.path());
		}
		final String path = parent.path().concat(Branch.SEPARATOR).concat(name);
		if (getBranchFromStore(path) != null) {
			throw new AlreadyExistsException(Branch.class.getSimpleName(), path);
		}
		
		return reopen(parent, name, metadata);
	}

	// TODO convert this to abstract method
	InternalBranch reopen(InternalBranch parent, String name, Metadata metadata) {
		return reopen(parent, name, metadata, clock.getTimestamp());
	}
	
	InternalBranch reopen(InternalBranch parent, String name, Metadata metadata, long baseTimestamp) {
		final InternalBranch child = createBranch(name, parent.path(), metadata, baseTimestamp);
		registerBranch(child);
		return child;
	}

	private InternalBranch createBranch(String name, String parentPath, Metadata metadata, long baseTimestamp) {
		final InternalBranch branch = new BranchImpl(name, parentPath, baseTimestamp);
		branch.metadata(metadata);
		return branch;
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

	protected final Branch getBranchFromStore(String path) {
		final InternalBranch branch = branchStore.get(path);
		if (branch != null) {
			branch.setBranchManager(this);
		}
		return branch;
	}

	@Override
	public Collection<? extends Branch> getBranches() {
		final Collection<InternalBranch> values = branchStore.values();
		for (InternalBranch branch : values) {
			branch.setBranchManager(this);
		}
		return values;
	}

	// TODO convert this to abstract method
	InternalBranch merge(InternalBranch target, InternalBranch source, String commitMessage) {
		// Changes from source will appear on target as a single commit
		return applyChangeSet(target, source, clock.getTimestamp(), commitMessage);
	}

	// TODO convert this to abstract method
	Branch rebase(InternalBranch source, InternalBranch target, String commitMessage) {
		InternalBranch rebasedSource = reopen((InternalBranch) source.parent(), source.name(), source.metadata());
		
		if (source.state() == BranchState.DIVERGED) {
			return applyChangeSet(rebasedSource, source, clock.getTimestamp(), commitMessage);
		} else {
			return rebasedSource;
		}
	}

	InternalBranch applyChangeSet(InternalBranch target, InternalBranch source, long timestamp, String commitMessage) {
		return handleCommit(target, timestamp);
	}

	/*package*/ final InternalBranch delete(InternalBranch branchImpl) {
		final InternalBranch deleted = branchImpl.withDeleted();
		branchStore.replace(branchImpl.path(), branchImpl, deleted);
		return deleted;
	}
	
	/*package*/ final InternalBranch handleCommit(InternalBranch branch, long timestamp) {
		InternalBranch branchAfterCommit = branch.withHeadTimestamp(timestamp);
		registerBranch(branchAfterCommit);
		return branchAfterCommit;
	}
}
