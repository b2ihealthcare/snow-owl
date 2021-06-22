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

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

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
@ApiModel("Operation outcome")
public final class OperationOutcome {
	
	@JsonProperty
	private String resourceType = "OperationOutcome";
	
	@NotEmpty
	private final Collection<Issue> issues;
	
	OperationOutcome(Collection<Issue> issues) {
		this.issues = issues;
	}

	@JsonProperty("issue")
	public Collection<Issue> getIssues() {
		return issues;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<OperationOutcome> {

		private Collection<Issue> issues;
		
		public Builder addIssue(Issue issue) {
			if (this.issues == null) {
				this.issues = new ArrayList<>();
			}
			issues.add(issue);
			return this;
		}
		
		public void addIssues(Collection<Issue> outComeIssues) {
			if (this.issues == null) {
				this.issues = new ArrayList<>();
			}
			issues.addAll(outComeIssues);
		}

		@Override
		protected OperationOutcome doBuild() {
			return new OperationOutcome(issues);
		}


	}
	
}
