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

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.core.annotation.Order;

import com.b2international.snowowl.fhir.api.model.dt.Code;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.google.common.collect.Lists;

/**
 * http://hl7.org/fhir/concept-properties#status	A property that indicates the status of the concept. If the property is identified by this URL, then it SHALL use at least these status values (where appropriate):
 * active - the concept is for normal use (this is the default value)
 * experimental - provided for trial, but may be removed in the future
 * deprecated - planned to be removed from use
 * retired - still present for historical reasons, but no longer allowed to be used
 * http://hl7.org/fhir/concept-properties#retirementDate	Date Concept was retired
 * http://hl7.org/fhir/concept-properties#deprecationDate	Date Concept was deprecated
 * http://hl7.org/fhir/concept-properties#parent	An immediate parent of the concept in the hierarchy
 * http://hl7.org/fhir/concept-properties#child	An immediate child of the concept in the hierarchy
 * http://hl7.org/fhir/concept-properties#notSelectable	This concept is a grouping concept and not intended to be used in the normal use of the code system (though my be used for filters etc). This is also known as 'Abstract'
 *
 */
public class Property extends SerializableParameters {
	
	//Identifies the property returned (1..1)
	@Order(value=1)
	@Valid
	@NotNull
	private Code code;
	
	/*
	 * The value of the property returned (0..1)
	 * code | Coding | string | integer | boolean | dateTime
	 */
	@Order(value=2)
	private Object value;
	
	//Human Readable representation of the property value (e.g. display for a code) 0..1
	@Order(value=3)
	private String description;
	
	@Order(value=4)
	private Collection<SubProperty> subProperties = Lists.newArrayList();
	
	Property(final Code code, final Object value, final String description, Collection<SubProperty> subproperties) {
		this.code = code;
		this.value = value;
		this.description = description;
		this.subProperties = subproperties;
	}
	
	public Code getCode() {
		return code;
	}
	
	public String getCodeValue() {
		return code.getCodeValue();
	}

	/**
	 * How are we going to get the proper type serialized?
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}
	
	public Collection<SubProperty> getSubProperties() {
		return subProperties;
	}
	
	@Override
	protected Collection<SerializableParameter> getCollectionParameters(Object value) throws Exception {
		
		Collection<SerializableParameter> collectionParameters = Lists.newArrayList();

		@SuppressWarnings("rawtypes")
		Collection values = (Collection) value;
		
		for (Object object : values) {
			if (object instanceof SubProperty) {
				Collection<SerializableParameter> propertyParams = ((SubProperty) object).toParameters();
				SerializableParameter fhirParam = new SerializableParameter("subproperty", "part", propertyParams);
				collectionParameters.add(fhirParam);
			}
		}
		return collectionParameters;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<Property> {
		
		private Code code;
		private Object value;
		private String description;
		private Collection<SubProperty> subProperties = Lists.newArrayList();

		public Builder code(final String code) {
			this.code = new Code(code);
			return this;
		}
		
		public Builder value(final Object value) {
			this.value = value;
			return this;
		}

		public Builder description(final String description) {
			this.description = description;
			return this;
		}
		
		public Builder addSubProperty(final SubProperty subProperty) {
			subProperties.add(subProperty);
			return this;
		}
		
		@Override
		protected Property doBuild() {
			return new Property(code, value, description, subProperties);
		}
	}

}
