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
package com.b2international.snowowl.datastore.server.editor.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcThreadLocal;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 2.9
 */
public class Net4jLifecycleChangeHandler implements ILifecycleChangeHandler {

	private class SessionLifecycleEventAdapter extends LifecycleEventAdapter {

		private final EditorSessionLifecycleManager lifecycleManager;

		public SessionLifecycleEventAdapter(EditorSessionLifecycleManager lifecycleManager) {
			this.lifecycleManager = lifecycleManager;
		}

		@Override
		protected void onDeactivated(ILifecycle lifecycle) {
			Collection<UUID> sessionIds = clientToSessionIdMap.get((IChannelMultiplexer) lifecycle);
			ArrayList<UUID> sessionIdsCopy = Lists.newArrayList(sessionIds);
			for (UUID uuid : sessionIdsCopy) {
				try {
					lifecycleManager.closeSession(uuid);
				} catch (Exception e) {
					// ignore, try to close other sessions
				}
			}
			EventUtil.removeListener(lifecycle, lifecycleEventAdapter);
		}
	}

	private final Multimap<IChannelMultiplexer, UUID> clientToSessionIdMap = Multimaps.synchronizedSetMultimap(HashMultimap.<IChannelMultiplexer, UUID> create());
	private final  LifecycleEventAdapter lifecycleEventAdapter;

	public Net4jLifecycleChangeHandler(EditorSessionLifecycleManager lifecycleManager) {
		lifecycleEventAdapter = new SessionLifecycleEventAdapter(lifecycleManager);
	}

	@Override
	public void handleOpen(IEditorSession session) {
		RpcSession rpcSession = RpcThreadLocal.getSession();
		IChannelMultiplexer channelMultiplexer = rpcSession.getProtocol().getChannel().getMultiplexer();
		EventUtil.addUniqueListener(channelMultiplexer, lifecycleEventAdapter);
		clientToSessionIdMap.put(channelMultiplexer, session.getUuid());
	}

	@Override
	public void handleClose(UUID sessionId) {
		clientToSessionIdMap.values().remove(sessionId);
	}

	@Override
	public void dispose() {
		for (IChannelMultiplexer channelMultiplexer : clientToSessionIdMap.keySet()) {
			EventUtil.removeListener(channelMultiplexer, lifecycleEventAdapter);
		}
		clientToSessionIdMap.clear();
	}

}