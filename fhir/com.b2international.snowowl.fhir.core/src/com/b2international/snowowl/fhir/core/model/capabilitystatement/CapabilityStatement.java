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
package com.b2international.snowowl.fhir.core.model.capabilitystatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.operationdefinition.OperationDefinition.Builder;
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
 * This class represents a FHIR Capability Statement.  
 * A Capability Statement documents a set of capabilities of a FHIR Server. 
 * 
 * @see <a href="https://www.hl7.org/fhir/capabilitystatement.html">CapabilityStatement</a>
 * @since 8.0.0
 */
@JsonDeserialize(builder = CapabilityStatement.Builder.class, using = JsonDeserializer.None.class)
public class CapabilityStatement extends MetadataResource {

	private static final long serialVersionUID = 1L;
	
	public static final String RESOURCE_TYPE_CAPABILITY_STATEMENT = "CapabilityStatement";
	
	@Mandatory
	@JsonProperty
	private final String resourceType;
	
	@Summary
	@JsonProperty("identifier")
	@JsonInclude(value = Include.NON_EMPTY)
	private Collection<Identifier> identifiers;

	@JsonProperty
	private String copyright;
	
	@Mandatory
	@Valid
	@NotNull
	@JsonProperty
	private final Code kind;
	
	@Summary
	@Valid
	@JsonProperty
	private final Collection<Uri> instantiates;
	
	@Summary
	@Valid
	private final Collection<Uri> imports;
	
	@Summary
	@Valid
	@JsonProperty
	private final Software software;
	
	@Summary
	@Valid
	@JsonProperty
	private final Implementation implementation;
	
	@Valid
	@Mandatory
	@JsonProperty
	private final Id fhirVersion;
	
	//Should be bound to http://hl7.org/fhir/ValueSet/mimetypes
	@Mandatory
	@Valid
	@NotEmpty
	@JsonProperty("format")
	private final Collection<Code> formats;

	@Summary
	@Valid
	@JsonProperty("patchFormat")
	private final Collection<Code> patchFormats;
	
	@Summary
	@Valid
	@JsonProperty("implementationGuide")
	private final Collection<Uri> implementationGuides;
	
	@Summary
	@Valid
	@JsonProperty("rest")
	private final Collection<Rest> rests;
	
	@Valid
	@JsonProperty("messaging")
	private final Collection<Messaging> messagings;
	
	@Valid
	@JsonProperty("document")
	private final Collection<Document> documents;
	
