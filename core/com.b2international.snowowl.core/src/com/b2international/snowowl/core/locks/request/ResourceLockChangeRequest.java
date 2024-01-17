/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.commons.exceptions.LockedException;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.locks.Lockable;

/**
 * @since 9.0.0
 */
final class ResourceLockChangeRequest implements Request<BranchContext, Boolean> {

	private static final long serialVersionUID = 1L;

	private final boolean lock;

	@NotEmpty
	private final String description;

	@NotEmpty
	private final String parentDescription;

	// Nullable
	private String userId;

	/*package*/ ResourceLockChangeRequest(
		final boolean lock, 
		final String description, 
		final String parentDescription, 
		final String userId
	) {
		this.lock = lock;
		this.description = description;
		this.parentDescription = parentDescription;
		this.userId = userId;
	}

	@Override
	public Boolean execute(final BranchContext context) throws LockedException, IllegalArgumentException {
		final String repositoryId = context.service(Repository.class).id();
		final List<Lockable> targets = List.of(new Lockable(repositoryId, context.path()));
		final LockChangeRequest delegateLockRequest = new LockChangeRequest(lock, description, parentDescription, userId, targets);
		return delegateLockRequest.execute(context);
	}
}
