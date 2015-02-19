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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import com.b2international.snowowl.rpc.test.service.IExceptionThrowingService;
import com.b2international.snowowl.rpc.test.service.RpcTestException;
import com.b2international.snowowl.rpc.test.service.impl.ExceptionThrowingService;

/**
 * Contains test cases for remote method calls that throw a checked exception declared on the service interface.
 *
 */
public class ExceptionHandlingTest extends AbstractRpcTest<IExceptionThrowingService, ExceptionThrowingService> {

	public ExceptionHandlingTest() {
		super(IExceptionThrowingService.class);
	}

	@Test(expected=RpcTestException.class)
	public void testZeroArgThrowsException() throws RpcTestException {
		final IExceptionThrowingService serviceProxy = initializeService();
		serviceProxy.zeroArgThrowsException();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testZeroArgThrowsRuntimeException() {
		final IExceptionThrowingService serviceProxy = initializeService();
		serviceProxy.zeroArgThrowsRuntimeException();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testZeroArgThrowsDeclaredRuntimeException2() {
		final IExceptionThrowingService serviceProxy = initializeService();
		serviceProxy.zeroArgThrowsDeclaredRuntimeException();
	}
	
	@Test(expected=RpcTestException.class)
	public void testZeroArgReturnSerializableThrowsException() throws RpcTestException {
		final IExceptionThrowingService serviceProxy = initializeService();
		serviceProxy.zeroArgReturnSerializableThrowsException();
	}
	
	@Test(expected=RpcTestException.class)
	public void testZeroArgReturnPrimitiveThrowsException() throws RpcTestException {
		final IExceptionThrowingService serviceProxy = initializeService();
		serviceProxy.zeroArgReturnPrimitiveThrowsException();
	}
	
	@Test(expected=RpcTestException.class)
	public void testReportWithIProgressMonitorThrowsException() throws RpcTestException {
		final IExceptionThrowingService serviceProxy = initializeService();
		serviceProxy.reportWithIProgressMonitorThrowsException(new NullProgressMonitor());
	}
	
	@Override
	protected ExceptionThrowingService createServiceImplementation() {
		return new ExceptionThrowingService();
	}
}