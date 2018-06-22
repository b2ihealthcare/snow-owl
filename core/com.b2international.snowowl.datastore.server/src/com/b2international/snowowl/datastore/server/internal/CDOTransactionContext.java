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
package com.b2international.snowowl.datastore.server.internal;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.exceptions.CycleDetectedException;
import com.b2international.commons.exceptions.Exceptions;
import com.b2international.commons.exceptions.LockedException;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.oplock.IOperationLockManager;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 4.5
 */
public final class CDOTransactionContext extends DelegatingBranchContext implements TransactionContext {

	private final String userId;
	private final String commitComment;
	private final String parentContextDescription;
	
	private boolean isNotificationEnabled = true;
	
	@JsonIgnore
	private transient IOperationLockTarget operationLockTarget;

	CDOTransactionContext(BranchContext context, String userId, String commitComment, String parentContextDescription) {
		super(context);
		this.userId = userId;
		this.commitComment = commitComment;
		this.parentContextDescription = parentContextDescription;
	}
	
	@Override
	public long commit() {
		return commit(userId(), commitComment, parentContextDescription);
	}
	
	@Override
	public String userId() {
		return userId;
	}
	
	@Override
	public <T> T service(Class<T> type) {
		if (CDOEditingContext.class.isAssignableFrom(type)) {
			return type.cast(editingContext);
		}
		return super.service(type);
	}
	
	@Override
	public <T> T lookup(String componentId, Class<T> type) {
		return editingContext.lookup(componentId, type);
	}
	
	@Override
	public <T> T lookupIfExists(String componentId, Class<T> type) {
		return editingContext.lookupIfExists(componentId, type);
	}
	
	@Override
	public <T> Map<String, T> lookup(Collection<String> componentIds, Class<T> type) {
		return editingContext.lookup(componentIds, type);
	}
	
	@Override
	public void add(Object o) {
		editingContext.add(o);
	}
	
	@Override
	public void update(Revision oldVersion, Revision newVersion) {
		
	}
	
	@Override
	public void delete(Object o) {
		editingContext.delete(o);
	}
	
	@Override
	public void delete(Object o, boolean force) {
		editingContext.delete(o, force);
	}
	
	@Override
	public void close() throws Exception {
		editingContext.close();
	}

	@Override
	public void rollback() {
		editingContext.rollback();
	}

	@Override
	public long commit(String userId, String commitComment, String parentContextDescription) {
		IOperationLockManager<DatastoreLockContext> locks = service(IOperationLockManager.class);
		try {
			acquireLock(locks);
			final CDOCommitInfo info = new CDOServerCommitBuilder(userId, commitComment, editingContext.getTransaction())
					.notifyWriteAccessHandlers(isNotificationEnabled())
					.sendCommitNotification(isNotificationEnabled())
					.parentContextDescription(parentContextDescription)
					.commitOne();
			return info.getTimeStamp();
		} catch (final CommitException e) {
			final RepositoryLockException cause = Exceptions.extractCause(e, getClass().getClassLoader(), RepositoryLockException.class);
			if (cause != null) {
				throw new LockedException(cause.getMessage());
			}
			
			final ConcurrentModificationException cause2 = Exceptions.extractCause(e, getClass().getClassLoader(), ConcurrentModificationException.class);
			if (cause2 != null) {
				throw new ConflictException("Concurrent modifications prevented the commit from being processed. Please try again.");
			}
			
			final CycleDetectedException cause3 = Exceptions.extractCause(e.getCause(), getClass().getClassLoader(), CycleDetectedException.class);
			if (cause3 != null) {
				throw cause3;
			}
			throw new SnowowlRuntimeException(e.getMessage(), e);
		} finally {
			if (null != operationLockTarget) {
				locks.unlock(createLockContext(), operationLockTarget);
				operationLockTarget = null;
			}
		}
	}

	private void acquireLock(IOperationLockManager<DatastoreLockContext> locks) {
		operationLockTarget = null;
		final IOperationLockTarget repositoryAndBranchTarget = createLockTarget();
		
		try {
			locks.lock(createLockContext(), 1000L, repositoryAndBranchTarget);
		} catch (final DatastoreOperationLockException e) {
			final DatastoreLockContext lockOwnerContext = e.getContext(repositoryAndBranchTarget);
			
			throw new RepositoryLockException(MessageFormat.format("Write access to {0} was denied because {1} is {2}. Please try again later.", 
					repositoryAndBranchTarget,
					lockOwnerContext.getUserId(), 
					lockOwnerContext.getDescription()));
		} catch (InterruptedException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	@Override
	public boolean isNotificationEnabled() {
		return isNotificationEnabled;
	}
	
	@Override
	public void setNotificationEnabled(boolean isNotificationEnabled) {
		this.isNotificationEnabled = isNotificationEnabled;
	}

	@Override
	public void clearContents() {
		editingContext.clearContents();
	}
	
	private DatastoreLockContext createLockContext() {
		return new DatastoreLockContext(userId(), DatastoreLockContextDescriptions.COMMIT, parentContextDescription);
	}
	
	private SingleRepositoryAndBranchLockTarget createLockTarget() {
		final IBranchPath branchPath = BranchPathUtils.createPath(branchPath());
		return new SingleRepositoryAndBranchLockTarget(id(), branchPath);
	}

}
