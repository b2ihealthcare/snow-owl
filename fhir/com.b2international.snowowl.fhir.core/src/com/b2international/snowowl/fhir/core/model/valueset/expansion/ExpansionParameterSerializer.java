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
package com.b2international.snowowl.fhir.core.model.valueset.expansion;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 
 * Custom serializer for value set expansion parameters
 * Example:
 * 
 * <pre>
 * [{
 *   "name": "code",
 *   "valueCode": "sufficientlyDefined"
 *  },
 *  {
 *    "name": "status",
 *    "valueBoolean": false
 *  }]
 * </pre>
 * 
 * @since 6.7
 */
public class ExpansionParameterSerializer extends JsonSerializer<Parameter<?>> {

	@Override
	public void serialize(Parameter<?> parameter, JsonGenerator jGen, SerializerProvider sp) throws IOException, JsonProcessingException {
		
		jGen.writeStartObject();
		jGen.writeStringField("name", parameter.getName());
		jGen.writeObjectField(parameter.getType().getSerializedName(), parameter.getValue());
		jGen.writeEndObject();
	}

}