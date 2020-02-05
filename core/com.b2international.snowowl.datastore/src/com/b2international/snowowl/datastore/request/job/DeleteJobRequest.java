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

import java.util.Collection;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.7
 */
final class DeleteJobRequest implements Request<ServiceProvider, Boolean> {

	@JsonProperty
	private final Collection<String> jobIds;

	DeleteJobRequest(Collection<String> jobIds) {
		this.jobIds = jobIds;
	}
	
	@Override
	public Boolean execute(ServiceProvider context) {
		context.service(RemoteJobTracker.class).requestDeletes(jobIds);
		return Boolean.TRUE;
	}
	
}
