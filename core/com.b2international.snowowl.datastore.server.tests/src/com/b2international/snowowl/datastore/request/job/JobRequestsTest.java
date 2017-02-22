/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.DelegatingServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobState;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobTracker;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 5.7
 */
public class JobRequestsTest {

	private static final String USER = "test@b2i.sg";
	private static final String RESULT = "result";
	private ServiceProvider context;

	@Before
	public void setup() {
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		final Index index = Indexes.createIndex("jobs", mapper, new Mappings(RemoteJobEntry.class));
		this.context = DelegatingServiceProvider
				.basedOn(ServiceProvider.EMPTY)
				.bind(RemoteJobTracker.class, new RemoteJobTracker(index))
				.build();
	}
	
	@Test
	public void scheduleAndWaitDone() throws Exception {
		final String jobId = schedule("scheduleAndWaitDone", context -> RESULT);
		final RemoteJobEntry entry = waitDone(jobId);
		assertEquals(RemoteJobState.FINISHED, entry.getState());
		assertEquals(RESULT, entry.getResultAs(String.class));
	}
	
	@Test
	public void scheduleAndCancel() throws Exception {
		final String jobId = schedule("scheduleAndCancel", context -> {
			// wait 50 ms, then throw cancelled if monitor is cancelled or return the result, so the main thread have time to actually initiate the cancel request
			final IProgressMonitor monitor = context.service(IProgressMonitor.class);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			return RESULT;
		});
		cancel(jobId);
		final RemoteJobEntry entry = waitDone(jobId);
		assertEquals(RemoteJobState.CANCELLED, entry.getState());
		assertNull(entry.getResult());
	}
	
	@Test(expected = NotFoundException.class)
	public void scheduleAndDelete() throws Exception {
		final String jobId = schedule("scheduleAndDelete", context -> {
			// wait 100 ms, then return the result, so the main thread have time to actually initiate the delete request
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			return RESULT;
		});
		delete(jobId);
		RemoteJobEntry job = get(jobId);
		System.err.println(job);
	}

	private RemoteJobEntry waitDone(final String jobId) {
		RemoteJobEntry entry = null;
		do {
			entry = get(jobId);
		} while (!entry.isDone());
		return entry;
	}

	private RemoteJobEntry get(final String jobId) {
		return JobRequests.prepareGet(jobId).build().execute(context);
	}
	
	private String schedule(String description, Request<ServiceProvider, ?> request) {
		return JobRequests.prepareSchedule()
				.setUser(USER)
				.setDescription(description)
				.setRequest(request)
				.build()
				.execute(context);
	}
	
	private void cancel(String jobId) {
		JobRequests.prepareCancel(jobId).build().execute(context);
	}
	
	private void delete(String jobId) {
		JobRequests.prepareDelete(jobId).build().execute(context);
	}
	
}
