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
package com.b2international.snowowl.datastore.remotejobs;

import static com.b2international.snowowl.datastore.remotejobs.RemoteJobUtils.getJobSpecificAddress;

import java.util.UUID;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 */
public abstract class RemoteJobEventBusHandler implements IHandler<IMessage> {

	private final UUID jobId;

	public RemoteJobEventBusHandler(final UUID jobId) {
		this.jobId = jobId;
	}

	@Override 
	public void handle(final IMessage message) {
		final RemoteJobState finishState = message.body(RemoteJobState.class);
		unregisterHandler();
		handleResult(jobId, isCancelRequested(finishState));
	}

	private boolean isCancelRequested(final RemoteJobState finishState) {
		return RemoteJobState.CANCEL_REQUESTED.equals(finishState);
	}

	protected abstract void handleResult(final UUID jobId, final boolean cancelRequested);
	
	private IEventBus unregisterHandler() {
		return getEventBus().unregisterHandler(getJobSpecificAddress(IRemoteJobManager.ADDRESS_REMOTE_JOB_COMPLETED, jobId), this);
	}

	private IEventBus getEventBus() {
		return getApplicationContext().getService(IEventBus.class);
	}

	private ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
}