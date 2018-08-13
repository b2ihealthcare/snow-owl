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

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.rpc.RpcSession;

public class SessionEventListener implements IListener {

	@Override
	public void notifyEvent(final IEvent event) {

		if (!(event instanceof SessionEvent)) {
			notifyOtherEvent(event);
		}

		final SessionEvent sessionEvent = (SessionEvent) event;

		if (sessionEvent instanceof LoginEvent) {
			onLogin(sessionEvent.getSource(), sessionEvent.getSession());
		} else if (event instanceof LogoutEvent) {
			onLogout(sessionEvent.getSource(), sessionEvent.getSession());
		} else {
			notifyOtherEvent(event);
		}
	}

	protected void notifyOtherEvent(final IEvent event) {
	}

	protected void onLogin(final IApplicationSessionManager manager, final RpcSession session) {
	}

	protected void onLogout(final IApplicationSessionManager manager, final RpcSession session) {
	}
}