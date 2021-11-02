/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Identifies a concept from an external specification that roughly corresponds to this element.
 * @since 7.1
 */
@JsonDeserialize(builder = MappingElement.Builder.class)
public class MappingElement extends Element {

	@NotNull
	@Valid
	@Summary
	@JsonProperty
	private final Id identity;
	
	@Valid
	@Summary
	@JsonProperty
	private final Code language;
	
	@NotNull
	@Summary
	@JsonProperty
	private String map;
	
	@Summary
	@JsonProperty
	private String comment;

	MappingElement(final String id, 
			final List<Extension<?>> extensions,
			final Id identity, 
			final Code language,
			final String map,
			final String comment) {
		
		super(id, extensions);
		this.identity = identity;
		this.language = language;
		this.map = map;
		this.comment = comment;
	}
	
	public Id getIdentity() {
		return identity;
	}
	
	public Code getLanguage() {
		return language;
	}
	
	public String getMap() {
		return map;
	}
	
	public String getComment() {
		return comment;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Element.Builder<Builder, MappingElement> {
		
		private Id identity;
		private Code language;
		private String map;
		private String comment;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder identity(String identity) {
			this.identity = new Id(identity);
			return getSelf();
		}

		public Builder language(String language) {
			this.language = new Code(language);
			return getSelf();
		}
		
		public Builder map(String map) {
			this.map = map;
			return getSelf();
		}
		
		public Builder comment(String comment) {
			this.comment = comment;
			return getSelf();
		}
		
		@Override
		protected MappingElement doBuild() {
			return new MappingElement(id, extensions, identity, language, map, comment);
		}
	}

}
