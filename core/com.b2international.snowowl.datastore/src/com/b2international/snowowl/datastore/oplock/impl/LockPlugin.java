/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.b2international.snowowl.datastore.oplock.DatastoreLockIndexEntry;
import com.b2international.snowowl.datastore.oplock.DatastoreOperationLockManager;
import com.b2international.snowowl.datastore.oplock.IOperationLockManager;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.0
 */
@Component
public final class LockPlugin extends Plugin {

	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isServer()) {
			final Index locksIndex = Indexes.createIndex("locks", env.service(ObjectMapper.class), new Mappings(DatastoreLockIndexEntry.class), env.service(IndexSettings.class));
			final DatastoreOperationLockManager lockManager = new DatastoreOperationLockManager(locksIndex);
			final RemoteLockTargetListener remoteLockTargetListener = new RemoteLockTargetListener();
			lockManager.addLockTargetListener(new Slf4jOperationLockTargetListener());
			lockManager.addLockTargetListener(remoteLockTargetListener);
			env.services().registerService(IOperationLockManager.class, lockManager);
			final RpcSession session = RpcUtil.getInitialServerSession(env.container());
			session.registerClassLoader(IOperationLockManager.class, DatastoreOperationLockManager.class.getClassLoader());
		} else {
			env.services().registerService(IOperationLockManager.class, RpcUtil.createProxy(env.container(), IOperationLockManager.class));
		}
	}
	
	
}
