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

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils;
import com.google.common.base.Preconditions;

/**
 * An implementation of {@link IJobChangeListener} that forwards events related to remote jobs to the configured remote job manager instance.
 * 
 */
public class RemoteJobChangeListener extends JobChangeAdapter {

	private final RemoteJobManager remoteJobManager;

	public RemoteJobChangeListener(RemoteJobManager remoteJobManager) {
		Preconditions.checkNotNull(remoteJobManager, "Remote job management service reference may not be null.");
		this.remoteJobManager = remoteJobManager;
	}

	@Override
	public void scheduled(IJobChangeEvent event) {
		if (RemoteJobUtils.isJobRemote(event.getJob())) {
			UUID id = RemoteJobUtils.getRemoteJobId(event.getJob());
			String description = RemoteJobUtils.getRemoteJobDescription(event.getJob());
			String requestingUserId = RemoteJobUtils.getRequestingUserId(event.getJob());
			String customCommandId = RemoteJobUtils.getCustomCommandId(event.getJob());
			remoteJobManager.add(id, description, requestingUserId, customCommandId);
		}
	}

	@Override
	public void running(IJobChangeEvent event) {
		if (RemoteJobUtils.isJobRemote(event.getJob())) {
			UUID id = RemoteJobUtils.getRemoteJobId(event.getJob());
			remoteJobManager.running(id);
		}
	}

	@Override
	public void done(IJobChangeEvent event) {
		if (RemoteJobUtils.isJobRemote(event.getJob())) {
			UUID id = RemoteJobUtils.getRemoteJobId(event.getJob());
			remoteJobManager.finished(id, event.getResult());
		}
	}
}