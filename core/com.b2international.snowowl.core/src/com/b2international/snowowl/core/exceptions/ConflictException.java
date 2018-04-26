/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.exceptions;

/**
 * Thrown when the same component is modified concurrently by multiple clients, or the request is based on an outdated
 * version of a component.
 * 
 * @since 4.0
 */
public class ConflictException extends ApiException {

	private static final long serialVersionUID = -2887608541911973086L;

	/**
	 * Creates a new exception instance with the specified message.
	 * 
	 * @param message the exception message
	 * @param args format string arguments (used when the exception message contains {@code %s} placeholders)
	 */
	public ConflictException(String message, Object... args) {
		super(message, args);
	}
	
	@Override
	protected Integer getStatus() {
		return 409;
	}
}
