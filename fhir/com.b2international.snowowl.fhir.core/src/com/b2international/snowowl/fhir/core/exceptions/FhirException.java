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

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;

/**
 * @since 6.3
 */
public class FhirException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	//default outcome code
	private OperationOutcomeCode operationOutcomeCode = OperationOutcomeCode.MSG_BAD_SYNTAX;
	
	private OperationOutcome.Builder operationOutcomeBuilder = OperationOutcome.builder();
		
	public FhirException(Issue issue) {
		operationOutcomeBuilder.addIssue(issue);
	}
	
	public FhirException(Collection<Issue> issues) {
		operationOutcomeBuilder.addIssues(issues);
	}
	
	public FhirException(IssueSeverity issueSeverity, IssueType issueType, String message, OperationOutcomeCode operationOutcomeCode, String location) {
		
		super(message);
		
		Issue issue = Issue.builder()
			.severity(issueSeverity)
			.code(issueType)
			.codeableConceptWithDisplayArgs(operationOutcomeCode, location)
			.diagnostics(message)
			.addLocation(location)
			.build();
		
		operationOutcomeBuilder.addIssue(issue);
	}
	
	public FhirException(IssueSeverity issueSeverity, IssueType issueType, String message, OperationOutcomeCode operationOutcomeCode) {
		
		super(message);
		
		Issue issue = Issue.builder()
			.severity(issueSeverity)
			.code(issueType)
			.codeableConcept(operationOutcomeCode)
			.diagnostics(message)
			.build();
		
		operationOutcomeBuilder.addIssue(issue);
	}
	
	public static FhirException createFhirError(String message, OperationOutcomeCode operationOutcomeCode, String location) {
		return new FhirException(IssueSeverity.ERROR, IssueType.EXCEPTION, message, operationOutcomeCode, location);
	}
	
	public static FhirException createFhirError(String message, OperationOutcomeCode operationOutcomeCode) {
		return new FhirException(IssueSeverity.ERROR, IssueType.EXCEPTION, message, operationOutcomeCode);
	}
	
	/**
	 * Returns the issue type associated with this exception.
	 * Subclasses to override.
	 * @return
	 */
	public IssueType getIssueType() {
		return IssueType.EXCEPTION;
	}
	
	/**
	 * Returns the most generic operation outcome code associated with this exception.
	 * Sublasses may override.
	 * @return
	 */
	public OperationOutcomeCode getOperationOutcomeCode() {
		return operationOutcomeCode;
	}
	
	/**
	 * Creates an OperationOutcome representation from this exception. Useful when the exception must be propagated through protocols where Java serialization
	 * cannot be used (eg. HTTP), or the possible receiver cannot understand serialized Java class and object byte sequences.
	 * 
	 * @return {@link OperationOutcome} representation of this {@link FhirException}, never <code>null</code>.
	 */
	public OperationOutcome toOperationOutcome() {
		return operationOutcomeBuilder.build();
	}


}
