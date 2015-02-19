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
package com.b2international.snowowl.rpc.test.service.impl;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.rpc.test.service.IExceptionThrowingService;
import com.b2international.snowowl.rpc.test.service.RpcTestException;
import com.b2international.snowowl.rpc.test.service.SampleSerializable;

/**
 * Implementation of the {@link IExceptionThrowingService} interface.
 * 
 */
public class ExceptionThrowingService implements IExceptionThrowingService {

	private static final String TASK_NAME = "Test";
	private static final int TOTAL_WORK = 3;

	@Override
	public void zeroArgThrowsException() throws RpcTestException {
		throw new RpcTestException();
	}

	@Override
	public SampleSerializable zeroArgReturnSerializableThrowsException() throws RpcTestException {
		throw new RpcTestException();
	}
	
	@Override
	public int zeroArgReturnPrimitiveThrowsException() throws RpcTestException {
		throw new RpcTestException();
	}
	
	@Override
	public void zeroArgThrowsRuntimeException() {
		throw new IllegalArgumentException();
	}
	
	@Override
	public void zeroArgThrowsDeclaredRuntimeException() throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}
	
	@Override
	public void reportWithIProgressMonitorThrowsException(IProgressMonitor monitor) throws RpcTestException {
		monitor.beginTask(TASK_NAME, TOTAL_WORK);
		try {
			Thread.sleep(50L);
		} catch (InterruptedException e) {
			// ignore
		}
		monitor.worked(1);
		throw new RpcTestException();
	}
}