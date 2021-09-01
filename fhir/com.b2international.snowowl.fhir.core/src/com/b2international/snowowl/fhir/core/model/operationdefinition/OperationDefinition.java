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
package com.b2international.snowowl.fhir.core.model.operationdefinition;

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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * This class represents a FHIR Operation Definition providing a formal computable definition of an operation or a named query. 
 * 
 * @see <a href="https://www.hl7.org/fhir/operationdefinition.html">OperationDefinition</a>
 * @since 8.0.0
 */
@JsonDeserialize(builder = OperationDefinition.Builder.class, using = JsonDeserializer.None.class)
public class OperationDefinition extends MetadataResource {

	private static final long serialVersionUID = 1L;
	
	public static final String RESOURCE_TYPE_OPERATION_DEFINITION = "OperationDefinition";
	
	@Mandatory
	@JsonProperty
	private final String resourceType;
	
	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Code kind;
	
	@Summary
	@JsonProperty
	private Boolean affectState;
	
	@Mandatory
	@Valid
	@NotNull
	@JsonProperty
	private Code code;
	
	@Summary
	@JsonProperty
	private String comment;
	
	@Valid
	@Summary
	@JsonProperty
	private final Uri base;
	
	@Summary
	@JsonProperty("resource")
	private final Collection<Code> resources;
	
	@Summary
	@NotNull
	@Mandatory
	@JsonProperty
	private Boolean system;

	@Summary
	@NotNull
	@Mandatory
	@JsonProperty
	private Boolean type;
	
	@Summary
	@NotNull
	@Mandatory
	@JsonProperty
	private Boolean instance;
	
	@Valid
	@Summary
	@JsonProperty
	private final Uri inputProfile;
	
	@Valid
	@Summary
	@JsonProperty
	private final Uri outputProfile;
	
	@Summary
	@JsonProperty("parameter")
	private final Collection<Parameter> parameters;
	
	
	@Summary
	@JsonProperty("overload")
	private final Collection<Overload> overloads;
	
	
	public OperationDefinition(Id id, Meta meta, Uri impliciteRules, Code language, Narrative text, Uri url,
			String version, String name, String title, Code status,
			Boolean experimental, Date date, String publisher, Collection<ContactDetail> contacts, String description,
			@SuppressWarnings("rawtypes") Collection<UsageContext> usageContexts, Collection<CodeableConcept> jurisdictions, String purpose,
			String toolingId,
			
			final String resourceType,
			final Code kind,
			final Boolean affectState,
			final Code code,
			final String comment,
			final Uri base,
			final Collection<Code> resources,
			final Boolean system,
			final Boolean type,
			final Boolean instance,
			final Uri inputProfile,
			final Uri outputProfile,
			final Collection<Parameter> parameters,
			final Collection<Overload> overloads) {
		
		super(id, meta, impliciteRules, language, text, url, version, name, title, status, experimental, date,
				publisher, contacts, description, usageContexts, jurisdictions, purpose, toolingId);
	
		this.resourceType = resourceType;
		this.kind = kind;
		this.affectState = affectState;
		this.code = code;
		this.comment = comment;
		this.base = base;
		this.resources = resources;
		this.system = system;
		this.type = type;
		this.instance = instance;
		this.inputProfile = inputProfile;
		this.outputProfile = outputProfile;
		this.parameters = parameters;
		this.overloads = overloads;
	}
	
	public String getResourceType() {
		return resourceType;
	}
	
	public Code getKind() {
		return kind;
	}
	
	public Boolean getAffectState() {
		return affectState;
	}
	
	public Code getCode() {
		return code;
	}
	
	public String getComment() {
		return comment;
	}
	
	public Uri getBase() {
		return base;
	}
	
	public Collection<Code> getResources() {
		return resources;
	}
	
	public Boolean getSystem() {
		return system;
	}
	
	public Boolean getType() {
		return type;
	}
	
