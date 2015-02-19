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
package com.b2international.snowowl.core.api.index;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;

/**
 * Exception for reporting index related issues.
 *  
 */
public class IndexException extends SnowowlRuntimeException {

	/**
	 * Wraps the specified throwable in an {@link IndexException} if it is not already an instance of it.
	 * 
	 * @param throwable the {@link Throwable} to wrap
	 * @return the wrapped exception
	 */
	public static IndexException wrap(final @Nullable Throwable throwable) {
		
		if (throwable instanceof IndexException) {
			return (IndexException) throwable;
		} else {
			return new IndexException(throwable);
		}
	}
	
	private static final long serialVersionUID = 1415393167196648834L;

	public IndexException(final Throwable exception) {
		super(exception);
	}
	
	public IndexException(final String message, final Throwable exception) {
		super(message, exception);
	}
	
	public IndexException(final String message) {
		super(message);
	}
}