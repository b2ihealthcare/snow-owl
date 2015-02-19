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

/**
 * The test service interface to use for RPC test method calls that return a value which does not need serialization
 * during transport.
 * 
 */
public interface INotSerializedReturnService {

	/**
	 * Runs synchronously.
	 * 
	 * @return always {@code true}, {@code false} otherwise
	 */
	boolean zeroArgReturnTrue();
	
	/**
	 * Captures the specified arguments, and returns the input argument of the appropriate type.
	 * 
	 * @param b1
	 * @param b2
	 * @param i
	 * @param l
	 * @param f
	 * @param d
	 */
	boolean multipleArgsReturnBoolean(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str);
	
	byte multipleArgsReturnByte(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str);
	
	byte[] multipleArgsReturnByteArray(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str);
	
	int multipleArgsReturnInt(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str);
	
	long multipleArgsReturnLong(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str);
	
	float multipleArgsReturnFloat(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str);
	
	double multipleArgsReturnDouble(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str);
	
	String multipleArgsReturnString(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str);
	
	/**
	 * Captures the passed in enum value.
	 * 
	 * @param enumValue
	 * @return the same enum value which was specified in {@code enumValue}
	 */
	SampleEnum enumArgReturnEnum(SampleEnum enumValue);
}