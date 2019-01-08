/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import static com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions.ROOT ;

import java.util.Map;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @since 4.6
 */
public final class Locks implements AutoCloseable {

	private final String repositoryId;
	private final IDatastoreOperationLockManager lockManager;
	private final DatastoreLockContext lockContext;
	private final Map<String, IOperationLockTarget> lockTargets;
	
	public Locks(RepositoryContext context, String userId, String description, Branch firstBranch, Branch... nextBranches) throws OperationLockException, InterruptedException {
		this(context, userId, description, ROOT, firstBranch, nextBranches);
	}
	
	public Locks(RepositoryContext context, String userId, String description, String parentLockContext, Branch firstBranch, Branch... nextBranches) throws OperationLockException, InterruptedException {
		repositoryId = context.id();
		lockManager = context.service(IDatastoreOperationLockManager.class);
		lockContext = new DatastoreLockContext(userId, description, Strings.isNullOrEmpty(parentLockContext) ? ROOT : parentLockContext);
	
		lockTargets = Maps.newHashMap();
		for (Branch branch : Lists.asList(firstBranch, nextBranches)) {
			lockTargets.put(branch.path(), new SingleRepositoryAndBranchLockTarget(repositoryId, branch.branchPath()));	
		}
		
		lock();
	}

	private void lock() throws OperationLockException, InterruptedException {
		lockManager.lock(lockContext, IDatastoreOperationLockManager.IMMEDIATE, lockTargets.values());
	}
	
	public void unlock(String path) throws OperationLockException {
		if (lockTargets.containsKey(path)) {
			lockManager.unlock(lockContext, lockTargets.get(path));
			lockTargets.remove(path); // Not reached if an exception is thrown above
		}
	}

	public void unlockAll() throws OperationLockException {
		if (!lockTargets.isEmpty()) {
			lockManager.unlock(lockContext, lockTargets.values());
			lockTargets.clear(); // Not reached if an exception is thrown above
		}
	}
	
	@Override
	public void close() throws OperationLockException {
		unlockAll();
	}
}
