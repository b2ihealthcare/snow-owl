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
package com.b2international.snowowl.core.events.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 5.7
 */
public class RequestTest {
	
	private static class BasicClass implements Request<ServiceProvider, String> {
		@Override
		public String execute(ServiceProvider context) {
			return "TEST";
		}
	}
	
	private static class VoidReturnType implements Request<ServiceProvider, Void> {
		@Override
		public Void execute(ServiceProvider context) {
			return null;
		}
	}
	
	private static abstract class AbstractBase<R> implements Request<ServiceProvider, R> {}
	private static class Subclass extends AbstractBase<Boolean> {
		@Override
		public Boolean execute(ServiceProvider context) {
			return false;
		}
	}
	
	private static class ParameterizedReturnType implements Request<ServiceProvider, Promise<String>> {
		@Override
		public Promise<String> execute(ServiceProvider context) {
			return Promise.immediate("TEST");
		}
	}
	
	@Test
	public void basicClassReturnType() throws Exception {
		assertEquals(String.class, new BasicClass().getReturnType());
	}
	
	@Test
	public void lambdaGetReturnType() throws Exception {
		final Request<ServiceProvider, Integer> req = context -> 0;
		assertEquals(Integer.class, req.getReturnType());
	}
	
	@Test
	public void subClassReturnType() throws Exception {
		assertEquals(Boolean.class, new Subclass().getReturnType());
	}
	
	@Test
	public void voidReturnType() throws Exception {
		assertEquals(Void.class, new VoidReturnType().getReturnType());
	}
	
	@Test
	public void parameterizedReturnType() throws Exception {
		assertEquals(Promise.class, new ParameterizedReturnType().getReturnType());
	}
	
}
