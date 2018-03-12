/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.exceptions;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import com.b2international.snowowl.fhir.api.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.api.codesystems.IssueType;
import com.b2international.snowowl.fhir.api.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.api.model.Issue;
import com.b2international.snowowl.fhir.api.model.OperationOutcome;
import com.b2international.snowowl.fhir.api.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.api.model.dt.Coding;


/**
 * @since 4.1.1
 */
public final class ValidationException extends BadRequestException {

	public ValidationException(Collection<? extends ConstraintViolation<?>> violations) {
		super("%s validation error%s", violations.size(), violations.size() == 1 ? "" : "s");
		//final Builder<String, Object> builder = ImmutableMap.<String, Object>builder();
		//this.additionalInfo = builder.put("violations", ConstraintViolations.format(violations)).build();
	}
	
	private Object invalidValue;
	
	private String parameterName;
	
	/**
	 * Creates an OperationOutcome representation from this exception. Useful when the exception must be propagated through protocols where Java serialization
	 * cannot be used (eg. HTTP), or the possible receiver cannot understand serialized Java class and object byte sequences.
	 * 
	 * @return {@link OperationOutcome} representation of this {@link FhirException}, never <code>null</code>.
	 */
	public OperationOutcome toOperationOutcome() {
		OperationOutcomeCode operationOutcomeCode = OperationOutcomeCode.MSG_PARAM_INVALID;
		String text = null;
		
		if (invalidValue ==null) {
			text = String.format(operationOutcomeCode.displayName(), parameterName);
		}
		//operationOutcomeCode.getCodeValue()
		Coding coding = Coding.builder()
			.code(operationOutcomeCode.getCodeValue())	
			.display(text)
			.build();
		
		System.out.println("Coding " + coding);
		CodeableConcept codeableConcept = new CodeableConcept(coding, text);
		
		OperationOutcome operationOutcome = new OperationOutcome();
		Issue issue = Issue.builder()
				.severity(IssueSeverity.ERROR)
				.code(IssueType.INVALID)
				.codeableConcept(getOperationOutcomeCode())
				.diagnostics(getMessage())
				.build();
		
		operationOutcome.addIssue(issue);
		return operationOutcome;
	}
	
//	@Override
//	protected Map<String, Object> getAdditionalInfo() {
//		return additionalInfo;
//	}
//	
}
