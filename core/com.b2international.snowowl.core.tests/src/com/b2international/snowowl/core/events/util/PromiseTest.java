/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.core.Map;
import org.junit.Test;

import com.b2international.commons.collections.Procedure;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.RequestTimeoutException;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;

/**
 * @since 4.2
 */
public class PromiseTest {

	Object resolution = new Object();
	Exception rejection = new Exception();
	
	@Test
	public void unresolvedPromiseState() throws Exception {
		final Promise<Object> p = new Promise<>();
		assertFalse(p.isDone());
		assertFalse(p.isCancelled());
	}
	
	@Test
	public void resolvedPromiseState() throws Exception {
		final Promise<Object> p = Promise.immediate(resolution);
		assertTrue(p.isDone());
		assertFalse(p.isCancelled());
	}
	
	@Test
	public void failedPromiseState() throws Exception {
		final Promise<Object> p = Promise.fail(rejection);
		assertTrue(p.isDone());
		assertFalse(p.isCancelled());
	}
	
	@Test
	public void resolveImmediately() throws Exception {
		final Promise<Object> p = Promise.immediate(resolution);
		
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
	public void rejectImmediately() throws Exception {
		final Promise<Object> p = Promise.fail(rejection);
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
		
		assertFalse(p.isDone());
		assertFalse(p.isCancelled());
		p.resolve(resolution);
		latch.await(100, TimeUnit.MILLISECONDS);
		assertTrue(p.isDone());
		assertFalse(p.isCancelled());
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
		
		assertFalse(p.isDone());
		assertFalse(p.isCancelled());
		p.reject(rejection);
		latch.await(100, TimeUnit.MILLISECONDS);
		assertTrue(p.isDone());
		assertFalse(p.isCancelled());
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
	public void failWith() throws Exception {
		final Long finalValue = Promise.<Long>fail(rejection)
			.failWith(error -> Promise.immediate(1L))
			.getSync();
		assertEquals(Long.valueOf(1L), finalValue);
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
	
	@Test(expected = RequestTimeoutException.class)
	public void timeoutHandling() throws Exception {
		Promise<Object> p = new Promise<>();
		p.getSync(10, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected = BadRequestException.class)
	public void handleApiException() throws Exception {
		Promise<Object> p = new Promise<>();
		p.reject(new BadRequestException("Invalid request params"));
		p.getSync();
	}
	
	@Test(expected = BadRequestException.class)
	public void handleApiExceptionWithTimeout() throws Exception {
		Promise<Object> p = new Promise<>();
		p.reject(new BadRequestException("Invalid request params"));
		p.getSync(1, TimeUnit.SECONDS);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void handleRuntimeException() throws Exception {
		Promise<Object> p = new Promise<>();
		p.reject(new IllegalArgumentException("Invalid request params"));
		p.getSync();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void handleRuntimeExceptionWithTimeout() throws Exception {
		Promise<Object> p = new Promise<>();
		p.reject(new IllegalArgumentException("Invalid request params"));
		p.getSync(1, TimeUnit.SECONDS);
	}
	
	@Test(expected = SnowowlRuntimeException.class)
	public void handleAnyOtherException() throws Exception {
		Promise<Object> p = new Promise<>();
		p.reject(new IOException("Invalid request params"));
		p.getSync();
	}
	
	@Test(expected = SnowowlRuntimeException.class)
	public void handleAnyOtherExceptionWithTimeout() throws Exception {
		Promise<Object> p = new Promise<>();
		p.reject(new IOException("Invalid request params"));
		p.getSync(1, TimeUnit.SECONDS);
	}
	
}
