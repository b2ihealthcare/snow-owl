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
package com.b2international.snowowl.datastore.remotejobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.commons.status.Statuses;
import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.DelegatingServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.google.common.base.Predicate;

/**
 * @since 5.7
 * @param <R>
 */
public final class RemoteJob extends Job {

	private final String id;
	private final ServiceProvider context;
	private final Request<ServiceProvider, ?> request;
	
	private Object response;
	private String user;

	public RemoteJob(
			final String id, 
			final String description, 
			final String user, 
			final ServiceProvider context, 
			final Request<ServiceProvider, ?> request) {
		super(description);
		this.id = id;
		this.user = user;
		this.context = context;
		this.request = request;
	}
	
	@Override
	protected final IStatus run(IProgressMonitor monitor) {
		final IProgressMonitor trackerMonitor = this.context.service(RemoteJobTracker.class).createMonitor(id, monitor);
		try {
			// seed the monitor instance into the current context, so the request can use it for progress reporting
			final DelegatingServiceProvider context = DelegatingServiceProvider
					.basedOn(this.context)
					.bind(IProgressMonitor.class, trackerMonitor)
					.bind(RemoteJob.class, this)
					.build();
			this.response = request.execute(context);
			return Statuses.ok();
		} catch (OperationCanceledException e) {
			return Statuses.cancel();
		} catch (Throwable e) {
			return Statuses.error(CoreActivator.PLUGIN_ID, "Failed to execute long running request", e);
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
	
	public String getId() {
		return id;
	}
	
	public String getDescription() {
		return getName();
	}
	
	public String getUser() {
		return user;
	}
	
	public Object getResponse() {
		return response;
	}
	
}