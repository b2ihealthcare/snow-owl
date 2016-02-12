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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.server.oplock.OperationLockInfo;
import com.b2international.snowowl.datastore.server.oplock.impl.DatastoreOperationLockManager;
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
		return sendChangeEvent(reopen(parent, name, metadata)); // Explicit notification (creation)
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
		
		final IDatastoreOperationLockManager lockManager = ApplicationContext.getInstance().getService(IDatastoreOperationLockManager.class);
		final List<OperationLockInfo<DatastoreLockContext>> locks = ((DatastoreOperationLockManager) lockManager).getLocks();
		for (OperationLockInfo<DatastoreLockContext> operationLockInfo : locks) {
			final IOperationLockTarget target = operationLockInfo.getTarget();
			if (target instanceof SingleRepositoryAndBranchLockTarget) {
				SingleRepositoryAndBranchLockTarget lockTarget = (SingleRepositoryAndBranchLockTarget) target;
				if (lockTarget.getBranchPath().equals(branch.branchPath())) {
					Map<String, Object> lockInfo = new HashMap<>();
					lockInfo.put("creationDate", operationLockInfo.getCreationDate());
					lockInfo.put("context", operationLockInfo.getContext());
					branch.metadata().put("lock", lockInfo);
				}
			}
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
		final InternalBranch mergedTarget = applyChangeSet(target, source, false, commitMessage); // Implicit notification (commit)
		final InternalBranch reopenedSource = reopen((InternalBranch) source.parent(), source.name(), source.metadata());
		sendChangeEvent(reopenedSource); // Explicit notification (reopen)
		return mergedTarget;
	}

	final InternalBranch rebase(final InternalBranch source, final InternalBranch target, final String commitMessage) {
		applyChangeSet(target, source, true, commitMessage);
		final InternalBranch reopenedSource = reopen(target, source.name(), source.metadata());
		
		if (source.headTimestamp() > source.baseTimestamp()) {
			return applyChangeSet(reopenedSource, source, false, commitMessage); // Implicit notification (reopen & commit)
		} else {
			return sendChangeEvent(reopenedSource); // Explicit notification (reopen)
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
		return sendChangeEvent(deleted); // Explicit notification (delete)
	}
	
	/*package*/ final InternalBranch handleCommit(final InternalBranch branch, final long timestamp) {
		final InternalBranch branchAfterCommit = branch.withHeadTimestamp(timestamp);
		registerBranch(branchAfterCommit);
		return sendChangeEvent(branchAfterCommit); // Explicit notification (commit)
	}

	/**
	 * Subclasses should override this method if they want to broadcast notifications of changed branches.
	 * @param branch the subject of the notification (may not be {@code null})
	 * @return {@code branch} (for convenience)
	 */
	@OverridingMethodsMustInvokeSuper
	InternalBranch sendChangeEvent(final InternalBranch branch) {
		return branch;
	}

	/*package*/ final Collection<? extends Branch> getChildren(BranchImpl branchImpl) {
		final Collection<InternalBranch> values = branchStore.search(QueryBuilder.newQuery().prefixMatch(PATH_FIELD, branchImpl.path() + Branch.SEPARATOR).build());
		initialize(values);
		return values;
	}
}
