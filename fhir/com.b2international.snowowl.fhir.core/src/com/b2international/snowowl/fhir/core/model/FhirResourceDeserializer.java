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

import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Polymorphic deserializer to handle different types of {@link FhirResource} implementations.
 * @see {@link FhirResource}
 * @since 8.0.0
 */
public class FhirResourceDeserializer extends StdDeserializer<FhirResource> {

	private static final long serialVersionUID = 1L;

	protected FhirResourceDeserializer() {
		super(Entry.class);
	}

	@Override
	public FhirResource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		TreeNode node = p.readValueAsTree();
		
		TreeNode resourceTypeNode = node.get("resourceType");
		if (resourceTypeNode instanceof TextNode) {
			TextNode resourceTypeTextNode = (TextNode) resourceTypeNode;
			switch (resourceTypeTextNode.textValue()) {
			
			case CodeSystem.RESOURCE_TYPE_CODE_SYSTEM:
				return p.getCodec().treeToValue(node, CodeSystem.class); 
			
			case ValueSet.RESOURCE_TYPE_VALUE_SET:
				return p.getCodec().treeToValue(node, ValueSet.class); 
			
			case ConceptMap.RESOURCE_TYPE_CONCEPT_MAP:
				return p.getCodec().treeToValue(node, ConceptMap.class); 

			case Bundle.RESOURCE_TYPE_BUNDLE:
				return p.getCodec().treeToValue(node, Bundle.class);
			
			case OperationOutcome.RESOURCE_TYPE_OPERATION_OUTCOME:
				return p.getCodec().treeToValue(node, OperationOutcome.class);
			
			default:
				break;
			}
		}
		throw new IllegalArgumentException("Unknown resource type for '" + node.toString() + "'.");
	}
}
