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
package com.b2international.snowowl.internal.eventbus;

import java.util.concurrent.CountDownLatch;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.databene.contiperf.junit.ContiPerfRuleExt;
import org.junit.Rule;
import org.junit.Test;

/**
 * @since 3.1
 */
public class EventBusSendPerformanceTest extends AbstractEventBusTest {

	@Rule
	public ContiPerfRule rule = new ContiPerfRuleExt();

	@Test
	@PerfTest(invocations = 10000, threads = 20)
	@Required(percentile99 = 150)
	public void test_Send_ShouldWorkInMultiThreadedEnv() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		// use the current thread name + the latch hash for the unique address, so the bus will have 1000 handler
		final String address = Thread.currentThread().getName() + Integer.toHexString(latch.hashCode());
		registerHandlersWithLatch(1, address, latch);
		bus.send(address, SEND_MESSAGE);
		wait(latch);
	}

}
