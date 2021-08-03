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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Capability statement Resource backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Resource.Builder.class)
public class Resource {
	
	@Mandatory
	@NotNull
	@Valid
	@JsonProperty
	private final Code type;
	
	@Summary
	@Valid
	@JsonProperty
	private final Uri profile;
	
	@Summary
	@Valid
	@JsonProperty("supportedProfile")
	private final Collection<Uri> supportedProfiles;
	
	@JsonProperty
	private final String documentation;
	
	@Valid
	@JsonProperty("interaction")
	private final Collection<Interaction> interactions;
	
	@Valid
	@JsonProperty
	private final Code versioning;
	
	@JsonProperty
	private final Boolean readHistory;

	@JsonProperty
	private final Boolean updateCreate;
	
	@JsonProperty
	private final Boolean conditionalCreate;
	
	@Valid
	@JsonProperty
	private final Code conditionalRead;
	
	@JsonProperty
	private final Boolean conditionalUpdate;
	
	@Valid
	@JsonProperty
	private final Code conditionalDelete;
	
	@Valid
	@JsonProperty("referencePolicy")
	private final Collection<Code> referencePolicies;
	
	@JsonProperty("searchInclude")
	private final Collection<String> searchIncludes;

	@JsonProperty("searchRevInclude")
	private final Collection<String> searchRevIncludes;

	@JsonProperty("searchParam")
	private final Collection<SearchParam> searchParams;

	@JsonProperty("operation")
	private final Collection<Operation> operations;
	
	Resource(
			final Code type,
			final Uri profile,
			final Collection<Uri> supportedProfiles,
			final String documentation,
			final Collection<Interaction> interactions,
			final Code versioning,
			final Boolean readHistory,
			final Boolean updateCreate,
			final Boolean conditionalCreate,
			final Code conditionalRead,
			final Boolean conditionalUpdate,
			final Code conditionalDelete,
			final Collection<Code> referencePolicies,
			final Collection<String> searchIncludes,
			final Collection<String> searchRevIncludes,
			final Collection<SearchParam> searchParams,
			final Collection<Operation> operations) {
		
		this.type = type;
		this.profile = profile;
		this.supportedProfiles = supportedProfiles;
		this.documentation = documentation;
		this.interactions = interactions;
		this.versioning = versioning;
		this.readHistory = readHistory;
		this.updateCreate = updateCreate;
		this.conditionalCreate = conditionalCreate;
		this.conditionalRead = conditionalRead;
		this.conditionalUpdate = conditionalUpdate;
		this.conditionalDelete = conditionalDelete;
		this.referencePolicies = referencePolicies;
		this.searchIncludes = searchIncludes;
		this.searchRevIncludes = searchRevIncludes;
		this.searchParams = searchParams;
		this.operations = operations;
		
	}
	
	public Code getType() {
		return type;
	}
	
	public Uri getProfile() {
		return profile;
	}
	public Collection<Uri> getSupportedProfiles() {
		return supportedProfiles;
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public Collection<Interaction> getInteractions() {
		return interactions;
	}
	
	public Code getVersioning() {
		return versioning;
	}
	
	public Boolean getReadHistory() {
		return readHistory;
	}
	
	public Boolean getUpdateCreate() {
		return updateCreate;
	}
	
	public Boolean getConditionalCreate() {
		return conditionalCreate;
	}
	
	public Code getConditionalRead() {
		return conditionalRead;
	}
	
	public Boolean getConditionalUpdate() {
		return conditionalUpdate;
	}
	
	public Code getConditionalDelete() {
		return conditionalDelete;
	}
	
	public Collection<Code> getReferencePolicies() {
		return referencePolicies;
	}
	
	public Collection<String> getSearchIncludes() {
		return searchIncludes;
	}
	
	public Collection<String> getSearchRevIncludes() {
		return searchRevIncludes;
	}
	
	public Collection<SearchParam> getSearchParams() {
		return searchParams;
	}
	
	public Collection<Operation> getOperations() {
		return operations;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Resource> {
		
		private Code type;
		private Uri profile;
		private Collection<Uri> supportedProfiles;
		private String documentation;
		private Collection<Interaction> interactions;
		private Code versioning;
		private Boolean readHistory;
		private Boolean updateCreate;
		private Boolean conditionalCreate;
		private Code conditionalRead;
		private Boolean conditionalUpdate;
		private Code conditionalDelete;
		private Collection<Code> referencePolicies;
		private Collection<String> searchIncludes;
		private Collection<String> searchRevIncludes;
		private Collection<SearchParam> searchParams;
		private Collection<Operation> operations;
		
		public Builder type(final Code type) {
			this.type = type;
			return this;
		}

		public Builder profile(final String profile) {
			this.profile = new Uri(profile);
			return this;
		}
		
		public Builder profile(final Uri profile) {
			this.profile = profile;
			return this;
		}

		public Builder type(final String type) {
			this.type = new Code(type);
			return this;
		}
		
		@Override
		protected Resource doBuild() {
			return new Resource(type, profile, supportedProfiles, documentation, interactions,
					versioning, readHistory, updateCreate, conditionalCreate, conditionalRead, 
					conditionalUpdate, conditionalDelete, referencePolicies, searchIncludes, searchRevIncludes,
					searchParams, operations);
		}
	}

}
