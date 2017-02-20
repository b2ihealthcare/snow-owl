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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobStore;

/**
 * @since 5.7
 */
final class DeleteJobRequest extends BaseRequest<ServiceProvider, Void> {

	private final String id;

	DeleteJobRequest(String id) {
		this.id = id;
	}
	
	@Override
	public Void execute(ServiceProvider context) {
		context.service(RemoteJobStore.class).delete(id);
		return null;
	}

	@Override
	protected Class<Void> getReturnType() {
		return Void.class;
	}

}
