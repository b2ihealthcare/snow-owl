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

import javax.validation.constraints.NotNull;

import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.datastore.remotejobs.SingleRemoteJobFamily;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.7
 */
final class ScheduleJobRequest implements Request<ServiceProvider, String> {

	private static final long serialVersionUID = 1L;
	
	private static final ILock SCHEDULE_LOCK = Job.getJobManager().newLock();
	
	@JsonProperty
	@NotEmpty
	private final String id;
	
	@JsonProperty
	@NotEmpty
	private final String user;
	
	@JsonProperty
	@NotEmpty
	private final String description;
	
	@NotNull
	private final Request<ServiceProvider, ?> request;

	ScheduleJobRequest(String id, String user, Request<ServiceProvider, ?> request, String description) {
		this.id = id;
		this.user = user;
		this.request = request;
		this.description = description;
	}
	
	@Override
	public String execute(ServiceProvider context) {
		try {
			
			SCHEDULE_LOCK.acquire();
			
			Job[] remoteJobsWithId = Job.getJobManager().find(SingleRemoteJobFamily.create(id));
			if (remoteJobsWithId.length > 0) {
				throw new BadRequestException("Multiple remote jobs scheduled with identifier '%s'.", id);
			} else {
				RemoteJob job = new RemoteJob(id, description, user, context, request);
				job.setSystem(true);
				job.schedule();
				return id;
			}
			
		} finally {
			SCHEDULE_LOCK.release();
		}
	}

}
