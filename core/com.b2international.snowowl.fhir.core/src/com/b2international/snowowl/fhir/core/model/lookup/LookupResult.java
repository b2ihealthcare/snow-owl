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
package com.b2international.snowowl.fhir.core.model.lookup;

import java.util.Collection;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.FhirType;
import com.b2international.snowowl.fhir.core.model.dt.Property;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableList;

/**
 * Model object for the lookup service request response
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem-operations.html#lookup">FHIR:CodeSystem:Operations:lookup</a>
 * @since 6.4
 */
@JsonPropertyOrder({"name", "version", "display", "designation", "property"})
public final class LookupResult {
	
	//A display name for the code system (1..1)
	@NotEmpty
	private final String name;
	
	//The version that these details are based on (0..1)
	private final String version;
	
	//The preferred display for this concept (1..1)
	private final String display;
	
	//Additional representations for this concept (0..*)
	@FhirType(FhirDataType.PART)
	private final Collection<Designation> designation;   
	
	/*
	 * One or more properties that contain additional information about the code, 
	 * including status. For complex terminologies (e.g. SNOMED CT, LOINC, medications), these properties serve to decompose the code
	 * 0..*
	 */
	@FhirType(FhirDataType.PART)
	private final Collection<Property> property;
	
	private LookupResult(final String name, 
			final String version, 
			final String display, 
			final Collection<Designation> designation,
			final Collection<Property> property) {
		
		this.name = name;
		this.version = version;
		this.display = display;
		this.designation = designation;
		this.property = property;
	}

	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public Collection<Designation> getDesignation() {
		return designation;
	}
	
	public Collection<Property> getProperty() {
		return property;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		
		private String name;
		private String version;
		private String display;
		
		private final ImmutableList.Builder<Designation> designations = ImmutableList.builder();
		private final ImmutableList.Builder<Property> properties = ImmutableList.builder();

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
			return new LookupResult(name, version, display, designations.build(), properties.build());
		}
	}
	
}
