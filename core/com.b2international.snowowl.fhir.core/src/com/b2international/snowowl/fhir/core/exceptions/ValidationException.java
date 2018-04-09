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
package com.b2international.snowowl.fhir.core.exceptions;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;

/**
 * @since 6.3
 */
public final class ValidationException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	private Collection<? extends ConstraintViolation<?>> violations;

	public ValidationException(Collection<? extends ConstraintViolation<?>> violations) {
		super("%s validation error%s", violations.size(), violations.size() == 1 ? "" : "s");
		this.violations = violations;
	}

	/**
	 * Creates an OperationOutcome representation from this exception. Useful when
	 * the exception must be propagated through protocols where Java serialization
	 * cannot be used (eg. HTTP), or the possible receiver cannot understand
	 * serialized Java class and object byte sequences.
	 * 
	 * @return {@link OperationOutcome} representation of this
	 *         {@link FhirException}, never <code>null</code>.
	 */
	@Override
	public OperationOutcome toOperationOutcome() {

		if (violations.isEmpty()) {
			throw new IllegalArgumentException("There are no violations to report");
		}
		
		OperationOutcome operationOutcome = new OperationOutcome();
		
		for (ConstraintViolation<?> violation : violations) {
				
			String issueDetails = String.format(getOperationOutcomeCode().getDisplayName(), violation.getPropertyPath());
			StringBuilder builder = new StringBuilder(issueDetails);
			builder.append(" [");
			builder.append(violation.getInvalidValue());
			builder.append("]. Violation: ");
			builder.append(violation.getMessage());
			builder.append(".");
			
			Coding coding = Coding.builder().
				code(getOperationOutcomeCode().getCodeValue())
				.system(OperationOutcomeCode.CODE_SYSTEM_URI)
				.display(builder.toString())
				.build();
	
			CodeableConcept codeableConcept = new CodeableConcept(coding, builder.toString());
	
			String location = violation.getRootBean().getClass().getSimpleName() 
					+ "." + violation.getPropertyPath().toString();
			
			Issue issue = Issue.builder()
					.severity(IssueSeverity.ERROR)
					.code(IssueType.INVALID)
					.codeableConcept(codeableConcept)
					.diagnostics(getMessage())
					.addLocation(location)
					.build();
	
				operationOutcome.addIssue(issue);
			}
		
		return operationOutcome;
	}

	@Override
	public OperationOutcomeCode getOperationOutcomeCode() {
		return OperationOutcomeCode.MSG_PARAM_INVALID;
	}
	
	/**
	 * @return the violations
	 */
	public Collection<? extends ConstraintViolation<?>> getViolations() {
		return violations;
	}
}
