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

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * Thrown when a terminology component can not be found for a given component identifier.
 */
public class ComponentNotFoundException extends NotFoundException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception instance with the specified category and component identifier.
	 * 
	 * @param category
	 *            the category of the missing component (may not be {@code null})
	 * @param key
	 *            the identifier of the missing component (may not be {@code null})
	 */
	public ComponentNotFoundException(final ComponentCategory category, final String key) {
		this(category.getDisplayName(), key);
	}
	
	/**
	 * Creates a new exception instance with the specified category and component identifier.
	 * 
	 * @param category
	 *            the category of the missing component (may not be {@code null})
	 * @param key
	 *            the identifier of the missing component (may not be {@code null})
	 */
	public ComponentNotFoundException(final String category, final String key) {
		super(category, key);
	}

}
