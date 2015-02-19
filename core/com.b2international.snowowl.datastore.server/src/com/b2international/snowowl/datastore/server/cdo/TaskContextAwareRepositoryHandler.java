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

import java.util.Set;

import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.users.IUserManager;
import com.b2international.snowowl.core.users.Role;
import com.b2international.snowowl.core.users.SpecialRole;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.b2international.snowowl.datastore.server.session.ApplicationSessionManager;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.datastore.tasks.ITaskContext;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Customized write access handler for prohibiting repository content modifications 
 * based on the client side's current task context.
 */
public class TaskContextAwareRepositoryHandler implements WriteAccessHandler {

	private final String repositoryUuid;

	public TaskContextAwareRepositoryHandler(final String repositoryUuid) {
		this.repositoryUuid = Preconditions.checkNotNull(repositoryUuid, "Repository UUID argument cannot be null.");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler#handleTransactionBeforeCommitting(org.eclipse.emf.cdo.server.ITransaction, org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	public void handleTransactionBeforeCommitting(final ITransaction transaction, final CommitContext commitContext, final OMMonitor monitor) throws RuntimeException {
		check(transaction);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler#handleTransactionAfterCommitted(org.eclipse.emf.cdo.server.ITransaction, org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	public void handleTransactionAfterCommitted(final ITransaction transaction, final CommitContext commitContext, final OMMonitor monitor) {
		check(transaction);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler#handleTransactionRollback(org.eclipse.emf.cdo.server.ITransaction, org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext)
	 */
	@Override
	public void handleTransactionRollback(final ITransaction transaction, final CommitContext commitContext) {
		//rollback check is intentionally ignored
	}

	/*checks whether modification is allowed on the current repository based on the client side's current task state or not.*/
	private void check(final ITransaction transaction) {
		if (hasRpcSession(transaction)) {
			checkRemoteCommitRequest(transaction);
		} else {
			//XXX removed due to missing configuration for JAAS when using LDAP
//			checkEmbeddedCommitRequest(transaction);
		}
	}

	private void checkRemoteCommitRequest(final ITransaction transaction) {
		if (!isAdministrator(transaction)) {
			
			final String activeTaskId = getActiveTaskId(transaction);
			if (null == activeTaskId) {
				throw new RepositoryLockException("Insufficient privileges to modify the content of " + getToolingName() + ". " + getUserId(transaction) + " is not working on active task.");
			}
			
			if (!canModifyRepositoryContent(activeTaskId)) {
				throw new RepositoryLockException("Modifying the content of " + getToolingName() + " is not allowed from Task " + activeTaskId + ".");
			}
			
		}
	}

	private void checkEmbeddedCommitRequest(final ITransaction transaction) {
		final String userId = getUserId(transaction);
		if (!canRecognizeUser(userId)) {
			throw new RepositoryLockException("Insufficient privileges to modify the content of " + getToolingName() + ". Cannot authenticate user with ID: '" + userId + "'.");
		}
	}

	private boolean canRecognizeUser(final String userId) {
		return null != getUserManager().getUser(userId);
	}

	private IUserManager getUserManager() {
		return ApplicationContext.getInstance().getService(IUserManager.class);
	}
	
	private boolean hasRpcSession(ITransaction transaction) {
		return null != getApplicationSessionManager().getSession(transaction);
	}

	/*returns true if the repository content is allowed for the given task ID. otherwise returns false.*/
	private boolean canModifyRepositoryContent(final String activeTaskId) {
		return getAllowedRepositoryUuids(activeTaskId).contains(repositoryUuid);
	}

	/*returns with the active task ID associated with the transaction*/
	private String getActiveTaskId(final ITransaction transaction) {
		return getTaskStateManager().getActiveTaskId(getUserId(transaction));
	}

	/*returns with the user ID associated with the transaction argument*/
	private String getUserId(final ITransaction transaction) {
		return transaction.getSession().getUserID();
	}

	/*returns with the human readable name of the tooling support where the underlying repository is working on*/
	private String getToolingName() {
		return CodeSystemUtils.getSnowOwlToolingName(repositoryUuid);
	}

	/*returns with a set of repository UUIDs where modifications are allowed.*/
	private Set<String> getAllowedRepositoryUuids(final String taskId) {
		return Sets.newHashSet(getTaskContext(taskId).getPolicy().getRepositoryUuids());
	}

	/*return with the task context for the given task ID argument*/
	private ITaskContext getTaskContext(final String taskId) {
		return getTaskStateManager().getTask(taskId).getTaskContext();
	}

	/*returns with the task state manager service*/
	private ITaskStateManager getTaskStateManager() {
		return ApplicationContext.getInstance().getService(ITaskStateManager.class);
	}

	/*returns true if the repository modification (represented as the transaction argument) was performed by an administrator user.*/
	private boolean isAdministrator(final ITransaction transaction) {
		return getCurrentRoles(transaction).contains(SpecialRole.ADMINISTRATOR);
	}

	/*extracts and returns with the associated roles of the user who started the transaction argument.*/
	private Set<Role> getCurrentRoles(final ITransaction transaction) {
		return getApplicationSessionManager().getRoles(transaction);
	}

	/*returns with the application session manager*/
	private ApplicationSessionManager getApplicationSessionManager() {
		return (ApplicationSessionManager) ApplicationContext.getInstance().getService(IApplicationSessionManager.class);
	}
	
}