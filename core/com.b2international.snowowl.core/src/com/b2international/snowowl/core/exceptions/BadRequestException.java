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
 * Thrown when a request contains incorrect parameters or is otherwise malformed.
 * 
 * @since 4.0
 */
public class BadRequestException extends ApiException {

	private static final long serialVersionUID = 7998450893448621719L;

	public BadRequestException(final String message, final Object...args) {
		super(message, args);
	}
	
	@Override
	protected String getDeveloperMessage() {
		return "Input representation syntax or validation errors. Check input values.";
	}
	
	@Override
	protected Integer getStatus() {
		return 400;
	}
	
}
