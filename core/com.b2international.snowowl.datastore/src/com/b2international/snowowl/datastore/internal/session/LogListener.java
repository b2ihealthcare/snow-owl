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
package com.b2international.snowowl.datastore.internal.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.rpc.RpcSession;

public final class LogListener extends SessionEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogListener.class);
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.session.SessionEventListener#onLogin(com.b2international.snowowl.datastore.session.IApplicationSessionManager, 
	 * com.b2international.snowowl.rpc.RpcSession)
	 */
	@Override
	protected void onLogin(final IApplicationSessionManager manager, final RpcSession session) {
		LOGGER.info("RPC session login: " + session.get(IApplicationSessionManager.KEY_USER_ID));
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.session.SessionEventListener#onLogout(com.b2international.snowowl.datastore.session.IApplicationSessionManager, 
	 * com.b2international.snowowl.rpc.RpcSession)
	 */
	@Override
	protected void onLogout(final IApplicationSessionManager manager, final RpcSession session) {
		LOGGER.info("RPC session logout: " + session.get(IApplicationSessionManager.KEY_USER_ID));
	}
}