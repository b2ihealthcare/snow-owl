/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.LockedException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;
import com.b2international.snowowl.core.jobs.RemoteJob;

/**
 * @since 4.6
 */
public final class Locks<T extends ServiceProvider> implements AutoCloseable {

	/**
	 * @since 7.5
	 */
	public static final class Builder {

		private final String lockContext;
		private final String parentLockContext;
		
		private String user;
		private Set<Lockable> lockables;
		private long timeoutMillis = IOperationLockManager.IMMEDIATE;

		public Builder(String lockContext, String parentLockContext) {
			this.lockContext = lockContext;
			this.parentLockContext = parentLockContext;
		}

		public Builder by(String user) {
			this.user = user;
			return this;
		}

		public Builder on(TerminologyResource resourceToLock) {
			return on(resourceToLock.asLockable());
		}
		
		public Builder on(Collection<TerminologyResource> resourcesToLock) {
			return on(resourcesToLock.stream().map(TerminologyResource::asLockable)::iterator);
		}
		
		public Builder on(String repositoryId, String branchPath) {
			this.lockables = Set.of(new Lockable(repositoryId, branchPath));
			return this;
		}
		
		public Builder on(Lockable lockable) {
			return on(Set.of(lockable));
		}
		
		public Builder on(Iterable<Lockable> lockables) {
			this.lockables = Collections3.toImmutableSet(lockables);
			return this;
		}
		
		public Builder waitUntil(long timeoutMillis) {
			this.timeoutMillis = timeoutMillis;
			return this;
		}
		
		public <T extends ServiceProvider> Locks<T> lock(T context) {
			if (CompareUtils.isEmpty(lockables) && context instanceof BranchContext bctx) {
				// by default if no lockable targets have been set, and this is a branch context target the current repository/branch
				on(bctx.info().id(), bctx.path());
			}
			return new Locks<>(context, user, lockContext, parentLockContext, lockables, timeoutMillis);
		} 
		
	}
	
	public static Builder forContext(String lockContext) {
		return forContext(lockContext, null);
	}
	
	public static Builder forContext(String lockContext, String parentLockContext) {
		return new Builder(lockContext, parentLockContext);
	}
	
	private final IOperationLockManager lockManager;
	private final DatastoreLockContext lockContext;
	private final Set<Lockable> lockables;
	private final T serviceContext;
	
	private Locks(T context, String userId, String description, String parentLockDescription, Set<Lockable> lockables, long timeoutMillis) throws LockedException {
		this.lockManager = context.service(IOperationLockManager.class);
		String lockOwner = Optional.ofNullable(userId).or(() -> context.optionalService(RemoteJob.class).map(RemoteJob::getUser)).orElse(context.service(User.class).getUserId());
		this.lockContext = new DatastoreLockContext(lockOwner, description, Optional.ofNullable(parentLockDescription).or(() -> context.optionalService(Locks.class).map(Locks::lockContext)).orElse(ROOT));
		this.lockables = new HashSet<>(lockables);
		this.lockManager.lock(lockContext, Math.max(timeoutMillis, IOperationLockManager.IMMEDIATE), lockables);
		this.serviceContext = (T) context.inject().bind(Locks.class, this).build();
	}
	
	public String lockContext() {
		return lockContext.getDescription();
	}
	
	public T ctx() {
		return serviceContext;
	}
	
	public void unlock(String repositoryId, String branchPath) {
		Lockable toUnlock = new Lockable(repositoryId, branchPath);
		if (lockables.contains(toUnlock)) {
			lockManager.unlock(lockContext, toUnlock);
			lockables.remove(toUnlock); // Not reached if an exception is thrown above
		}
	}

	public void unlockAll() {
		if (!lockables.isEmpty()) {
			lockManager.unlock(lockContext, lockables);
			lockables.clear(); // Not reached if an exception is thrown above
		}
	}
	
	@Override
	public void close() {
		unlockAll();
	}

}