	public CapabilityStatement(Id id, Meta meta, Uri impliciteRules, Code language, Narrative text, Uri url,
			String version, String name, String title, Code status,
			Boolean experimental, Date date, String publisher, Collection<ContactDetail> contacts, String description,
			@SuppressWarnings("rawtypes") Collection<UsageContext> usageContexts, Collection<CodeableConcept> jurisdictions, String purpose,
			String toolingId,
			
			final String resourceType,
			final Collection<Identifier> identifiers,
			final String copyright,
			final Code kind,
			final Collection<Uri> instantiates,
			final Collection<Uri> imports,
			final Software software,
			final Implementation implementation,
			final Id fhirVersion,
			final Collection<Code> formats,
			final Collection<Code> patchFormats,
			final Collection<Uri> implementationGuides,
			final Collection<Rest> rests,
			final Collection<Messaging> messagings,
			final Collection<Document> documents) {
		
		super(id, meta, impliciteRules, language, text, url, version, name, title, status, experimental, date,
				publisher, contacts, description, usageContexts, jurisdictions, purpose, toolingId);
	
		this.resourceType = resourceType;
		this.identifiers = identifiers;
		this.copyright = copyright;
		this.kind = kind;
		this.instantiates = instantiates;
		this.imports = imports;
		this.software = software;
		this.implementation = implementation;
		this.fhirVersion = fhirVersion;
		this.formats = formats;
		this.patchFormats = patchFormats;
		this.implementationGuides = implementationGuides;
		this.rests = rests;
		this.messagings = messagings;
		this.documents = documents;
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
	
	public Code getKind() {
		return kind;
	}
	
	public Collection<Uri> getInstantiates() {
		return instantiates;
	}
	
	public Collection<Uri> getImports() {
		return imports;
	}
	
	public Software getSoftware() {
		return software;
	}
	
	public Implementation getImplementation() {
		return implementation;
	}
	
	public Id getFhirVersion() {
		return fhirVersion;
	}
	
	public Collection<Code> getFormats() {
		return formats;
	}
	
	public Collection<Code> getPatchFormats() {
		return patchFormats;
	}
	
	public Collection<Uri> getImplementationGuides() {
		return implementationGuides;
	}
	
	public Collection<Rest> getRests() {
		return rests;
	}
	
	public Collection<Messaging> getMessagings() {
		return messagings;
	}
	
	public Collection<Document> getDocuments() {
		return documents;
	}
	
	public static Builder builder(String id) {
		return new Builder(id);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends MetadataResource.Builder<Builder, CapabilityStatement> {

		private String resourceType = RESOURCE_TYPE_CAPABILITY_STATEMENT;
		private Collection<Identifier> identifiers;
		private String copyright;
		private Code kind;
		private Collection<Uri> instantiates;
		private Collection<Uri> imports;
		private Software software;
		private Implementation implementation;
		private Id fhirVersion;
		private Collection<Code> formats;
		private Collection<Code> patchFormats;
		private Collection<Uri> implementationGuides;
		private Collection<Rest> rests;
		private Collection<Messaging> messagings;
		private Collection<Document> documents;
		
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
		
		public Builder kind(final Code kind) {
			this.kind = kind;
			return getSelf();
		}

		public Builder kind(final String kind) {
			this.kind = new Code(kind);
			return getSelf();
		}
		
		@JsonProperty("instantiates")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder instantiates(final Collection<Uri> instantiates) {
			this.instantiates = instantiates;
			return getSelf();
		}
		
		public Builder addInstantiate(final Uri instantiate) {
			if (instantiates == null) {
				instantiates = new ArrayList<>();
			}
			instantiates.add(instantiate);
			return getSelf();
		}
		
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder imports(final Collection<Uri> imports) {
			this.imports = imports;
			return getSelf();
		}
		
		public Builder addImport(final Uri importUri) {
			if (imports == null) {
				imports = new ArrayList<>();
			}
			imports.add(importUri);
			return getSelf();
		}
		
		public Builder software(final Software software) {
			this.software = software;
			return getSelf();
		}

		public Builder implementation(final Implementation implementation) {
			this.implementation = implementation;
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
		
		@JsonProperty("format")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder formats(final Collection<Code> formats) {
			this.formats = formats;
			return getSelf();
		}
		
		public Builder addFormat(final Code format) {
			if (formats == null) {
				formats = new ArrayList<>();
			}
			formats.add(format);
			return getSelf();
		}

		@JsonProperty("patchFormat")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder patchFormats(final Collection<Code> patchFormats) {
			this.patchFormats = patchFormats;
			return getSelf();
		}
		
		public Builder addPatchFormat(final Code patchFormat) {
			if (patchFormats == null) {
				patchFormats = new ArrayList<>();
			}
			patchFormats.add(patchFormat);
			return getSelf();
		}
		
		@JsonProperty("implementationGuide")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder implementationGuides(final Collection<Uri> implementationGuides) {
			this.implementationGuides = implementationGuides;
			return getSelf();
		}
		
		public Builder addImplementationGuide(final Uri implementationGuide) {
			if (implementationGuides == null) {
				implementationGuides = new ArrayList<>();
			}
			implementationGuides.add(implementationGuide);
			return getSelf();
		}

		@JsonProperty("rest")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder rests(final Collection<Rest> rests) {
			this.rests = rests;
			return getSelf();
		}
		
		public Builder addRest(final Rest rest) {
			if (rests == null) {
				rests = new ArrayList<>();
			}
			rests.add(rest);
			return getSelf();
		}
		
		@JsonProperty("messaging")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder messagings(Collection<Messaging> messagings) {
			this.messagings = messagings;
			return this;
		}
		
		public Builder addMessaging(final Messaging messaging) {
			if (messagings == null) {
				messagings = Lists.newArrayList();
			}
			messagings.add(messaging);
			return this;
		}
		
		@JsonProperty("document")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder documents(Collection<Document> documents) {
			this.documents = documents;
			return this;
		}
		
		public Builder addDocument(final Document document) {
			if (documents == null) {
				documents = Lists.newArrayList();
			}
			documents.add(document);
			return this;
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected CapabilityStatement doBuild() {
			return new CapabilityStatement(id, meta, implicitRules, language, text, url, version, name, title,
					status, experimental, date, publisher, contacts, description, usageContexts, jurisdictions, purpose, toolingId,
					
			resourceType,
			identifiers,
			copyright,
			kind,
			instantiates,
			imports,
			software,
			implementation,
			fhirVersion,
			formats,
			patchFormats,
			implementationGuides,
			rests,
			messagings,
			documents);
		}
	}
	
	
}
