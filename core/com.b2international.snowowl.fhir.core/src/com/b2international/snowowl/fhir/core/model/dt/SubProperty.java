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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @since 6.4
 */
@JsonPropertyOrder({"code", "value", "description"})
public final class SubProperty extends FhirProperty {
	
	//Identifies the property returned (1..1)
	@NotNull
	private String code;
	
	//Human Readable representation of the property value (e.g. display for a code) 0..1
	private String description;
	
	SubProperty(FhirDataType type, Object value, final String code, final String description) {
		super(type, value);
		this.code = code;
		this.description = description;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder extends FhirProperty.Builder<SubProperty, Builder> {
		
		private String code;
		private String description;

		public Builder code(final String code) {
			this.code = code;
			return this;
		}
		
		public Builder description(final String description) {
			this.description = description;
			return this;
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		@Override
		protected SubProperty doBuild() {
			return new SubProperty(type(), value(), code, description);
		}
	}

}
