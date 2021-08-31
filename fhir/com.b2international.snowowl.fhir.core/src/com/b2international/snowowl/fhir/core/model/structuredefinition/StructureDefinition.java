/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;

/**
 * This class represents a FHIR Structure Definition.  
 * This resource is used to describe the underlying resources, data types defined in FHIR, 
 * and also for describing extensions and constraints on resources and data types.
 * 
 * @see <a href="https://www.hl7.org/fhir/structuredefinition.html">FHIR:StructureDefinition</a>
 * @since 7.1
 */
@JsonDeserialize(builder = StructureDefinition.Builder.class, using = JsonDeserializer.None.class)
public class StructureDefinition extends MetadataResource {

	private static final long serialVersionUID = 1L;
	
	public static final String RESOURCE_TYPE_STRUCTURE_DEFINITION = "StructureDefinition";

	@Mandatory
	@JsonProperty
	private final String resourceType;
	
	@Summary
	@JsonProperty("identifier")
	@JsonInclude(value = Include.NON_EMPTY)
	private Collection<Identifier> identifiers;
	
	@JsonProperty
	private String copyright;
	
	@Valid
	@Summary
	@JsonProperty("keyword")
	private final Collection<Coding> keywords;
	
	@Valid
	@Mandatory
	@JsonProperty
	private final Id fhirVersion;
	
	@Valid
	@Summary
	@JsonProperty("mapping")
	private final Collection<Mapping> mappings;
	
	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Code kind;
	
	@NotNull
	@Valid
	@Mandatory
	@JsonProperty("abstract")
	private final boolean isAbstract;
	
	@Valid
	@Summary
	@JsonProperty
	private final Code contextType;
	
	@Valid
	@Summary
	@JsonProperty("context")
	private final Collection<String> contexts;
	
	@Summary
	@JsonProperty("contextInvariant")
	private final Collection<String> contextInvariants;
	
	@Valid
	@Mandatory
	@JsonProperty
	private final Code type;
	
	@Valid
	@Summary
	@JsonProperty
	private final Uri baseDefinition;
	
	@Valid
	@Summary
	@JsonProperty
	private final Code derivation;
	
	@Valid
	@Summary
	@JsonProperty
	private final StructureView snapshot;
	
	@Valid
	@Summary
	@JsonProperty
	private final StructureView differential;
	
	@SuppressWarnings("rawtypes")
	public StructureDefinition(Id id, Meta meta, Uri impliciteRules, Code language, Narrative text, Uri url,
			String version, String name, String title, Code status, 
			Boolean experimental, Date date, String publisher, Collection<ContactDetail> contacts, String description, 
			Collection<UsageContext> usageContexts, Collection<CodeableConcept> jurisdictions, String purpose, String toolingId,
			
			final String resourceType,
			final Collection<Identifier> identifiers,
			final String copyright,
			final Collection<Coding> keywords,
			final Id fhirVersion,
			final Collection<Mapping> mappings,
			final Code kind,
			final boolean isAbstract,
			final Code contextType,
			final Collection<String> contexts,
			final Collection<String> contextInvariants,
			final Code type,
			final Uri baseDefinition,
			final Code derivation,
			final StructureView snapshot,
			final StructureView differential) {
		
		super(id, meta, impliciteRules, language, text, url, version, name, title, status, experimental, 
				date, publisher, contacts, description, usageContexts, jurisdictions, purpose, toolingId);
		
		this.resourceType = resourceType;
		this.identifiers = identifiers;
		this.copyright = copyright;
		this.keywords = keywords;
		this.fhirVersion = fhirVersion;
		this.mappings = mappings;
		this.kind = kind;
		this.isAbstract = isAbstract;
		this.contextType = contextType;
		this.contexts = contexts;
		this.contextInvariants = contextInvariants;
		this.type = type;
		this.baseDefinition = baseDefinition;
		this.derivation = derivation;
		this.snapshot = snapshot;
		this.differential = differential;
	}
	
	public String getResourceType() {
		return resourceType;
	}
	
	public Collection<Identifier> getIdentifiers() {
		return identifiers;
	}
	
	public String getCopyright() {
		return copyright;
	}
	
	public Collection<Coding> getKeywords() {
		return keywords;
	}
	
	public Id getFhirVersion() {
		return fhirVersion;
	}
	
	public Collection<Mapping> getMappings() {
		return mappings;
	}
	
