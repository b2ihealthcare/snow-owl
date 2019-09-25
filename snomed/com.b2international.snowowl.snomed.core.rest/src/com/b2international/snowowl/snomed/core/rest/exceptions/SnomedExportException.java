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
package com.b2international.snowowl.snomed.core.rest.exceptions;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Runtime exception indicating the failure of the SNOMED CT export process.
 */
public class SnomedExportException extends RuntimeException {

	private static final long serialVersionUID = 3399575806529622257L;

	/**
	 * Creates a new exception instance with the given message.
	 * 
	 * @param message the message for the exception to describe the cause of the problem
	 */
	public SnomedExportException(final String message) {
		super(nullToEmpty(message));
	}
}
