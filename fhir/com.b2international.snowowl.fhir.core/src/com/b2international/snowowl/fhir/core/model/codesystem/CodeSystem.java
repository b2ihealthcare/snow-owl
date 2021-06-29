/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

import io.swagger.annotations.ApiModel;

/**
 * This class represents a FHIR code system. The CodeSystem resource is used to
 * declare the existence of a code system, and its key properties:
 * 
 * <ul>
 * <li>Identifying URL and version
 * <li>Description, Copyright, publication date, and other metadata
 * <li>Some key properties of the code system itself - whether it's case
 * sensitive, version safe, and whether it defines a compositional grammar
 * <li>What filters can be used in value sets that use the code system in a
 * ValueSet.compose element
 * <li>What properties the concepts defined by the code system
 * </ul>
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystem.html">FHIR:CodeSystem</a>
 * @since 6.3
 */
@ApiModel("CodeSystem")
public class CodeSystem extends MetadataResource {

	private static final long serialVersionUID = 1L;

	/**
	 * @since 8.0
	 */
	public static final class Fields extends MetadataResource.Fields {

		// XXX do we need caseSensitive???
		public static final String CONTENT = "content";
		// TODO valueSet
		public static final String COUNT = "count";
		// XXX do we need hierarchyMeaning???
		// XXX do we need compositional???
		// XXX do we need versionNeeded???
		// XXX do we need supplements???
		
		// complex properties
		public static final String FILTER = "filter";
		public static final String PROPERTY = "property";
		public static final String CONCEPT = "concept";
		
		public static final Set<String> MANDATORY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.Fields.MANDATORY)
				.add(CONTENT)
				.build();
		
		public static final Set<String> SUMMARY = ImmutableSet.<String>builder()
				.addAll(MetadataResource.Fields.SUMMARY)
				.add(COUNT, FILTER, PROPERTY)
				.build();
		
		public static final Set<String> SUMMARY_TEXT = ImmutableSet.<String>builder()
				.addAll(MetadataResource.Fields.MANDATORY)
				.add(TEXT)
				.build();
		
		public static final Set<String> SUMMARY_DATA = MANDATORY;
		
