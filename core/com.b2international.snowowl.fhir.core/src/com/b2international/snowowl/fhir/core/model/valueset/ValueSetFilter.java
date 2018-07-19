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
package com.b2international.snowowl.fhir.core.model.valueset;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Value set filter domain object
 * @since 6.7
 */
public class ValueSetFilter {
	
	@Valid
	@NotNull
	@JsonProperty
	private final Code code;
	
	@Valid
	@NotEmpty
	@JsonProperty("op")
	private final Code operator;
	
	@Valid
	@NotEmpty
	@JsonProperty 
	private final Code value;
	
	ValueSetFilter(Code code, Code operator, Code value) {
		this.code = code;
		this.operator = operator;
		this.value = value;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ValidatingBuilder<ValueSetFilter> {
		
		private Code code;
		private Code operator;
		private Code value;
		
		public Builder code(final String codeValue) {
			this.code = new Code(codeValue);
			return this;
		}
		
		public Builder operator(final String operator) {
			this.operator = new Code(operator);
			return this;
		}
		
		public Builder value(final String value) {
			this.value = new Code(value);
			return this;
		}

		@Override
		protected ValueSetFilter doBuild() {
			return new ValueSetFilter(code, operator, value);
		}
		
	}

}
