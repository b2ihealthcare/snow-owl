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

import java.util.concurrent.CountDownLatch;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.junit.Test;

import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.Pipe;
import com.b2international.snowowl.eventbus.util.CountDownHandler;

/**
 * @since 4.5
 */
public class PipeTest extends AbstractEventBusTest {

	private final IEventBus target = new EventBus();
	
	@Override
	public void before() {
		super.before();
		LifecycleUtil.activate(target);
	}
	
	@Test
	public void pipeSourceToTarget() throws Exception {
		final CountDownLatch targetLatch = new CountDownLatch(1);
		
		target.registerHandler(ADDRESS, new CountDownHandler(SEND_MESSAGE, targetLatch));
		bus.registerHandler(ADDRESS, new Pipe(target, ADDRESS));
		
		bus.send(ADDRESS, SEND_MESSAGE, null);
		
		wait(targetLatch);
	}
	
	@Test
	public void pipeSourceToTargetWithReply() throws Exception {
		final CountDownLatch sourceLatch = new CountDownLatch(1);
		final CountDownLatch targetLatch = new CountDownLatch(1);
		
		target.registerHandler(ADDRESS, new CountDownHandler(SEND_MESSAGE, targetLatch) {
			@Override
			public void handle(IMessage message) {
				super.handle(message);
				message.reply(REPLY_MESSAGE);
			}
		});
		bus.registerHandler(ADDRESS, new Pipe(target, ADDRESS));
		
		bus.send(ADDRESS, SEND_MESSAGE, null, new CountDownHandler(REPLY_MESSAGE, sourceLatch));
		
		wait(targetLatch);
		wait(sourceLatch);
	}

	@Test
	public void pipeToWorker() throws Exception {
		IEventBus target = EventBusUtil.getWorkerBus("worker", 2);
		LifecycleUtil.activate(target);
		
		final CountDownLatch sourceLatch = new CountDownLatch(1);
		final CountDownLatch targetLatch = new CountDownLatch(1);
		
		bus.registerHandler(ADDRESS, new Pipe(target, ADDRESS));

		target.registerHandler("work-address", new IHandler<IMessage>() {
			@Override
			public void handle(IMessage message) {
				try {
					Thread.sleep(30_000L);
				} catch (InterruptedException ignored) { }
			}
		});

		target.registerHandler(ADDRESS, new CountDownHandler(SEND_MESSAGE, targetLatch) {
			@Override
			public void handle(IMessage message) {
				super.handle(message);
				message.reply(REPLY_MESSAGE);
			}
		});
		
		/* 
		 * XXX: In a regular event bus, the third (reply) registered message handler would be queued after the 
		 * long-running "work-address" handler, and would block.
		 */
		setWaitTime(1);
		
		target.send("work-address", new Object(), null);
		bus.send(ADDRESS, SEND_MESSAGE, null, new CountDownHandler(REPLY_MESSAGE, sourceLatch));
		
		wait(targetLatch);
		wait(sourceLatch);
	}
}
