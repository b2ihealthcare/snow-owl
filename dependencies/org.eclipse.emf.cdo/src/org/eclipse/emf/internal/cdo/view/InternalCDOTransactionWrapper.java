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
package org.eclipse.emf.internal.cdo.view;

import java.lang.reflect.Method;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.net4j.util.ReflectUtil;

/**
 * Wrapper for {@link CDOTransaction CDO transaction}s, exposing non-API methods of the subject.
 */
@Deprecated
public class InternalCDOTransactionWrapper {

	private final CDOTransactionImpl delegate;

	/** 
	 * Creates a new instance, wrapping the specified transaction. 
	 */
	public InternalCDOTransactionWrapper(final CDOTransaction delegate) {
		this.delegate = (CDOTransactionImpl) delegate;
	}

	/**
	 * @see org.eclipse.emf.internal.cdo.view.AbstractCDOView.basicSetBranchPoint(CDOBranchPoint)
	 */
	public void basicSetBranchPoint(final CDOBranchPoint branchPoint) {
		delegate.basicSetBranchPoint(branchPoint);
	}

	/**
	 * @see org.eclipse.emf.internal.cdo.view.CDOViewImpl.getChangeSubscriptionManager()
	 */
	public void notifyChangeSubscribers(final CDOCommitContext commitContext) {
		delegate.getChangeSubscriptionManager().committedTransaction(delegate, commitContext);
	}

	/**
	 * @see org.eclipse.emf.internal.cdo.view.CDOViewImpl.getAdapterManager()
	 */
	public void notifyAdapters(final CDOCommitContext commitContext) {
		delegate.getAdapterManager().committedTransaction(delegate, commitContext);
	}

	/**
	 * @see org.eclipse.emf.internal.cdo.view.CDOViewImpl.updateAndNotifyLockStates(Operation, LockType, long, CDOLockState[])
	 */
	public void releaseLockStates(final CommitTransactionResult result) {
		releaseLockStates(Operation.UNLOCK, result.getNewLockStates(), result.getTimeStamp());
	}

	public void releaseLockStates(final Operation op, final CDOLockState[] lockStates, final long timestamp) {
		if (lockStates != null) {
			delegate.updateAndNotifyLockStates(op, null, timestamp, lockStates);
		}
	}

	/**
	 * @see org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl.cleanUp(CDOCommitContext)
	 */
	public void cleanUp(final CDOCommitContext commitContext) {
		final Method cleanUpMethod = ReflectUtil.getMethod(CDOTransactionImpl.class, "cleanUp", CDOCommitContext.class);
		ReflectUtil.invokeMethod(cleanUpMethod, delegate, commitContext);
	}

	/**
	 * @see org.eclipse.emf.internal.cdo.view.AbstractCDOView.getBranchPoint()
	 */
	public CDOBranchPoint getBranchPoint() {
		return delegate.getBranchPoint();
	}
}
