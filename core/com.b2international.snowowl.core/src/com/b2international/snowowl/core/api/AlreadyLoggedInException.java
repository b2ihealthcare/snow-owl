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
package com.b2international.snowowl.core.api;

/**
 * {@link SecurityException Security exception} extension. This exception should be raised 
 * to indicate authentication failed because requesting part is already authenticated. 
 */
public class AlreadyLoggedInException extends SecurityException {

	private static final long serialVersionUID = 4840480288469687887L;

	/**Exception message. {@value}*/
	private static final String MESSAGE = "Already logged in.";

	/**Sole constructor.*/
	public AlreadyLoggedInException() {
		super(MESSAGE);
	}
	
}