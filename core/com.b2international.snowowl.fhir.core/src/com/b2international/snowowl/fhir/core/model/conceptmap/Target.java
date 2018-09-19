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
package com.b2international.snowowl.fhir.core.model.conceptmap;

import java.util.Collection;

import javax.validation.Valid;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * FHIR Concept map target backbone element
 * <br> Concept in target system for element
 * @since 6.4
 */
public class Target {

	@Valid
	@JsonProperty
	private final Code code;

	@Summary
	@JsonProperty
	private final String display;

	@Valid
	@JsonProperty
	private final Code equivalence;

	@JsonProperty
	private final String comment;

	@Valid
	@JsonProperty("dependsOn")
	private final Collection<DependsOn> dependsOnElements;

	@Valid
	@JsonProperty("product")
	private final Collection<DependsOn> products;

	Target(Code code, String display, Code equivalence, String comment, Collection<DependsOn> dependsOnElements,
			Collection<DependsOn> products) {
		this.code = code;
		this.display = display;
		this.equivalence = equivalence;
		this.comment = comment;
		this.dependsOnElements = dependsOnElements;
		this.products = products;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ValidatingBuilder<Target> {


		private Code code;
		private String display;
		private Code equivalence;
		private String comment;
		private Collection<DependsOn> dependsOnElements = Sets.newHashSet();
		private Collection<DependsOn> products = Sets.newHashSet();

	
		public Builder code(final Code code) {
			this.code = code;
			return this;
		}
		
		public Builder display(final String display) {
			this.display = display;
			return this;
		}

		public Builder mode(final Code equivalence) {
			this.equivalence = equivalence;
			return this;
		}
		
		public Builder comment(final String comment) {
			this.comment = comment;
			return this;
		}
		
		public Builder addDependsOnElement(final DependsOn dependsOn) {
			this.dependsOnElements.add(dependsOn);
			return this;
		}
		
		public Builder addProduct(final DependsOn product) {
			this.products.add(product);
			return this;
		}
		
		@Override
		protected Target doBuild() {

			return new Target(code, display, equivalence, comment, dependsOnElements, products);
		}

	}


}
