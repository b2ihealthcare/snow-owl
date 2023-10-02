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
package com.b2international.snowowl.fhir.core.model.r5.datatype;

import java.util.regex.Pattern;

import com.b2international.snowowl.fhir.core.model.r5.base.PrimitiveType;

/**
 * Any combination of upper- or lower-case ASCII letters ('A'..'Z', and
 * 'a'..'z', numerals ('0'..'9'), '-' and '.', with a length limit of 64
 * characters. (This might be an integer, an un-prefixed OID, UUID or any other
 * identifier pattern that meets these constraints.)
 * 
 * @see <a href="https://hl7.org/fhir/R5/datatypes.html#id">2.1.28.0.1 Primitive Types - id</a>
 * @since 9.0
 */
public class IdType extends PrimitiveType {

	public static final Pattern VALID_PATTERN = Pattern.compile("[A-Za-z0-9\\-\\.]{1,64}");
	
	private final String value;

	public IdType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public Object getRawValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
