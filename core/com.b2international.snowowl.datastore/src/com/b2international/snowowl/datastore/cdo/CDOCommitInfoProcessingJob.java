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
package com.b2international.snowowl.datastore.cdo;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.google.common.base.Preconditions;

/**
 * Job for processing whether a remote change affects the underlying component and the transaction.
 * Also contains information about the detached CDO objects that should be removed explicitly from the editor page inputs.
 */
public abstract class CDOCommitInfoProcessingJob extends Job {

	public enum CommitInfoAction {
		NONE,
		MERGE_NO_REFRESH,
		MERGE_WITH_REFRESH,
		CONFLICT
	}
	
	private final CDOObject component;
	private final CDOCommitInfo commitInfo;
	private final CDOTransaction transaction;

	private CommitInfoAction action = CommitInfoAction.NONE;
	
	/**
	 * Creates a new job instance.
	 * @param component the component to check. 
	 * @param transaction the underlying transaction for the component.
	 * @param commitInfo the commit info arrived from the remote change notification. Can be {@code null}.
	 */
	public CDOCommitInfoProcessingJob(final CDOObject component, final CDOTransaction transaction, @Nullable final CDOCommitInfo commitInfo) {
		super("Processing incoming remote changes...");

		this.component = component;
		this.transaction = transaction;
		this.commitInfo = commitInfo;
		
		Preconditions.checkNotNull(this.component, "Component argument cannot be null.");
		Preconditions.checkNotNull(this.transaction, "Transaction argument cannot be null.");
	}

	public CommitInfoAction getAction() {
		return action;
	}

	/**
	 * Returns with the component.
	 * @return the component.
	 */
	protected CDOObject getComponent() {
		return component;
	}
	
	protected void setAction(CommitInfoAction action) {
		this.action = checkNotNull(action, "action");
	}
	
	/**
	 * Returns with the commit info. Can be {@code null}.
	 * @return the commit info or {@code null} if it was not specified.
	 */
	@Nullable
	protected CDOCommitInfo getCommitInfo() {
		return commitInfo;
	}
	
	/**
	 * Returns with the underlying CDO transaction. <p>
	 * Clients should not take care whether the transaction is active. If the transaction is closed this method returns with {@code null}.
	 * @return the CDO transaction or {@code null}.
	 */
	@Nullable
	protected synchronized CDOTransaction getTransactionSafe() {
		return checkTransaction() ? transaction : null;
	}
	
	protected synchronized boolean checkTransaction() {
		final boolean transactionNull = isTransactionNull();
		final boolean transactionClosed = isTransactionClosed();
		return !transactionNull && !transactionClosed;
	}
	
	protected synchronized boolean isTransactionNull() {
		return null == transaction;
	}
	
	protected synchronized boolean isTransactionClosed() {
		return transaction.isClosed();
	}
	
	protected synchronized boolean isTransactionDirty() {
		return transaction.isDirty();
	}
}