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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Sets;

/**
 * FHIR Value Set Include backbone element
 * 
 * @since 6.4
 */
@JsonDeserialize(builder = Include.Builder.class)
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
	
	public Uri getSystem() {
		return system;
	}
	
	public String getVersion() {
		return version;
	}
	
	public Collection<ValueSetConcept> getConcepts() {
		return concepts;
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

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Include> {
		
		private Uri system;
		
		private String version;
		
		private Collection<ValueSetConcept> concepts;
		
		private Collection<ValueSetFilter> filters;
		
		private Collection<Uri> valueSets;
		
		public Builder system(final String uriValue) {
			this.system = new Uri(uriValue);
			return this;
		}

		public Builder version(final String version) {
			this.version = version;
			return this;
		}
		
		@JsonProperty("concept")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder profiles(Collection<ValueSetConcept> concepts) {
			this.concepts = concepts;
			return this;
		}
		
		public Builder addConcept(final ValueSetConcept concept) {
			
			if (concepts == null) {
				concepts = Sets.newHashSet();
			}
			
			concepts.add(concept);
			return this;
		}
		
		@JsonProperty("filter")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder filters(Collection<ValueSetFilter> filters) {
			this.filters = filters;
			return this;
		}
		
		public Builder addFilters(final ValueSetFilter filter) {
			
			if (filters == null) {
				filters = Sets.newHashSet();
			}
			
			filters.add(filter);
			return this;
		}
		
		@JsonProperty("valueSet")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder valueSets(Collection<Uri> valueSetUris) {
			this.valueSets = valueSetUris;
			return this;
		}
		
		public Builder addValueSet(final String valueSetUriValue) {
			
			if (valueSets == null) {
				valueSets = Sets.newHashSet();
			}
			
			valueSets.add(new Uri(valueSetUriValue));
			return this;
		}
		
		@Override
		protected Include doBuild() {
			return new Include(system, version, concepts, filters, valueSets);
		}
		
	}

}
