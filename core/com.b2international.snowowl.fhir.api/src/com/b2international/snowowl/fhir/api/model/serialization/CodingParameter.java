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

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.api.model.dt.Coding;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CodingParameter extends TypedSerializableParameter<Coding> {

	@JsonProperty
	@NotNull
	private Coding value;
	
	public CodingParameter(String name, String type, Coding value) {
		super(name, type);
		this.value = value;
	}
	
	@Override
	public Coding getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "FhirParameter [name=" + getName() + ", type=" + getType() + ", value=" + getValue() + "]";
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
