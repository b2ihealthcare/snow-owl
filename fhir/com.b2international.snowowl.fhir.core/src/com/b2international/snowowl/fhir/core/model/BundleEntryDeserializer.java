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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Polymorphic deserializer to handle different types of {@link Bundle} entries.
 * @see {@link Entry}
 * @since 8.0.0
 */
public class BundleEntryDeserializer extends StdDeserializer<Entry> {

	private static final long serialVersionUID = 1L;

	protected BundleEntryDeserializer() {
		super(Entry.class);
	}

	@Override
	public Entry deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		TreeNode node = parser.readValueAsTree();
		ObjectCodec objectCodec = parser.getCodec();

		// Select the concrete class based on the resource type header
		TreeNode resourceTypeNode = node.path("resource").path("resourceType");
		if (resourceTypeNode instanceof TextNode) {
			TextNode textNode = (TextNode) resourceTypeNode;

			if (node.get("request") !=null) {
				if (textNode.textValue().equals("Parameters")) {
					return objectCodec.treeToValue(node, ParametersRequestEntry.class);
				} else {
					return objectCodec.treeToValue(node, ResourceRequestEntry.class);
				}
			} else if (node.get("response") !=null) {
				if (textNode.textValue().equals("Parameters")) {
					return objectCodec.treeToValue(node, ParametersResponseEntry.class); 
				}
				else if (textNode.textValue().equals("OperationOutcome")) {
					return objectCodec.treeToValue(node, OperationOutcomeEntry.class); 
				} else {
					return objectCodec.treeToValue(node, ResourceResponseEntry.class);
				}
			}
		}
		return objectCodec.treeToValue(node, RequestEntry.class);
	}
}
