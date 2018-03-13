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
package com.b2international.snowowl.fhir.api.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.exceptions.ValidationException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModel;

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
public class OperationOutcome {
	
	//FHIR header "resourceType" : "Parameters",
	@JsonProperty
	private String resourceType = "OperationOutcome";
	
	@JsonProperty("issue")
	@NotEmpty //TODO: this should be called from somewhere, how about the validatingBuilder?
	private Collection<Issue> issues = Lists.newArrayList();
	
	public void addIssue(final Issue issue) {
		issues.add(issue);
	}
	
	public Collection<Issue> getIssues() {
		return issues;
	}
	
	/**
	 * Validates this operation outcome.
	 * @throws ValidationException
	 */
	public void validate() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<OperationOutcome>> violations = validator.validate(this);
			if (!violations.isEmpty()) {
				//bit of a hack
				ValidationException validationException = new ValidationException(violations);
				Map<String, Object> additionalInfo = validationException.toApiError().getAdditionalInfo();
				throw new javax.validation.ValidationException(additionalInfo.toString());
			}
	}

}
