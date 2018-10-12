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
package com.b2international.snowowl.fhir.core.model.valueset;

import java.util.Collection;

import javax.validation.Valid;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * FHIR Value Set Include backbone element
 * 
 * @since 6.4
 */
public class Include {
	
	@Valid
	@JsonProperty
	private final Uri system;
	
	@Summary
	@JsonProperty
	private final String version;
	
	@JsonProperty("concept")
	private final Collection<ValueSetConcept> concepts;
	
	@JsonProperty("filter")
	private final Collection<ValueSetFilter> filters;
	
	@JsonProperty("valueSet")
	private final Collection<Uri> valueSets;
	
	Include(Uri system, String version, Collection<ValueSetConcept> concepts, Collection<ValueSetFilter> filters, Collection<Uri> valueSets) {
		this.system = system;
		this.version = version;
		this.concepts = concepts;
		this.filters = filters;
		this.valueSets = valueSets;
	}
	
	public Collection<ValueSetFilter> getFilters() {
		return filters;
	}
	
	public Collection<Uri> getValueSets() {
		return valueSets;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ValidatingBuilder<Include> {
		
		private Uri system;
		
		private String version;
		
		private Collection<ValueSetConcept> concepts = Sets.newHashSet();
		
		private Collection<ValueSetFilter> filters = Sets.newHashSet();
		
		private Collection<Uri> valueSets = Sets.newHashSet();
		
		public Builder system(final String uriValue) {
			this.system = new Uri(uriValue);
			return this;
		}

		public Builder version(final String version) {
			this.version = version;
			return this;
		}
		
		public Builder addConcept(final ValueSetConcept concept) {
			this.concepts.add(concept);
			return this;
		}
		
		public Builder addFilters(final ValueSetFilter filter) {
			this.filters.add(filter);
			return this;
		}
		
		public Builder addValueSet(final String valueSetUriValue) {
			this.valueSets.add(new Uri(valueSetUriValue));
			return this;
		}
		

		@Override
		protected Include doBuild() {
			return new Include(system, version, concepts, filters, valueSets);
		}
		
	}

}
