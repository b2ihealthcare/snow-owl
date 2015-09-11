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
package com.b2international.snowowl.datastore.server.internal.branch;

import java.util.Collection;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.branch.BranchManager;
import com.b2international.snowowl.datastore.branch.Branch.BranchState;
import com.b2international.snowowl.datastore.store.Store;
import com.b2international.snowowl.datastore.store.query.Query;
import com.b2international.snowowl.datastore.store.query.QueryBuilder;
import com.google.common.collect.Iterables;

/**
 * @since 4.1
 */
public abstract class BranchManagerImpl implements BranchManager {

	private static final String PATH_FIELD = "path";
	
	private final Store<InternalBranch> branchStore;
	
	public BranchManagerImpl(final Store<InternalBranch> branchStore) {
		this.branchStore = branchStore;
		branchStore.configureSearchable(PATH_FIELD);
	}
	
	protected final void initBranchStore(final InternalBranch main) {
    	try {
    		getMainBranch();
    	} catch (NotFoundException e) {
    		doInitBranchStore(main);
    	}
	}

	protected void doInitBranchStore(InternalBranch main) {
		branchStore.clear();
		registerBranch(main);
	}

	void registerBranch(final InternalBranch branch) {
		branch.setBranchManager(this);
		branchStore.put(branch.path(), branch);
	}
	
	final InternalBranch createBranch(final InternalBranch parent, final String name, final Metadata metadata) {
		if (parent.isDeleted()) {
			throw new BadRequestException("Cannot create '%s' child branch under deleted '%s' parent.", name, parent.path());
		}
		final String path = parent.path().concat(Branch.SEPARATOR).concat(name);
		if (getBranchFromStore(path) != null) {
			throw new AlreadyExistsException(Branch.class.getSimpleName(), path);
		}
		return reopen(parent, name, metadata);
	}

	abstract InternalBranch reopen(InternalBranch parent, String name, Metadata metadata);

	@Override
	public Branch getMainBranch() {
		return getBranch(MainBranchImpl.MAIN_PATH);
	}

	@Override
	public Branch getBranch(final String path) {
		final Branch branch = getBranchFromStore(path);
		if (branch == null) {
			throw new NotFoundException(Branch.class.getSimpleName(), path);
		}
		return branch;
	}

	protected final Branch getBranchFromStore(final Query query) {
		final InternalBranch branch = Iterables.getOnlyElement(branchStore.search(query, 0, 1), null);
		if (branch != null) {
			branch.setBranchManager(this);
		}
		return branch;
	}
	
	private final Branch getBranchFromStore(final String path) {
		final InternalBranch branch = branchStore.get(path);
		if (branch != null) {
			branch.setBranchManager(this);
		}
		return branch;
	}

	@Override
	public Collection<? extends Branch> getBranches() {
		final Collection<InternalBranch> values = branchStore.values();
		initialize(values);
		return values;
	}

	private void initialize(final Collection<InternalBranch> values) {
		for (final InternalBranch branch : values) {
			branch.setBranchManager(this);
		}
	}

	final InternalBranch merge(final InternalBranch target, final InternalBranch source, final String commitMessage) {
		final InternalBranch mergedTarget = applyChangeSet(target, source, false, commitMessage);
		reopen((InternalBranch) source.parent(), source.name(), source.metadata());
		return mergedTarget;
	}

	final InternalBranch rebase(final InternalBranch source, final InternalBranch target, final String commitMessage) {
		InternalBranch parent = (InternalBranch) source.parent();
		applyChangeSet(parent, source, true, commitMessage);
		final InternalBranch rebasedSource = reopen(parent, source.name(), source.metadata());
		
		if (source.state() == BranchState.DIVERGED) {
			return applyChangeSet(rebasedSource, source, false, commitMessage);
		} else {
			return rebasedSource;
		}
	}

	abstract InternalBranch applyChangeSet(InternalBranch target, InternalBranch source, boolean dryRun, String commitMessage);

	/*package*/ final InternalBranch delete(final InternalBranch branchImpl) {
		for (Branch child : branchImpl.children()) {
			doDelete((InternalBranch) child);
		}
		return doDelete(branchImpl);
	}

	private InternalBranch doDelete(final InternalBranch branchImpl) {
		final InternalBranch deleted = branchImpl.withDeleted();
		branchStore.replace(branchImpl.path(), branchImpl, deleted);
		return deleted;
	}
	
	/*package*/ final InternalBranch handleCommit(final InternalBranch branch, final long timestamp) {
		final InternalBranch branchAfterCommit = branch.withHeadTimestamp(timestamp);
		registerBranch(branchAfterCommit);
		return branchAfterCommit;
	}

	/*package*/ final Collection<? extends Branch> getChildren(BranchImpl branchImpl) {
		final Collection<InternalBranch> values = branchStore.search(QueryBuilder.newQuery().prefixMatch(PATH_FIELD, branchImpl.path() + Branch.SEPARATOR).build());
		initialize(values);
		return values;
	}
}
