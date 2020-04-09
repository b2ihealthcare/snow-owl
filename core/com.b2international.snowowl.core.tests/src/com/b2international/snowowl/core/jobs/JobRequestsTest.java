/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.SystemNotification;
import com.b2international.snowowl.core.identity.IdentityProvider;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 5.7
 */
public class JobRequestsTest {

	private static final String USER = "test@b2i.sg";
	private static final String RESULT = "result";
	private ServiceProvider context;
	private RemoteJobTracker tracker;
	private IEventBus bus;
	private ObjectMapper mapper;
	
	private final BlockingQueue<RemoteJobNotification> notifications = new ArrayBlockingQueue<>(100);

	@Before
	public void setup() {
		mapper = JsonSupport.getDefaultObjectMapper();
		final Index index = Indexes.createIndex("jobs", mapper, new Mappings(RemoteJobEntry.class));
		this.bus = EventBusUtil.getBus();
		this.tracker = new RemoteJobTracker(index, bus, mapper, 200);
		this.context = ServiceProvider.EMPTY.inject()
				.bind(ObjectMapper.class, mapper)
				.bind(RemoteJobTracker.class, tracker)
				.bind(IdentityProvider.class, IdentityProvider.NOOP)
				.build();
		this.bus.registerHandler(SystemNotification.ADDRESS, message -> {
			try {
				notifications.offer(message.body(RemoteJobNotification.class), 1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				throw new RuntimeException();
			}
		});
	}
	
	@After
	public void after() {
		this.tracker.dispose();
	}
	
	@Test
	public void scheduleAndWaitDone() throws Exception {
		final String jobId = schedule("scheduleAndWaitDone", context -> RESULT);
		final RemoteJobEntry entry = waitDone(jobId);
		assertEquals(RemoteJobState.FINISHED, entry.getState());
		assertEquals(RESULT, entry.getResult(mapper).get("value"));
		// verify job events
		// 1 added
		// 1 changed - RUNNING
		// 1 changed - FINISHED
		verifyJobEvents(jobId, 1, 2, 0);
	}
	
	@Test
	public void scheduleAndCancel() throws Exception {
		CyclicBarrier barrier = new CyclicBarrier(2);
		final String jobId = schedule("scheduleAndCancel", context -> {
			// wait 1000 ms, then throw cancelled if monitor is cancelled or return the result, so the main thread have time to actually initiate the cancel request
			final IProgressMonitor monitor = context.service(IProgressMonitor.class);
			
			try {
				Thread.sleep(50);
				barrier.await();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			return RESULT;
		});
		cancel(jobId);
		barrier.await();
		
		final RemoteJobEntry entry = waitDone(jobId);
		assertEquals(RemoteJobState.CANCELED, entry.getState());
		assertNull(entry.getResult());
		// verify job events
		// 1 added
		// 1 changed - RUNNING
		// 1 changed - CANCEL_REQUESTED
		// 1 changed - CANCELLED
		verifyJobEvents(jobId, 1, 3, 0);
	}
	
	@Test
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
		// delete should immediately mark the object deleted, but wait until the job actually completes then delete the entry
		delete(jobId);
		// get throws NotFoundException
		try {
			get(jobId);
			fail("Expected " + NotFoundException.class.getName() + " to be thrown after deleting job");
		} catch (NotFoundException e) {
			// expected exception
		}
		// assert that the tracker will eventually delete the job entry
		RemoteJobEntry entry = null;
		int numberOfTries = 10; 
		do {
			entry = tracker.get(jobId);
			Thread.sleep(50);
		} while (entry != null && --numberOfTries > 0);
		assertNull("Entry couldn't be removed by tracker after marked for deletion", entry);
		// verify job events
		// 1 added
		// 1 changed - RUNNING
		// 1 changed - FINISHED
		// 1 removed
		verifyJobEvents(jobId, 1, 2, 1);
	}
	
	@Test
	public void scheduleAndMonitor() throws Exception {
		final String jobId = schedule("scheduleAndMonitor", context -> {
			final IProgressMonitor monitor = context.service(IProgressMonitor.class);
			final int totalWork = 10;
			monitor.beginTask("Reticulating splines...", totalWork);
			final RemoteJob job = context.service(RemoteJob.class);
			for (int i = 0; i < totalWork; i++) {
				RemoteJobEntry inProgress = get(job.getId());
				assertEquals(i * totalWork, inProgress.getCompletionLevel());
				monitor.worked(1);
			}
			return RESULT;
		});
		
		final RemoteJobEntry job = waitDone(jobId);
		assertEquals(100, job.getCompletionLevel());
		// verify job events
		// 1 added
		// 1 changed - RUNNING
		// 10 changed - PROGRESS updates
		// 1 changed - FINISHED
		verifyJobEvents(jobId, 1, 12, 0);
	}
	
	@Test(expected = NotFoundException.class)
	public void scheduleAndClean() throws Exception {
		final String jobId = schedule("scheduleAndClean", true, context -> RESULT);
		waitDone(jobId);
	}

	private void verifyJobEvents(String jobId, int expectedAdded, int expectedChanged, int expectedRemoved) {
		int numberOfNotificationsToExpect = expectedAdded + expectedChanged + expectedRemoved;
		
		for (int i = 0; i < numberOfNotificationsToExpect; i++) {
			try {
				RemoteJobNotification notification = notifications.poll(5, TimeUnit.SECONDS);
				if (notification == null) {
					// did not receive a notification in time, fail the test
					fail("No notification has arrived but still expecting '" + (numberOfNotificationsToExpect - i) + "' notifications.");
				}
				if (RemoteJobNotification.isAdded(notification)) {
					expectedAdded--;
				} else if (RemoteJobNotification.isChanged(notification)) {
					expectedChanged--;
				} else if (RemoteJobNotification.isRemoved(notification)) {
					expectedRemoved--;;
				}
			} catch (InterruptedException e) {
				throw new RuntimeException();
			}
		}

		assertEquals(String.format("Expecting '%s' ADDED notifications to arrive", expectedAdded), 0, expectedAdded);
		assertEquals(String.format("Expecting '%s' CHANGED notifications to arrive", expectedChanged), 0, expectedChanged);
		assertEquals(String.format("Expecting '%s' REMOVED notifications to arrive", expectedRemoved), 0, expectedRemoved);
	}

	private RemoteJobEntry waitDone(final String jobId) {
		RemoteJobEntry entry = null;
		do {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			entry = get(jobId);
		} while (!entry.isDone());
		return entry;
	}

	private RemoteJobEntry get(final String jobId) {
		return JobRequests.prepareGet(jobId).build().execute(context);
	}
	
	private String schedule(String description, Request<ServiceProvider, ?> request) {
		return schedule(description, false, request);
	}
	
	private String schedule(String description, boolean autoClean, Request<ServiceProvider, ?> request) {
		return JobRequests.prepareSchedule()
				.setUser(USER)
				.setDescription(description)
				.setRequest(request)
				.setAutoClean(autoClean)
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
