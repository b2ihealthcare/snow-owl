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
package com.b2international.snowowl.fhir.api.model;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.annotation.Order;

import com.b2international.snowowl.fhir.api.model.dt.Code;
import com.b2international.snowowl.fhir.api.model.dt.Coding;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

public class LookupRequest {

	// The code that is to be located. If a code is provided, a system must be provided (0..1)
	@Order(value = 1)
	@NotEmpty
	private Code code;

	// The system for the code that is to be located (0..1)
	@Order(value = 2)
	@Uri
	private String system;
	
	// The version that these details are based on (0..1)
	@Order(value = 3)
	private String version;

	// The cooding to look up (0..1)
	@Order(value = 4)
	private Coding coding;
	
	/*
	 * The date for which the information should be returned. Normally, this is the
	 * current conditions (which is the default value) but under some circumstances,
	 * systems need to acccess this information as it would have been in the past. A
	 * typical example of this would be where code selection is constrained to the
	 * set of codes that were available when the patient was treated, not when the
	 * record is being edited. Note that which date is appropriate is a matter for
	 * implementation policy.
	 */
	@Order(value = 5)
	private Date date;
	
	//The requested language for display (see ExpansionProfile.displayLanguage)
	@Order(value = 6)
	private Code displayLanguage;
	
	/*
	 * A property that the client wishes to be returned in the output. If no
	 * properties are specified, the server chooses what to return. The following
	 * properties are defined for all code systems: url, name, version (code system
	 * info) and code information: display, definition, designation, parent and
	 * child, and for designations, lang.X where X is a designation language code.
	 * Some of the properties are returned explicit in named parameters (when the
	 * names match), and the rest (except for lang.X) in the property parameter
	 * group
	 */
	@Order(value = 7)
	private Collection<Property> properties = Lists.newArrayList();
	
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

	public void toModelObject() throws IllegalArgumentException, IllegalAccessException {
		
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
					//f.setAccessible(true);
					return f.getName().equals(fieldName);
				})
				.findFirst();
			
			fieldOptional.orElseThrow(() -> new NullPointerException("Could not find field '" + fieldName + "'."));
			Field field = fieldOptional.get();
			field.set(this, serializableParameter.getValue());
		}
	}

	@Override
	public String toString() {
		return "LookupRequest [code=" + code + ", system=" + system + ", version=" + version + ", coding=" + coding
				+ ", date=" + date + ", displayLanguage=" + displayLanguage + ", properties=" + Arrays.toString(properties.toArray()) + "]";
	}

}
