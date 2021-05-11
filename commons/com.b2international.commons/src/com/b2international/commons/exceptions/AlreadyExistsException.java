/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Thrown when an item to be created already exists in the system.
 * 
 * @since 1.0
 */
public class AlreadyExistsException extends ConflictException {

	private static final long serialVersionUID = 6347436684320140303L;

	/**
	 * Creates a new exception instance with the specified arguments.
	 * 
	 * @param type
	 *            - the type of the existing item
	 * @param id
	 *            - the identifier of the existing item
	 */
	public AlreadyExistsException(final String type, final String id) {
		this(type, "identifier", id);
	}

	/**
	 * Creates a new exception instance with the specified arguments.
	 * 
	 * @param type
	 *            - the type of the existing item
	 * @param property
	 *            - the conflicting non-unique property's name
	 * @param propertyValue
	 *            - the conflicting non-unique property value
	 */
	public AlreadyExistsException(final String type, final String property, final String propertyValue) {
		super("%s with '%s' %s already exists.", checkNotNull(type, "type"), checkNotNull(propertyValue, "propertyValue"), checkNotNull(property, "property"));
	}

}
