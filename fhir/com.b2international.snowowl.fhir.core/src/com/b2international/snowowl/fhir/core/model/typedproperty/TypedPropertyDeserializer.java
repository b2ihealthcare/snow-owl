/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.typedproperty;

import java.io.IOException;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Polymorphic deserializer to handle different types of value set {@link TypedProperty}s.
 * @see {@link TypedProperty}
 * @since 8.0.0
 */
@SuppressWarnings("rawtypes")
public class TypedPropertyDeserializer extends StdDeserializer<TypedProperty> {

	private static final long serialVersionUID = 1L;

	private static final String VALUE_PREFIX = "value";
	
	protected TypedPropertyDeserializer() {
		super(TypedProperty.class);
	}

	@Override
	public TypedProperty deserialize(JsonParser parser, DeserializationContext ctx)
			throws IOException, JsonProcessingException {
		
		String type = null;
		String currentName = parser.currentName();
		if (currentName.startsWith(VALUE_PREFIX)) {
			type = currentName.replace(VALUE_PREFIX, "");
		}
		
		if (type == null) {
			throw new IllegalArgumentException("Invalid parameter type with null value.");
		}
		
		TextNode node = (TextNode) parser.readValueAsTree();
		String value = node.asText();
		
		switch (type) {
		
		case "String": return new StringProperty(value);
		case "Date": return new DateProperty(FhirDates.parseDate(value));
		case "DateTime": return new DateTimeProperty(FhirDates.parseDate(value));
		case "Instant": return new InstantProperty(Instant.builder().instant(FhirDates.parseDate(value)).build());

		default:
			throw new IllegalArgumentException("Unsupported property type '" + type + "'.");
		}
		
	}

}
