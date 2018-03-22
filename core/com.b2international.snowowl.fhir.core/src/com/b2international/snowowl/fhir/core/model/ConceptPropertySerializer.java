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
package com.b2international.snowowl.fhir.core.model;

import java.io.IOException;

import com.b2international.commons.StringUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * {
     "name": "code",
      "valueCode": "sufficientlyDefined"
    },
    {
      "name": "valueBoolean",
      "valueBoolean": false
    }
    
 * + "{\"name\":\"code\","
	+ "\"valueCode\":\"inactive\"},"
	+ "{\"name\":\"valueBoolean\","
	+ "\"valueBoolean\":true}";
 */
public class ConceptPropertySerializer extends JsonSerializer<ConceptProperty<?>> {

	@Override
	public void serialize(ConceptProperty<?> property, JsonGenerator jGen, SerializerProvider arg2) throws IOException, JsonProcessingException {
		
		String typeName = "value" + StringUtils.capitalizeFirstLetter(property.getPropertyType().getCodeValue());
		jGen.writeStartArray();
		jGen.writeStartObject();
		jGen.writeStringField("name", "code");
		jGen.writeStringField("valueCode", property.getCodeValue());
		jGen.writeEndObject();
		jGen.writeStartObject();
		jGen.writeStringField("name", typeName);
		jGen.writeObjectField(typeName, property.getValue());
		jGen.writeEndObject();
		jGen.writeEndArray();
	}

}
