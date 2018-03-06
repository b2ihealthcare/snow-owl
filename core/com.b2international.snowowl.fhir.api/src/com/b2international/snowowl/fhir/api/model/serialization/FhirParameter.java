/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = FhirParameter.class)
public class FhirParameter extends JsonSerializer<FhirParameter> {

	@JsonProperty
	private String name;
	
	@JsonProperty
	private String type;
	
	@JsonProperty
	private Object value;

	//For Jackson
	private FhirParameter() {
	}

	public FhirParameter(String name, String type, Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public void serialize(FhirParameter parameter, JsonGenerator jGen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		jGen.writeStartObject();
		jGen.writeStringField("name", parameter.name);
		jGen.writeObjectField(parameter.type, parameter.value);
		jGen.writeEndObject();

	}
}
