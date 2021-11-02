/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal.locks;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.config.IndexSettings;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.locks.DatastoreLockIndexEntry;
import com.b2international.snowowl.core.locks.DefaultOperationLockManager;
import com.b2international.snowowl.core.locks.IOperationLockManager;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.0
 */
@Component
public final class LockPlugin extends Plugin {

	private static final String LOCKS_INDEX = "locks";

	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isServer()) {
			final Index locksIndex = Indexes.createIndex(
				LOCKS_INDEX, 
				env.service(ObjectMapper.class), 
				new Mappings(DatastoreLockIndexEntry.class), 
				env.service(IndexSettings.class).forIndex(env.service(RepositoryConfiguration.class).getIndexConfiguration(), LOCKS_INDEX)
			);
			final DefaultOperationLockManager lockManager = new DefaultOperationLockManager(locksIndex);
			final RemoteLockTargetListener remoteLockTargetListener = new RemoteLockTargetListener();
			lockManager.addLockTargetListener(new Slf4jOperationLockTargetListener());
			lockManager.addLockTargetListener(remoteLockTargetListener);
			env.services().registerService(IOperationLockManager.class, lockManager);
			final RpcSession session = RpcUtil.getInitialServerSession(env.container());
			session.registerClassLoader(IOperationLockManager.class, DefaultOperationLockManager.class.getClassLoader());
		} else {
			env.services().registerService(IOperationLockManager.class, RpcUtil.createProxy(env.container(), IOperationLockManager.class));
		}
	}
	
}
