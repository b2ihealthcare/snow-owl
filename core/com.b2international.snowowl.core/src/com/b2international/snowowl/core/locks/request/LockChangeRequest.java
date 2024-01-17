/*
 * Copyright 2022-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.locks.request;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.LockedException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.RequestHeaders;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;
import com.b2international.snowowl.core.internal.locks.RemoteLockTargetListener;
import com.b2international.snowowl.core.locks.IOperationLockManager;
import com.b2international.snowowl.core.locks.Lockable;
import com.b2international.snowowl.eventbus.netty.EventBusNettyUtil;

/**
 * @since 8.1.0
 */
final class LockChangeRequest implements Request<ServiceProvider, Boolean> {

	private static final long serialVersionUID = 414737555546463276L;

	private static final long LOCK_TIMEOUT_MILLIS = 3000L;

	private final boolean lock;

	@NotEmpty
	private final String description;

	@NotEmpty
	private final String parentDescription;

	@NotEmpty
	private final List<Lockable> targets;

	// Nullable
	private String userId;

	/*package*/ LockChangeRequest(
		final boolean lock, 
		final String description, 
		final String parentDescription, 
		final String userId, 
		final List<Lockable> targets) {
		
		this.lock = lock;
		this.description = description;
		this.parentDescription = parentDescription;
		this.userId = userId;
		this.targets = targets;
	}

	@Override
	public Boolean execute(final ServiceProvider context) throws LockedException, IllegalArgumentException {
		final String userId = StringUtils.isEmpty(this.userId) 
			? context.service(User.class).getUserId() 
			: this.userId;
			
		final IOperationLockManager lockManager = context.service(IOperationLockManager.class);
		final DatastoreLockContext lockContext = new DatastoreLockContext(userId, description, parentDescription);

		if (lock) {
			lockManager.lock(lockContext, LOCK_TIMEOUT_MILLIS, targets);
		} else {
			lockManager.unlock(lockContext, targets);
		}
		
		final RequestHeaders headers = context.service(RequestHeaders.class);
		final String clientId = headers.header(EventBusNettyUtil.HEADER_CLIENT_ID);
		if (!StringUtils.isEmpty(clientId)) {
			final RemoteLockTargetListener listener = context.service(RemoteLockTargetListener.class);
			
			if (lock) {
				listener.targetAcquired(clientId, targets, lockContext);
			} else {
				listener.targetRemoved(clientId, targets, lockContext);
			}
		}
		
		return Boolean.TRUE;
	}
}
