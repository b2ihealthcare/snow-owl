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

import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Delegating commit context implementation with overridden {@link #getRevision(CDOID)} behavior.
 */
@SuppressWarnings("restriction")
final class CDODelegatingCommitContext extends org.eclipse.emf.cdo.internal.server.DelegatingCommitContext {
	
	
	private final TransactionCommitContext delegate;
	
	private CDORevisionProvider provider;

	CDODelegatingCommitContext(final TransactionCommitContext delegate, final CDORevisionProvider provider) {
		this.delegate = delegate;
		this.provider = provider;
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public InternalTransaction getTransaction() {
		return delegate.getTransaction();
	}

	public CDOBranchPoint getBranchPoint() {
		return delegate.getBranchPoint();
	}

	public String getUserID() {
		return delegate.getUserID();
	}

	public String getCommitComment() {
		return delegate.getCommitComment();
	}

	public boolean isAutoReleaseLocksEnabled() {
		return delegate.isAutoReleaseLocksEnabled();
	}

	public String getRollbackMessage() {
		return delegate.getRollbackMessage();
	}

	public List<CDOIDReference> getXRefs() {
		return delegate.getXRefs();
	}

	public InternalCDOPackageRegistry getPackageRegistry() {
		return delegate.getPackageRegistry();
	}

	public InternalCDOPackageUnit[] getNewPackageUnits() {
		return delegate.getNewPackageUnits();
	}

	public CDOLockState[] getLocksOnNewObjects() {
		return delegate.getLocksOnNewObjects();
	}

	public InternalCDORevision[] getNewObjects() {
		return delegate.getNewObjects();
	}

	public InternalCDORevision[] getDirtyObjects() {
		return delegate.getDirtyObjects();
	}

	public CDOID[] getDetachedObjects() {
		return delegate.getDetachedObjects();
	}

	public Map<CDOID, EClass> getDetachedObjectTypes() {
		return delegate.getDetachedObjectTypes();
	}

	public InternalCDORevision[] getDetachedRevisions() {
		return delegate.getDetachedRevisions();
	}

	public InternalCDORevisionDelta[] getDirtyObjectDeltas() {
		return delegate.getDirtyObjectDeltas();
	}

	public CDORevision getRevision(CDOID id) {
		return delegate.getRevision(id, provider);
	}

	public Map<CDOID, CDOID> getIDMappings() {
		return delegate.getIDMappings();
	}

	public void addIDMapping(CDOID oldID, CDOID newID) {
		delegate.addIDMapping(oldID, newID);
	}

	public void applyIDMappings(OMMonitor monitor) {
		delegate.applyIDMappings(monitor);
	}

	public void preWrite() {
		delegate.preWrite();
	}

	public void setNewPackageUnits(InternalCDOPackageUnit[] newPackageUnits) {
		delegate.setNewPackageUnits(newPackageUnits);
	}

	public void setLocksOnNewObjects(CDOLockState[] locksOnNewObjects) {
		delegate.setLocksOnNewObjects(locksOnNewObjects);
	}

	public void setNewObjects(InternalCDORevision[] newObjects) {
		delegate.setNewObjects(newObjects);
	}

	public void setDirtyObjectDeltas(InternalCDORevisionDelta[] dirtyObjectDeltas) {
		delegate.setDirtyObjectDeltas(dirtyObjectDeltas);
	}

	public void setDetachedObjects(CDOID[] detachedObjects) {
		delegate.setDetachedObjects(detachedObjects);
	}

	public void setDetachedObjectTypes(Map<CDOID, EClass> detachedObjectTypes) {
		delegate.setDetachedObjectTypes(detachedObjectTypes);
	}

	public void setAutoReleaseLocksEnabled(boolean on) {
		delegate.setAutoReleaseLocksEnabled(on);
	}

	public void setCommitComment(String commitComment) {
		delegate.setCommitComment(commitComment);
	}

	public ExtendedDataInputStream getLobs() {
		return delegate.getLobs();
	}

	public void setLobs(ExtendedDataInputStream in) {
		delegate.setLobs(in);
	}

	public void write(OMMonitor monitor) {
		delegate.write(monitor);
	}

	public void commit(OMMonitor monitor) {
		delegate.commit(monitor);
	}

	public List<LockState<Object, IView>> getPostCommmitLockStates() {
		return delegate.getPostCommmitLockStates();
	}

	public long getPreviousTimeStamp() {
		return delegate.getPreviousTimeStamp();
	}

	public void postCommit(boolean success) {
		delegate.postCommit(success);
	}

	public CDOCommitInfo createCommitInfo() {
		return delegate.createCommitInfo();
	}

	public CDOCommitInfo createFailureCommitInfo() {
		return delegate.createFailureCommitInfo();
	}

	public void rollback(String message) {
		delegate.rollback(message);
	}

	public String toString() {
		return delegate.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.internal.server.DelegatingCommitContext#getDelegate()
	 */
	@Override
	protected CommitContext getDelegate() {
		return delegate;
	}
}