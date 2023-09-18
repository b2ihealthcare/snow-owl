/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.locks.IOperationLockManager;
import com.b2international.snowowl.core.locks.Lockable;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.events.ClientConnectionNotification;
import com.b2international.snowowl.eventbus.events.SystemNotification;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class RemoteLockTargetListener implements IHandler<IMessage>, IDisposableService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteLockTargetListener.class);

	private final Map<String, Multimap<Lockable, DatastoreLockContext>> remotelyLockedContexts = Maps.newHashMap();

	private final AtomicBoolean active = new AtomicBoolean(false);

	private IEventBus bus;
	
	public void register(final IEventBus bus) {
		if (!active.compareAndExchange(false, true)) {
			this.bus = bus;
			bus.registerHandler(SystemNotification.ADDRESS, this);
		}
	}
	
	public void targetAcquired(final String clientId, final Iterable<Lockable> targets, final DatastoreLockContext context) {
		final Multimap<Lockable, DatastoreLockContext> targetsForClient = remotelyLockedContexts.computeIfAbsent(clientId, key -> HashMultimap.create());
		targets.forEach(target -> targetsForClient.put(target, context));
	}

	public void targetRemoved(final String clientId, final Iterable<Lockable> targets, final DatastoreLockContext context) {
		final Multimap<Lockable, DatastoreLockContext> targetsForClient = remotelyLockedContexts.computeIfAbsent(clientId, key -> HashMultimap.create());
		targets.forEach(target -> targetsForClient.remove(target, context));
	}
	
	private void clientLogout(final String clientId) {
		final Multimap<Lockable, DatastoreLockContext> targetsForClient = remotelyLockedContexts.remove(clientId);
		if (targetsForClient == null || targetsForClient.isEmpty()) {
			return;
		}

		LOGGER.warn("Disconnected client had locks granted, unlocking.");

		final IOperationLockManager lockManager = ApplicationContext.getInstance().getServiceChecked(IOperationLockManager.class);
		for (final Map.Entry<Lockable, DatastoreLockContext> targetContextPair : targetsForClient.entries()) {
			try {
				lockManager.unlock(targetContextPair.getValue(), targetContextPair.getKey());
			} catch (final IllegalArgumentException e) {
				LOGGER.error("Failed to unlock targets left after closed session.", e);
			}
		}
	}
	
	@Override
	public void handle(final IMessage message) {
		final Object body = message.body();
		if (!(body instanceof ClientConnectionNotification)) {
			return;
		}
		
		final ClientConnectionNotification notification = (ClientConnectionNotification) body;
		if (!notification.isJoining()) {
			clientLogout(notification.getClientId());
		}
	}

	@Override
	public void dispose() {
		if (active.compareAndExchange(true, false)) {
			this.bus.unregisterHandler(SystemNotification.ADDRESS, this);
			this.bus = null;
		}
	}

	@Override
	public boolean isDisposed() {
		return active.get();
	}
}
