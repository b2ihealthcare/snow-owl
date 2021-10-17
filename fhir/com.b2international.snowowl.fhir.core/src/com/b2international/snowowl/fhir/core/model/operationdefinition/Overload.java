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
package com.b2international.snowowl.fhir.core.model.operationdefinition;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;

/**
 * FHIR {@link Overload} definition.
 * Defines an appropriate combination of parameters to use when invoking this operation, 
 * to help code generators when generating overloaded parameter sets for this operation.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Overload.Builder.class)
public class Overload extends Element {

	@JsonProperty("parameterName")
	private final Collection<String> parameterNames;
	
	@JsonProperty
	private final String comment;
	
	Overload(final String id, 
			final List<Extension<?>> extensions,
			final Collection<String> parameterNames, 
			final String comment) {
		
		super(id, extensions);
		
		this.parameterNames = parameterNames;
		this.comment = comment;
	}
	
	public Collection<String> getParameterNames() {
		return parameterNames;
	}
	
	public String getComment() {
		return comment;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Element.Builder<Builder, Overload> {
		
		private Collection<String> parameterNames;
		private String comment;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		@JsonProperty("parameterName")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder parameterNames(Collection<String> parameterNames) {
			this.parameterNames = parameterNames;
			return getSelf();
		}
		
		public Builder addParameterName(String parameterName) {
			
			if (parameterNames == null) {
				parameterNames = Lists.newArrayList();
			}
			parameterNames.add(parameterName);
			return getSelf();
		}
		
		public Builder comment(String comment) {
			this.comment = comment;
			return getSelf();
		}
		
		@Override
		protected Overload doBuild() {
			return new Overload(id, extensions, parameterNames, comment);	
		}
	}
}
