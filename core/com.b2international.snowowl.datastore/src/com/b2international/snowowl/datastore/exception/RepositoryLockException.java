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
package com.b2international.snowowl.datastore.exception;

/**
 * Runtime exception indicating that currently the repository is locked and no modifications
 * are allowed.
 *
 */
public final class RepositoryLockException extends RuntimeException {

	private static final long serialVersionUID = -7319187157955028457L;
	
	/**Creates a new lock exception instance.*/
	public RepositoryLockException() {
		super();
	}
	
	/**
	 * Creates a new exception instance with a specified message.
	 * @param message the message.
	 */
	public RepositoryLockException(final String message) {
		super(message);
	}
	
	/**
	 * Creates a new repository lock exception with the specified cause.
	 * @param cause the cause.
	 */
	public RepositoryLockException(final Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates a new exception instance with the specified message and cause.
	 * @param message the message.
	 * @param cause the cause.
	 */
	public RepositoryLockException(final String message, final Throwable cause) {
		super(message, cause);
	}

}