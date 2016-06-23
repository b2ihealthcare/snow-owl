/*******************************************************************************
 * Copyright (c) 2016 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.importer.rf2.command;

import java.util.List;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
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
 *
 */
@SuppressWarnings("restriction")
public class NonWritingReplicatorCommitContext extends TransactionCommitContext {

	private final CDOCommitInfo commitInfo;

	//private IStoreAccessor reader;

	public NonWritingReplicatorCommitContext(InternalTransaction transaction, CDOCommitInfo commitInfo) {
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
		return new long[] { commitInfo.getTimeStamp() -1 , commitInfo.getPreviousTimeStamp() };
	}

	public void preWrite() {
		
		// Allocate a store writer
	    accessor = getTransaction().getRepository().getStore().getWriter(getTransaction());

		//hide everything the writes in the store
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
			}
		};

		// Make the store reader available as a ThreadLocal variable
		//StoreThreadLocal.setAccessor(accessor);
		StoreThreadLocal.setCommitContext(this);
	}
	
	@Override
	protected void adjustForCommit() {
		// Do nothing
	}

	@Override
	public void applyIDMappings(OMMonitor monitor) {
		monitor.begin();

		// this notification will drive the change processor
		try {
			notifyBeforeCommitting(monitor);
		} finally {
			monitor.done();
		}
	}

	@Override
	protected void lockObjects() throws InterruptedException {
		// Do nothing
	}

	@Override
	protected void checkXRefs() {
		// Do nothing
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

}
