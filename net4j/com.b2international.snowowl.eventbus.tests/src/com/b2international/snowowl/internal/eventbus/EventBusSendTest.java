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
package com.b2international.snowowl.internal.eventbus;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * @since 3.1
 */
public class EventBusSendTest extends AbstractEventBusTest {

	@Test(expected = IllegalArgumentException.class)
	public void test_Send_Null_Null() {
		bus.send(null, null, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_Send_NonNull_Null() {
		bus.send(ADDRESS, null, null);
	}
	
	@Test
	public void test_Send_No_Handler() {
		final IEventBus actual = bus.send(ADDRESS, SEND_MESSAGE, null);
		assertEquals(bus, actual);
	}
	
	@Test
	public void test_Send_WithHandler() {
		final CountDownLatch latch = new CountDownLatch(1);
		registerHandlersWithLatch(1, ADDRESS, latch);
		bus.send(ADDRESS, SEND_MESSAGE, null);
		wait(latch);
	}
	
	@Test
	public void test_Send_WithMultipleHandlers() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		registerHandlersWithLatch(5, ADDRESS, latch);
		bus.send(ADDRESS, SEND_MESSAGE, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_Publish_Null_Null() throws InterruptedException {
		bus.publish(null, null, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_Publish_NonNull_Null() throws InterruptedException {
		bus.publish(ADDRESS, null, null);
	}
	
	@Test
	public void test_Publish_NoHandlers() throws InterruptedException {
		final IEventBus actual = bus.publish(ADDRESS, SEND_MESSAGE, null);
		assertEquals(bus, actual);
	}
	
	@Test
	public void test_Publish_WithHandlers() throws InterruptedException {
		final int numberOfHandlers = 5;
		final CountDownLatch latch = new CountDownLatch(numberOfHandlers);
		registerHandlersWithLatch(numberOfHandlers, ADDRESS, latch);
		bus.publish(ADDRESS, SEND_MESSAGE, null);
		wait(latch);
	}
	
	@Test
	public void test_Send_Reply() throws InterruptedException {
		// one when message arrives, and one for the reply
		final int expectedHandlerCalls = 2;
		final CountDownLatch latch = new CountDownLatch(expectedHandlerCalls);
		bus.registerHandler(ADDRESS, new IHandler<IMessage>() {
			@Override
			public void handle(IMessage message) {
				assertEquals(SEND_MESSAGE, message.body(String.class));
				message.reply(REPLY_MESSAGE);
				latch.countDown();
			}
		});
		bus.send(ADDRESS, SEND_MESSAGE, null, new IHandler<IMessage>() {
			@Override
			public void handle(IMessage message) {
				assertEquals(REPLY_MESSAGE, message.body(String.class));
				latch.countDown();
			}
		});
		wait(latch);
	}
	
}