		public static final Set<String> ALL = ImmutableSet.<String>builder()
				.addAll(MANDATORY)
				.addAll(SUMMARY)
				.add(TEXT)
				.add(CONCEPT)
				.build();
		
	}
	
	@Mandatory
	@JsonProperty
	private String resourceType = "CodeSystem";

	@Summary
	@JsonProperty
	private Boolean caseSensitive;
	
	@Summary
	@Valid
	@JsonProperty
	private Uri valueSet;
	
	@Summary
	@JsonProperty
	private Code hierarchyMeaning;
	
	@Summary
	@JsonProperty
	private Boolean compositional;
	
	@Summary
	@JsonProperty
	private Boolean versionNeeded;
	
	@Mandatory
	@Valid
	@NotNull
	@JsonProperty
	private Code content;
	
	@Summary
	@Valid
	@JsonProperty
	private Uri supplements;

	//not primitive int to avoid serialization when the default value is 0
	@Summary
	@Min(value = 0, message = "Count must be equal to or larger than 0")
	@JsonProperty
	private Integer count;

	@Summary
	@Valid
	private Collection<Filter> filters;

	/*
	 * The properties supported by this code system
	 */
	@Summary
	@Valid
	private Collection<SupportedConceptProperty> properties;

	/*
	 * Concepts in the code system, up to the server if they are returned
	 */
	@Valid
	private Collection<Concept> concepts;

	@SuppressWarnings("rawtypes")
	CodeSystem(Id id, final Meta meta, final Uri impliciteRules, Code language, 
			final Narrative text, Uri url, Identifier identifier, String version, String name, String title, Code status,
			final Date date, final String publisher, final Collection<ContactDetail> contacts, final String description, final Collection<UsageContext> usageContexts, 
			final Collection<CodeableConcept> jurisdictions, final String purpose, final String copyright,
			
			//CodeSystem only
			final Boolean caseSensitive, final Uri valueSet, final Code hierarchyMeaning, final Boolean compositional, final Boolean versionNeeded,
			final Code content, final Uri supplements, final Integer count, 
			Collection<Filter> filters, Collection<SupportedConceptProperty> properties, Collection<Concept> concepts) {

		super(id, meta, impliciteRules, language, text, url, identifier, version, name, title, status, date, publisher, contacts, 
				description, usageContexts, jurisdictions, purpose, copyright);

		this.caseSensitive = caseSensitive;
		this.valueSet = valueSet;
		this.hierarchyMeaning = hierarchyMeaning;
		this.compositional = compositional;
		this.versionNeeded = versionNeeded;
		this.content = content;
		this.supplements = supplements;
		this.count = count;
		this.filters = filters;
		this.properties = properties;
		this.concepts = concepts;
	}

	@JsonProperty(CodeSystem.Fields.CONCEPT)
	@JsonInclude(value = Include.NON_EMPTY)
	public Collection<Concept> getConcepts() {
		return concepts;
	}
	
	@JsonProperty(CodeSystem.Fields.FILTER)
	@JsonInclude(value = Include.NON_EMPTY)
	public Collection<Filter> getFilters() {
		return filters;
	}
	
	@JsonProperty(CodeSystem.Fields.PROPERTY)
	@JsonInclude(value = Include.NON_EMPTY)
	public Collection<SupportedConceptProperty> getProperties() {
		return properties;
	}
	
	@JsonIgnore
	public ResourceURI getResourceURI() {
		return ResourceURI.of(com.b2international.snowowl.core.codesystem.CodeSystem.RESOURCE_TYPE, getId().getIdValue());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(String codeSystemId) {
		return new Builder(codeSystemId);
	}

	public static class Builder extends MetadataResource.Builder<Builder, CodeSystem> {

		private Boolean caseSensitive;
		
		private Uri valueSet;
		
		private Code hierarchyMeaning;
		
		private Boolean compositional;
		
		private Boolean versionNeeded;

		private Code content;
		
		private Uri supplements;

		private Integer count;

		private Collection<Filter> filters;

		private Collection<SupportedConceptProperty> properties;

		private Collection<Concept> concepts;

		/**
		 * Use this constructor when a new resource is sent to the server to be created.
		 */
		public Builder() {
		}

		public Builder(String codeSystemId) {
			super(codeSystemId);
		}

		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder caseSensitive(Boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
			return getSelf();
		}
		
		public Builder valueSet(Uri valueSetUri) {
			this.valueSet = valueSetUri;
			return getSelf();
		}
		
		public Builder valueSet(String valueSet) {
			this.valueSet = new Uri(valueSet);
			return getSelf();
		}
		
		public Builder hierarchyMeaning(CodeSystemHierarchyMeaning codeSystemHierarchyMeaning) {
			this.hierarchyMeaning = codeSystemHierarchyMeaning.getCode();
			return getSelf();
		}
		
		public Builder compositional(Boolean compositional) {
			this.compositional = compositional;
			return getSelf();
		}
		
		public Builder versionNeeded(Boolean versionNeeded) {
			this.versionNeeded = versionNeeded;
			return getSelf();
		}

		public Builder content(CodeSystemContentMode contentMode) {
			this.content = contentMode.getCode();
			return getSelf();
		}

		public Builder supplements(Uri supplementsUri) {
			this.supplements = supplementsUri;
			return getSelf();
		}

		public Builder count(int count) {
			this.count = count;
			return getSelf();
		}

		public Builder addFilter(Filter filter) {
			this.filters.add(filter);
			return getSelf();
		}

		public Builder addProperty(SupportedConceptProperty property) {
			if (this.properties == null) {
				this.properties = new ArrayList<>();
			}
			this.properties.add(property);
			return getSelf();
		}

		public Builder addConcept(Concept concept) {
			if (this.concepts == null) {
				this.concepts = new ArrayList<>();
			}
			this.concepts.add(concept);
			return getSelf();
		}
		
		public Builder filters(Collection<Filter> filters) {
			if (this.filters == null) {
				this.filters = new ArrayList<>();
			}
			this.filters = filters;
			return getSelf();
		}
		
		public Builder properties(Collection<SupportedConceptProperty> properties) {
			this.properties = properties;
			return getSelf();
		}
		
		public Builder concepts(Collection<Concept> concepts) {
			this.concepts = concepts;
			return getSelf();
		}

		@Override
		protected CodeSystem doBuild() {
			return new CodeSystem(id, meta, implicitRules, language, text, url, identifier, version, name, title, status, date, publisher, contacts, 
				description, usageContexts, jurisdictions, purpose, copyright,
				caseSensitive, valueSet, hierarchyMeaning, compositional, versionNeeded,
				content, supplements, count, filters, properties, concepts);
		}
	}

}
