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

import java.io.IOException;
import java.util.regex.Pattern;

import org.eclipse.xtext.util.RuntimeIOException;

import com.b2international.commons.encoding.Base64;
import com.b2international.snowowl.fhir.core.model.r5.base.PrimitiveType;

/**
 * A stream of bytes, base64 encoded (<a href="http://tools.ietf.org/html/rfc4648">RFC 4648</a>). 
 * 
 * @see <a href="https://hl7.org/fhir/R5/datatypes.html#base64Binary">2.1.28.0.1 Primitive Types - base64Binary</a>
 * @since 9.0
 */
public class Base64BinaryType extends PrimitiveType<byte[]> {
	
	/**
	 * base64Binary content does not include any whitespace or line feeds, but
	 * reading applications should ignore whitespace characters (per RFC 4648).
	 */
	public static final Pattern VALID_PATTERN = Pattern.compile("(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?");
	
	private final String value;

	public Base64BinaryType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public byte[] getRawValue() {
		try {
			return Base64.decode(value);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}
}
