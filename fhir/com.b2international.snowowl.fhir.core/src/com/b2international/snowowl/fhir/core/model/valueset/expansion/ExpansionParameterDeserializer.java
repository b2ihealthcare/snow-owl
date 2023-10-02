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
package com.b2international.snowowl.fhir.core.model.valueset.expansion;

import java.io.IOException;
import java.util.Iterator;

import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.Sets;

/**
 * Polymorphic deserializer to handle different types of value set {@link Parameter}s.
 * @see {@link Parameter}
 * @since 8.0.0
 */
public class ExpansionParameterDeserializer extends StdDeserializer<Parameter<?>> {

	private static final long serialVersionUID = 1L;
	
	private static final String VALUE_PREFIX = "value";

	protected ExpansionParameterDeserializer() {
		super(Parameter.class);
	}
	
	@Override
	public Parameter<?> deserialize(JsonParser parser, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		
		TreeNode node = parser.readValueAsTree();
		ObjectCodec objectCodec = parser.getCodec();
		
		Iterator<String> fieldNames = node.fieldNames();
		
		FhirDataType[] parameterTypes = FhirDataType.values();
		FhirDataType parameterType = null;
		
		while (fieldNames.hasNext()) {
			String fieldName = (String) fieldNames.next();
			if (fieldName.startsWith(VALUE_PREFIX)) {
				
				parameterType = Sets.newHashSet(parameterTypes).stream()
					.filter(t -> t.getSerializedName().equalsIgnoreCase(fieldName))
					.findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown expansion parameter type '" + fieldName + "'."));
				
				break;
			}
		}

		if (parameterType == null) {
			throw new IllegalArgumentException("Invalid parameter type with null value.");
		}
		
		switch (parameterType) {
		case URI:
			return objectCodec.treeToValue(node, UriParameter.class);
		case CODE:
			return objectCodec.treeToValue(node, CodeParameter.class);
		case DATETIME:
			return objectCodec.treeToValue(node, DateTimeParameter.class);
		case STRING:
			return objectCodec.treeToValue(node, StringParameter.class);
		case BOOLEAN:
			return objectCodec.treeToValue(node, BooleanParameter.class);
		case DECIMAL:
			return objectCodec.treeToValue(node, DecimalParameter.class);
		case INTEGER:
			return objectCodec.treeToValue(node, IntegerParameter.class);
		default:
			throw new IllegalArgumentException("Unsupported property type '" + parameterType + "'.");
		}
	}
	
	
}