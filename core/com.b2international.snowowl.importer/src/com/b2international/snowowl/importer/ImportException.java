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
package com.b2international.snowowl.importer;

/**
 * General runtime exception for reporting import related errors. 
 *
 */
public class ImportException extends RuntimeException {

	private static final long serialVersionUID = -768895127608732041L;

	/**
	 * Creates an import exception instance with no detail message or cause.
	 */
	public ImportException() {
		super();
	}

	/**
	 * Creates an import exception instance with the given detail message and
	 * cause.
	 * 
	 * @param message
	 *            the exception message
	 *            
	 * @param cause
	 *            the causing {@link Throwable}
	 */
	public ImportException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates an import exception instance with the given detail message.
	 * 
	 * @param message
	 *            the exception message
	 */
	public ImportException(final String message) {
		super(message);
	}

	/**
	 * Creates an import exception with the given cause.
	 * 
	 * @param cause
	 *            the causing {@link Throwable}
	 */
	public ImportException(final Throwable cause) {
		super(cause);
	}
}