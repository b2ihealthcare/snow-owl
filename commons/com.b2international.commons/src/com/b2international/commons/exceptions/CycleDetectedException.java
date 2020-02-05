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
package com.b2international.commons.exceptions;

/**
 * @since 4.2
 */
public class CycleDetectedException extends BadRequestException {

	private static final long serialVersionUID = -411703765332374586L;
	
	public CycleDetectedException() {
		super("Only for deserialization from exception message");
	}
	
	public CycleDetectedException(String message) {
		super(message);
	}

}
