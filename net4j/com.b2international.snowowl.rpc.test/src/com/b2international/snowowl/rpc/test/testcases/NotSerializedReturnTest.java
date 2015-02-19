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
package com.b2international.snowowl.rpc.test.testcases;

import static junit.framework.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.b2international.snowowl.rpc.test.service.INotSerializedReturnService;
import com.b2international.snowowl.rpc.test.service.SampleEnum;
import com.b2international.snowowl.rpc.test.service.impl.NotSerializedReturnService;

/**
 * Contains test cases for remote method calls that do return a value which does not need serialization (primitive types, enums and Strings). 
 *
 */
public class NotSerializedReturnTest extends AbstractRpcTest<INotSerializedReturnService, NotSerializedReturnService> {

	public NotSerializedReturnTest() {
		super(INotSerializedReturnService.class);
	}

	@Test
	public void testZeroArgReturnsTrue() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final boolean value = serviceProxy.zeroArgReturnTrue();
		
		assertEquals(true, value);
	}
	
	@Test
	public void testMultipleArgsReturnsBoolean() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final boolean value = serviceProxy.multipleArgsReturnBoolean(true, (byte) 5, new byte[] { 6, 7, 8 }, 10, 15L, 20.0f, 40.0d, "hello world");

		assertEquals(true, value);
	}
	
	@Test
	public void testMultipleArgsReturnsByte() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final byte value = serviceProxy.multipleArgsReturnByte(true, (byte) 5, new byte[] { 6, 7, 8 }, 10, 15L, 20.0f, 40.0d, "hello world");
		
		assertEquals((byte) 5, value);
	}
	
	@Test
	public void testMultipleArgsReturnsByteArray() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final byte[] oldValue = new byte[] { 6, 7, 8 };
		final byte[] value = serviceProxy.multipleArgsReturnByteArray(true, (byte) 5, oldValue, 10, 15L, 20.0f, 40.0d, "hello world");
		
		assertTrue(Arrays.equals(oldValue, value));
		assertNotSame(value, oldValue);
	}
	
	@Test
	public void testMultipleArgsReturnsInt() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final int value = serviceProxy.multipleArgsReturnInt(true, (byte) 5, new byte[] { 6, 7, 8 }, 10, 15L, 20.0f, 40.0d, "hello world");
		
		assertEquals(10, value);
	}
	
	@Test
	public void testMultipleArgsReturnsLong() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final long value = serviceProxy.multipleArgsReturnLong(true, (byte) 5, new byte[] { 6, 7, 8 }, 10, 15L, 20.0f, 40.0d, "hello world");
		
		assertEquals(15L, value);
	}
	
	@Test
	public void testMultipleArgsReturnsFloat() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final float value = serviceProxy.multipleArgsReturnFloat(true, (byte) 5, new byte[] { 6, 7, 8 }, 10, 15L, 20.0f, 40.0d, "hello world");
		
		assertEquals(20.0f, value);
	}
	
	@Test
	public void testMultipleArgsReturnsDouble() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final double value = serviceProxy.multipleArgsReturnDouble(true, (byte) 5, new byte[] { 6, 7, 8 }, 10, 15L, 20.0f, 40.0d, "hello world");
		
		assertEquals(40.0d, value);
	}
	
	@Test
	public void testMultipleArgsReturnsString() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final String oldValue = "hello world";
		final String value = serviceProxy.multipleArgsReturnString(true, (byte) 5, new byte[] { 6, 7, 8 }, 10, 15L, 20.0f, 40.0d, oldValue);
		
		assertEquals(oldValue, value);
		assertNotSame(value, oldValue);
	}
	
	@Test
	public void testNullEnumArgReturnsNull() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final SampleEnum value = serviceProxy.enumArgReturnEnum(null);
		
		assertNull(value);
	}
	
	@Test
	public void testNonNullEnumArgReturnsSame() {
		
		final INotSerializedReturnService serviceProxy = initializeService();
		final SampleEnum value = serviceProxy.enumArgReturnEnum(SampleEnum.TWO);
		
		assertSame(SampleEnum.TWO, value);
	}

	@Override
	protected NotSerializedReturnService createServiceImplementation() {
		return new NotSerializedReturnService();
	}
}