/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.dt;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

import io.swagger.annotations.ApiModel;

/**
 * 
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
 * @since 6.3
 */
@ApiModel("Parameter")
@JsonDeserialize(builder=Parameter.Builder.class)
public final class Parameter extends FhirProperty {

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder extends FhirProperty.Builder<Parameter, Parameter.Builder> {
		
		private String name;
		
		Builder() {}
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder part(List<Parameter> parameters) {
			return setValue(FhirDataType.PART, new Parameters(parameters));
		}
		
		@Override
		protected Parameter doBuild() {
			return new Parameter(name, type(), value());
		}
		
	}
	
	private final String name;
	
	Parameter(String name, FhirDataType type, Object value) {
		super(type, value);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public static Parameter valueString(String name, String value) {
		return new Builder()
				.name(name)
				.valueString(value)
				.build();
	}
	
	public static Parameter valueBoolean(String name, Boolean value) {
		return new Builder()
				.name(name)
				.valueBoolean(value)
				.build();
	}
	
	public static Parameter valueUri(String name, String uri) {
		return new Builder()
				.name(name)
				.valueUri(uri)
				.build();
	}
	
	public static Parameter valueCode(String name, String code) {
		return new Builder()
				.name(name)
				.valueCode(code)
				.build();
	}
	
	public static Parameter part(String name, Parameter...parts) {
		return new Builder()
				.name(name)
				.part(ImmutableList.copyOf(parts))
				.build();
	}

	@Override
	public String toString() {
		return "Parameter [name=" + name + " value=" + getValue() + "]";
	}
	
}
