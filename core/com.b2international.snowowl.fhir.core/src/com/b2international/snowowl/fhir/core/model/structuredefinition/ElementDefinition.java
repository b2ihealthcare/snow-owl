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
import java.util.Set;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.typedproperty.StringProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.TypedProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.TypedPropertySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
	
	@JsonProperty("code")
	private final Collection<Coding> codes;
	
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
	
	@JsonSerialize(using = TypedPropertySerializer.class)
	@JsonUnwrapped
	@JsonProperty
	private final TypedProperty<?> defaultValue;
	
	private final String meaningWhenMissing;
	
	private final String orderMeaning;
	
	private final TypedProperty<?> fixed;
	
	private final TypedProperty<?> pattern;
	
	@JsonProperty("example")
	private final Collection<Example> examples;
	
	private final TypedProperty<?> minValue;
	
	private final TypedProperty<?> maxValue;
	
	private final Integer maxLength;
	
	@JsonProperty("condition")
	private final Set<Id> conditions;
	
	@JsonProperty("constraint")
	private final Set<Constraint> constraints;
	
	private final Boolean mustSupport;
	
	private final Boolean isModifier;
	
	private final Boolean isSummary;
	
	private final Binding binding;
	
	private final MappingElement mapping;
	
	@SuppressWarnings("rawtypes")
	protected ElementDefinition(final String id, final Collection<Extension> extensions,
			final String path,
			final String sliceName,
			final String label,
			final Collection<Coding> codes,
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
			final Collection<Type> types,
			final TypedProperty<?> defaultValue,
			final String meaningWhenMissing,
			final String orderMeaning,
			final TypedProperty<?> fixed,
			final TypedProperty<?> pattern,
			final Collection<Example> examples,
			final TypedProperty<?> minValue,
			final TypedProperty<?> maxValue,
			final Integer maxLength,
			final Set<Id> conditions,
			final Set<Constraint> constraints,
			final Boolean mustSupport,
			final Boolean isModifier,
			final Boolean isSummary,
			final Binding binding,
			final MappingElement mapping
		) {
		
		super(id, extensions);
		
		this.path = path;
		this.sliceName = sliceName;
		this.label = label;
		this.codes = codes;
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
		this.defaultValue = defaultValue;
		this.meaningWhenMissing = meaningWhenMissing;
		this.orderMeaning = orderMeaning;
		this.fixed = fixed;
		this.pattern = pattern;
		this.examples = examples;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.maxLength = maxLength;
		this.conditions = conditions;
		this.constraints = constraints;
		this.mustSupport = mustSupport;
		this.isModifier = isModifier;
		this.isSummary = isSummary;
		this.binding = binding;
		this.mapping = mapping;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, ElementDefinition> {

		private String path;
		private String sliceName;
		private String label;
		private Collection<Coding> codes = Sets.newHashSet();
		private Slicing slicing;
		private String shortDefinition;
		private String definition;
		private String comment;
		private String requirements;
		private Collection<String> aliases = Sets.newHashSet();
		private int min;
		private String max;
		private Base base;
		private	Uri contentReference;
		private Collection<Type> types = Lists.newArrayList();
		private TypedProperty<?> defaultValue;
		private String meaningWhenMissing;
		private String orderMeaning;
		private TypedProperty<?> fixed;
		private TypedProperty<?> pattern;
		private Collection<Example> examples;
		private TypedProperty<?> minValue;
		private TypedProperty<?> maxValue;
		private Integer maxLength;
		private Set<Id> conditions;
		private Set<Constraint> constraints;
		private Boolean mustSupport;
		private Boolean isModifier;
		private Boolean isSummary;
		private Binding binding;
		private MappingElement mapping;
		
		public Builder path(final String path) {
			this.path = path;
			return getSelf();
		}
		
		public Builder sliceName(final String sliceName) {
			this.sliceName = sliceName;
			return getSelf();
		}
		
		public Builder label(final String label) {
			this.label = label;
			return getSelf();
		}
		
		public Builder defaultValue(final String stringValue) {
			this.defaultValue = new StringProperty(stringValue);
			return getSelf();
		}

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
					codes,
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
					types,
					defaultValue,
					meaningWhenMissing,
					orderMeaning,
					fixed,
					pattern,
					examples,
					minValue,
					maxValue,
					maxLength,
					conditions,
					constraints,
					mustSupport,
					isModifier,
					isSummary,
					binding,
					mapping
			);
		}
	
	}

}