	public Code getKind() {
		return kind;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public Code getContextType() {
		return contextType;
	}
	
	public Collection<String> getContexts() {
		return contexts;
	}
	
	public Collection<String> getContextInvariants() {
		return contextInvariants;
	}
	
	public Code getType() {
		return type;
	}
	
	public Uri getBaseDefinition() {
		return baseDefinition;
	}
	
	public Code getDerivation() {
		return derivation;
	}
	
	public StructureView getSnapshot() {
		return snapshot;
	}
	
	public StructureView getDifferential() {
		return differential;
	}
	
	public static Builder builder(String id) {
		return new Builder(id);
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends MetadataResource.Builder<Builder, StructureDefinition> {

		private String resourceType = RESOURCE_TYPE_STRUCTURE_DEFINITION;
		private Collection<Identifier> identifiers;
		private String copyright;
		private Collection<Coding> keywords;
		private Id fhirVersion;
		private Collection<Mapping> mappings;
		private Code kind;
		private boolean isAbstract;
		private Code contextType;
		private Collection<String> contexts;
		private Collection<String> contextInvariants;
		private Code type;
		private Uri baseDefinition;
		private Code derivation;
		private StructureView snapshot;
		private StructureView differential;
		
		/**
		 * Use this constructor when a new resource is sent to the server to be created.
		 */
		public Builder() {
		}
		
		public Builder(String id) {
			super(id);
		}
		
		public Builder resourceType(String resourceType) {
			this.resourceType = resourceType;
			return getSelf();
		}
		
		@JsonProperty("identifier")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder identifiers(final Collection<Identifier> identifers) {
			this.identifiers = identifers;
			return getSelf();
		}
		
		public Builder addIdentifier(Identifier identifier) {
			if (identifiers == null) {
				identifiers = new ArrayList<>();
			}
			identifiers.add(identifier);
			return getSelf();
		}
		
		public Builder copyright(final String copyright) {
			this.copyright = copyright;
			return getSelf();
		}
		
		@JsonProperty("keyword")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder keywords(Collection<Coding> keywords) {
			this.keywords = keywords;
			return getSelf();
		}
		
		public Builder addKeyword(Coding keyword) {
			
			if (keywords == null) {
				keywords = Lists.newArrayList();
			}
			keywords.add(keyword);
			return getSelf();
		}
		
		public Builder fhirVersion(Id fhirVersion) {
			this.fhirVersion = fhirVersion;
			return getSelf();
		}
		
		public Builder fhirVersion(String fhirVersion) {
			this.fhirVersion = new Id(fhirVersion);
			return getSelf();
		}
		
		@JsonProperty("mapping")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder mappings(Collection<Mapping> mappings) {
			this.mappings = mappings;
			return getSelf();
		}
		
		public Builder addMapping(Mapping mapping) {
			
			if (mappings == null) {
				mappings = Lists.newArrayList();
			}
			
			mappings.add(mapping);
			return getSelf();
		}
		
		public Builder kind(Code kind) {
			this.kind = kind;
			return getSelf();
		}
		
		public Builder kind(String kind) {
			this.kind = new Code(kind);
			return getSelf();
		}
		
		@JsonProperty("abstract")
		public Builder setAbstract(boolean isAbstract) {
			this.isAbstract = isAbstract;
			return getSelf();
		}
		
		public Builder contextType(Code contextType) {
			this.contextType = contextType;
			return getSelf();
		}
		
		@JsonProperty("context")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder contexts(Collection<String> contexts) {
			this.contexts = contexts;
			return getSelf();
		}
		
		public Builder addContext(String context) {
			
			if (contexts == null) {
				contexts = Lists.newArrayList();
			}
			
			contexts.add(context);
			return getSelf();
		}
		
		@JsonProperty("contextInvariant")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder contextInvariants(Collection<String> contextInvariants) {
			this.contextInvariants = contextInvariants;
			return getSelf();
		}
		
		public Builder addContextInvariant(String contextInvariant) {
			
			if (contextInvariants == null) {
				contextInvariants = Lists.newArrayList();
			}
			
			contextInvariants.add(contextInvariant);
			return getSelf();
		}
		
		public Builder type(Code type) {
			this.type = type;
			return getSelf();
		}
		
		public Builder type(String type) {
			this.type = new Code(type);
			return getSelf();
		}

		public Builder baseDefinition(Uri baseDefinition) {
			this.baseDefinition = baseDefinition;
			return getSelf();
		}
		
		public Builder baseDefinition(String baseDefinition) {
			this.baseDefinition = new Uri(baseDefinition);
			return getSelf();
		}
		
		public Builder derivation(Code derivation) {
			this.derivation = derivation;
			return getSelf();
		}
		
		public Builder derivation(String derivation) {
			this.derivation = new Code(derivation);
			return getSelf();
		}
		
		public Builder snapshot(StructureView snapshot) {
			this.snapshot = snapshot;
			return getSelf();
		}
		
		public Builder differential(StructureView differential) {
			this.differential = differential;
			return getSelf();
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected StructureDefinition doBuild() {
			return new StructureDefinition(id, meta, implicitRules, language, text, url, version, name, title,
					status, experimental, date, publisher, contacts, description, usageContexts, jurisdictions, purpose, toolingId,
					
					resourceType,
					identifiers,
					copyright,
					keywords,
					fhirVersion,
					mappings,
					kind,
					isAbstract,
					contextType,
					contexts,
					contextInvariants,
					type,
					baseDefinition,
					derivation,
					snapshot,
					differential	
				);
		}
	}
}
