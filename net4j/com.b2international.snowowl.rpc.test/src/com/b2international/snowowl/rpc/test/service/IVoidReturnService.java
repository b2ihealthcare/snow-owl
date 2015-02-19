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
package com.b2international.snowowl.rpc.test.service;

import java.util.concurrent.TimeUnit;

/**
 * The test service interface to use for RPC test method calls that do not return a value.
 * 
 */
public interface IVoidReturnService {

	/**
	 * Runs asynchronously.
	 */
	void zeroArg();
	
	/**
	 * Waits for the specified amount of milliseconds to elapse before proceeding. 
	 * 
	 * @param delay the number of milliseconds to wait
	 */
	void zeroArgWithDelay(long delay);
	
	/**
	 * Captures the specified arguments.
	 * 
	 * @param b1
	 * @param b2
	 * @param ba
	 * @param i
	 * @param l
	 * @param f
	 * @param d
	 * @param str
	 */
	void primitiveArgs(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str);
	
	/**
	 * Captures the passed in enum value.
	 * 
	 * @param enumValue
	 */
	void enumArg(SampleEnum enumValue);
	
	/**
	 * Captures the specified object arguments.
	 * 
	 * @param a
	 * @param b
	 */
	void objectArgs(Object a, Object b);
	
	/**
	 * Blocks until the specified time elapses, or a method has been invoked on the service.
	 * 
	 * @param timeout
	 * @param unit
	 * @return 
	 * @throws InterruptedException
	 */
	boolean awaitCall(long timeout, TimeUnit unit) throws InterruptedException;
}