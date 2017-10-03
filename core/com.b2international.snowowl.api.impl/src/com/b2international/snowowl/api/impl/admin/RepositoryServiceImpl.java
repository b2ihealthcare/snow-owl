/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.impl.admin;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;

import com.b2international.snowowl.api.admin.exception.LockConflictException;
import com.b2international.snowowl.api.admin.exception.LockException;
import com.b2international.snowowl.api.admin.exception.RepositoryNotFoundException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.AllRepositoriesLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryLockTarget;
import com.b2international.snowowl.identity.domain.User;

/**
 */
public class RepositoryServiceImpl implements InternalRepositoryService {

	private static IDatastoreOperationLockManager getLockManager() {
		return ApplicationContext.getServiceForClass(IDatastoreOperationLockManager.class);
	}
	
	protected void checkValidRepositoryUuid(final String repositoryUuid) {
		checkNotNull(repositoryUuid, "Repository identifier may not be null.");

		final Repository repository = ApplicationContext.getServiceForClass(RepositoryManager.class).get(repositoryUuid);
		if (repository == null) {
			throw new RepositoryNotFoundException(repositoryUuid);
		}
	}

	@Override
	public void lockGlobal(final int timeoutMillis) {
		checkValidTimeout(timeoutMillis);

		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), 
				DatastoreLockContextDescriptions.CREATE_BACKUP);

		final IOperationLockTarget target = AllRepositoriesLockTarget.INSTANCE;
		doLock(timeoutMillis, context, target);
	}

	@Override
	public void unlockGlobal() {
		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), 
				DatastoreLockContextDescriptions.CREATE_BACKUP);

		final IOperationLockTarget target = AllRepositoriesLockTarget.INSTANCE;
		doUnlock(context, target);
	}

	@Override
	public void lockRepository(final String repositoryUuid, final int timeoutMillis) {
		checkValidRepositoryUuid(repositoryUuid);
		checkValidTimeout(timeoutMillis);

		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), 
				DatastoreLockContextDescriptions.CREATE_REPOSITORY_BACKUP,
				DatastoreLockContextDescriptions.CREATE_BACKUP);

		final IOperationLockTarget target = new SingleRepositoryLockTarget(repositoryUuid);
		doLock(timeoutMillis, context, target);
	}

	@Override
	public void unlockRepository(final String repositoryUuid) {
		checkValidRepositoryUuid(repositoryUuid);

		final DatastoreLockContext context = new DatastoreLockContext(User.SYSTEM.getUsername(), 
				DatastoreLockContextDescriptions.CREATE_REPOSITORY_BACKUP,
				DatastoreLockContextDescriptions.CREATE_BACKUP);

		final IOperationLockTarget target = new SingleRepositoryLockTarget(repositoryUuid);
		doUnlock(context, target);
	}

	private void checkValidTimeout(final int timeoutMillis) {
		checkArgument(timeoutMillis >= 0, "Timeout in milliseconds may not be negative.");
	}

	private void doLock(final int timeoutMillis, final DatastoreLockContext context, final IOperationLockTarget target) {
		try {
			getLockManager().lock(context, timeoutMillis, target);
		} catch (final DatastoreOperationLockException e) {

			final DatastoreLockContext conflictingContext = e.getContext(target);
			throw new LockConflictException(MessageFormat.format("Failed to acquire lock for all repositories because {0} is {1}.", 
					conflictingContext.getUserId(), 
					conflictingContext.getDescription()));

		} catch (final OperationLockException | InterruptedException e) {
			throw new LockException(e.getMessage());
		}
	}

	private void doUnlock(final DatastoreLockContext context, final IOperationLockTarget target) {
		try {
			getLockManager().unlock(context, target);
		} catch (final OperationLockException e) {
			throw new LockException(e.getMessage());
		}
	}
}