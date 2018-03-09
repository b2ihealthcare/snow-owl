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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.b2international.snowowl.fhir.api.model.LookupRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * The de-serializable representation of the lookup response.
 * 
 * @since 6.3
 */
public class DeserializableLookupRequest {
	
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

	public LookupRequest toModelObject() throws IllegalArgumentException, IllegalAccessException {
		
		LookupRequest request = new LookupRequest();
		
		for (SerializableParameter serializableParameter : parameters) {
			System.out.println(serializableParameter);
			
			String fieldName = serializableParameter.getName();
			System.out.println("Name: " + fieldName + " : " + serializableParameter.getValue());
			
			Field[] fields = LookupRequest.class.getDeclaredFields();
			for (Field field : fields) {
				System.out.println("Field" + field);
			}
			
			Optional<Field> fieldOptional = Arrays.stream(fields)
				.filter(f -> {
					f.setAccessible(true);
					return f.getName().equals(fieldName);
				})
				.findFirst();
			
			fieldOptional.orElseThrow(() -> new NullPointerException("Could not find field '" + fieldName + "'."));
			Field field = fieldOptional.get();
			field.set(request, serializableParameter.getValue());
			
		}
		return request;
	}

}
