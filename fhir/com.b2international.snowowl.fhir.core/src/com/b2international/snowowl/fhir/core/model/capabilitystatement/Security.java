/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.capabilitystatement;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.codesystems.RestfulSecurityService;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;

/**
 * FHIR Capability statement Security backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Security.Builder.class)
public class Security {
	
	@Summary
	@JsonProperty("cors")
	private final Boolean isCors;
	
	@Summary
	@JsonProperty("service")
	private final Collection<CodeableConcept> services;
	
	@Summary
	@JsonProperty
	private final String description;
	
	Security(final Boolean isCors, final Collection<CodeableConcept> services, final String description) {
		this.isCors = isCors;
		this.services = services;
		this.description = description;
	}
	
	public Boolean getIsCors() {
		return isCors;
	}
	
	public Collection<CodeableConcept> getServices() {
		return services;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Security> {
		
		private Boolean isCors;
		private Collection<CodeableConcept> services;
		private String description;
		
		public Builder cors(final Boolean isCors) {
			this.isCors = isCors;
			return this;
		}
		
		@JsonProperty("service")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder services(Collection<CodeableConcept> services) {
			this.services = services;
			return this;
		}
		
		@JsonProperty
		public Builder addService(final CodeableConcept service) {
			
			if (services == null) {
				services = Lists.newArrayList();
			}
			
			services.add(service);
			return this;
		}
		
		@JsonIgnore
		public Builder addService(RestfulSecurityService service, String version, String text) {
			
			CodeableConcept serviceConcept = CodeableConcept.builder()
					.addCoding(Coding.builder()
							.code(service.getCode())
							.display(service.getDisplayName())
							.system(service.getCodeSystemUri())
							.version(version)
							.build())
					.text(text)
					.build();
			
			if (services == null) {
				services = Lists.newArrayList();
			}
			
			services.add(serviceConcept);
			return this;
			
		}

		public Builder description(final String description) {
			this.description = description;
			return this;
		}
		
		@Override
		protected Security doBuild() {
			return new Security(isCors, services, description);
		}
	}

}
