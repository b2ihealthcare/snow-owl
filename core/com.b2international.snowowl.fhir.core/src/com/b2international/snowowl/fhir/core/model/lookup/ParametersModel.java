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
package com.b2international.snowowl.fhir.core.model.lookup;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.fhir.core.model.conversion.SerializableParametersConverter;
import com.b2international.snowowl.fhir.core.model.serialization.SerializableParameter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since 6.4
 */
@JsonDeserialize(converter=SerializableParametersConverter.class)
public abstract class ParametersModel {
	
	//the serializable format
	
	//header "resourceType" : "Parameters",
	@JsonProperty
	private final String resourceType = "Parameters";
		
	@JsonProperty(value="parameter")
	private List<SerializableParameter> parameters = Collections.emptyList();
	
	/**
	 * @param parameters
	 */
	public void setParameters(List<SerializableParameter> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * @return the parameters
	 */
	public List<SerializableParameter> getParameters() {
		return parameters;
	}
	
}
