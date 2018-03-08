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
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @since 6.3
 */
public class ParameterDeserializer extends JsonDeserializer<SerializableParameter> {

	@Override
	public SerializableParameter deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {

		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);
		
		Iterator<String> fieldNames = node.fieldNames();
		String type = null;
		Object value = null;
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			if (fieldName.startsWith("value")) {
				String typeString = fieldName.replaceAll("value", "");
				JsonNode valueNode = node.get(fieldName);
				
				if (typeString.equals("Boolean")) {
					value = valueNode.asBoolean();
				} else {
					value = valueNode.asText();
				}
				type = fieldName;
			}
		}
		final String name = node.get("name").asText();
		SerializableParameter parameter = new SerializableParameter(name, type, value);
		return parameter;
	}

}