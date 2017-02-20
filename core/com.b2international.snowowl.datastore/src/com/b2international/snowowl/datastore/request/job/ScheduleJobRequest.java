/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.job;

import java.util.UUID;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobStore;

/**
 * @since 5.7
 */
final class ScheduleJobRequest<R> extends BaseRequest<ServiceProvider, String> {

	private static final long serialVersionUID = 1L;
	
	private final String userId;
	private final String description;
	private final Request<ServiceProvider, R> request;

	ScheduleJobRequest(String userId, Request<ServiceProvider, R> request, String description) {
		this.userId = userId;
		this.request = request;
		this.description = description;
	}
	
	@Override
	public String execute(ServiceProvider context) {
		final RemoteJobStore store = context.service(RemoteJobStore.class);
		final UUID uuid = UUID.randomUUID();
		final String id = uuid.toString();
		final RemoteJobEntry job = new RemoteJobEntry(uuid, description, userId, null);
		new RemoteJob<>(id, description, context, request).schedule();
		store.put(id, job);
		return id;
	}

	@Override
	protected Class<String> getReturnType() {
		return String.class;
	}

}
