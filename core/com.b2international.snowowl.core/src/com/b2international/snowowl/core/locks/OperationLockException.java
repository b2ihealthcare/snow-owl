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
package com.b2international.snowowl.core.locks;

import javax.annotation.Nullable;

/**
 * Common exception superclass for reporting issues related to locking.
 *
 */
public class OperationLockException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance with no detail message.
	 */
	public OperationLockException() {
		super();
	}

	/**
	 * Creates a new instance with the specified detail message and cause.
	 * 
	 * @param message the detail message
	 * @param cause the causing {@link Throwable}
	 */
	public OperationLockException(final @Nullable String message, final @Nullable Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new instance with the specified detail message.
	 * 
	 * @param message the detail message
	 */
	public OperationLockException(final @Nullable String message) {
		super(message);
	}

	/**
	 * Creates a new instance with no detail message and the specified cause
	 * 
	 * @param cause the causing {@link Throwable}
	 */
	public OperationLockException(final @Nullable Throwable cause) {
		super(cause);
	}
}