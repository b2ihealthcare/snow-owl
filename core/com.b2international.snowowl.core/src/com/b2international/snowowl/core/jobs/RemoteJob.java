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

import java.util.Collections;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.ApiException;
import com.b2international.commons.status.Statuses;
import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;

/**
 * @since 5.7
 * @param <R>
 */
public final class RemoteJob extends Job {

	public static final QualifiedName REQUEST_STATUS = new QualifiedName(null, "requestStatus");
	private final String id;
	private final String key;
	private final String description;
	private final ServiceProvider context;
	private final Request<ServiceProvider, ?> request;
	
	private String response;
	private String user;
	private boolean autoClean;
	
	public RemoteJob(
			final String id,
			final String key, 
			final String description, 
			final String user, 
			final ServiceProvider context, 
			final Request<ServiceProvider, ?> request,
			final boolean autoClean) {
		super(key);
		this.id = id;
		this.key = key;
		this.description = description;
		this.user = user;
		this.context = context;
		this.request = request;
		this.autoClean = autoClean;
	}
	
	@Override
	protected final IStatus run(IProgressMonitor monitor) {
		final ObjectMapper mapper = this.context.service(ObjectMapper.class);
		final IProgressMonitor trackerMonitor = this.context.service(RemoteJobTracker.class).createMonitor(key, monitor);
		try {
			// seed the monitor instance into the current context, so the request can use it for progress reporting
			final ServiceProvider context = this.context.inject()
					.bind(IProgressMonitor.class, trackerMonitor)
					.bind(RemoteJob.class, this)
					.build();
			final Object response = request.execute(context);
			if (response != null) {
				final Class<? extends Object> responseType = response.getClass();
				if (Primitives.isWrapperType(responseType) || String.class.isAssignableFrom(responseType) || UUID.class.isAssignableFrom(responseType)) {
					this.response = toJson(mapper, ImmutableMap.of("value", response));
				} else {
					this.response = toJson(mapper, response);
				}
			}
			
			final IStatus status = (IStatus) getProperty(REQUEST_STATUS);
			return (status != null) ? status : Statuses.ok();
		} catch (OperationCanceledException e) {
			return Statuses.cancel();
		} catch (Throwable e) {
			final ApiError apiError;
			
			if (e instanceof ApiException) {
				apiError = ((ApiException) e).toApiError();
			} else {
				apiError = ApiError.Builder.of(e.getMessage())
					.status(500)
					.developerMessage("Exception caught while executing request in remote job.")
					.addInfo("exception-class", e.getClass().getSimpleName())
					.build();
			}
			
			this.response = toJson(mapper, apiError);
			// XXX: Don't delete remote jobs with errors
			autoClean = false;
			return Statuses.error(CoreActivator.PLUGIN_ID, "Failed to execute long running request", e);
		} finally {
			if (autoClean) {
				context.service(RemoteJobTracker.class).requestDeletes(Collections.singleton(id));
			}
		}
	}

	private String toJson(ObjectMapper mapper, Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean belongsTo(Object family) {
		if (family instanceof Predicate/*<Job>*/) {
			return ((Predicate<Job>) family).apply(this);
		} else {
			return super.belongsTo(family);
		}
	}
	
	String getId() {
		return id;
	}
	
	public String getKey() {
		return key;
	}
	
	String getDescription() {
		return description;
	}
	
	public String getUser() {
		return user;
	}
	
	String getResponse() {
		return response;
	}

	Request<ServiceProvider, ?> getRequest() {
		return request;
	}
	
}