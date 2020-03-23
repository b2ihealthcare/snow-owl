/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.UUID;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * A request builder that wraps existing {@link Request} instances to run them as jobs.
 * 
 * @since 5.7
 */
public final class ScheduleJobRequestBuilder extends BaseRequestBuilder<ScheduleJobRequestBuilder, ServiceProvider, String> implements SystemRequestBuilder<String> {

	private String key = UUID.randomUUID().toString();
	private String user;
	private String description;
	private Request<ServiceProvider, ?> request;
	private SerializableSchedulingRule schedulingRule;
	private boolean autoClean = false;
	private boolean restart = false;
	
	ScheduleJobRequestBuilder() {
	}

	/**
	 * Set a custom unique identifier - a key - to identity this job. The final identifier of the job will be computed from this value.
	 * 
	 * @param key
	 *            - the unique identifier the job will be assigned to
	 * @return this builder
	 */
	public ScheduleJobRequestBuilder setKey(final String key) {
		this.key = key;
		return getSelf();
	}

	/**
	 * Set the initiator/author of the job request.
	 * @param user - the user the job will be assigned to
	 * @return this builder
	 */
	public ScheduleJobRequestBuilder setUser(final String user) {
		this.user = user;
		return getSelf();
	}
	
	/**
	 * Set a human readable description about the {@link Request} that will be executed by the job.
	 * @param description - the human readable description of the job
	 * @return this builder
	 */
	public ScheduleJobRequestBuilder setDescription(final String description) {
		this.description = description;
		return getSelf();
	}
	
	/**
	 * Extracts the {@link Request} from the specified {@link AsyncRequest} that will be 
	 * {@link Request#execute(ServiceProvider) executed} by the job. 
	 * @param request - the request to execute
	 * @return this builder
	 */
	public ScheduleJobRequestBuilder setRequest(final AsyncRequest<?> request) {
		this.request = request.getRequest();
		return getSelf();
	}
	
	/**
	 * Set the {@link Request} that will be {@link Request#execute(ServiceProvider) executed} by the job. 
	 * @param request - the request to execute
	 * @return this builder
	 */
	public ScheduleJobRequestBuilder setRequest(final Request<ServiceProvider, ?> request) {
		this.request = request;
		return getSelf();
	}
	
	/**
	 * Set to automatically remove the job from index after completion if it was successful.
	 * @param autoClean - if the job should be deleted from index after completion
	 * @return this builder
	 */
	public ScheduleJobRequestBuilder setAutoClean(final boolean autoClean) {
		this.autoClean = autoClean;
		return getSelf();
	}
	
	/**
	 * Restarts (re-schedules) the job if present with the same key.
	 * 
	 * @param restart - to restart the job if present with the same key, and it is not running.
	 * @return
	 */
	public ScheduleJobRequestBuilder setRestart(final boolean restart) {
		this.restart = restart;
		return getSelf();
	}
	
	/**
	 * Sets the scheduling rule for the remote job, controlling which instances can be executed side-by-side.
	 * @param schedulingRule - the scheduling rule to apply for this job
	 * @return this builder
	 */
	public ScheduleJobRequestBuilder setSchedulingRule(final SerializableSchedulingRule schedulingRule) {
		this.schedulingRule = schedulingRule;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, String> doBuild() {
		return new ScheduleJobRequest(key, user, description, request, schedulingRule, autoClean, restart);
	}
	
}
