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

import com.b2international.snowowl.rpc.test.service.INotSerializedReturnService;
import com.b2international.snowowl.rpc.test.service.SampleEnum;

public class NotSerializedReturnService implements INotSerializedReturnService {

	@Override
	public boolean zeroArgReturnTrue() {
		return true;
	}

	@Override
	public boolean multipleArgsReturnBoolean(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		return b1;
	}

	@Override
	public byte multipleArgsReturnByte(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		return b2;
	}
	
	@Override
	public byte[] multipleArgsReturnByteArray(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		return ba;
	}

	@Override
	public int multipleArgsReturnInt(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		return i;
	}

	@Override
	public long multipleArgsReturnLong(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		return l;
	}

	@Override
	public float multipleArgsReturnFloat(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		return f;
	}

	@Override
	public double multipleArgsReturnDouble(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		return d;
	}
	
	@Override
	public String multipleArgsReturnString(boolean b1, byte b2, byte[] ba, int i, long l, float f, double d, String str) {
		return str;
	}

	@Override
	public SampleEnum enumArgReturnEnum(SampleEnum enumValue) {
		return enumValue;
	}
}