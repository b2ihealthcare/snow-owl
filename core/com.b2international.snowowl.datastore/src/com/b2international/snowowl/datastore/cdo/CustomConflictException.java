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
package com.b2international.snowowl.datastore.cdo;

import java.util.Set;

/**
 * 
 * Custom exception to wrap conflicts when comparing CDO change sets.
 * 
 *
 */
public class CustomConflictException extends Exception {

	private static final long serialVersionUID = 3279539750086670253L;
	
	/**
	 * Set of detected conflicts.
	 */
	private final Set<ConflictWrapper> conflicts;
	
	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public CustomConflictException(final String message, final Set<ConflictWrapper> conflicts) {
		super(message);
		this.conflicts = conflicts;
	}

	/**
	 * Constructor
	 * 
	 * @param exception
	 */
	public CustomConflictException(final Exception exception, final Set<ConflictWrapper> conflicts) {
		super(exception);
		this.conflicts = conflicts;
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 * @param exception
	 */
	public CustomConflictException(final String message, final Exception exception, final Set<ConflictWrapper> conflicts) {
		super(message, exception);
		this.conflicts = conflicts;
	}
	
	/**
	 * @return set of detected conflicts
	 */
	public Set<ConflictWrapper> getConflicts() {
		return conflicts;
	}

}