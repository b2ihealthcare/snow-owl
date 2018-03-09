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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

import com.b2international.snowowl.fhir.api.model.dt.Code;
import com.b2international.snowowl.fhir.api.model.dt.Coding;
import com.b2international.snowowl.fhir.api.model.dt.DateFormats;
import com.b2international.snowowl.fhir.api.model.dt.FhirType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.collect.Lists;

/**
 * Custom Jackson deserializer for FHIR parameters.
 * 
 * @since 6.3
 */
public class ParameterDeserializer extends JsonDeserializer<SerializableParameter> {

	private static final String NAME_TAG = "name";
	private static final String VALUE_PREFIX = "value"; //$NON-NLS-N$
	private static final String COLLECTION_PART = "part"; //$NON-NLS-N$

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
			
			if (valueNode.getNodeType() == JsonNodeType.ARRAY) {
				Collection<SerializableParameter> embeddedParameters = Lists.newArrayList();
				
				Iterator<JsonNode> elements = valueNode.elements();
				while (elements.hasNext()) {
					JsonNode childNode = elements.next();
					embeddedParameters.add(parseNode(childNode, oc));
				}
				value = embeddedParameters;
				type = COLLECTION_PART;
			} else { //simple types for value definition
				if (fieldName.startsWith(VALUE_PREFIX)) {
					type = fieldName;
					value = getTypedValue(fieldName, valueNode, oc);
				}
			}
		}
			
		final String name = node.get(NAME_TAG).asText();
		return new SerializableParameter(name, type, value);
	}

	/**
	 * TODO: can we really rely on the type information? 
	 * @param typeString
	 * @param valueNode
	 * @param oc 
	 * @return
	 * @throws JsonProcessingException 
	 * @throws ParseException 
	 */
	private Object getTypedValue(String fieldName, JsonNode valueNode, ObjectCodec oc) throws JsonProcessingException {
		Object value = null;
		
		String typeString = fieldName.replaceAll(VALUE_PREFIX, "");
		
		FhirType fhirType = FhirType.fhirTypeOf(typeString);
		
		switch (fhirType) {
		case BOOLEAN:
			value = valueNode.asBoolean();
			break;
		case INTEGER:
			value = valueNode.asInt();
			break;
		case DECIMAL:
			value = valueNode.asDouble();
			break;
		case STRING:
			value = valueNode.asText();
			break;
		case DATETIME:
			String dateText = valueNode.asText();
			SimpleDateFormat sdf = new SimpleDateFormat(DateFormats.DATE_TIME_FORMAT);
			try {
				value = sdf.parse(dateText);
			} catch (ParseException e) {
				throw new IllegalArgumentException("Could not parse date string '"+ dateText + "'.", e);
			}
			break;
		case CODE:
			value = new Code(valueNode.asText());
			break;
		case CODING:
			value = oc.treeToValue(valueNode, Coding.class);
			break;
		default:
			value = valueNode.asText();
			break;
		}
		return value;
	}

}