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

import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;

/**
 */
public class DelegatingTransaction implements InternalTransaction {

	private final InternalTransaction delegate;

	public DelegatingTransaction(final InternalTransaction delegate) {
		this.delegate = delegate;
	}
	
	public InternalTransaction getDelegate() {
		return delegate;
	}

	@Override
	public void close() {
		delegate.close();
	}

	@Override
	public boolean isClosed() {
		return delegate.isClosed();
	}

	@Override
	public InternalCommitContext createCommitContext() {
		return delegate.createCommitContext();
	}

	@Override
	public int getSessionID() {
		return delegate.getSessionID();
	}

	@Override
	public InternalRepository getRepository() {
		return delegate.getRepository();
	}

	@Override
	public CDORevision getRevision(final CDOID id) {
		return delegate.getRevision(id);
	}

	@Override
	public InternalSession getSession() {
		return delegate.getSession();
	}

	@Override
	public boolean isDurableView() {
		return delegate.isDurableView();
	}

	@Override
	public void activate() throws LifecycleException {
		delegate.activate();
	}

	@Override
	public void addListener(final IListener listener) {
		delegate.addListener(listener);
	}

	@Override
	public void setBranchPoint(final CDOBranchPoint branchPoint) {
		delegate.setBranchPoint(branchPoint);
	}

	@Override
	public Exception deactivate() {
		return delegate.deactivate();
	}

	@Override
	public void setDurableLockingID(final String durableLockingID) {
		delegate.setDurableLockingID(durableLockingID);
	}

	@Override
	public LifecycleState getLifecycleState() {
		return delegate.getLifecycleState();
	}

	@Override
	public boolean isActive() {
		return delegate.isActive();
	}

	@Override
	public void changeTarget(final CDOBranchPoint branchPoint, final List<CDOID> invalidObjects,
			final List<CDORevisionDelta> allChangedObjects, final List<CDOID> allDetachedObjects) {
		delegate.changeTarget(branchPoint, invalidObjects, allChangedObjects, allDetachedObjects);
	}

	@Override
	public void removeListener(final IListener listener) {
		delegate.removeListener(listener);
	}

	@Override
	public void subscribe(final CDOID id) {
		delegate.subscribe(id);
	}

	@Override
	public CDOBranch getBranch() {
		return delegate.getBranch();
	}

	@Override
	public int getViewID() {
		return delegate.getViewID();
	}

	@Override
	public void unsubscribe(final CDOID id) {
		delegate.unsubscribe(id);
	}

	@Override
	public boolean isReadOnly() {
		return delegate.isReadOnly();
	}

	@Override
	public boolean hasSubscription(final CDOID id) {
		return delegate.hasSubscription(id);
	}

	@Override
	public boolean hasListeners() {
		return delegate.hasListeners();
	}

	@Override
	public void clearChangeSubscription() {
		delegate.clearChangeSubscription();
	}

	@Override
	public void doClose() {
		delegate.doClose();
	}

	@Override
	public String getDurableLockingID() {
		return delegate.getDurableLockingID();
	}

	@Override
	public Options options() {
		return delegate.options();
	}

	@Override
	public long getTimeStamp() {
		return delegate.getTimeStamp();
	}

	@Override
	public IListener[] getListeners() {
		return delegate.getListeners();
	}
}