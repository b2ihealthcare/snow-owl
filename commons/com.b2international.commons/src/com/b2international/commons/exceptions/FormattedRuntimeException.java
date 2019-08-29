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
package com.b2international.commons.exceptions;

/**
 * Exception using {@link String#format(String, Object...)} to create a final exception message.
 * 
 * @since 4.1
 */
public class FormattedRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 2260275826641517268L;

	/**
	 * Creates a new {@link FormattedRuntimeException} with the specified template and args. If arguments are defined, and the last argument is a
	 * {@link Throwable} instance, then it will use to populate the {@link #getCause()} property.
	 * 
	 * @param template
	 *            - the format to use in {@link String#format(String, Object...)}, may not be <code>null</code>
	 * @param args
	 *            - the args to use in {@link String#format(String, Object...)}, may be <code>null</code>
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public FormattedRuntimeException(String template, Object... args) {
		super(String.format(template, args), extractCause(args)); // lgtm[java/tainted-format-string]
	}

	private static Throwable extractCause(Object[] args) {
		if (args != null && args.length > 0) {
			final Object lastArg = args[args.length - 1];
			if (lastArg instanceof Throwable) {
				return (Throwable) lastArg;
			}
		}
		return null;
	}

}
