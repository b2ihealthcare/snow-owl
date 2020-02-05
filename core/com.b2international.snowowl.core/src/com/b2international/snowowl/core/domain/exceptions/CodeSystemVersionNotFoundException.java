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
package com.b2international.snowowl.core.domain.exceptions;

import com.b2international.commons.exceptions.NotFoundException;

/**
 * Thrown when a version within a code system can not be found for a given version identifier.
 */
public class CodeSystemVersionNotFoundException extends NotFoundException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance with the specified code system version identifier.
	 * 
	 * @param version the identifier of the version which could not be found (may not be {@code null})
	 */
	public CodeSystemVersionNotFoundException(final String version) {
		super("Code system version", version);
	}
}
