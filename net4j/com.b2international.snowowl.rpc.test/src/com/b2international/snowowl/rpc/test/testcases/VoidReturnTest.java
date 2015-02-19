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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.snowowl.rpc.RpcException;
import com.b2international.snowowl.rpc.test.service.IVoidReturnService;
import com.b2international.snowowl.rpc.test.service.PrimitiveArgs;
import com.b2international.snowowl.rpc.test.service.SampleEnum;
import com.b2international.snowowl.rpc.test.service.SampleSerializable;
import com.b2international.snowowl.rpc.test.service.impl.VoidReturnService;

/**
 * Contains test cases for remote method calls that do not return a value. 
 *
 */
public class VoidReturnTest extends AbstractRpcTest<IVoidReturnService, VoidReturnService> {

	private static final int SERVICE_TIMEOUT_MILLIS = 60;

	public VoidReturnTest() {
		super(IVoidReturnService.class);
	}

	@Test
	public void testZeroArg() throws InterruptedException {
		
		final IVoidReturnService serviceProxy = initializeService();
		serviceProxy.zeroArg();
		
		assertRequestProcessed();
	}

	@Test
	public void testPrimitiveArgs() throws InterruptedException {
		
		final IVoidReturnService serviceProxy = initializeService();
		
		byte[] oldArray = new byte[] { 6, 7, 8 };
		byte[] newArray = new byte[] { 6, 7, 8 };
		
		String oldString = "hello world";
		String newString = new String(oldString); // explicit copy
		
		serviceProxy.primitiveArgs(true, (byte) 5, oldArray, 10, 15L, 20.0f, 40.0d, oldString);

		assertRequestProcessed();

		assertEquals(new PrimitiveArgs(true, (byte) 5, newArray, 10, 15L, 20.0f, 40.0d, newString), serviceImplementation.getPrimitiveArgs());
		
		// Demonstrates that sending objects over the wire breaks reference equality for strings and byte arrays.
		assertNotSame(oldArray, serviceImplementation.getPrimitiveArgs().getBa());
		assertNotSame(oldString, serviceImplementation.getPrimitiveArgs().getStr());
	}
	
	@Test
	public void testNullEnumArg() throws InterruptedException {
		
		final IVoidReturnService serviceProxy = initializeService();
		serviceProxy.enumArg(null);
		
		assertRequestProcessed();
		assertNull(serviceImplementation.getEnumValue());
	}
	
	@Test
	public void testNonNullEnumArg() throws InterruptedException {
		
		final IVoidReturnService serviceProxy = initializeService();
		serviceProxy.enumArg(SampleEnum.THREE);
		
		assertRequestProcessed();
		assertSame(SampleEnum.THREE, serviceImplementation.getEnumValue());
	}
	
	@Test
	public void testNullObjectArg() throws InterruptedException {
		
		final IVoidReturnService serviceProxy = initializeService();
		serviceProxy.objectArgs(null, null);
		
		assertRequestProcessed();
		assertNull(serviceImplementation.getA());
		assertNull(serviceImplementation.getB());
	}
	
	@Test(expected = RpcException.class)
	public void testNotSerializableObjectArg() throws InterruptedException {
		
		final IVoidReturnService serviceProxy = initializeService();
		serviceProxy.objectArgs(new Object(), new Object());
		
		assertRequestProcessed();
	}

	@Test
	public void testObjectArgsAreEqualButNotSame() throws InterruptedException {
		
		final IVoidReturnService serviceProxy = initializeService();
		final SampleSerializable obj = new SampleSerializable(5);
		
		serviceProxy.objectArgs(obj, obj);
		
		assertRequestProcessed();
		
		// Demonstrates that sending objects over the wire breaks reference equality.
		assertEquals(obj, serviceImplementation.getA());
		assertEquals(obj, serviceImplementation.getB());
		assertEquals(serviceImplementation.getA(), serviceImplementation.getB());
		
		assertNotSame(obj, serviceImplementation.getA());
		assertNotSame(obj, serviceImplementation.getB());
		assertNotSame(serviceImplementation.getA(), serviceImplementation.getB());
	}

	@Override
	protected VoidReturnService createServiceImplementation() {
		return new VoidReturnService();
	}

	protected void assertRequestProcessed() throws InterruptedException {
		assertEquals("Service did not respond in time.", true, serviceImplementation.awaitCall(SERVICE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
	}

	protected void assertRequestNotProcessed() throws InterruptedException {
		assertEquals("Unexpected response while waiting.", false, serviceImplementation.awaitCall(SERVICE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
	}
}