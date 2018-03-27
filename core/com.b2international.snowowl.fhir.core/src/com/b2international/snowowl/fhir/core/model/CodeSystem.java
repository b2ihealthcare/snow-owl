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
package com.b2international.snowowl.fhir.core.model;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModel;

/**
 * This class represents a FHIR code system.
 * The CodeSystem resource is used to declare the existence of a code system, and its key properties:

 * <ul>
 * <li>Identifying URL and version
 * <li>Description, Copyright, publication date, and other metadata
 * <li>Some key properties of the code system itself - whether it's case sensitive, version safe, and whether it defines a compositional grammar
 * <li>What filters can be used in value sets that use the code system in a ValueSet.compose element
 * <li>What properties the concepts defined by the code system
 * </ul>
 * @see <a href="https://www.hl7.org/fhir/codesystem.html">FHIR:CodeSystem</a>
 * @since 6.3
 */
@ApiModel("CodeSystem")
public class CodeSystem extends DomainResource {
	
	//FHIR header "resourceType" : "CodeSystem",
	@JsonProperty
	private String resourceType = "CodeSystem";
	
	//same as logical id
	@JsonProperty
	private Uri url; //ORG_LINK or hardcoded provider value
	
	@JsonProperty
	private Identifier identifier; //OID
	
	@JsonProperty
	private String version; //not necessarily available - and what to do when we have more than 1??
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private String description;
	
	@Valid
	@NotNull
	@JsonProperty
	private Code status;
	
	@JsonProperty
	private Code hierarchyMeaning;
	
	@JsonProperty
	private String publisher;
	
	@Valid
	@JsonProperty("property")
	private Collection<SupportedConceptProperty> properties;
	
	public CodeSystem(Id id, Code language, Narrative text, Uri url, Identifier identifier, String version, String name, 
			String title, String description, Code status, Code hierarchyMeaning, String publisher, Collection<SupportedConceptProperty> properties) {
		
		super(id, language, text);
		
		this.url = url;
		this.identifier = identifier;
		this.version = version;
		this.name = name;
		this.title = title;
		this.description = description;
		this.status = status;
		this.hierarchyMeaning = hierarchyMeaning;
		this.publisher = publisher;
		this.properties = properties;
	}
	
	public Uri getUrl() {
		return url;
	}
	
	public static Builder builder(String cdoId) {
		return new Builder(cdoId);
	}

	public static class Builder extends DomainResource.Builder<Builder, CodeSystem> {

		private Uri url; //ORG_LINK or hardcoded provider value
		
		private Identifier identifier; //OID
		
		private String version; //not necessarily available - and what to do when we have more than 1??
		
		private String name;
		
		private String description;
		
		private String title;
		
		private Code status;
		
		private Code hierarchyMeaning;
		
		private String publisher;
		
		private Collection<SupportedConceptProperty> properties = Lists.newArrayList();
		
		public Builder(String codeSystemId) {
			super(codeSystemId);
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		public Builder url(final Uri url) {
			this.url = url;
			return getSelf();
		}
		
		public Builder identifier(final Identifier identifer) {
			this.identifier = identifer;
			return getSelf();
		}

		public Builder version(final String version) {
			this.version = version;
			return getSelf();
		}

		public Builder name(final String name) {
			this.name = name;
			return getSelf();
		}
		
		public Builder description(final String description) {
			this.description = description;
			return getSelf();
		}
		
		public Builder title(final String title) {
			this.title = title;
			return getSelf();
		}

		public Builder status(PublicationStatus status) {
			this.status = status.getCode();
			return getSelf();
		}
		
		public Builder hierarchyMeaning(CodeSystemHierarchyMeaning codeSystemHierarchyMeaning) {
			this.hierarchyMeaning = codeSystemHierarchyMeaning.getCode();
			return getSelf();
		}
		
		public Builder publisher(String publisher) {
			this.publisher = publisher;
			return getSelf();
		}
		
		public Builder addProperty(SupportedConceptProperty property) {
			this.properties.add(property);
			return getSelf();
		}
		
		@Override
		protected CodeSystem doBuild() {
			return new CodeSystem(id, language, text, url, identifier, version, name, title, description, status, hierarchyMeaning, publisher, properties);
		}
	}
		
}
