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

public interface IObjectReturnService {

	/**
	 * @return always a {@link SampleSerializable} instance with its value set to 0
	 */
	SampleSerializable zeroArgReturnSerializable();
	
	/**
	 * @return always {@code null}
	 */
	SampleSerializable zeroArgReturnNull();
	
	/**
	 * @return an object instance which is not serializable
	 */
	Object zeroArgReturnNotSerializable();
	
	/**
	 * @param value an integer value
	 * @return a {@link SampleSerializable} instance with the argument set as its value
	 */
	SampleSerializable intArgReturnSerializable(int value);
	
	/**
	 * @param value the input instance
	 * @return always the same instance which was passed in {@code value}
	 */
	SampleSerializable objectArgReturnSerializable(SampleSerializable value);
}