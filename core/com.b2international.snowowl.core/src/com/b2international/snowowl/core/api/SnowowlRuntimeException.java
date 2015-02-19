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

import java.io.Serializable;

import javax.annotation.Nullable;

/**
 * Snow Owl specific runtime exception wrapping and indicating a lower level {@link Throwable exception}.
 * 
 */
public class SnowowlRuntimeException extends RuntimeException implements Serializable {

	/**
	 * Wraps the specified throwable in a {@link SnowowlRuntimeException} if it is not already an instance of it.
	 * 
	 * @param throwable the {@link Throwable} to wrap
	 * @return the wrapped exception
	 */
	public static SnowowlRuntimeException wrap(final @Nullable Throwable throwable) {
		
		if (throwable instanceof SnowowlRuntimeException) {
			return (SnowowlRuntimeException) throwable;
		} else {
			return new SnowowlRuntimeException(throwable);
		}
	}
	
	private static final long serialVersionUID = 470883896862076678L;
	
	/**
	 * Constructs a new Snow Owl service runtime exception without message nor cause.
	 */
	public SnowowlRuntimeException() {
		super();
	}
	
	/**
	 * Constructs a new Snow Owl specific runtime exception with a message.
	 * @param message the detailed message of the exception.
	 */
	public SnowowlRuntimeException(final String message) {
		super(message);
	}
	
	/**
	 * Creates a new Snow Owl runtime exception instance with a cause {@link Throwable exception}.
	 * @param throwable the wrapped cause of this Snow Owl runtime exception instance. 
	 */
	public SnowowlRuntimeException(final Throwable throwable) {
		super(throwable);
	}
	
	/**
	 * Creates a new Snow Owl runtime exception with a detailed message and the cause {@link Throwable exception}.
	 * @param message the detailed message of the exception.
	 * @param throwable the cause of this exception.
	 */
	public SnowowlRuntimeException(final String message, final Throwable throwable) {
		super(message, throwable);
	}

}