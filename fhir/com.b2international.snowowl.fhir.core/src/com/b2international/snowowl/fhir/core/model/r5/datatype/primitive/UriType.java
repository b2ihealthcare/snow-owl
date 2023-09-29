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
 * A Uniform Resource Identifier Reference (described in RFC 3986). Note: URIs
 * are case sensitive. For UUID (urn:uuid:53fefa32-fcbb-4ff8-8a92-55ee120877b7)
 * use all lowercase letters.
 * 
 * @see <a href="https://hl7.org/fhir/R5/datatypes.html#uri">2.1.28.0.1 Primitive Types - uri</a>
 * @since 9.0
 */
public class UriType extends StringWrapper {

	public static final Pattern VALID_PATTERN = Pattern.compile("\\S*");
	
	public UriType(String value) {
		super(value);
	}
}
