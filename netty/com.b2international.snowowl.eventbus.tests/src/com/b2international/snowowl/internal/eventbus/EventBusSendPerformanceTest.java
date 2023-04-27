/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 * @since 3.1
 */
public class EventBusSendPerformanceTest extends AbstractEventBusTest {

	private static final int NUMBER_OF_WORKERS = 10;

	@Test
	public void test_Send_ShouldWorkInMultiThreadedEnv() throws InterruptedException {
		setWaitTime(60); // increase wait seconds just in case
		final int numberOfMessagesToSend = 1_000_000;
		
		final CountDownLatch latch = new CountDownLatch(numberOfMessagesToSend * 2); // measuring both receive and reply
		
		final String address = Thread.currentThread().getName() + Integer.toHexString(latch.hashCode());
		registerHandlersWithLatch(1, address, latch);
		
		Stopwatch w = Stopwatch.createStarted();
		for (int i = 0; i < numberOfMessagesToSend; i++) {
			bus.send(address, SEND_MESSAGE, Collections.emptyMap(), (reply) -> {
				latch.countDown();
			});
		}
		wait(latch);
		long execTime = w.elapsed(TimeUnit.MICROSECONDS);
		
		System.err.println("Took: " + execTime + " microsec");
		System.err.println("Avg exec time: " + (double) execTime / numberOfMessagesToSend + " microsec");
		System.err.println("Throughput: " +  numberOfMessagesToSend / ((double) execTime / 1_000_000) + " request/s");
		
		// at the end the bus should have maximum configured thread count in its executor service
		assertEquals(NUMBER_OF_WORKERS, ((ThreadPoolExecutor) bus.getExecutorService()).getPoolSize());
	}

}
