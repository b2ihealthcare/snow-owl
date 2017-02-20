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

import org.eclipse.core.runtime.jobs.Job;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobChanged;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobStore;
import com.b2international.snowowl.datastore.remotejobs.SingleRemoteJobFamily;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 5.7
 */
class CancelJobRequest extends BaseRequest<ServiceProvider, Void> {
	
	private static final long serialVersionUID = 1L;
	
	private final String id;

	CancelJobRequest(String id) {
		this.id = id;
	}

	@Override
	public Void execute(ServiceProvider context) {
		final RemoteJobStore store = context.service(RemoteJobStore.class);
		final RemoteJobEntry job = store.get(id);
		if (job != null) {
			if (job.isDone()) {
				store.delete(id);
			} else {
				job.cancel();
				Job.getJobManager().cancel(SingleRemoteJobFamily.create(id));
				store.put(id, job);
				new RemoteJobChanged(id).publish(context.service(IEventBus.class));
			}
		}
		return null;
	}

	@Override
	protected Class<Void> getReturnType() {
		return Void.class;
	}
	
}
