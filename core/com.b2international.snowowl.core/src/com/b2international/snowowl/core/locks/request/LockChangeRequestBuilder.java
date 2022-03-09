/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.internal.locks.DatastoreLockTarget;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 8.1.0
 */
public class LockChangeRequestBuilder 
	extends BaseRequestBuilder<LockChangeRequestBuilder, ServiceProvider, Boolean> 
	implements SystemRequestBuilder<Boolean> {

	private final boolean lock;

	private String description;
	private String parentDescription = DatastoreLockContextDescriptions.ROOT;
	private String userId;
	private List<DatastoreLockTarget> targets = List.of();


	/*package*/ LockChangeRequestBuilder(final boolean lock) {
		this.lock = lock;
	}

	public LockChangeRequestBuilder setDescription(final String description) {
		this.description = description;
		return getSelf();
	}

	public LockChangeRequestBuilder setParentDescription(final String parentDescription) {
		this.parentDescription = parentDescription;
		return getSelf();
	}

	public LockChangeRequestBuilder setUserId(final String userId) {
		this.userId = userId;
		return getSelf();
	}
	
	public LockChangeRequestBuilder setTargets(final DatastoreLockTarget... targets) {
		this.targets = Collections3.toImmutableList(targets);
		return getSelf();
	}

	@Override
	protected Request<ServiceProvider, Boolean> doBuild() {
		return new LockChangeRequest(lock, description, parentDescription, userId, targets);
	}
}
