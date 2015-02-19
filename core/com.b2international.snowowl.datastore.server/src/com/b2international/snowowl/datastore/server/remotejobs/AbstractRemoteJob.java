/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.remotejobs;

import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.google.common.base.Predicate;

/**
 */
public abstract class AbstractRemoteJob extends Job {

	private static IRemoteJobManager getRemoteJobManager() {
		return ApplicationContext.getServiceForClass(IRemoteJobManager.class);
	}
	
	public AbstractRemoteJob(final String name) {
		super(name);
	}
	
	@Override
	protected final IStatus run(IProgressMonitor monitor) {
		
		final UUID jobId = RemoteJobUtils.getRemoteJobId(this);
		final ListenableProgressMonitor listenableMonitor = new ListenableProgressMonitor();
		final RemoteJobManager remoteJobManager = (RemoteJobManager) getRemoteJobManager();
		
		listenableMonitor.addListener(monitor);
		listenableMonitor.addListener(new RemoteJobProgressMonitor(remoteJobManager, jobId));
		remoteJobManager.addMonitor(jobId, listenableMonitor);

		return runWithListenableMonitor(listenableMonitor);
	}

	protected abstract IStatus runWithListenableMonitor(final IProgressMonitor monitor);

	@Override
	@SuppressWarnings("unchecked")
	public boolean belongsTo(Object family) {
		if (family instanceof Predicate/*<Job>*/) {
			return ((Predicate<Job>) family).apply(this);
		} else {
			return super.belongsTo(family);
		}
	}
}