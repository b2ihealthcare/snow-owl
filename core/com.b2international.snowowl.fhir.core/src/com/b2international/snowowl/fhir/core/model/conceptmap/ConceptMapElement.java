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
 * FHIR Concept map element backbone element
 * <br> Mappings for a concept from the source set
 * @since 6.10
 */
public class ConceptMapElement {

	@Valid
	@JsonProperty
	private final Code code;
	
	@Summary
	@JsonProperty
	private final String display;
	
	@Valid
	@JsonProperty("target")
	private final Collection<Target> targets;

	ConceptMapElement(Code code, String display, Collection<Target> targets) {
		
		this.code = code;
		this.display = display;
		this.targets = targets;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
public static class Builder extends ValidatingBuilder<ConceptMapElement> {
		
		private Code code;
		private String display;
		private final Collection<Target> targets = Sets.newHashSet();
		
		
		public Builder code(final Code code) {
			this.code = code;
			return this;
		}
		
		public Builder code(final String codeString) {
			this.code = new Code(codeString);
			return this;
		}
		
		public Builder display(final String display) {
			this.display = display;
			return this;
		}
		
		public Builder addTarget(final Target target) {
			this.targets.add(target);
			return this;
		}
		
		
		
		@Override
		protected ConceptMapElement doBuild() {
			return new ConceptMapElement(code, display, targets);
		}
	}
	
	
}
