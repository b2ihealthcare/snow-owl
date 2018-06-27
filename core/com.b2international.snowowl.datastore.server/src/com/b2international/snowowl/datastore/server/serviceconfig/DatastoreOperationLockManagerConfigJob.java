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
package com.b2international.snowowl.datastore.server.serviceconfig;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IServiceChangeListener;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.internal.session.ApplicationSessionManager;
import com.b2international.snowowl.datastore.oplock.impl.IDatastoreOperationLockManager;
import com.b2international.snowowl.datastore.server.DatastoreServerActivator;
import com.b2international.snowowl.datastore.server.oplock.impl.DatastoreOperationLockManager;
import com.b2international.snowowl.datastore.server.oplock.impl.RemoteLockTargetListener;
import com.b2international.snowowl.datastore.server.oplock.impl.Slf4jOperationLockTargetListener;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;

/**
 * Initializes the datastore lock manager on server startup.
 * 
 */
public class DatastoreOperationLockManagerConfigJob extends AbstractServerServiceConfigJob<IDatastoreOperationLockManager> {

	private static final String JOB_NAME = "Datastore operation lock management service configuration...";

	public DatastoreOperationLockManagerConfigJob() {
		super(JOB_NAME, DatastoreServerActivator.PLUGIN_ID);
	}

	@Override
	protected Class<IDatastoreOperationLockManager> getServiceClass() {
		return IDatastoreOperationLockManager.class;
	}

	@Override
	protected IDatastoreOperationLockManager createServiceImplementation() throws SnowowlServiceException {
		
		final DatastoreOperationLockManager service = new DatastoreOperationLockManager();
		final RemoteLockTargetListener remoteLockTargetListener = new RemoteLockTargetListener();
		service.addLockTargetListener(new Slf4jOperationLockTargetListener());
		service.addLockTargetListener(remoteLockTargetListener);
		
		ApplicationContext.getInstance().addServiceListener(IApplicationSessionManager.class, new IServiceChangeListener<IApplicationSessionManager>() {
			
			@Override
			public void serviceChanged(final IApplicationSessionManager oldService, final IApplicationSessionManager newService) {
				if (oldService != null) {
					((ApplicationSessionManager) oldService).removeListener(remoteLockTargetListener);
				}
				
				if (newService != null) {
					((ApplicationSessionManager) newService).addListener(remoteLockTargetListener);
				}
			}
		});
		
		return service;
	}
}