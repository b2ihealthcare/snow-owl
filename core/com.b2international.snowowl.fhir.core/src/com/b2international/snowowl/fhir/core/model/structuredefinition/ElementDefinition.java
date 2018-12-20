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

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * FHIR definition of an element in a resource or an extension.
 * @since 7.1
 */
public class ElementDefinition extends Element {

	private final String path;
	
	@JsonProperty("representation")
	private Collection<Code> representations;
	
	private final String sliceName;
	
	private final String label;
	
	private final Slicing slicing;
	
	@JsonProperty("short")
	private final String shortDefinition;
	
	private final String definition;
	
	private final String comment;
	
	private final String requirements;
	
	@JsonProperty("alias")
	private final Collection<String> aliases;
	
	private final int min;
	
	private final String max;
	
	private final Base base;
	
	private final Uri contentReference;
	
	@JsonProperty("type")
	private final Collection<Type> types;
	
	@SuppressWarnings("rawtypes")
	protected ElementDefinition(final String id, final Collection<Extension> extensions,
			final String path,
			final String sliceName,
			final String label,
			final Slicing slicing,
			final String shortDefinition,
			final String definition,
			final String comment,
			final String requirements,
			final Collection<String> aliases,
			final int min,
			final String max,
			final Base base,
			final Uri contentReference,
			final Collection<Type> types) {
		
		super(id, extensions);
		
		this.path = path;
		this.sliceName = sliceName;
		this.label = label;
		this.slicing = slicing;
		this.shortDefinition = shortDefinition;
		this.definition = definition;
		this.comment = comment;
		this.requirements = requirements;
		this.aliases = aliases;
		this.min = min;
		this.max = max;
		this.base = base;
		this.contentReference = contentReference;
		this.types = types;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, ElementDefinition> {

		private String path;
		private String sliceName;
		private String label;
		private Slicing slicing;
		private String shortDefinition;
		private String definition;
		private String comment;
		private String requirements;
		private Collection<String> aliases;
		private int min;
		private String max;
		private Base base;
		private	Uri contentReference;
		private Collection<Type> types = Lists.newArrayList();
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected ElementDefinition doBuild() {
			return new ElementDefinition(id, extensions,
					path,
					sliceName,
					label,
					slicing,
					shortDefinition,
					definition,
					comment,
					requirements,
					aliases,
					min,
					max,
					base,
					contentReference,
					types);
		}
	
	}

}
