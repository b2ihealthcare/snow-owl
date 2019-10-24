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

import com.b2international.snowowl.fhir.core.codesystems.FilterOperator;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.codesystem.Filters.FilterPropertyCode;
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
	private final Code property;
	
	@Valid
	@NotNull
	@JsonProperty("op")
	private final Code operator;
	
	@Valid
	@NotNull
	@JsonProperty 
	private final Code value;
	
	ValueSetFilter(Code property, Code operator, Code value) {
		this.property = property;
		this.operator = operator;
		this.value = value;
	}
	
	public Code getProperty() {
		return property;
	}
	
	public Code getOperator() {
		return operator;
	}
	
	public Code getValue() {
		return value;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ValidatingBuilder<ValueSetFilter> {
		
		private Code property;
		private Code operator;
		private Code value;
		
		public Builder property(final String property) {
			this.property = new Code(property);
			return this;
		}
		
		public Builder operator(final FilterOperator operator) {
			this.operator = operator.getCode();
			return this;
		}
		
		public Builder value(final String value) {
			this.value = new Code(value);
			return this;
		}
		
		/**
		 * Creates a reference set expression filter (expression = ^ + refsetConceptId) 
		 * @param id
		 * @return
		 */
		public Builder refsetExpression(String id) {
			property = new Code(FilterPropertyCode.EXPRESSION.getDisplayName());
			operator = FilterOperator.EQUALS.getCode();
			value = new Code("^"+ id);
			return this;
		}
		
		/**
		 * Creates an ECL expression filter (expression = eclExpression) 
		 * @param eclExpression
		 * @return
		 */
		public Builder eclExpression(String eclExpression) {
			property = new Code(FilterPropertyCode.EXPRESSION.getDisplayName());
			operator = FilterOperator.EQUALS.getCode();
			value = new Code(eclExpression);
			return this;
		}

		@Override
		protected ValueSetFilter doBuild() {
			return new ValueSetFilter(property, operator, value);
		}

	}

}
