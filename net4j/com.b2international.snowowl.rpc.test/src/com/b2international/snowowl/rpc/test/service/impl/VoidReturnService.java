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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.b2international.snowowl.rpc.test.service.IVoidReturnService;
import com.b2international.snowowl.rpc.test.service.PrimitiveArgs;
import com.b2international.snowowl.rpc.test.service.SampleEnum;

public class VoidReturnService implements IVoidReturnService {

	private final CountDownLatch invocationCountDownLatch = new CountDownLatch(1);

	private PrimitiveArgs primitiveArgs;

	private SampleEnum enumValue;

	private Object a;

	private Object b;

	@Override
	public void zeroArg() {
		registerCall();
	}

	@Override
	public void zeroArgWithDelay(long delay) {
		
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		registerCall();
	}

	@Override
	public void primitiveArgs(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		primitiveArgs = new PrimitiveArgs(b1, b2, ba, i, l, f, d, str);
		registerCall();
	}
	
	@Override
	public void enumArg(SampleEnum enumValue) {
		this.enumValue = enumValue;
		registerCall();
	}
	
	@Override
	public void objectArgs(Object a, Object b) {
		this.a = a;
		this.b = b;
		registerCall();
	}

	@Override
	public boolean awaitCall(long timeout, TimeUnit unit) throws InterruptedException {
		return invocationCountDownLatch.await(timeout, unit);
	}

	public PrimitiveArgs getPrimitiveArgs() {
		return primitiveArgs;
	}

	public SampleEnum getEnumValue() {
		return enumValue;
	}
	
	public Object getA() {
		return a;
	}
	
	public Object getB() {
		return b;
	}

	private void registerCall() {
		invocationCountDownLatch.countDown();
	}
}