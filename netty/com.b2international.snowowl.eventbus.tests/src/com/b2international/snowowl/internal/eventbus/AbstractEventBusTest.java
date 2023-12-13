/*
 * Copyright 2011-2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.internal.eventbus;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;

import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.util.CountDownHandler;

/**
 * @since 3.1
 */
public class AbstractEventBusTest {

	protected static final int DEFAULT_WAIT_TIME = 1; 
	protected static final String ADDRESS = "address";
	protected static final String SEND_MESSAGE = "Ping";
	protected static final String REPLY_MESSAGE = "Pong";
	
	protected IHandler<IMessage> noopHandler = IHandler.NOOP;
	protected EventBus bus;
	private int waitTime = DEFAULT_WAIT_TIME;
	
	@Before
	public void before() {
		bus = createBus();
		bus.activate();
	}
	
	protected EventBus createBus() {
		return new EventBus();
	}

	@After
	public void after() {
		bus.deactivate();
	}
	
	protected void wait(CountDownLatch latch) {
		try {
			if (waitTime < 0) {
				latch.await();
			} else {
				assertTrue(latch.await(waitTime, TimeUnit.SECONDS));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	
	protected Collection<IHandler<IMessage>> registerHandlersWithLatch(final int numberOfHandlers, final String address, final CountDownLatch latch) {
		final Collection<IHandler<IMessage>> handlers = new HashSet<IHandler<IMessage>>();
		for (int i = 0; i < numberOfHandlers; i++) {
			CountDownHandler handler = new CountDownHandler(SEND_MESSAGE, latch);
			handlers.add(handler);
			bus.registerHandler(address, handler);
		}
		return handlers;
	}
	
}
