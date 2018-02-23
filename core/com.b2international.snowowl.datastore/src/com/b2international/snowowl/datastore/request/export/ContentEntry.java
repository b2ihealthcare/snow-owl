/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.export;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * @since 6.3
 */
public abstract class ContentEntry implements Serializable {
	
	/**
	 * <ul>
	 * <li>Maximum 192 characters;
	 * <li>Should start with an alphanumeric letter;
	 * <li>Following letters can be alphanumeric, '-', '_', '.', '(' and ')'
	 * </ul>
	 */
	private static final Pattern ALLOWED_FILENAME = Pattern.compile("^[0-9a-zA-Z][0-9a-zA-Z\\-\\.\\(\\)_]{0,191}$");
	
	private final String name;

	protected ContentEntry(final String name) {
		checkArgument(ALLOWED_FILENAME.matcher(name).matches(), "Entry name is invalid.");
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
