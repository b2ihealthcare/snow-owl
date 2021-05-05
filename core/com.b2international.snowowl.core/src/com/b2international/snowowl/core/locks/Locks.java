/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.locks;

import static com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions.ROOT;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.b2international.commons.exceptions.LockedException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.internal.locks.DatastoreLockTarget;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @since 4.6
 */
public final class Locks implements AutoCloseable {

	/**
	 * @since 7.5
	 */
	public static final class Builder {
		
		private final RepositoryContext context;
		private String user;
		private List<String> branches;
		
		public Builder(RepositoryContext context) {
			this.context = checkNotNull(context, "Context is missing");
			this.user = context.service(User.class).getUsername();
			if (context instanceof BranchContext) {
				this.branches = List.of(((BranchContext) context).branch().path()); 
			}
		}
		
		public Builder user(String user) {
			this.user = user;
			return this;
		}
		
		public Builder branch(String branch) {
			return branches(List.of(branch));
		}
		
		public Builder branches(String...branches) {
			return branches(List.of(branches));
		}
		
		public Builder branches(List<String> branches) {
			this.branches = branches;
			return this;
		}
		
		public Locks lock(String lockContext) {
			return lock(lockContext, DatastoreLockContextDescriptions.ROOT);
		}
		
		public Locks lock(String lockContext, String parentLockContext) {
			return new Locks(context, user, lockContext, parentLockContext, branches);
		}

	}
	
	public static Builder on(RepositoryContext context) {
		return new Builder(context);
	}
	
	private final String repositoryId;
	private final IOperationLockManager lockManager;
	private final DatastoreLockContext lockContext;
	private final Map<String, DatastoreLockTarget> lockTargets;
	
	private Locks(RepositoryContext context, String userId, String description, String parentLockContext, List<String> branchesToLock) throws LockedException {
		this.repositoryId = context.info().id();
		this.lockManager = context.service(IOperationLockManager.class);
		this.lockContext = new DatastoreLockContext(userId, description, Strings.isNullOrEmpty(parentLockContext) ? ROOT : parentLockContext);
	
		this.lockTargets = Maps.newHashMapWithExpectedSize(branchesToLock.size());
		for (String branch : branchesToLock) {
			this.lockTargets.put(branch, new DatastoreLockTarget(repositoryId, branch));	
		}
		
		lock();
	}
	
	public String lockContext() {
		return lockContext.getDescription();
	}

	private void lock() {
		lockManager.lock(lockContext, IOperationLockManager.IMMEDIATE, lockTargets.values());
	}
	
	public void unlock(String path) {
		if (lockTargets.containsKey(path)) {
			lockManager.unlock(lockContext, lockTargets.get(path));
			lockTargets.remove(path); // Not reached if an exception is thrown above
		}
	}

	public void unlockAll() {
		if (!lockTargets.isEmpty()) {
			lockManager.unlock(lockContext, lockTargets.values());
			lockTargets.clear(); // Not reached if an exception is thrown above
		}
	}
	
	@Override
	public void close() {
		unlockAll();
	}
}
