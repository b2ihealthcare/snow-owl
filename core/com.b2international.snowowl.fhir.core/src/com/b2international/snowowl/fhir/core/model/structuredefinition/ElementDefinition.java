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

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.typedproperty.StringProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.TypedProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.TypedPropertySerializer;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
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

	@NotNull
	@Mandatory
	@JsonProperty
	private final String path;
	
	@Valid
	@Summary
	@JsonProperty("representation")
	private Collection<Code> representations;
	
	@Summary
	@JsonProperty
	private final String sliceName;
	
	@Summary
	@JsonProperty
	private final String label;
	
	@Valid
	@Summary
	@JsonProperty("code")
	private final Collection<Coding> codes;
	
	@Valid
	@Summary
	@JsonProperty
	private final Slicing slicing;
	
	@Summary
	@JsonProperty("short")
	private final String shortDefinition;

	@Summary
	@JsonProperty
	private final String definition;
	
	@Summary
	@JsonProperty
	private final String comment;
	
	@Summary
	@JsonProperty
	private final String requirements;
	
	@Summary
	@JsonProperty("alias")
	private final Collection<String> aliases;

	@Summary
	@JsonProperty
	private final int min;
	
	@Summary
	@JsonProperty
	private final String max;
	
	@Valid
	@Summary
	@JsonProperty
	private final Base base;
	
	@Valid
	@Summary
	@JsonProperty
	private final Uri contentReference;
	
	@Valid
	@Summary
	@JsonProperty("type")
	private final Collection<Type> types;
	
	@Valid
	@Summary
	@JsonSerialize(using = TypedPropertySerializer.class)
	@JsonUnwrapped
	@JsonProperty
	private final TypedProperty<?> defaultValue;
	
	@Summary
	@JsonProperty
	private final String meaningWhenMissing;
	
	@Summary
	@JsonProperty
	private final String orderMeaning;
	
	@Valid
	@Summary
	@JsonSerialize(using = TypedPropertySerializer.class)
	@JsonUnwrapped
	@JsonProperty
	private final TypedProperty<?> fixed;
	
	@Valid
	@Summary
	@JsonSerialize(using = TypedPropertySerializer.class)
	@JsonUnwrapped
	@JsonProperty
	private final TypedProperty<?> pattern;
	
	@Valid
	@Summary
	@JsonProperty("example")
	private final Collection<Example> examples;
	
	@Valid
	@Summary
	@JsonSerialize(using = TypedPropertySerializer.class)
	@JsonUnwrapped
	@JsonProperty
	private final TypedProperty<?> minValue;
	
	@Valid
	@Summary
	@JsonSerialize(using = TypedPropertySerializer.class)
	@JsonUnwrapped
	@JsonProperty
	private final TypedProperty<?> maxValue;
	
	@Summary
	@JsonProperty
	private final Integer maxLength;
	
	@Valid
	@Summary
	@JsonProperty("condition")
	private final Collection<Id> conditions;

	@Valid
	@Summary
	@JsonProperty("constraint")
	private final Collection<Constraint> constraints;

	@Summary
	@JsonProperty
	private final Boolean mustSupport;
	
	@Summary
	@JsonProperty
	private final Boolean isModifier;
	
	@Summary
	@JsonProperty
	private final Boolean isSummary;
	
	@Valid
	@Summary
	@JsonProperty
	private final Binding binding;
	
	@Valid
	@Summary
	@JsonProperty
	private final Collection<MappingElement> mappings;
	
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
			final Collection<Id> conditions,
			final Collection<Constraint> constraints,
			final Boolean mustSupport,
			final Boolean isModifier,
			final Boolean isSummary,
			final Binding binding,
			final Collection<MappingElement> mappings
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
		this.mappings = mappings;
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
		private Collection<Example> examples = Sets.newHashSet();
		private TypedProperty<?> minValue;
		private TypedProperty<?> maxValue;
		private Integer maxLength;
		private Collection<Id> conditions = Sets.newHashSet();
		private Collection<Constraint> constraints = Sets.newHashSet();
		private Boolean mustSupport;
		private Boolean isModifier;
		private Boolean isSummary;
		private Binding binding;
		private Collection<MappingElement> mappings = Sets.newHashSet();
		
		public Builder path(final String path) {
			this.path = path;
			return getSelf();
		}
		
		public Builder sliceName(final String sliceName) {
			this.sliceName = sliceName;
			return getSelf();
		}
		
		public Builder codes(final Collection<Coding> codes) {
			this.codes = codes;
			return getSelf();
		}
		
		public Builder addCode(final Coding coding) {
			this.codes.add(coding);
			return getSelf();
		}
		
		public Builder slicing(final Slicing slicing) {
			this.slicing = slicing;
			return getSelf();
		}
		
		public Builder shortDefinition(final String shortDefinition) {
			this.shortDefinition = shortDefinition;
			return getSelf();
		}
		
		public Builder definition(final String definition) {
			this.definition = definition;
			return getSelf();
		}
		
		public Builder comment(final String comment) {
			this.comment = comment;
			return getSelf();
		}
		
		public Builder requirements(final String requirements) {
			this.requirements = requirements;
			return getSelf();
		}
		
		public Builder aliases(final Collection<String> aliases) {
			this.aliases = aliases;
			return getSelf();
		}
		
		public Builder addAlias(final String alias) {
			this.aliases.add(alias);
			return getSelf();
		}
		
		public Builder min(final Integer min) {
			this.min = min;
			return getSelf();
		}
		
		public Builder max(final String max) {
			this.max = max;
			return getSelf();
		}
		
		public Builder base(final Base base) {
			this.base = base;
			return getSelf();
		}
		
		public Builder contentReference(final Uri contentReference) {
			this.contentReference = contentReference;
			return getSelf();
		}
		
		public Builder contentReference(final String contentReference) {
			this.contentReference = new Uri(contentReference);
			return getSelf();
		}
		
		public Builder types(final Collection<Type> types) {
			this.types = types;
			return getSelf();
		}
		
		public Builder types(final Type type) {
			this.types.add(type);
			return getSelf();
		}
		
		public Builder defaultValue(final TypedProperty<?> defaultValue) {
			this.defaultValue = defaultValue;
			return getSelf();
		}
		
		public Builder defaultValue(final String stringValue) {
			this.defaultValue = new StringProperty(stringValue);
			return getSelf();
		}
		
		public Builder meaningWhenMissing(final String meaningWhenMissing) {
			this.meaningWhenMissing = meaningWhenMissing;
			return getSelf();
		}
		
		public Builder orderMeaning(final String orderMeaning) {
			this.orderMeaning = orderMeaning;
			return getSelf();
		}
		
		public Builder fixed(final TypedProperty<?> fixed) {
			this.fixed = fixed;
			return getSelf();
		}
		
		public Builder fixed(final String fixed) {
			this.fixed = new StringProperty(fixed);
			return getSelf();
		}
		
		public Builder pattern(final TypedProperty<?> pattern) {
			this.pattern = pattern;
			return getSelf();
		}
		
		public Builder pattern(final String pattern) {
			this.pattern = new StringProperty(pattern);
			return getSelf();
		}
		
		public Builder example(final Collection<Example> examples) {
			this.examples = examples;
			return getSelf();
		}
		
		public Builder example(final Example example) {
			this.examples.add(example);
			return getSelf();
		}
		
		public Builder minValue(final TypedProperty<?> minValue) {
			this.minValue = minValue;
			return getSelf();
		}
		
		public Builder minValue(final String minValue) {
			this.minValue = new StringProperty(minValue);
			return getSelf();
		}
		
		public Builder maxValue(final TypedProperty<?> maxValue) {
			this.maxValue = maxValue;
			return getSelf();
		}
		
		public Builder maxValue(final String maxValue) {
			this.maxValue = new StringProperty(maxValue);
			return getSelf();
		}
		
		public Builder maxLength(final Integer maxLength) {
			this.maxLength = maxLength;
			return getSelf();
		}
		
		public Builder condition(final Collection<Id> conditions) {
			this.conditions = conditions;
			return getSelf();
		}
		
		public Builder addCondition(final Id condition) {
			this.conditions.add(condition);
			return getSelf();
		}
		
		public Builder constraint(final Collection<Constraint> constraints) {
			this.constraints = constraints;
			return getSelf();
		}
		
		public Builder constraint(final Constraint constraint) {
			this.constraints.add(constraint);
			return getSelf();
		}
		
		public Builder mustSupport(final Boolean mustSupport) {
			this.mustSupport = mustSupport;
			return getSelf();
		}
		
		public Builder isModifier(final Boolean isModifier) {
			this.isModifier = isModifier;
			return getSelf();
		}
		
		public Builder isSummary(final Boolean isSummary) {
			this.isSummary = isSummary;
			return getSelf();
		}

		public Builder binding(final Binding binding) {
			this.binding = binding;
			return getSelf();
		}
		
		public Builder mapping(final Collection<MappingElement> mappings) {
			this.mappings = mappings;
			return getSelf();
		}
		
		public Builder addMapping(final MappingElement mapping) {
			this.mappings.add(mapping);
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
					mappings
			);
		}
	
	}

}
