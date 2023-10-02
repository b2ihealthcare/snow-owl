/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5.datatype.primitive;

import java.util.regex.Pattern;

/**
 * Indicates that the value is taken from a set of controlled strings defined elsewhere.
 * 
 * @see <a href="https://hl7.org/fhir/R5/datatypes.html#code">2.1.28.0.1 Primitive Types - code</a>
 * @since 9.0
 */
public class CodeType extends StringWrapper {

	/**
	 * A code is restricted to a string which has at least one character and no
	 * leading or trailing whitespace, and where there is no whitespace other than
	 * single spaces in the contents.
	 */
	public static final Pattern VALID_PATTERN = Pattern.compile("[^\\s]+( [^\\s]+)*");

	public CodeType(String value) {
		super(value);
	}
}
