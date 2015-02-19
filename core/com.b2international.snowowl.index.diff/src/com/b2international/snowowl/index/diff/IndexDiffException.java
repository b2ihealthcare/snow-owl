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
package com.b2international.snowowl.index.diff;

/**
 * Exception that should be thrown when an error occurs during the index difference calculation.
 */
public class IndexDiffException extends RuntimeException {

	private static final long serialVersionUID = 851285846907966286L;

	public IndexDiffException(final String message) {
		super(message);
	}
	
	public IndexDiffException(final Throwable cause) {
		super(cause);
	}
	
	public IndexDiffException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
}