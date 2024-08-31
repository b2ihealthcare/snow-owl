/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.exceptions;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.codesystems.OperationOutcomeCode;
import org.hl7.fhir.r5.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r5.model.OperationOutcome.IssueType;
import org.hl7.fhir.r5.model.OperationOutcome.OperationOutcomeIssueComponent;

import jakarta.validation.ConstraintViolation;

/**
 * @since 6.3
 */
public final class ValidationException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	private Collection<? extends ConstraintViolation<?>> violations;

	public ValidationException(Collection<? extends ConstraintViolation<?>> violations) {
		
		super(String.format("%s validation error%s", violations.size(), violations.size() == 1 ? "" : "s"),
				OperationOutcomeCode.MSGPARAMINVALID, null);
		
		if (violations.isEmpty()) {
			throw new IllegalArgumentException("There are no violations to report.");
		}
		this.violations = violations;
	}

	@Override
	protected List<OperationOutcomeIssueComponent> getAdditionalIssues() {
		return violations.stream().map(violation -> {
			String issueDetails = String.format(getOperationOutcomeCode().getDisplay(), violation.getPropertyPath());
			StringBuilder message = new StringBuilder(issueDetails);
			message.append(" [");
			message.append(violation.getInvalidValue());
			message.append("]. Violation: ");
			message.append(violation.getMessage());
			message.append(".");
			
			String location = String.join(".", violation.getRootBean().getClass().getSimpleName(), violation.getPropertyPath().toString()); 
			
			return buildIssue(IssueSeverity.ERROR, IssueType.INVALID, message.toString(), getOperationOutcomeCode(), location);
		}).toList();
	}
	
	/**
	 * @return the violations
	 */
	public Collection<? extends ConstraintViolation<?>> getViolations() {
		return violations;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
        
        Collection<? extends ConstraintViolation<?>> violations = getViolations();
        if (!violations.isEmpty()) {
	        sb.append(" : ");
	       
	        String violationMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
	        
	       sb.append(violationMessages);
        }
        return sb.toString();
    }
	
}
