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
package com.b2international.snowowl.fhir.core.model.dt;

import java.util.Arrays;
import java.util.Collection;

import javax.validation.Valid;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * {
  // from Element: extension
  "coding" : [{ Coding }], // Code defined by a terminology system
  "text" : "<string>" // Plain text representation of the concept
}
 * @see <a href="http://hl7.org/fhir/datatypes.html#CodeableConcept">FHIR:Foundation:Data types</a>
 * @since 6.3
 */
public class CodeableConcept {
	
	// Code defined by a terminology system 0..*
	@JsonProperty("coding")
	@Valid
	private Collection<Coding> codings;
	
	// Plain text representation of the concept 0..1
	// same as display most of the time
	private String text;
	
	CodeableConcept(Collection<Coding> codings, String text) {
		this.codings = codings;
		this.text = text;
	}
	
	public Collection<Coding> getCodings() {
		return codings;
	}
	
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "CodeableConcept [codings=" + Arrays.toString(codings.toArray()) + ", text=" + text + "]";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<CodeableConcept> {
		
		private Collection<Coding> codings = Lists.newArrayList();
		private String text;

		public Builder addCoding(final Coding coding) {
			codings.add(coding);
			return this;
		}
		
		public Builder codings(final Collection<Coding> codings) {
			this.codings = codings;
			return this;
		}
		
		public Builder text(final String text) {
			this.text = text;
			return this;
		}
		
		@Override
		protected CodeableConcept doBuild() {
			return new CodeableConcept(codings, text);
		}
	}
	
}
