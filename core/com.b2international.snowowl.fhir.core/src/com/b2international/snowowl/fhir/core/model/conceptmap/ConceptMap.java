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
package com.b2international.snowowl.fhir.core.model.conceptmap;

import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;

import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.MetadataResource;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Reference;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModel;

/**
 * A concept map defines a mapping from a set of concepts defined in a code
 * system to one or more concepts defined in other code systems.
 * 
 * Key properties:
 * <ul>
 * <li>Each mapping for a concept from source to target includes an equivalence
 * property that specifies how similar the mapping is
 * <li>There is one element for each concept or field in the source that needs
 * to be mapped
 * <li>Each source concept may have multiple targets
 * </ul>
 * 
 * @see <a href="https://www.hl7.org/fhir/conceptmap.html">FHIR:ConceptMap</a>
 * @since 6.10
 */
@ApiModel("ConceptMap")
@JsonFilter(FhirBeanPropertyFilter.FILTER_NAME)
public class ConceptMap extends MetadataResource {

	// FHIR header "resourceType" : "ConceptMap",
	@Mandatory
	@JsonProperty
	private final String resourceType = "ConceptMap";

	@Summary
	@Valid
	private Uri sourceUri;

	@Summary
	@Valid
	private Reference sourceReference;

	@Summary
	@Valid
	private Uri targetUri;

	@Summary
	@Valid
	private Reference targetReference;

	@Valid
	@JsonProperty("group")
	private final Collection<Group> groups;

	@JsonProperty
	public Uri getSourceUri() {
		return sourceUri;
	}

	@JsonProperty
	public Reference getSourceReference() {
		return sourceReference;
	}

	@JsonProperty
	public Uri getTargetUri() {
		return targetUri;
	}

	@JsonProperty
	public Reference getTargetReference() {
		return targetReference;
	}

	@SuppressWarnings("rawtypes")
	public ConceptMap(Id id, Meta meta, Uri impliciteRules, Code language, Narrative text, Uri url,
			Identifier identifier, String version, String name, String title, Code status, Date date, String publisher,
			Collection<ContactDetail> contacts, String description, Collection<UsageContext> usageContexts,
			Collection<CodeableConcept> jurisdictions, String purpose, String copyright, Uri sourceUri,
			Reference sourceReference, Uri targetUri, Reference targetReference, Collection<Group> groups) {
		
		super(id, meta, impliciteRules, language, text, url, identifier, version, name, title, status, date, publisher,
				contacts, description, usageContexts, jurisdictions, purpose, copyright);
		
		this.sourceUri = sourceUri;
		this.sourceReference = sourceReference;
		this.targetUri = targetUri;
		this.targetReference = targetReference;
		this.groups = groups;
	}

	@AssertTrue(message = "Both URI and Reference cannot be set for the 'source' and 'target' fields")
	private boolean isValid() {

		if (sourceUri != null && sourceReference != null) {
			return false;
		}

		if (targetUri != null && targetReference != null) {
			return false;
		}
		return true;
	}

	public static Builder builder(String conceptMapId) {
		return new Builder(conceptMapId);
	}

	public static class Builder extends MetadataResource.Builder<Builder, ConceptMap> {

		private Uri sourceUri;
		private Reference sourceReference;
		private Uri targetUri;
		private Reference targetReference;
		private Collection<Group> groups = Lists.newArrayList();

		public Builder(String conceptMapId) {
			super(conceptMapId);
		}

		public Builder sourceUri(Uri sourceUri) {
			this.sourceUri = sourceUri;
			return getSelf();
		}

		public Builder sourceUri(String sourceUriString) {
			this.sourceUri = new Uri(sourceUriString);
			return getSelf();
		}

		public Builder sourceUri(Reference sourceReference) {
			this.sourceReference = sourceReference;
			return getSelf();
		}

		public Builder targetUri(Uri targetUri) {
			this.targetUri = targetUri;
			return getSelf();
		}

		public Builder targetUri(String targetUriString) {
			this.targetUri = new Uri(targetUriString);
			return getSelf();
		}

		public Builder targetReference(Reference targetReference) {
			this.targetReference = targetReference;
			return getSelf();
		}
		
		public Builder groups(Collection<Group> groups) {
			this.groups = groups;
			return getSelf();
		}

		public Builder addGroup(final Group group) {
			this.groups.add(group);
			return getSelf();
		}

		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected ConceptMap doBuild() {
			return new ConceptMap(id, meta, implicitRules, language, text, url, identifier, version, name, title,
					status, date, publisher, contacts, description, usageContexts, jurisdictions, purpose, copyright,
					sourceUri, sourceReference, targetUri, targetReference, groups);
		}

	}

}
