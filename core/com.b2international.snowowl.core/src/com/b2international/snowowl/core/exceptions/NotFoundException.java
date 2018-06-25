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
 * Thrown when a requested item could not be found.
 * 
 * @since 4.0
 */
public class NotFoundException extends ApiException {

	private static final long serialVersionUID = 1L;

	private final String type;
	private final String key;

	/**
	 * Creates a new instance with the specified type and key.
	 * 
	 * @param type
	 *            the type of the item which was not found (may not be {@code null})
	 * @param key
	 *            the unique key of the item which was not found (may not be {@code null})
	 */
	public NotFoundException(final String type, final String key) {
		super("%s with identifier '%s' could not be found.", type, key);
		this.type = type;
		this.key = key;
	}

	/**
	 * Returns the type of the item which was not found.
	 * 
	 * @return the type of the missing item
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the identifier of the item which was not found.
	 * 
	 * @return the unique key of the missing item
	 */
	public String getKey() {
		return key;
	}

	@Override
	protected String getDeveloperMessage() {
		return String.format("The requested instance resource (id = '%s', type = '%s') does not exist and/or not yet created.", getKey(), getType());
	}
	
	@Override
	protected Integer getStatus() {
		return 404;
	}
	
	/**
	 * Converts this {@link ComponentNotFoundException} to a {@link BadRequestException}. It is useful when someone would try to indicate problems in
	 * the request body, but some high-level API throws {@link NotFoundException} subclasses.
	 * 
	 * @return
	 */
	public BadRequestException toBadRequestException() {
		return new BadRequestException("'%s' with identifier '%s' is required, but it does not exist.", getType(), getKey());
	}

}
