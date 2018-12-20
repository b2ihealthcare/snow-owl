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
import java.util.Date;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.model.conceptmap.Group;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Reference;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

/**
 * This class represents a FHIR Structure Definition.  
 * This resource is used to describe the underlying resources, data types defined in FHIR, 
 * and also for describing extensions and constraints on resources and data types.
 * 
 * @see <a href="https://www.hl7.org/fhir/structuredefinition.html">FHIR:StructureDefinition</a>
 * @since 7.1
 */
@ApiModel("ConceptMap")
@JsonFilter(FhirBeanPropertyFilter.FILTER_NAME)
public class StructureDefinition extends MetadataResource {

	// FHIR header "resourceType" : "StructureDefinition",
	@Mandatory
	@JsonProperty
	private final String resourceType = "StructureDefinition";
	
	/*
	
	@Valid
	@JsonProperty("keyword")
	private final Set<Coding> keywords;
	
	private final Id fhirVersion;
	
	@JsonProperty("mapping")
	private final Collection<Mapping> mappings;
	
	@Mandatory
	@NotNull
	@Valid
	@JsonProperty
	private final Code kind;
	
	@Mandatory
	@NotNull
	@Valid
	@JsonProperty("abstract")
	private final boolean isAbstract;
	
	private final Code contextType;
	
	@JsonProperty("context")
	private final Collection<String> contexts;
	
	@JsonProperty("contextInvariant")
	private final Collection<String> contextInvariants;
	
	private final Code type;
	
	private final Uri baseDefinition;
	
	private final Code derivation;
	
	private final StructureView snapshot;
	
	private final StructureView differential;
	*/
	
	@SuppressWarnings("rawtypes")
	public StructureDefinition(Id id, Meta meta, Uri impliciteRules, Code language, Narrative text, Uri url,
			Identifier identifier, String version, String name, String title, Code status, Date date, String publisher,
			Collection<ContactDetail> contacts, String description, Collection<UsageContext> usageContexts,
			Collection<CodeableConcept> jurisdictions, String purpose, String copyright, 
			
			Uri sourceUri, Reference sourceReference, Uri targetUri, Reference targetReference, Collection<Group> groups) {
		
		super(id, meta, impliciteRules, language, text, url, identifier, version, name, title, status, date, publisher,
				contacts, description, usageContexts, jurisdictions, purpose, copyright);
		
	}
	
}
