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
package com.b2international.snowowl.datastore.reindex;

import java.util.List;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.StoreAccessorDelegate;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Commit context that does not actually write into the repository, only pretends to do so
 * in order to trigger notifications.
 */
@SuppressWarnings("restriction")
class NonWritingTransactionCommitContext extends TransactionCommitContext {

	private final CDOCommitInfo commitInfo;

	NonWritingTransactionCommitContext(InternalTransaction transaction, CDOCommitInfo commitInfo) {
		super(transaction);
		this.commitInfo = commitInfo;

		getBranchPoint();
		setCommitComment(commitInfo.getComment());

		InternalCDOPackageUnit[] newPackageUnits = getNewPackageUnits(commitInfo, getPackageRegistry());
		setNewPackageUnits(newPackageUnits);

		InternalCDORevision[] newObjects = getNewObjects(commitInfo);
		setNewObjects(newObjects);

		InternalCDORevisionDelta[] dirtyObjectDeltas = getDirtyObjectDeltas(commitInfo);
		setDirtyObjectDeltas(dirtyObjectDeltas);

		CDOID[] detachedObjects = getDetachedObjects(commitInfo);
		setDetachedObjects(detachedObjects);
	}

	@Override
	public String getUserID() {
		return commitInfo.getUserID();
	}

	@Override
	protected long[] createTimeStamp(OMMonitor monitor) {
		
		//roll back the clock -1
		return new long[] { commitInfo.getTimeStamp()-1 , commitInfo.getPreviousTimeStamp() };
	}
	
	@Override
	public long getTimeStamp() {
		return commitInfo.getTimeStamp();
	}
	
	public void preWrite() {
		
		// Allocate a store writer
	    accessor = getTransaction().getRepository().getStore().getWriter(getTransaction());

		//hide everything that writes in the store
		accessor = new StoreAccessorDelegate(accessor) {

			@Override
			public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo) {
				throw new RuntimeException("Branch should not be created: " + branchInfo);
			}
			
			@Override
			public void write(InternalCommitContext context, OMMonitor monitor) {
				applyIDMappings(monitor);
			}
			
			@Override
			public void commit(OMMonitor monitor) {
				//do nothing
			}
		};

		StoreThreadLocal.setCommitContext(this);
	}
	
	@Override
	public void postCommit(boolean success) {
		accessor.release();
		super.postCommit(success);
	}
	
	@Override
	protected void adjustForCommit() {
		// Do nothing
	}

	@Override
	public void applyIDMappings(OMMonitor monitor) {
		monitor.begin();

		//this is the notification that will drive the change processor
		//and the sole reason of this class
		try {
			notifyBeforeCommitting(monitor);
		} finally {
			monitor.done();
		}
	}

	private static InternalCDOPackageUnit[] getNewPackageUnits(CDOCommitInfo commitInfo, InternalCDOPackageRegistry packageRegistry) {
		List<CDOPackageUnit> list = commitInfo.getNewPackageUnits();
		InternalCDOPackageUnit[] result = new InternalCDOPackageUnit[list.size()];

		int i = 0;
		for (CDOPackageUnit packageUnit : list) {
			result[i] = (InternalCDOPackageUnit) packageUnit;
			packageRegistry.putPackageUnit(result[i]);
			++i;
		}
		return result;
	}

	private static InternalCDORevision[] getNewObjects(CDOCommitInfo commitInfo) {
		List<CDOIDAndVersion> list = commitInfo.getNewObjects();
		InternalCDORevision[] result = new InternalCDORevision[list.size()];

		int i = 0;
		for (CDOIDAndVersion revision : list) {
			result[i++] = (InternalCDORevision) revision;
		}

		return result;
	}

	private static InternalCDORevisionDelta[] getDirtyObjectDeltas(CDOCommitInfo commitInfo) {
		List<CDORevisionKey> list = commitInfo.getChangedObjects();
		InternalCDORevisionDelta[] result = new InternalCDORevisionDelta[list.size()];

		int i = 0;
		for (CDORevisionKey delta : list) {
			result[i++] = (InternalCDORevisionDelta) delta;
		}

		return result;
	}

	private static CDOID[] getDetachedObjects(CDOCommitInfo commitInfo) {
		List<CDOIDAndVersion> list = commitInfo.getDetachedObjects();
		CDOID[] result = new CDOID[list.size()];

		int i = 0;
		for (CDOIDAndVersion key : list) {
			result[i++] = key.getID();
		}
		return result;
	}
	
	@Override
	protected void lockObjects() throws InterruptedException {
		// Do nothing
	}

	@Override
	protected void checkXRefs() {
		// Do nothing
	}

}
