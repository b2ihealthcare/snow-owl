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

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.annotation.Order;

public class SubProperty extends FhirModel {
	
	//Identifies the property returned (1..1)
	@Order(value=1)
	@FhirDataType(type = FhirType.CODE)
	@NotEmpty
	private String code;
	
	/*
	 * The value of the property returned (0..1)
	 * code | Coding | string | integer | boolean | dateTime
	 */
	@Order(value=2)
	@NotNull //only subproperty value is 1..1, property.value is 0..1 (?)
	@FhirDataType(type = FhirType.OBJECT)
	private Object value;
	
	//Human Readable representation of the property value (e.g. display for a code) 0..1
	@Order(value=3)
	private String description;
	
	SubProperty(final String code, final Object value, final String description) {
		this.code = code;
		this.value = value;
		this.description = description;
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
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ModelValidator<SubProperty> {
		
		private String code;
		private Object value;
		private String description;

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
		
		protected SubProperty doBuild() {
			return new SubProperty(code, value, description);
		}
	}

}
