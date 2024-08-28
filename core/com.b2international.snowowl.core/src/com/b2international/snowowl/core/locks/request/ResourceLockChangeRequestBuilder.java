/*
 * Copyright 2023-2024 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.snowowl.core.context.TerminologyResourceContentRequestBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;

/**
 * @since 9.0.0
 */
public class ResourceLockChangeRequestBuilder 
	extends BaseRequestBuilder<ResourceLockChangeRequestBuilder, BranchContext, Boolean> 
	implements TerminologyResourceContentRequestBuilder<Boolean> {

	private final boolean lock;

	private String description;
	private String parentDescription = DatastoreLockContextDescriptions.ROOT;
	private String userId;
	private Long timeout = 3000L; // to keep backward compatibility in this API, 3 seconds of lock timeout is applied here

	/*package*/ ResourceLockChangeRequestBuilder(final boolean lock) {
		this.lock = lock;
	}

	public ResourceLockChangeRequestBuilder setDescription(final String description) {
		this.description = description;
		return getSelf();
	}

	public ResourceLockChangeRequestBuilder setParentDescription(final String parentDescription) {
		this.parentDescription = parentDescription;
		return getSelf();
	}

	public ResourceLockChangeRequestBuilder setUserId(final String userId) {
		this.userId = userId;
		return getSelf();
	}
	
	public ResourceLockChangeRequestBuilder setTimeout(final Long timeout) {
		this.timeout = timeout;
		return getSelf();
	}

	@Override
	protected Request<BranchContext, Boolean> doBuild() {
		return new ResourceLockChangeRequest(lock, description, parentDescription, timeout, userId);
	}
}
