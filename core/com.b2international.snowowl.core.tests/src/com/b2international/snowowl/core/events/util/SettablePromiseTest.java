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
package com.b2international.snowowl.core.events.util;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.commons.collections.Procedure;

/**
 * @since 4.2
 */
public class SettablePromiseTest {

	Object resolution = new Object();
	Exception rejection = new Exception();
	
	@Test
	public void resolveBeforeThenHandlerAdded() throws Exception {
		final SettablePromise<Object> p = new SettablePromise<>();
		p.resolve(resolution);
		final CountDownLatch latch = new CountDownLatch(1);
		p.then(new Procedure<Object>() {
			@Override
			protected void doApply(Object input) {
				assertEquals(resolution, input);
				latch.countDown();
			}
		});
		latch.await(100, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void rejectBeforeFailHandlerAdded() throws Exception {
		final SettablePromise<Object> p = new SettablePromise<>();
		p.reject(rejection);
		final CountDownLatch latch = new CountDownLatch(1);
		p.fail(new Procedure<Throwable>() {
			@Override
			protected void doApply(Throwable input) {
				assertEquals(rejection, input);
				latch.countDown();
			}
		});
		latch.await(100, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void resolveAfterThenHandlerAdded() throws Exception {
		final SettablePromise<Object> p = new SettablePromise<>();
		final CountDownLatch latch = new CountDownLatch(1);
		p.then(new Procedure<Object>() {
			@Override
			protected void doApply(Object input) {
				assertEquals(resolution, input);
				latch.countDown();
			}
		});
		p.resolve(resolution);
		latch.await(100, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void rejectAfterFailHandlerAdded() throws Exception {
		final SettablePromise<Object> p = new SettablePromise<>();
		final CountDownLatch latch = new CountDownLatch(1);
		p.fail(new Procedure<Throwable>() {
			@Override
			protected void doApply(Throwable input) {
				assertEquals(rejection, input);
				latch.countDown();
			}
		});
		p.reject(rejection);
		latch.await(100, TimeUnit.MILLISECONDS);
	}
	
}
