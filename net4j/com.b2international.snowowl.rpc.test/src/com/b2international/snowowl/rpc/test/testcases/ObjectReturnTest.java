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

import org.junit.Test;

import com.b2international.snowowl.rpc.RpcException;
import com.b2international.snowowl.rpc.test.service.IObjectReturnService;
import com.b2international.snowowl.rpc.test.service.SampleSerializable;
import com.b2international.snowowl.rpc.test.service.impl.ObjectReturnService;

/**
 * Contains test cases for remote method calls that do not return a value. 
 *
 */
public class ObjectReturnTest extends AbstractRpcTest<IObjectReturnService, ObjectReturnService> {

	public ObjectReturnTest() {
		super(IObjectReturnService.class);
	}

	@Test
	public void testZeroArgReturnsNull() {
		
		final IObjectReturnService serviceProxy = initializeService();
		final SampleSerializable value = serviceProxy.zeroArgReturnNull();
		
		assertNull(value);
	}
	
	@Test
	public void testZeroArgReturnsSerializable() {
		
		final IObjectReturnService serviceProxy = initializeService();
		final SampleSerializable value = serviceProxy.zeroArgReturnSerializable();
		
		assertEquals(new SampleSerializable(0), value);
	}
	
	@Test(expected = RpcException.class)
	public void testZeroArgReturnsNotSerializable() {
		
		final IObjectReturnService serviceProxy = initializeService();
		serviceProxy.zeroArgReturnNotSerializable();
	}
	
	@Test
	public void testIntArgReturnsSerializable() {
		
		final IObjectReturnService serviceProxy = initializeService();
		final SampleSerializable value = serviceProxy.intArgReturnSerializable(5);
		
		assertEquals(new SampleSerializable(5), value);
	}
	
	@Test
	public void testObjectArgReturnsNull() {
		
		final IObjectReturnService serviceProxy = initializeService();
		final SampleSerializable value = serviceProxy.objectArgReturnSerializable(null);
		
		assertNull(value);
	}
	
	@Test
	public void testObjectArgReturnsEqualButNotSame() {
		
		final IObjectReturnService serviceProxy = initializeService();
		
		final SampleSerializable input = new SampleSerializable(5);
		final SampleSerializable value = serviceProxy.objectArgReturnSerializable(input);
		
		assertEquals(input, value);
		assertNotSame(input, value);
	}

	@Override
	protected ObjectReturnService createServiceImplementation() {
		return new ObjectReturnService();
	}
}