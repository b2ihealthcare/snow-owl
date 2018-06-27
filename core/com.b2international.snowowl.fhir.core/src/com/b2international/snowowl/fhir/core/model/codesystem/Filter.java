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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.codesystems.FilterOperator;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * FHIR Code system filter backbone definition.
 * @since 6.4
 */
public class Filter {
	
	/**
	 * Generic is-a and value set containment filters
	 * @see <a href="https://www.hl7.org/fhir/codesystem-snomedct.json.html">IS_A an IN filter</a>
	 */
	@JsonIgnore
	public static final Filter IS_A_FILTER = Filter.builder()
		.code("concept")
		.description("Filter that includes concepts based on their logical definition. e.g. [concept] [is-a] [x] - include all concepts with an is-a relationship to concept x,"
				+ " or [concept] [in] [x]- include all concepts in the reference set identified by concept x")
		.addOperator(FilterOperator.IS_A)
		.addOperator(FilterOperator.IN)
		.value("code system code")
		.build();
	
	/**
	 * SNOMED CT filter
	 * @see <a href="https://www.hl7.org/fhir/codesystem-snomedct.json.html">SNOMED CT ECL filter</a>
	 */
	@JsonIgnore
	public static final Filter EXPRESSION_FILTER = Filter.builder()
		.code("expression")
		.description("Filter result of the given SNOMED CT Expression Constraint")
		.addOperator(FilterOperator.EQUALS)
		.value("SNOMED CT ECL Expression (http://snomed.org/ecl")
		.build();
	
	/**
	 * SNOMED CT filter
	 * @see <a href="https://www.hl7.org/fhir/codesystem-snomedct.json.html">SNOMED CT post cordinated expressions filter</a>
	 */
	@JsonIgnore
	public static final Filter EXPRESSIONS_FILTER = Filter.builder()
		.code("expressions")
		.description("Whether post-coordinated expressions are included in the value set")
		.addOperator(FilterOperator.EQUALS)
		.value("true or false")
		.build();
	
	@Valid
	@NotNull
	@JsonProperty
	private final Code code;
	
	@JsonProperty
	private final String description;
	
	@Valid
	@NotEmpty
	@JsonProperty("operator")
	private final Collection<Code> operators;
	
	@Valid
	@NotNull
	@JsonProperty 
	private final String value;
	
	Filter(Code code, String description, Collection<Code> operators, String value) {
		this.code = code;
		this.description = description;
		this.operators = operators;
		this.value = value;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ValidatingBuilder<Filter> {
		
		private Code code;
		
		private String description;
		
		private Collection<Code> operators = Lists.newArrayList();
		
		private String value;
		
		public Builder code(final String codeValue) {
			this.code = new Code(codeValue);
			return this;
		}

		public Builder description(final String description) {
			this.description = description;
			return this;
		}
		
		public Builder addOperator(final FilterOperator filterOperator) {
			this.operators.add(filterOperator.getCode());
			return this;
		}
		
		public Builder value(final String value) {
			this.value = value;
			return this;
		}

		@Override
		protected Filter doBuild() {
			return new Filter(code, description, operators, value);
		}
		
	}

}
