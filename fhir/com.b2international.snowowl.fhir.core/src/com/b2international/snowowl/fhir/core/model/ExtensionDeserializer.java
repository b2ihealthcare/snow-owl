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
package com.b2international.snowowl.fhir.core.model;

import java.io.IOException;
import java.util.Iterator;

import com.b2international.snowowl.fhir.core.codesystems.ExtensionType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.Sets;

/**
 * Polymorphic deserializer to handle different types of {@link Extension}s.
 * @see {@link Extension}
 * @since 8.0.0
 */
public class ExtensionDeserializer extends StdDeserializer<Extension<?>> {

	private static final long serialVersionUID = 1L;

	private static final String VALUE_PREFIX = "value";

	protected ExtensionDeserializer() {
		super(Extension.class);
	}

	@Override
	public Extension<?> deserialize(JsonParser parser, DeserializationContext ctx)
			throws IOException, JsonProcessingException {
		
		TreeNode node = parser.readValueAsTree();
		ObjectCodec objectCodec = parser.getCodec();
		Iterator<String> fieldNames = node.fieldNames();

		ExtensionType[] extensionTypes = ExtensionType.values();
		
		ExtensionType extensionType = null;
		while (fieldNames.hasNext()) {
			String fieldName = (String) fieldNames.next();
			if (fieldName.startsWith(VALUE_PREFIX)) {
				
				String type = fieldName.replace(VALUE_PREFIX, "");
				extensionType = Sets.newHashSet(extensionTypes).stream()
					.filter(t -> t.getDisplayName().equalsIgnoreCase(type))
					.findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown extension type '" + fieldName + "'."));
				
				break;
			}
		}
		
		if (extensionType == null) {
			throw new IllegalArgumentException("Invalid extension with null value type.");
		}
		
		switch (extensionType) {
		case INTEGER:
			return objectCodec.treeToValue(node, IntegerExtension.class);
		case STRING:
			return objectCodec.treeToValue(node, StringExtension.class);
		default:
			throw new IllegalArgumentException("Unsupported extension type '" + extensionType + "'.");
		}
	}

}
