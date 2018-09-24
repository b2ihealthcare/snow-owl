/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.oplock.impl;

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.datastore.internal.session.ApplicationSessionManager;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 * @since 7.0
 */
@Component
public final class LockPlugin extends Plugin {

	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isServer() || env.isEmbedded()) {
			final DatastoreOperationLockManager lockManager = new DatastoreOperationLockManager();
			final RemoteLockTargetListener remoteLockTargetListener = new RemoteLockTargetListener();
			lockManager.addLockTargetListener(new Slf4jOperationLockTargetListener());
			lockManager.addLockTargetListener(remoteLockTargetListener);
			
			ApplicationContext.getInstance().addServiceListener(IApplicationSessionManager.class, (oldService, newService) -> {
				if (oldService != null) {
					((ApplicationSessionManager) oldService).removeListener(remoteLockTargetListener);
				}
				
				if (newService != null) {
					((ApplicationSessionManager) newService).addListener(remoteLockTargetListener);
				}
			});
			env.services().registerService(IDatastoreOperationLockManager.class, lockManager);
			final RpcSession session = RpcUtil.getInitialServerSession(env.container());
			session.registerClassLoader(IDatastoreOperationLockManager.class, DatastoreOperationLockManager.class.getClassLoader());
		}
		
		if (!env.isEmbedded()) {
			env.services().registerService(IDatastoreOperationLockManager.class, RpcUtil.createProxy(env.container(), IDatastoreOperationLockManager.class));
		}
	}
	
}
