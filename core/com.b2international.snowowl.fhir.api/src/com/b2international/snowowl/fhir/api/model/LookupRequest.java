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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.annotation.Order;

import com.b2international.snowowl.fhir.api.model.dt.Code;
import com.b2international.snowowl.fhir.api.model.dt.Coding;
import com.b2international.snowowl.fhir.api.model.dt.DateFormats;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModel;

/**
 * This class represents a FHIR lookup operation request.
 * This domain model class is capable of building itself from a collection of
 * serialized parameters. See {@link #toModelObject()}.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#lookup">FHIR:CodeSystem:Operations:lookup</a>
 * @since 6.3
 */
@ApiModel
@JsonDeserialize(converter=LookupRequest.class)
public class LookupRequest extends StdConverter<LookupRequest,LookupRequest> {
	
	//FHIR header "resourceType" : "Parameters",
	@JsonProperty
	private String resourceType = "Parameters";
	
	@JsonProperty(value="parameter")
	private List<SerializableParameter> parameters = Lists.newArrayList();
	
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

	// The coding to look up (0..1)
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
	private Collection<Code> properties = Lists.newArrayList();
	
	//For Jackson
	@SuppressWarnings("unused")
	private LookupRequest() {}
	
	public LookupRequest(String code, String system, String version, String dateString, String displayLanguage,
			Collection<String> properties) throws ParseException {
		
		this.code = new Code(code);
		this.system = system;
		this.version = version;
		this.date = new SimpleDateFormat(DateFormats.DATE_TIME_FORMAT).parse(dateString);
		this.displayLanguage = new Code(displayLanguage);
		this.properties = properties.stream().map(p-> new Code(p)).collect(Collectors.toSet());;
	}

	public Code getCode() {
		return code;
	}

	public String getSystem() {
		return system;
	}

	public String getVersion() {
		return version;
	}

	public Coding getCoding() {
		return coding;
	}

	public Date getDate() {
		return date;
	}

	public Code getDisplayLanguage() {
		return displayLanguage;
	}

	public Collection<Code> getProperties() {
		return properties;
	}

	//for testing only
	public Collection<SerializableParameter> getParameters() {
		return parameters;
	}


	/**
	 * Converts the set of parameters into this populated domain object.
	 * This method is called right after the deserialization.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Override
	public LookupRequest convert(LookupRequest lookupRequest) {
		try {
			for (SerializableParameter serializableParameter : lookupRequest.parameters) {
				
				String fieldName = serializableParameter.getName();
				
				Field[] fields = LookupRequest.class.getDeclaredFields();
				Optional<Field> fieldOptional = Arrays.stream(fields)
					.filter(f -> f.getName().equals(fieldName))
					.findFirst();
				
				fieldOptional.orElseThrow(() -> new NullPointerException("Could not find field '" + fieldName + "'."));
				Field field = fieldOptional.get();
				field.set(lookupRequest, serializableParameter.getValue());
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalArgumentException("Error when converting lookup request." + e);
		}
		return lookupRequest;
	}

	@Override
	public String toString() {
		return "LookupRequest [code=" + code + ", system=" + system + ", version=" + version + ", coding=" + coding
				+ ", date=" + date + ", displayLanguage=" + displayLanguage + ", properties=" + Arrays.toString(properties.toArray()) + "]";
	}
}