	public Boolean getInstance() {
		return instance;
	}
	
	public Uri getInputProfile() {
		return inputProfile;
	}
	
	public Uri getOutputProfile() {
		return outputProfile;
	}
	
	public Collection<Parameter> getParameters() {
		return parameters;
	}
	
	public Collection<Overload> getOverloads() {
		return overloads;
	}
	
	public static Builder builder(String id) {
		return new Builder(id);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends MetadataResource.Builder<Builder, OperationDefinition> {

		private String resourceType = RESOURCE_TYPE_OPERATION_DEFINITION;
		private Code kind;
		private Boolean affectState;
		private Code code;
		private String comment;
		private Uri base;
		private Collection<Code> resources;
		private Boolean system;
		private Boolean type;
		private Boolean instance;
		private Uri inputProfile;
		private Uri outputProfile;
		private Collection<Parameter> parameters;
		private Collection<Overload> overloads;
		
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
		
		public Builder kind(final Code kind) {
			this.kind = kind;
			return getSelf();
		}

		public Builder kind(final String kind) {
			this.kind = new Code(kind);
			return getSelf();
		}
		
		public Builder affectState(final Boolean affectState) {
			this.affectState = affectState;
			return getSelf();
		}
		
		public Builder code(final Code code) {
			this.code = code;
			return getSelf();
		}

		public Builder code(final String code) {
			this.code = new Code(code);
			return getSelf();
		}
		
		public Builder comment(String comment) {
			this.comment = comment;
			return getSelf();
		}
		
		public Builder base(Uri base) {
			this.base = base;
			return getSelf();
		}
		
		public Builder base(String base) {
			this.base = new Uri(base);
			return getSelf();
		}
		
		@JsonProperty("resource")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder resources(final Collection<Code> resources) {
			this.resources = resources;
			return getSelf();
		}
		
		public Builder addResource(final Code resource) {
			if (resources == null) {
				resources = new ArrayList<>();
			}
			resources.add(resource);
			return getSelf();
		}
		
		public Builder system(final Boolean system) {
			this.system = system;
			return getSelf();
		}

		public Builder type(final Boolean type) {
			this.type = type;
			return getSelf();
		}
		
		public Builder instance(final Boolean instance) {
			this.instance = instance;
			return getSelf();
		}
		
		public Builder inputProfile(Uri inputProfile) {
			this.inputProfile = inputProfile;
			return getSelf();
		}
		
		public Builder inputProfile(String inputProfile) {
			this.inputProfile = new Uri(inputProfile);
			return getSelf();
		}
		
		public Builder outputProfile(Uri outputProfile) {
			this.outputProfile = outputProfile;
			return getSelf();
		}
		
		public Builder outputProfile(String outputProfile) {
			this.outputProfile = new Uri(outputProfile);
			return getSelf();
		}
		
		@JsonProperty("parameter")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder parameters(final Collection<Parameter> parameters) {
			this.parameters = parameters;
			return getSelf();
		}
		
		public Builder addParameter(final Parameter parameter) {
			if (parameters == null) {
				parameters = new ArrayList<>();
			}
			parameters.add(parameter);
			return getSelf();
		}
		
		@JsonProperty("overload")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder overloads(final Collection<Overload> overloads) {
			this.overloads = overloads;
			return getSelf();
		}
		
		public Builder addOverload(final Overload overload) {
			if (overloads == null) {
				overloads = new ArrayList<>();
			}
			overloads.add(overload);
			return getSelf();
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected OperationDefinition doBuild() {
			return new OperationDefinition(id, meta, implicitRules, language, text, url, version, name, title,
					status, experimental, date, publisher, contacts, description, usageContexts, jurisdictions, purpose, toolingId,
					
			resourceType, kind, affectState, code, comment, base, resources, system, type, instance,
			inputProfile, outputProfile, parameters, overloads);
		}
	}
	
	
}
