/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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

import static org.junit.Assert.*;

import java.util.concurrent.*;
import java.util.function.Predicate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.IdentityProvider;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.events.SystemNotification;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 5.7
 */
public class JobRequestsTest {

	private static final int DEFAULT_STALE_JOB_AGE = 200;
	private static final int DEFAULT_PURGE_THRESHOLD = 2;
	
	private static final String USER = "test@b2i.sg";
	private static final String RESULT = "result";
	private ServiceProvider context;
	private RemoteJobTracker tracker;
	private IEventBus bus;
	private ObjectMapper mapper;
	
	private final BlockingQueue<RemoteJobNotification> notifications = new ArrayBlockingQueue<>(100);
	private Index index;

	@Before
	public void setup() {
		
		this.mapper = JsonSupport.getDefaultObjectMapper();
		this.bus = EventBusUtil.getBus();
		
		index = Indexes.createIndex("jobs", mapper, new Mappings(RemoteJobEntry.class));

		this.tracker = new RemoteJobTracker(index, bus, mapper, DEFAULT_PURGE_THRESHOLD, DEFAULT_STALE_JOB_AGE);
		this.context = ServiceProvider.EMPTY.inject()
				.bind(ObjectMapper.class, mapper)
				.bind(RemoteJobTracker.class, tracker)
				.bind(IdentityProvider.class, IdentityProvider.UNPROTECTED)
				.bind(User.class, User.SYSTEM)
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
		this.index.admin().delete();
		this.tracker.dispose();
		if (context instanceof IDisposableService) {
			((IDisposableService) context).dispose();
		}
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
		// job counter is 0 at startup 
		assertEquals(0, tracker.getJobCounter());
		
		CyclicBarrier barrier = new CyclicBarrier(2);
		
		final String deletedJobId = schedule("scheduleAndDelete", context -> {
			// wait until barrier is ready (main thread initiated delete request), return the result
			try {
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				throw new RuntimeException(e);
			}
			return RESULT;
		});
		
		// delete should immediately mark the object deleted, but wait until the job actually completes then delete the entry
		delete(deletedJobId);
		
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			throw new RuntimeException(e);
		}
		
		// regular get must throw NotFoundException
		try {
			get(deletedJobId);
			fail("Expected " + NotFoundException.class.getName() + " to be thrown after deleting job");
		} catch (NotFoundException e) {
			// expected exception
		}
		
		// the job document should still exist, being marked as deletable
		RemoteJobEntry entry = tracker.get(deletedJobId);
		assertNotNull(entry);
		assertTrue(entry.isDeleted());
		
		// verify job events
		// 1 added
		// 1 changed - RUNNING
		// 1 changed - FINISHED
		// 1 removed
		verifyJobEvents(deletedJobId, 1, 2, 1);
		
		// ensure that the job is finished
		waitDoneByTracker(deletedJobId);
		
		// assert counter is increased properly
		assertEquals(1, tracker.getJobCounter());
		
		// schedule another job to trigger the purge (purge threshold is 2) 
		final String anotherJobId = schedule("anotherjob", context -> RESULT);
		waitDone(anotherJobId);
		
		// we need to wait for the purge to finish and there is no real notification for it :(
		Thread.sleep(100);
		
		// assert that nothing was removed beside the job that was marked for deletion
		try {
			get(anotherJobId);
		} catch (NotFoundException e) {
			fail();
		}
		
		// assert first job is properly deleted
		assertNull(tracker.get(deletedJobId));

		// assert job counter is 0 again
		assertEquals(0, tracker.getJobCounter());
		
	}
	
	@Test
	public void scheduleAndCleanupStale() throws Exception {
		
		// job counter is 0 at startup 
		assertEquals(0, tracker.getJobCounter());
		
		// this is a regular job, not marked for auto clean up
		final String jobId = schedule("scheduleAndCleanupStale", context -> RESULT);
		waitDone(jobId);
		
		// assert the job still exist
		try {
			get(jobId);
		} catch (NotFoundException e) {
			fail();
		}
		
		
		// assert job counter is 1
		assertEquals(1, tracker.getJobCounter());
		
		Thread.sleep(DEFAULT_STALE_JOB_AGE + 1); // make sure the job becomes stale
		
		// schedule second job
		final String secondJobId = schedule("secondjob", context -> RESULT);
		waitDone(secondJobId);
		
		// need to wait for the remove notification because the second job becomes 'done' before the purge finishes 
		waitForNotification(jobId, notification -> RemoteJobNotification.isRemoved(notification));
		
		// second job exists
		try {
			get(secondJobId);
		} catch (NotFoundException e) {
			fail();
		}
		
		// assert first job is gone because it was stale
		assertNull(tracker.get(jobId));

		// assert that nothing was removed beside the stale job
		try {
			get(secondJobId);
		} catch (NotFoundException e) {
			fail();
		}
		
		// assert job counter is 0 again
		assertEquals(0, tracker.getJobCounter());
		
	}

	private void waitForNotification(String jobId, Predicate<RemoteJobNotification> predicate) throws Exception {
		
		RemoteJobNotification notification;
		
		do {
			
			notification = notifications.poll(5, TimeUnit.SECONDS);
			
			if (predicate.test(notification) && notification.getJobIds().contains(jobId)) {
				break;
			}
			
		} while (notification != null);
		
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
				if (RemoteJobNotification.isAdded(notification) && notification.getJobIds().contains(jobId)) {
					expectedAdded--;
				} else if (RemoteJobNotification.isChanged(notification) && notification.getJobIds().contains(jobId)) {
					expectedChanged--;
				} else if (RemoteJobNotification.isRemoved(notification) && notification.getJobIds().contains(jobId)) {
					expectedRemoved--;
				}
			} catch (InterruptedException e) {
				throw new RuntimeException();
			}
		}

		assertEquals(String.format("Expecting '%s' ADDED notifications to arrive", expectedAdded), 0, expectedAdded);
		assertEquals(String.format("Expecting '%s' CHANGED notifications to arrive", expectedChanged), 0, expectedChanged);
		assertEquals(String.format("Expecting '%s' REMOVED notifications to arrive", expectedRemoved), 0, expectedRemoved);
	}

	private RemoteJobEntry waitDoneByTracker(final String jobId) {
		RemoteJobEntry entry = null;
		do {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			entry = tracker.get(jobId);
		} while (!entry.isDone());
		return entry;
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
