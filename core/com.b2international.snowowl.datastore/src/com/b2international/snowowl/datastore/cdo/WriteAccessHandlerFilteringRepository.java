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

import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.ObjectWriteAccessHandler;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.google.common.base.Preconditions;

/**
 * Delegating {@link InternalRepository repository} with customized
 * {@link #notifyWriteAccessHandlers(ITransaction, CommitContext, boolean, OMMonitor)} functionality.
 * @see InternalRepository
 * @see WriteAccessHandler
 * @see ICDOChangeManager
 * @see ObjectWriteAccessHandler
 */
@SuppressWarnings("restriction")
public class WriteAccessHandlerFilteringRepository extends org.eclipse.emf.cdo.internal.server.DelegatingRepository {

	private final boolean notifyWriteAccesshandlers;
	private final InternalRepository delegate;

	/**
	 * Creates a delegating {@link InternalRepository repository} instance. This repository instance could 
	 * be configured to ignore notifying all registered {@link WriteAccessHandler write access handler}s 
	 * to the delegating repository.
	 * @param delegate the delegate repository.
	 * @param notifyWriteAccesshandlers {@code false} if the {@link WriteAccessHandler} should not be notified 
	 * about changes, otherwise {@code false}.
	 */
	public WriteAccessHandlerFilteringRepository(final InternalRepository delegate, final boolean notifyWriteAccesshandlers) {
		this.delegate = Preconditions.checkNotNull(delegate, "Delegate repository argument cannot be null.");
		this.notifyWriteAccesshandlers = notifyWriteAccesshandlers;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.internal.server.DelegatingRepository#notifyWriteAccessHandlers(org.eclipse.emf.cdo.server.ITransaction, 
	 * org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext, boolean, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	public void notifyWriteAccessHandlers(final ITransaction transaction, final CommitContext commitContext, final boolean beforeCommit, final OMMonitor monitor) {
		if (notifyWriteAccesshandlers) {
			super.notifyWriteAccessHandlers(transaction, commitContext, beforeCommit, monitor);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.internal.server.DelegatingRepository#rollbackWriteAccessHandlers(org.eclipse.emf.cdo.server.ITransaction, org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext)
	 */
	@Override
	public void rollbackWriteAccessHandlers(final ITransaction transaction, final CommitContext commitContext) {
		if (notifyWriteAccesshandlers) {
			super.rollbackWriteAccessHandlers(transaction, commitContext);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.internal.server.DelegatingRepository#getDelegate()
	 */
	@Override
	protected InternalRepository getDelegate() {
		return delegate;
	}
}