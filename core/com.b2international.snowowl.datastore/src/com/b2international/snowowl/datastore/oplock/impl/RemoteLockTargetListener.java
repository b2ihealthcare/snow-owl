/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.datastore.oplock.IOperationLockTargetListener;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcThreadLocal;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * 
 */
public class RemoteLockTargetListener implements IOperationLockTargetListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteLockTargetListener.class);

	private final Map<RpcSession, Multimap<DatastoreLockTarget, DatastoreLockContext>> remotelyLockedContexts = Maps.newHashMap();

	@Override
	public void targetAcquired(final DatastoreLockTarget target, final DatastoreLockContext context) {

		final RpcSession session = RpcThreadLocal.getSessionUnchecked();

		if (null == session) {
			return;
		}

		final Multimap<DatastoreLockTarget, DatastoreLockContext> targetsForSession;

		if (remotelyLockedContexts.containsKey(session)) {
			targetsForSession = remotelyLockedContexts.get(session);
		} else {
			targetsForSession = HashMultimap.create();
			remotelyLockedContexts.put(session, targetsForSession);
		}

		targetsForSession.put(target, context);
	}

	@Override
	public void targetReleased(final DatastoreLockTarget target, final DatastoreLockContext context) {

		final RpcSession session = RpcThreadLocal.getSessionUnchecked();

		if (null == session) {
			return;
		}

		if (!remotelyLockedContexts.containsKey(session)) {
			return;
		}

		final Multimap<DatastoreLockTarget, DatastoreLockContext> targetsForSession = remotelyLockedContexts.get(session);
		targetsForSession.remove(target, context);
	}

//	@Override
//	protected void onLogout(final IApplicationSessionManager manager, final RpcSession session) {
//
//		if (null == session) {
//			return;
//		}
//
//		if (!remotelyLockedContexts.containsKey(session)) {
//			return;
//		}
//
//		final Multimap<DatastoreLockTarget, DatastoreLockContext> targetsForSession = remotelyLockedContexts.remove(session);
//
//		if (targetsForSession.isEmpty()) {
//			return;
//		}
//
//		final String disconnectedUserId = (String) session.get(IApplicationSessionManager.KEY_USER_ID);
//		if (null == disconnectedUserId) {
//			return;
//		}
//
//		LOGGER.warn("Disconnected client had locks granted, unlocking.");
//
//		final IOperationLockManager lockManager = ApplicationContext.getInstance().getServiceChecked(IOperationLockManager.class);
//
//		for (final Entry<DatastoreLockTarget, DatastoreLockContext> targetContextPair : targetsForSession.entries()) {
//			try {
//				lockManager.unlock(targetContextPair.getValue(), targetContextPair.getKey());
//			} catch (final OperationLockException e) {
//				LOGGER.error("Failed to unlock targets left after closed session.", e);
//			}
//		}
//	}
}