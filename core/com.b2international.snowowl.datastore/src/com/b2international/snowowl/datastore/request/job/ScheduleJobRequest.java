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

import java.util.Date;
import java.util.UUID;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobState;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobStore;

/**
 * @since 5.7
 */
final class ScheduleJobRequest extends BaseRequest<ServiceProvider, String> {

	private static final long serialVersionUID = 1L;
	
	private final String user;
	private final String description;
	private final Request<ServiceProvider, ?> request;

	ScheduleJobRequest(String user, Request<ServiceProvider, ?> request, String description) {
		this.user = user;
		this.request = request;
		this.description = description;
	}
	
	@Override
	public String execute(ServiceProvider context) {
		final RemoteJobStore store = context.service(RemoteJobStore.class);
		final String id = UUID.randomUUID().toString();
		final RemoteJob job = new RemoteJob(id, description, context, request);
		job.setSystem(true);
		// TODO add progress tracking and other useful capabilities
		job.addJobChangeListener(new RemoteJobTracker(store, id, description, user));
		job.schedule();
		return id;
	}

	@Override
	protected Class<String> getReturnType() {
		return String.class;
	}
	
	private static class RemoteJobTracker extends JobChangeAdapter {
		
		private final RemoteJobStore store;
		private String id;
		private String description;
		private String user;

		public RemoteJobTracker(RemoteJobStore store, String id, String description, String user) {
			this.store = store;
			this.id = id;
			this.description = description;
			this.user = user;
		}
		
		@Override
		public void scheduled(IJobChangeEvent event) {
			System.err.println("scheduled " + id);
			store.put(id, RemoteJobEntry.builder()
					.id(id)
					.description(description)
					.user(user)
					.scheduleDate(new Date())
					.build());
		}
		
		@Override
		public void running(IJobChangeEvent event) {
			System.err.println("running " + id);
			final Date startDate = new Date();
			store.update(id, current -> {
				return RemoteJobEntry.from(current)
						.state(RemoteJobState.RUNNING)
						.startDate(startDate)
						.build();
			});
		}
		
		@Override
		public void done(IJobChangeEvent event) {
			System.err.println("done " + id);
			final RemoteJob job = (RemoteJob) event.getJob();
			final IStatus result = job.getResult();
			final Object response = job.getResponse();
			final Date finishDate = new Date();
			final RemoteJobState newState = result.isOK() ? RemoteJobState.FINISHED : result.matches(IStatus.CANCEL) ? RemoteJobState.CANCELLED : RemoteJobState.FAILED;
			store.update(id, current -> {
				return RemoteJobEntry.from(current)
							.result(response)
							.finishDate(finishDate)
							.state(newState)
							.build();
			});
		}
	}

}
