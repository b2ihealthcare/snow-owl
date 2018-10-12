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
package com.b2international.snowowl.fhir.core.model.property;

import java.io.IOException;

import com.b2international.commons.StringUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom serializer for concept properties returned.
 * Example: 
 * <pre>
 * [{
 *   "name": "code",
 *   "valueCode": "sufficientlyDefined"
 *  },
 *  {
 *    "name": "valueBoolean",
 *    "valueBoolean": false
 *  }]
 *  
 *  "code": "child",
 *  "valueCode": "ID123"
 *  
 * </pre>
 *   
 *	@since 6.3
 */
public class ConceptPropertySerializer extends JsonSerializer<ConceptProperty<?>> {

	private static final String VALUE_PREFIX = "value";
	private static final String CODE = "code";

	@Override
	public void serialize(ConceptProperty<?> property, JsonGenerator jGen, SerializerProvider sp) throws IOException, JsonProcessingException {
		
		String typeName = VALUE_PREFIX + StringUtils.capitalizeFirstLetter(property.getPropertyType().getCodeValue());
		jGen.writeStartObject();
		jGen.writeStringField(CODE, property.getCodeValue());
		jGen.writeObjectField(typeName, property.getValue());
		jGen.writeEndObject();
	}

}
