/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.cdo;

import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.oplock.IOperationLockTarget;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContext;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreOperationLockException;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.impl.SingleRepositoryAndBranchLockTarget;
import com.google.common.base.Preconditions;

/**
 * Extended {@link TransactionCommitContext} implementation with configurable error logging strategy and application-level locking.
 * 
 * @see IErrorLoggingStrategy
 */
@SuppressWarnings("restriction")
public class CustomTransactionCommitContext extends TransactionCommitContext {

	private final IErrorLoggingStrategy strategy;
	
	private IOperationLockTarget operationLockTarget;
	private String userId;

	public CustomTransactionCommitContext(final InternalTransaction transaction) {
		this(transaction, OmErrorLoggingStrategy.INSTANCE);
	}
	
	public CustomTransactionCommitContext(final InternalTransaction transaction, final IErrorLoggingStrategy strategy) {
		super(transaction);
		this.userId = transaction.getSession().getUserID();
		this.strategy = Preconditions.checkNotNull(strategy, "strategy");
	}
	
	@Override
	protected void logError(final Throwable t) {
		strategy.logError(t);
	}
	
	@Override
	public String getUserID() {
		return userId;
	}
	
	@Override
	protected void lockObjects() throws InterruptedException {
		
		operationLockTarget = null;
		final IOperationLockTarget repositoryAndBranchTarget = createLockTarget();
		
		try {
			getDatastoreOperationLockManager().lock(createLockContext(), 1000L, repositoryAndBranchTarget);
		} catch (final DatastoreOperationLockException e) {
			final DatastoreLockContext lockOwnerContext = e.getContext(repositoryAndBranchTarget);
			
			throw new RepositoryLockException(MessageFormat.format("Write access to {0} was denied because {1} is {2}. Please try again later.", 
					repositoryAndBranchTarget,
					lockOwnerContext.getUserId(), 
					lockOwnerContext.getDescription()));
		}
		
		operationLockTarget = repositoryAndBranchTarget;
		super.lockObjects();
	}
	
	@Override
	protected synchronized void unlockObjects() {
		try {
			super.unlockObjects();
		} finally {
			if (null != operationLockTarget) {
				getDatastoreOperationLockManager().unlock(createLockContext(), operationLockTarget);
				operationLockTarget = null;
			}
		}
	}

	private DatastoreLockContext createLockContext() {
		return new DatastoreLockContext(getUserID(), DatastoreLockContextDescriptions.COMMIT, getParentContextDescription());
	}

	protected String getParentContextDescription() {
		return DatastoreLockContextDescriptions.ROOT;
	}

	private SingleRepositoryAndBranchLockTarget createLockTarget() {
		final String repositoryUuid = getTransaction().getRepository().getName();
		final IBranchPath branchPath = BranchPathUtils.createPath(getBranchPoint().getBranch());
		return new SingleRepositoryAndBranchLockTarget(repositoryUuid, branchPath);
	}

	private IDatastoreOperationLockManager getDatastoreOperationLockManager() {
		return getApplicationContext().getService(IDatastoreOperationLockManager.class);
	}

	private ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("{0}[{1}, {2}, {3}]", 
				getClass().getSimpleName(), 
				getTransaction().getSession(), 
				getTransaction(), 
				CDOCommonUtil.formatTimeStamp(getTimeStamp()));
	}
}