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
package com.b2international.snowowl.datastore.oplock.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.datastore.oplock.IOperationLock;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.ReentrantOperationLockManager;
import com.b2international.snowowl.identity.domain.User;

/**
 * Controls cross-cutting exclusive write access to the terminology stores.
 * <p>
 * This implementation unlocks all held locks on disposal.
 *
 */
public class DatastoreOperationLockManager extends ReentrantOperationLockManager<DatastoreLockContext> implements IDatastoreOperationLockManager, IDisposableService {

	private final AtomicBoolean disposed = new AtomicBoolean(false);
	
	@Override
	protected boolean canContextLock(final DatastoreLockContext context, final IOperationLock<DatastoreLockContext> existingLock) {
		return context.isCompatible(existingLock.getContext());
	}
	
	@Override
	protected boolean canContextUnlock(DatastoreLockContext context, IOperationLock<DatastoreLockContext> existingLock) {
		return context.userMatches(existingLock.getContext());
	}
	
	@Override
	protected void throwLockException(final String message, final Map<IOperationLockTarget, DatastoreLockContext> targets) throws DatastoreOperationLockException {
		throw new DatastoreOperationLockException(message, targets);
	}
	
	@Override
	protected void canContextLockTargets(final DatastoreLockContext context, final Iterable<? extends IOperationLockTarget> targets, final Map<IOperationLockTarget, DatastoreLockContext> alreadyLockedTargets) 
			throws DatastoreOperationLockException {

		if (!isDisposed()) {
			super.canContextLockTargets(context, targets, alreadyLockedTargets);
		} else {
			final DatastoreLockContext disposedContext = new DatastoreLockContext(User.SYSTEM.getUsername(), DatastoreLockContextDescriptions.DISPOSE_LOCK_MANAGER);
			for (final IOperationLockTarget target : targets) {
				alreadyLockedTargets.put(target, disposedContext);
			}
			
			throwLockException(ACQUIRE_FAILED_MESSAGE, alreadyLockedTargets);
		}
	}
	
	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			unlockAll();
			clearListeners();
		}
	}

	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
}