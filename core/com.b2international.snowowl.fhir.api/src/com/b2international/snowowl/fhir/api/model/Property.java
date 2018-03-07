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
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.springframework.core.annotation.Order;

import com.b2international.snowowl.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.google.common.collect.Lists;

public class Property extends FhirModel {
	
	//Identifies the property returned (1..1)
	@Order(value=1)
	@FhirDataType(type = FhirType.CODE)
	private String code;
	
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
	private Collection<Property> subProperties = Lists.newArrayList();
	
	Property(final String code, final Object value, final String description, Collection<Property> subproperties) {
		this.code = code;
		this.value = value;
		this.description = description;
		this.subProperties = subproperties;
	}
	
	public String getCode() {
		return code;
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
	
	public Collection<Property> getSubProperties() {
		return subProperties;
	}
	
	@Override
	protected Collection<SerializableParameter> getCollectionParameters(Object value) throws Exception {
		
		Collection<SerializableParameter> collectionParameters = Lists.newArrayList();

		@SuppressWarnings("rawtypes")
		Collection values = (Collection) value;
		
		for (Object object : values) {
			if (object instanceof Property) {
				Collection<SerializableParameter> propertyParams = ((Property) object).toParameters();
				SerializableParameter fhirParam = new SerializableParameter("subproperty", "part", propertyParams);
				collectionParameters.add(fhirParam);
			}
		}
		return collectionParameters;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		@NotNull
		private String code;
		
		@NotNull
		private Object value;
		private String description;
		private Collection<Property> subProperties = Lists.newArrayList();

		public Builder code(final String code) {
			this.code = code;
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
		
		public Builder addSubProperty(final Property subProperty) {
			subProperties.add(subProperty);
			return this;
		}
		
		public Property build() {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			Set<ConstraintViolation<Property.Builder>> violations = validator.validate(this);
			if (!violations.isEmpty()) {
				//bit of a hack
				ValidationException validationException = new ValidationException(violations);
				Map<String, Object> additionalInfo = validationException.toApiError().getAdditionalInfo();
				throw new javax.validation.ValidationException(additionalInfo.toString());
			}
			return new Property(code, value, description, subProperties);
		}
	}

}
