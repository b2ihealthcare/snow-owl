/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.eventbus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.snowowl.eventbus.net4j.EventBusConstants;
import com.b2international.snowowl.internal.eventbus.EventBus;

/**
 * @since 3.2
 */
public class EventBusUtil {

	/**
	 * Returns a simple unmanaged, activated EventBus with global identifier,
	 * {@link EventBusConstants#GLOBAL_BUS}.
	 * 
	 * @return
	 */
	public static final IEventBus getBus() {
		final EventBus bus = new EventBus();
		LifecycleUtil.activate(bus);
		return bus;
	}

	/**
	 * Returns a simple unmanaged, activated EventBus for the custom identifier.
	 * 
	 * @return
	 */
	public static final IEventBus getBus(String name, int numberOfWorkers) {
		final EventBus bus = new EventBus(name, numberOfWorkers);
		LifecycleUtil.activate(bus);
		return bus;
	}
	
	/**
	 * Sends an asynchronous message like it was a synchronous call. Waits until the response arrives, or the timeout happens.
	 * @param bus
	 * @param address
	 * @param message
	 * @param timeout
	 * @return
	 */
	public static IMessage sendWithResult(IEventBus bus, String address, Object message, long timeout) {
		final AtomicReference<IMessage> result = new AtomicReference<IMessage>();
		final CountDownLatch latch = new CountDownLatch(1);
		bus.send(address, message, new IHandler<IMessage>() {
			@Override
			public void handle(IMessage message) {
				result.set(message);
				latch.countDown();
			}
		});
		try {
			boolean success = latch.await(timeout, TimeUnit.MILLISECONDS);
			if (!success) {
				throw new RuntimeException(String.format("Timeout when calling %s with timeout %s", address, timeout));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result.get();
	}

	/**
	 * @return an {@link EventBus} with the specified description and number of workers, using a shared queue for distributing tasks
	 */
	public static IEventBus getWorkerBus(String name, int numberOfWorkers) {
		final IEventBus bus = new EventBus(name, numberOfWorkers);
		LifecycleUtil.activate(bus);
		return bus;
	}
}
