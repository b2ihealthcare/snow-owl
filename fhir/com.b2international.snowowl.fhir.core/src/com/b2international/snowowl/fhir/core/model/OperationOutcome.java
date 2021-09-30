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
package com.b2international.snowowl.fhir.core.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.AssertTrue;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 *  "resourceType" : "OperationOutcome",
 * // from Resource: id, meta, implicitRules, and language
 * // from DomainResource: text, contained, extension, and modifierExtension
 * "issue" : [{ // R!  A single issue associated with the action
 *   "severity" : "<code>", // R!  fatal | error | warning | information
 *   "code" : "<code>", // R!  Error or warning code
 *   "details" : { CodeableConcept }, // Additional details about the error
 *   "diagnostics" : "<string>", // Additional diagnostic information about the issue
 *   "location" : ["<string>"], // Path of element(s) related to issue
 *   "expression" : ["<string>"] // FHIRPath of element(s) related to issue
 * }]
 * }
 * 
 * @see <a href="http://hl7.org/fhir/operationoutcome.html">FHIR:OperationOutcome</a>
 * @since 6.3
 */
@JsonDeserialize(builder = OperationOutcome.Builder.class)
public final class OperationOutcome extends DomainResource {
	
	private static final long serialVersionUID = 1L;

	public static final String RESOURCE_TYPE_OPERATION_OUTCOME = "OperationOutcome";
	
	@JsonProperty
	private String resourceType;
	
	@NotEmpty
	private final Collection<Issue> issues;
	
	OperationOutcome(Id id, Meta meta, Uri impliciteRules, Code language, Narrative text, 
			String resourceType, Collection<Issue> issues) {
		
		super(id, meta, impliciteRules, language, text);
		
		this.resourceType = resourceType;
		this.issues = issues;
	}
	
	@AssertTrue(message = "Resource type must be 'OperationOutcome'")
	private boolean isResourceTypeValid() {
		return RESOURCE_TYPE_OPERATION_OUTCOME.equals(resourceType);
	}

	@JsonProperty("issue")
	public Collection<Issue> getIssues() {
		return issues;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends DomainResource.Builder<Builder, OperationOutcome> {

		private String resourceType  = RESOURCE_TYPE_OPERATION_OUTCOME;
		
		private Collection<Issue> issues;
		
		public Builder resourceType(final String resourceType) {
			this.resourceType = resourceType;
			return getSelf();
		}
		
		public Builder addIssue(Issue issue) {
			if (this.issues == null) {
				this.issues = new ArrayList<>();
			}
			issues.add(issue);
			return getSelf();
		}
		
		public Builder addIssues(Collection<Issue> outComeIssues) {
			if (this.issues == null) {
				this.issues = new ArrayList<>();
			}
			issues.addAll(outComeIssues);
			return getSelf();
		}
		
		@JsonProperty("issue")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder issues(Collection<Issue> issues) {
			this.issues = issues;
			return getSelf();
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected OperationOutcome doBuild() {
			return new OperationOutcome(id, meta, implicitRules, language, text, 
					resourceType, issues);
		}


	}
	
}
