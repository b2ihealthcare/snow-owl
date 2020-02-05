/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Formal constraints such as co-occurrence and other constraints that can be computationally evaluated within the context of the instance.
 * @since 7.1
 */
public class Constraint extends Element {

	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Id key;
	
	@Summary
	@JsonProperty
	private final String requirements;
	
	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Code severity;
	
	@NotNull
	@Mandatory
	@JsonProperty
	private final String human;

	@NotNull
	@Mandatory
	@JsonProperty
	private final String expression;
	
	@Summary
	@JsonProperty
	private final String xpath;
	
	@Summary
	@JsonProperty
	private final Uri source;
	
	protected Constraint(final String id, 
			@SuppressWarnings("rawtypes") final Collection<Extension> extensions,
			final Id key, 
			final String requirements, 
			final Code severity, 
			final String human, 
			final String expression, 
			final String xpath,
			final Uri source) {
		
		super(id, extensions);
		
		this.key = key;
		this.requirements = requirements;
		this.severity = severity;
		this.human = human;
		this.expression = expression;
		this.xpath = xpath;
		this.source = source;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, Constraint> {
		
		private Id key;
		private String requirements;
		private Code severity;
		private String human;
		private String expression;
		private String xpath;
		private Uri source;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder key(String key) {
			this.key = new Id(key);
			return getSelf();
		}
		
		public Builder requirements(String requirements) {
			this.requirements = requirements;
			return getSelf();
		}
		
		public Builder severity(String severity) {
			this.severity = new Code(severity);
			return getSelf();
		}
		
		public Builder human(String human) {
			this.human = human;
			return getSelf();
		}
		
		public Builder expression(String expression) {
			this.expression = expression;
			return getSelf();
		}
		
		public Builder xpath(String xpath) {
			this.xpath = xpath;
			return getSelf();
		}
		
		public Builder source(String source) {
			this.source = new Uri(source);
			return getSelf();
		}
		
		@Override
		protected Constraint doBuild() {
			return new Constraint(id, extensions, key, requirements, severity, human, expression, xpath, source);
		}
	}

}
