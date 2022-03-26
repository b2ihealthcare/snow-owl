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

import org.junit.Test;

import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 3.1
 */
public class EventBusHandlerRegistrationTest extends AbstractEventBusTest {

	@Test(expected = IllegalArgumentException.class)
	public void test_RegisterHandler_Null_Null() {
		bus.registerHandler(null, null);
	}
	
	@Test
	public void test_RegisterHandler_NonNull_Null() {
		final IEventBus actual = bus.registerHandler(ADDRESS, null);
		assertEquals(bus, actual);
	}
	
	@Test
	public void test_RegisterHandler_NonNull_NonNull() {
		final IEventBus actual = bus.registerHandler(ADDRESS, noopHandler);
		assertEquals(bus, actual);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_UnregisterHandler_Null_Null() {
		bus.unregisterHandler(null, null);
	}
	
	@Test
	public void test_UnregisterHandler_NonNull_Null() {
		final IEventBus actual = bus.unregisterHandler(ADDRESS, null);
		assertEquals(bus, actual);
	}
	
	@Test
	public void test_UnregisterHandler_NotRegistered() {
		final IEventBus actual = bus.unregisterHandler(ADDRESS, noopHandler);
		assertEquals(bus, actual);
	}
	
	@Test
	public void test_UnregisterHandler_Registered() {
		final IEventBus actual = bus.registerHandler(ADDRESS, noopHandler).unregisterHandler(ADDRESS, noopHandler);
		assertEquals(bus, actual);
	}
	
}
