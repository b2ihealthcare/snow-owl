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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
  "resourceType" : "Parameters",
  // from Resource: id, meta, implicitRules, and language
  "parameter" : [{ // Operation Parameter
    "name" : "<string>", // R!  Name from the definition
    // value[x]: If parameter is a data type. One of these 23:
    "valueInteger" : <integer>,
    "valueDecimal" : <decimal>,
    "valueDateTime" : "<dateTime>",
    "valueDate" : "<date>",
    "valueInstant" : "<instant>",
    "valueString" : "<string>",
    "valueUri" : "<uri>",
    "valueBoolean" : <boolean>,
    "valueCode" : "<code>",
    "valueBase64Binary" : "<base64Binary>",
    "valueCoding" : { Coding },
    "valueCodeableConcept" : { CodeableConcept },
    "valueAttachment" : { Attachment },
    "valueIdentifier" : { Identifier },
    "valueQuantity" : { Quantity },
    "valueRange" : { Range },
    "valuePeriod" : { Period },
    "valueRatio" : { Ratio },
    "valueHumanName" : { HumanName },
    "valueAddress" : { Address },
    "valueContactPoint" : { ContactPoint },
    "valueSchedule" : { Schedule },
    "valueReference" : { Reference },
    "resource" : { Resource }, // C? If parameter is a whole resource
    "part" : [{ Content as for Parameters.parameter }] // Named part of a multi-part parameter
  }]
}
 *
 */
@JsonSerialize(using = SerializableParameter.class)
@JsonDeserialize(using = ParameterDeserializer.class)
public class SerializableParameter extends JsonSerializer<SerializableParameter> {

	@JsonProperty
	private String name;
	
	@JsonProperty
	private String type;
	
	@JsonProperty
	@NotNull
	private Object value;

	//For Jackson
	@SuppressWarnings("unused")
	private SerializableParameter() {
	}

	public SerializableParameter(String name, String type, Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}
	

	@Override
	public void serialize(SerializableParameter parameter, JsonGenerator jGen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		jGen.writeStartObject();
		jGen.writeStringField("name", parameter.name);
		jGen.writeObjectField(parameter.type, parameter.value);
		jGen.writeEndObject();
	}

	@Override
	public String toString() {
		return "FhirParameter [name=" + name + ", type=" + type + ", value=" + value + "]";
	}
	
	/**
	 * Returns the type of the parameter value.
	 * For testing only.
	 * @return
	 */
	public Class getValueType() {
		return value.getClass();
	}

}
