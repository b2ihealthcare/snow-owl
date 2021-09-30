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
package com.b2international.snowowl.fhir.core.model.usagecontext;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Polymorphic deserializer to handle different types of value set {@link UsageContext}s.
 * @see {@link UsageContext}
 * @since 8.0.0
 */
public class UsageContextDeserializer extends StdDeserializer<UsageContext<?>> {

	private static final long serialVersionUID = 1L;
	
	private static final String VALUE_PREFIX = "value";

	protected UsageContextDeserializer() {
		super(UsageContext.class);
	}
	
	@Override
	public UsageContext<?> deserialize(JsonParser parser, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		
		TreeNode node = parser.readValueAsTree();
		ObjectCodec objectCodec = parser.getCodec();
		
		Iterator<String> fieldNames = node.fieldNames();
		
		String valueFieldName = null;
		
		while (fieldNames.hasNext()) {
			String fieldName = (String) fieldNames.next();
			if (fieldName.startsWith(VALUE_PREFIX)) {
				valueFieldName = fieldName.replace(VALUE_PREFIX, "");
				break;
			}
		}

		if (valueFieldName == null) {
			throw new IllegalArgumentException("Invalid parameter type with null value.");
		}
		
		switch (valueFieldName) {
		case CodeableConceptUsageContext.CONTEXT_TYPE:
			return objectCodec.treeToValue(node, CodeableConceptUsageContext.class);
		case QuantityUsageContext.CONTEXT_TYPE:
			return objectCodec.treeToValue(node, QuantityUsageContext.class);
		case RangeUsageContext.CONTEXT_TYPE:
			return objectCodec.treeToValue(node, RangeUsageContext.class);
		default:
			throw new IllegalArgumentException("Unsupported useage context type '" + valueFieldName + "'.");
		}
	}
	
	
}