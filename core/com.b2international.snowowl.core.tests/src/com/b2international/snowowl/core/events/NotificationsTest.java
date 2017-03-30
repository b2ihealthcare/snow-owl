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
package com.b2international.snowowl.core.events;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @since 5.7
 */
public class NotificationsTest {

	private static class Event1 extends SystemNotification {}
	private static class Event2 extends SystemNotification {}
	
	private IEventBus bus;
	private Notifications notifications;
	
	@Before
	public void givenNotificationStream() {
		this.bus = EventBusUtil.getBus();
		this.notifications = new Notifications(bus, getClass().getClassLoader());
	}
	
	@Test
	public void subscribe() throws Exception {
		final CountDownLatch latch = new CountDownLatch(2);
		this.notifications.subscribe(notification -> {
			latch.countDown();
		});
		new Event1().publish(bus);
		new Event2().publish(bus);
		latch.await();
	}
	
	@Test
	public void subscribeFiltered() throws Exception {
		final CountDownLatch latch = new CountDownLatch(2);
		this.notifications.filter(Event2.class::isInstance).subscribe(notification -> {
			assertTrue(latch.getCount() > 0);
			latch.countDown();
		});
		new Event2().publish(bus);
		new Event1().publish(bus);
		new Event2().publish(bus);
		latch.await();
	}
	
	@Test
	public void unsubscribe() throws Throwable {
		final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
		final CountDownLatch testLatch = new CountDownLatch(1);
		final Consumer<SystemNotification> onNext = t -> {
			assertTrue("notified more than once", testLatch.getCount() > 0);
			testLatch.countDown();				
		};
		final Disposable subscription = this.notifications.subscribe(onNext, error::set);
		new Event1().publish(bus);
		testLatch.await();
		subscription.dispose();
		// register a verifier subscriber that should get the message, but the other should not
		final CountDownLatch verifierLatch = new CountDownLatch(1);
		this.notifications.subscribe(notification -> {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				// ignore
			}
			verifierLatch.countDown();
		});
		new Event2().publish(bus);
		verifierLatch.await();
		
		// if for some reason we get the message in the first subscriber, error reference will not be null because of the assertion 
		if (error.get() != null) {
			throw error.get();
		}
	}
	
}
