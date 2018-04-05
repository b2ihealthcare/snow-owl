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

import java.util.Collection;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableList;

/**
 * FHIR Property representation.
 * 
 * @since 6.4
 */
@JsonPropertyOrder({"code", "value", "description", "subProperty"})
public final class Property extends FhirProperty {
	
	//Identifies the property returned (1..1)
	@NotEmpty
	private final String code;
	
	//Human Readable representation of the property value (e.g. display for a code) 0..1
	private final String description;
	
	@FhirType(FhirDataType.PART)
	private final Collection<SubProperty> subProperty;
	
	Property(final FhirDataType type, final Object value, final String code, final String description, final Collection<SubProperty> subproperty) {
		super(type, value);
		this.code = code;
		this.description = description;
		this.subProperty = subproperty;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Collection<SubProperty> getSubProperty() {
		return subProperty;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	/**
	 * @since 6.4
	 */
	public static final class Builder extends FhirProperty.Builder<Property, Builder> {
		
		private String code;
		private String description;
		private ImmutableList.Builder<SubProperty> subProperty = ImmutableList.builder();

		Builder() {}
		
		public Builder code(final String code) {
			this.code = code;
			return this;
		}
		
		public Builder description(final String description) {
			this.description = description;
			return this;
		}
		
		public Builder subProperty(final SubProperty subProperty) {
			this.subProperty.add(subProperty);
			return this;
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		@Override
		protected Property doBuild() {
			return new Property(type(), value(), code, description, subProperty.build());
		}
		
	}

}
