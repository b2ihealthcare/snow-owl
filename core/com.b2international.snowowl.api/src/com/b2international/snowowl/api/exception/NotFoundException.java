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
package com.b2international.snowowl.api.exception;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;

/**
 * Thrown when a requested item could not be found.
 */
public abstract class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static String getMessage(final String type, final String key) {
		checkNotNull(type, "Item type may not be null.");
		checkNotNull(key, "Item key may not be null.");
		return MessageFormat.format("{0} with identifier {1} could not be found.", type, key);
	}

	private final String type;
	private final String key;

	/**
	 * Creates a new instance with the specified type and key.
	 * 
	 * @param type the type of the item which was not found (may not be {@code null})
	 * @param key  the unique key of the item which was not found (may not be {@code null})
	 */
	protected NotFoundException(final String type, final String key) {
		super(getMessage(type, key));
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
}
