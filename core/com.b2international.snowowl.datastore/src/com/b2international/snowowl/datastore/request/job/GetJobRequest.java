/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

/**
 * @since 5.7
 */
final class GetJobRequest implements Request<ServiceProvider, RemoteJobEntry> {

	@JsonProperty
	private final String id;

	GetJobRequest(String id) {
		this.id = id;
	}
	
	@Override
	public RemoteJobEntry execute(ServiceProvider context) {
		final RemoteJobEntry entry = Iterables.getOnlyElement(context.service(RemoteJobTracker.class).search(RemoteJobEntry.Expressions.id(id), 2), null);
		if (entry == null) {
			throw new NotFoundException("job", id);
		} else {
			return entry;
		}
	}
	
}
