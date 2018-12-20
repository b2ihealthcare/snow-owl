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
package com.b2international.snowowl.fhir.core.model.structuredefinition;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.SlicingRules;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * FHIR {@link ElementDefinition} slicing definition.
 * 
 * Indicates that the element is sliced into a set of alternative definitions (i.e. in a structure definition, 
 * there are multiple different constraints on a single element in the base resource). 
 * Slicing can be used in any resource that has cardinality ..* on the base resource, 
 * or any resource with a choice of types. The set of slices is any elements that come after this in the element 
 * sequence that have the same path, until a shorter path occurs (the shorter path terminates the set).
 * 
 * @since 7.1
 */
public class Slicing {
	
	@Summary
	@JsonProperty("discriminator")
	private final Collection<Discriminator> discriminators;

	@Summary
	@JsonProperty
	private final String description;
	
	@Summary
	@JsonProperty
	private final Boolean ordered;
	
	@NotNull
	@Valid
	@Summary
	@JsonProperty
	private final Code rules;
	
	Slicing(Collection<Discriminator> discriminators, String description, Boolean ordered, Code rules) {
		this.discriminators = discriminators;
		this.description = description;
		this.ordered = ordered;
		this.rules = rules;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<Slicing> {
	
		private Collection<Discriminator> discriminators = Lists.newArrayList();
		private String description;
		private Boolean ordered;
		private Code rules;
		
		/**
		 * @param discriminators element values that are used to distinguish the slices
		 * @return
		 */
		public Builder discriminators(Collection<Discriminator> discriminators) {
			this.discriminators = discriminators;
			return this;
		}

		/**
		 * @param discriminator element value that is used to distinguish the slices
		 * @return
		 */
		public Builder addDiscriminator(final Discriminator discriminator) {
			this.discriminators.add(discriminator);
			return this;
		}
		
		/**
		 * Text description of how slicing works (or not)
		 * @param description
		 * @return
		 */
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		/**
		 * If elements must be in same order as slices
		 * @param ordered
		 * @return
		 */
		public Builder ordered(Boolean ordered) {
			this.ordered = ordered;
			return this;
		}
		
		/**
		 * closed | open | openAtEnd
		 * @param rules
		 * @return
		 */
		public Builder rules(SlicingRules rules) {
			this.rules = rules.getCode();
			return this;
		}

		@Override
		protected Slicing doBuild() {
			return new Slicing(discriminators, description, ordered, rules);
		}
		
	}
	
}
