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
package com.b2international.snowowl.fhir.core.model;

import java.util.Collection;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.conversion.LookupResultConverter;
import com.b2international.snowowl.fhir.core.model.conversion.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;

/**
 * Model object for the lookup service request response
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#lookup">FHIR:CodeSystem:Operations:lookup</a>
 * @since 6.3
 */
@JsonSerialize(converter=LookupResultConverter.class)
@JsonInclude(Include.NON_EMPTY) //covers nulls as well
public class LookupResult extends ParametersModel {
	
	//A display name for the code system (1..1)
	@Order(value=1)
	@NotEmpty
	private final String name;
	
	//The version that these details are based on (0..1)
	@Order(value=2)
	private final String version;
	
	//The preferred display for this concept (1..1)
	@Order(value=3)
	private final String display;
	
	//Additional representations for this concept (0..*)
	@Order(value=4)
	private final Collection<Designation> designations;   
	
	/*
	 * One or more properties that contain additional information about the code, 
	 * including status. For complex terminologies (e.g. SNOMED CT, LOINC, medications), these properties serve to decompose the code
	 * 0..*
	 */
	@Order(value=5)
	private final Collection<Property> properties;
	
	private LookupResult(final String name, 
			final String version, 
			final String display, 
			Collection<Designation> designations,
			Collection<Property> properties) {
		
		this.name = name;
		this.version = version;
		this.display = display;
		this.designations = designations;
		this.properties = properties;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name;
		private String version;
		private String display;
		
		private Collection<Designation> designations = Lists.newArrayList();
		
		private Collection<Property> properties = Lists.newArrayList();;

		public Builder name(final String name) {
			this.name = name;
			return this;
		}
		
		public Builder version(String version) {
			this.version = version;
			return this;
		}
		
		public Builder display(String display) {
			this.display = display;
			return this;
		}
		
		public Builder value(String display) {
			this.display = display;
			return this;
		}
		
		public Builder addDesignation(Designation designation) {
			designations.add(designation);
			return this;
		}

		public Builder addProperty(Property property) {
			properties.add(property);
			return this;
		}
		public LookupResult build() {
			return new LookupResult(name, version, display, designations, properties);
		}
	}
	
}
