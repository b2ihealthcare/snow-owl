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

import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.structuredefinition.StructureDefinition;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * This class represents a FHIR Capability Statement.  
 * A Capability Statement documents a set of capabilities of a FHIR Server. 
 * 
 * @see <a href="https://www.hl7.org/fhir/capabilitystatement.html">CapabilityStatement</a>
 * @since 8.0.0
 */
@JsonFilter(FhirBeanPropertyFilter.FILTER_NAME)
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
	@JsonProperty
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
	@JsonProperty("implmenentationGuide")
	private final Collection<Uri> implementationGuides;
	
	@Summary
	@Valid
	@JsonProperty("rest")
	private final Collection<Rest> rests;
	
	public CapabilityStatement(Id id, Meta meta, Uri impliciteRules, Code language, Narrative text, Uri url,
			String version, String name, String title, Code status,
			Boolean experimental, Date date, String publisher, Collection<ContactDetail> contacts, String description,
			@SuppressWarnings("rawtypes") Collection<UsageContext> usageContexts, Collection<CodeableConcept> jurisdictions, String purpose,
			String copyright,
			
			final String resourceType,
			final Collection<Identifier> identifiers,
			final Code kind,
			final Collection<Uri> instantiates,
			final Collection<Uri> imports,
			final Software software,
			final Implementation implementation,
			final Id fhirVersion,
			final Collection<Code> formats,
			final Collection<Code> patchFormats,
			final Collection<Uri> implementationGuides,
			final Collection<Rest> rests
			
			) {
		
		super(id, meta, impliciteRules, language, text, url, version, name, title, status, experimental, date,
				publisher, contacts, description, usageContexts, jurisdictions, purpose, copyright);
	
		this.resourceType = resourceType;
		this.identifiers = identifiers;
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
	
	}
	
	
}
