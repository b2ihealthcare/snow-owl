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

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * {
  "resourceType" : "Parameters",
  "parameter" : [
    {
    "name" : "name",
    "valueString" : "LOINC"
  },
  {
    "name" : "version",
    "valueString" : "2.48"
  },
  {
    "name" : "designation",
    "valueString" : "Bicarbonate [Moles/volume] in Serum"
  },
  {
    "name" : "abstract",
    "valueString" : "false"
  },
  {
      "name" : "designation",
    "part" : [
    {
      "name" : "value",
      "valueString" : "Bicarbonate [Moles/volume] in Serum "
    }
    ]
  }
  ]
 *
 */
@JsonInclude(Include.NON_EMPTY) //covers nulls as well
public class SerializableLookupResult {
	
	@JsonProperty(value="parameter")
	private List<SerializableParameter> parameters = Lists.newArrayList();
	
	//header "resourceType" : "Parameters",
	@JsonProperty
	private String resourceType = "Parameters";

	public void add(SerializableParameter parameter) {
		parameters.add(parameter);
	}

	public void addAll(Collection<SerializableParameter> fhirParameters) {
		this.parameters.addAll(fhirParameters);
	}

	public Collection<SerializableParameter> getParameters() {
		return parameters;
	}
}
