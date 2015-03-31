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
package com.b2international.snowowl.datastore.cdo


import org.eclipse.emf.cdo.common.branch.CDOBranchPoint
import org.eclipse.emf.cdo.common.id.CDOIDUtil
import org.eclipse.emf.cdo.common.lock.CDOLockState
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation
import org.eclipse.emf.internal.cdo.view.AbstractCDOView
import org.eclipse.emf.spi.cdo.InternalCDOTransaction
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult

/**
 * Wrapper for {@link InternalCDOTransaction CDO transaction}s. 
 * Workaround to modify instance behavior via NON-API interface. 
 * @groovy
 */
@SuppressWarnings("restriction")
class InternalCDOTransactionWrapper {

	final InternalCDOTransaction transaction

	/**Constructor wrapping a CDO transaction.*/
	InternalCDOTransactionWrapper(Object cdoTransaction) {
		transaction = cdoTransaction
	}

	/**@see org.eclipse.emf.internal.cdo.view.AbstractCDOView.basicSetBranchPoint(CDOBranchPoint)*/
	def basicSetBranchPoint(Object branchPoint) {
		transaction.basicSetBranchPoint(branchPoint)
	}

	/**@see org.eclipse.emf.internal.cdo.view.CDOViewImpl.getChangeSubscriptionManager()*/
	def notifyChangeSubscribers(Object commitContext) {

		transaction.changeSubscriptionManager.committedTransaction(transaction, commitContext)
	}

	/**@see org.eclipse.emf.internal.cdo.view.CDOViewImpl.getAdapterManager()*/
	def notifyAdapters(Object commitContext) {

		transaction.adapterManager.committedTransaction(transaction, commitContext)
	}

	/**@see org.eclipse.emf.internal.cdo.view.CDOViewImpl.updateAndNotifyLockStates(Operation, LockType, long, CDOLockState[])*/
	def releaseLockStates(CommitTransactionResult result) {
		releaseLockStates(Operation.UNLOCK, result.newLockStates, result.getTimeStamp())
	}
	
	def releaseLockStates(Operation op, CDOLockState[] lockStates, long timestamp) {
		if (lockStates != null) {
			transaction.updateAndNotifyLockStates(op, null, timestamp, lockStates)
		}
	}

	/**@see org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl.cleanUp(CDOCommitContext)*/
	def cleanUp(Object commitContext) {
		transaction.cleanUp(commitContext)
	}

	/**@see org.eclipse.emf.internal.cdo.view.AbstractCDOView.getBranchPoint()*/
	CDOBranchPoint getBranchPoint() {
		return transaction.getBranchPoint()
	}
	
	/**Completely cleans up any changes made through the wrapped transaction. Clears all object maps,
	 *resets all save points and moves the branch to the HEAD of the transaction.*/
	def reset() {
		
		synchronized (transaction) {
			
			((AbstractCDOView) transaction).getModifiableObjects().clear()
			//this will set lastLookupId and lastLookupObject to null reference 
			((AbstractCDOView) transaction).getObject(CDOIDUtil.createLong(CDOUtils.NO_STORAGE_KEY), false)
			basicSetBranchPoint(transaction.branch.getHead())
			transaction.cleanRevisions.clear()
			
			cleanUp(null)
			
		}
		
	}
	
}
