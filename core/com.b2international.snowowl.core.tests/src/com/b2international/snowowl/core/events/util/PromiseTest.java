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
package com.b2international.snowowl.core.events.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.core.Map;
import org.junit.Test;

import com.b2international.commons.collections.Procedure;

/**
 * @since 4.2
 */
public class PromiseTest {

	Object resolution = new Object();
	Exception rejection = new Exception();
	
	@Test
	public void resolveBeforeThenHandlerAdded() throws Exception {
		final Promise<Object> p = new Promise<>();
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
		final Promise<Object> p = new Promise<>();
		p.reject(rejection);
		final CountDownLatch latch = new CountDownLatch(1);
		p.fail(input -> {
			assertEquals(rejection, input);
			latch.countDown();
			return null;
		});
		latch.await(100, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void resolveAfterThenHandlerAdded() throws Exception {
		final Promise<Object> p = new Promise<>();
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
		final Promise<Object> p = new Promise<>();
		final CountDownLatch latch = new CountDownLatch(1);
		p.fail(input -> {
			assertEquals(rejection, input);
			latch.countDown();
			return null;
		});
		p.reject(rejection);
		latch.await(100, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void resolveWith() throws Exception {
		final Long finalValue = Promise.immediate(1L)
			.thenWith(input -> Promise.immediate(input * 2))
			.then(input -> input + 2)
			.getSync();
		assertEquals(Long.valueOf(4L), finalValue);
	}
	
	@Test
	public void thenRespond() throws Exception {
		Response<Long> r = Promise.immediateResponse(1L, Map.of("test", "value"))
			.thenRespond(response -> Response.of(2L, Map.of("success", response.getHeaders().get("test"))))
			.getSyncResponse();
		
		assertEquals(Long.valueOf(2L), r.getBody());
		assertEquals(Map.of("success", "value"), r.getHeaders());
	}
	
	@Test
	public void thenRespondWith() throws Exception {
		Response<Long> r = Promise.immediateResponse(1L, Map.of("test", "value"))
			.thenRespondWith(response -> Promise.immediateResponse(2L, Map.of("success", response.getHeaders().get("test"))))
			.getSyncResponse();
		
		assertEquals(Long.valueOf(2L), r.getBody());
		assertEquals(Map.of("success", "value"), r.getHeaders());
	}
	
	@Test
	public void all() throws Exception {
		List<Object> waitForAll = Promise.all(Promise.immediate(1L), Promise.immediate(2L)).getSync();
		assertThat(waitForAll).containsExactlyInAnyOrder(1L, 2L);
	}
	
}
