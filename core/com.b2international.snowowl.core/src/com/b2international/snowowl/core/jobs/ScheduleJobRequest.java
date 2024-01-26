/*
 * Copyright 2017-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.jobs;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import jakarta.validation.constraints.NotEmpty;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.identity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 5.7
 */
final class ScheduleJobRequest implements Request<ServiceProvider, String> {

	private static final long serialVersionUID = 1L;
	
	private static final ILock SCHEDULE_LOCK = Job.getJobManager().newLock();
	
	@JsonProperty
	@NotEmpty
	private final String key;
	
	@JsonProperty
	private final String user;
	
	@JsonProperty
	@NotEmpty
	private final String description;
	
	@JsonProperty
	@NotNull
	private final Request<ServiceProvider, ?> request;
	
	private final boolean autoClean;
	
	private final boolean restart;
	
	private final boolean cached;

	private final SerializableSchedulingRule schedulingRule;

	ScheduleJobRequest(String key, String user, String description, Request<ServiceProvider, ?> request, SerializableSchedulingRule schedulingRule, boolean autoClean, boolean restart, boolean cached) {
		this.key = key;
		this.user = user;
		this.request = request;
		this.description = description;
		this.schedulingRule = schedulingRule;
		this.autoClean = autoClean;
		this.restart = restart;
		this.cached = cached;
	}
	
	@Override
	public String execute(ServiceProvider context) {
		
		if (cached && autoClean) {
			throw new BadRequestException("Automatically cleaned jobs cannot be cached.");
		}
		
		final String id = IDs.sha1(key);
		
		try {
			SCHEDULE_LOCK.acquire();
			
			final Optional<RemoteJobEntry> existingJob = JobRequests.prepareSearch()
					.one()
					.filterById(id)
					.build()
					.execute(context)
					.first();
			
			if (existingJob.isPresent()) {
				RemoteJobEntry job = existingJob.get();

				// if cached reuse
				if (cached) {
					// use an existing running job entry until it is not done or when restart not requested, which kinda forms an asynchronous cache for any request 
					if (!job.isDeleted() && (!job.isDone() || !restart)) {
						return id;
					}
				} else {
					// if running, fail
					if (!job.isDone()) {
						throw new AlreadyExistsException(String.format("Job[%s]", request.getType()), key);
					}
					
					// if restart not requested, fail
					if (!restart) {
						throw new ConflictException("An existing job is present with the same '%s' key. Request 'restart' if the previous job can be safely overriden.", key);
					}
				}
				
				// otherwise delete the existing job and create a new one using the same key
				if (!job.isDeleted()) {
					JobRequests.prepareDelete(id).build().execute(context);
				}
			}
			
			final String userId = !Strings.isNullOrEmpty(user) ? user : context.service(User.class).getUserId();
			
			RemoteJob job = new RemoteJob(id, key, description, userId, context, request, autoClean);
			job.setSystem(true);
			if (schedulingRule != null) {
				job.setRule(schedulingRule);
			}
			return context.service(RemoteJobTracker.class).schedule(job);
		} finally {
			SCHEDULE_LOCK.release();
		}
	}
	
}
