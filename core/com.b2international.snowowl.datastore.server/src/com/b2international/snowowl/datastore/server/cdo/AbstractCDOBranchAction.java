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
package com.b2international.snowowl.datastore.server.cdo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Map.Entry;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;

/**
 * Encapsulates a CDO branch-manipulating action which has the following properties:
 * <ul>
 * <li>ensure that all branches and their parents are existing;
 * <li>a branch lock is acquired for all affected branches;
 * <li>the wrapped action is executed;
 * <li>thrown exceptions are caught and returned;
 * <li>locks are released when the action terminates for any reason.
 * </ul>
 */
public abstract class AbstractCDOBranchAction {

	private final IBranchPathMap branchPathMap;
	private final String userId;
	private final String lockDescription;
	private final List<IOperationLockTarget> lockTargets = newArrayList();

	public AbstractCDOBranchAction(final IBranchPathMap branchPathMap, final String userId, final String lockDescription) {
		checkNotNull(branchPathMap, "Branch path map may not be null.");
		checkNotNull(userId, "User identifier may not be null.");

		this.branchPathMap = branchPathMap;
		this.userId = userId;
		this.lockDescription = lockDescription;
	}

	public Throwable run() {

		try {

			acquireLocks();

			for (final Entry<String, IBranchPath> repositoryBranchPath : branchPathMap.getLockedEntries().entrySet()) {
				final String repositoryId = repositoryBranchPath.getKey();
				final IBranchPath taskBranchPath = repositoryBranchPath.getValue();
				
				if (isApplicable(repositoryId, taskBranchPath)) {
					apply(repositoryId, taskBranchPath);
				}
			}

			postRun();
			return null;

		} catch (final Throwable t) {
			return t;
		} finally {
			releaseLocks();
		}
	}

	protected abstract void apply(String repositoryId, IBranchPath taskBranchPath) throws Throwable;
	
	protected boolean isApplicable(String repositoryId, IBranchPath taskBranchPath) {
		
		if (taskBranchPath == null) {
			return false;
		} else if (BranchPathUtils.isMain(taskBranchPath)) {
			return false;
		} else {
			return true;
		}
	}

	protected void postRun() throws Exception {
		return;
	}

	protected ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getServiceForClass(ICDOConnectionManager.class);
	}

	protected String getUserId() {
		return userId;
	}

	protected String getLockDescription() {
		return lockDescription;
	}

	private void acquireLocks() throws OperationLockException, InterruptedException {

		for (final Entry<String, IBranchPath> repositoryBranchPath : branchPathMap.getLockedEntries().entrySet()) {
			final String repositoryId = repositoryBranchPath.getKey();
			final IBranchPath branchPath = repositoryBranchPath.getValue();

			if (isApplicable(repositoryId, branchPath)) {
				lockTargets.add(createLockTarget(repositoryBranchPath.getKey(), branchPath));
				lockTargets.add(createLockTarget(repositoryBranchPath.getKey(), branchPath.getParent()));	
			}
		}

		try {
			getDatastoreOperationLockManager().lock(createLockContext(), IDatastoreOperationLockManager.IMMEDIATE, lockTargets);
		} catch (OperationLockException | InterruptedException e) {
			lockTargets.clear();
			throw e;
		}
	}

	private void releaseLocks() {
		getDatastoreOperationLockManager().unlock(createLockContext(), lockTargets);
	}

	private DatastoreLockContext createLockContext() {
		return new DatastoreLockContext(userId, lockDescription);
	}

	private SingleRepositoryAndBranchLockTarget createLockTarget(final String repositoryUuid, final IBranchPath branchPath) {
		return new SingleRepositoryAndBranchLockTarget(repositoryUuid, branchPath);
	}

	private IDatastoreOperationLockManager getDatastoreOperationLockManager() {
		return ApplicationContext.getServiceForClass(IDatastoreOperationLockManager.class);
	}
}
