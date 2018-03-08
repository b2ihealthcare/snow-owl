/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.model.serialization;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import com.b2international.snowowl.fhir.api.model.dt.Coding;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.collect.Lists;

/**
 * Jackson custom deserializer for FHIR parameters.
 * 
 * @since 6.3
 */
public class ParameterDeserializer extends JsonDeserializer<SerializableParameter> {

	@Override
	public SerializableParameter deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {

		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);
		
		SerializableParameter parameter = parseNode(node, oc);
		return parameter;
	}

	private SerializableParameter parseNode(JsonNode node, ObjectCodec oc) throws JsonProcessingException {

		String type = null;
		Object value = null;
		
		Iterator<String> fieldNames = node.fieldNames();
		
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			
			JsonNode valueNode = node.get(fieldName);
			
			System.out.println("Json node: " + valueNode.getNodeType());
			
			//simple fields - everything is a string "" really
			if (valueNode.getNodeType() == JsonNodeType.STRING) {
				type = fieldName;
				value = getTypedValue(fieldName, valueNode);
				
			//embedded params
			} else if (valueNode.getNodeType() == JsonNodeType.OBJECT) {
				
				System.out.println(valueNode);
				type = fieldName;
				value = oc.treeToValue(valueNode, Coding.class);
			
				//embedded collection parameters
			} else if (valueNode.getNodeType() == JsonNodeType.ARRAY) {
				Collection<SerializableParameter> embeddedParameters = Lists.newArrayList();
				
				Iterator<JsonNode> elements = valueNode.elements();
				while (elements.hasNext()) {
					JsonNode childNode = elements.next();
					embeddedParameters.add(parseNode(childNode, oc));
				}
				value = embeddedParameters;
				type = "part";
			}
		}
		final String name = node.get("name").asText();
		return new SerializableParameter(name, type, value);
	}

	/**
	 * @param typeString
	 * @param valueNode
	 * @return
	 */
	private Object getTypedValue(String fieldName, JsonNode valueNode) {
		Object value = null;
		
		String typeString = fieldName.replaceAll("value", "");
		
		if (typeString.equals("Boolean")) {
			value = valueNode.asBoolean();
		} else {
			value = valueNode.asText();
		}
		return value;
	}

}