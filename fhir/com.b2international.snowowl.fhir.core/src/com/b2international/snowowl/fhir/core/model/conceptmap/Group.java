/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

/**
 * FHIR Concept map group backbone element
 * 
 * @since 6.10
 */
@JsonDeserialize(builder = Group.Builder.class)
public class Group {
	
	@Valid
	@JsonProperty
	private final Uri source;
	
	@Summary
	@JsonProperty
	private final String sourceVersion;

	@Valid
	@JsonProperty
	private final Uri target;
	
	@Summary
	@JsonProperty
	private final String targetVersion;
	
	@Valid
	@NotNull
	@JsonProperty("element")
	private final List<ConceptMapElement> elements;
	
	@Valid
	@JsonProperty
	private final UnMapped unmapped;

	Group(Uri source, String sourceVersion, Uri target, String targetVersion, List<ConceptMapElement> elements, UnMapped unmapped) {
		this.source = source;
		this.sourceVersion = sourceVersion;
		this.target = target;
		this.targetVersion = targetVersion;
		this.elements = Collections3.toImmutableList(elements);
		this.unmapped = unmapped;
	}
	
	public Uri getSource() {
		return source;
	}
	
	public String getSourceVersion() {
		return sourceVersion;
	}
	
	public Uri getTarget() {
		return target;
	}
	
	public String getTargetVersion() {
		return targetVersion;
	}
	
	public List<ConceptMapElement> getElements() {
		return elements;
	}
	
	public UnMapped getUnmapped() {
		return unmapped;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Group> {
		
		private Uri source;
		private String sourceVersion;
		private Uri target;
		private String targetVersion;
		private ImmutableList.Builder<ConceptMapElement> elements = ImmutableList.builder();
		private UnMapped unmapped;

		public Builder source(final Uri source) {
			this.source = source;
			return this;
		}

		public Builder source(final String sourceString) {
			this.source = new Uri(sourceString);
			return this;
		}
		
		public Builder sourceVersion(final String sourceVersion) {
			this.sourceVersion = sourceVersion;
			return this;
		}
		
		public Builder target(final Uri target) {
			this.target = target;
			return this;
		}
		
		public Builder target(final String targetString) {
			this.target = new Uri(targetString);
			return this;
		}
		
		public Builder targetVersion(final String targetVersion) {
			this.targetVersion = targetVersion;
			return this;
		}
		
		@JsonProperty("element")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder elements(Collection<ConceptMapElement> elements) {
			this.elements.addAll(elements);
			return this;
		}
		
		public Builder addElement(final ConceptMapElement element) {
			this.elements.add(element);
			return this;
		}
		
		public Builder unmapped(final UnMapped unmapped) {
			this.unmapped = unmapped;
			return this;
		}
		
		@Override
		protected Group doBuild() {
			return new Group(source, sourceVersion, target, targetVersion, elements.build(), unmapped);
		}
	}
	
}
