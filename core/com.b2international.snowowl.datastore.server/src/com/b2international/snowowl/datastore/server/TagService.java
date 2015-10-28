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
package com.b2international.snowowl.datastore.server;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.cdo.ICDORepository;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.oplock.IOperationLockManager;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.OperationLockException;
import com.b2international.snowowl.datastore.oplock.OperationLockRunner;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.version.ITagConfiguration;
import com.b2international.snowowl.datastore.version.ITagService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Preconditions;

/**
 * Service for tagging the content of a {@link ICDORepository repository}.
 */
public class TagService implements ITagService {

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.version.ITagService#tag(com.b2international.snowowl.datastore.version.ITagConfiguration)
	 */
	@Override
	public void tag(final ITagConfiguration tagConfiguration) {

		Preconditions.checkNotNull(tagConfiguration);
		
		final IBranchPath branchPath = Preconditions.checkNotNull(tagConfiguration.getBranchPath(), "Branch path argument cannot be null.");
		final String userId = Preconditions.checkNotNull(tagConfiguration.getUserId(), "User identifier argument cannot be null.");
		final String versionId = Preconditions.checkNotNull(tagConfiguration.getVersionId(), "Version ID argument cannot be null.");
		final String uuid = Preconditions.checkNotNull(tagConfiguration.getRepositoryUuid(), "Repository UUID argument cannot be null.");
		final String parentContextDescription = Preconditions.checkNotNull(tagConfiguration.getParentContextDescription(), "Parent lock context description cannot be null.");
		
		final IOperationLockTarget lockTarget = new SingleRepositoryAndBranchLockTarget(uuid, branchPath);
		final DatastoreLockContext lockContext = new DatastoreLockContext(userId, 
				DatastoreLockContextDescriptions.REGISTER_NEW_CODE_SYSTEM, 
				parentContextDescription);

		try {
			final IDatastoreOperationLockManager lockManager = getLockManager();
			
			OperationLockRunner.with(lockManager).run(new Runnable() { @Override public void run() {
				final IEventBus bus = ApplicationContext.getServiceForClass(IEventBus.class);
				try {
					RepositoryRequests
						.branching(uuid)
						.prepareCreate()
						.setParent(branchPath.getPath())
						.setName(versionId)
						.build()
						.execute(bus).get();
				} catch (final InterruptedException e) {
					throw new SnowowlRuntimeException(e);
				} catch (final ExecutionException e) {
					throw new SnowowlRuntimeException(e);
				}
			}}, lockContext, IOperationLockManager.IMMEDIATE, lockTarget);
		} catch (final OperationLockException | InterruptedException e) {
			throw new RepositoryLockException(e);
		} catch (final InvocationTargetException e) {
			throw SnowowlRuntimeException.wrap(e.getCause());
		}
	}

	private IDatastoreOperationLockManager getLockManager() {
		return ApplicationContext.getServiceForClass(IDatastoreOperationLockManager.class);
	}
}
