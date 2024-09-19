/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.List;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r5.model.OperationOutcome.IssueType;
import org.hl7.fhir.r5.model.OperationOutcome.OperationOutcomeIssueComponent;

import com.b2international.commons.exceptions.ApiException;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 6.3
 */
public class FhirException extends ApiException {

	private static final long serialVersionUID = 2L;
	
	private final IssueSeverity issueSeverity;
	private final IssueType issueType;
	private final org.hl7.fhir.r4.model.codesystems.OperationOutcome operationOutcomeCode;
	private final String location;
		
	public FhirException(String message, org.hl7.fhir.r4.model.codesystems.OperationOutcome operationOutcomeCode) {
		this(message, operationOutcomeCode, null);
	}
	
	public FhirException(String message, org.hl7.fhir.r4.model.codesystems.OperationOutcome operationOutcomeCode, String location) {
		this(IssueSeverity.ERROR, IssueType.EXCEPTION, message, operationOutcomeCode, location);
	}
	
	public FhirException(IssueSeverity issueSeverity, IssueType issueType, String message, org.hl7.fhir.r4.model.codesystems.OperationOutcome operationOutcomeCode) {
		this(issueSeverity, issueType, message, operationOutcomeCode, null);
	}
	
	public FhirException(IssueSeverity issueSeverity, IssueType issueType, String message, org.hl7.fhir.r4.model.codesystems.OperationOutcome operationOutcomeCode, String location) {
		super(message);
		this.issueSeverity = issueSeverity;
		this.issueType = issueType;
		this.operationOutcomeCode = operationOutcomeCode;
		this.location = location;
	}

	protected final OperationOutcomeIssueComponent buildIssue(IssueSeverity issueSeverity, IssueType issueType, String message, org.hl7.fhir.r4.model.codesystems.OperationOutcome operationOutcomeCode, String location) {
		return new OperationOutcome.OperationOutcomeIssueComponent()
			.setSeverity(issueSeverity)
			.setCode(issueType)
			.setDetails(toDetails(operationOutcomeCode, location))
			.setDiagnostics(message)
			.addLocation(location);
	}
	
	@Override
	protected Integer getStatus() {
		return 0;
	}
	
	/**
	 * @return the {@link IssueType} associated with this exception.
	 */
	public final IssueType getIssueType() {
		return issueType;
	}
	
	/**
	 * @return the {@link org.hl7.fhir.r4.model.codesystems.OperationOutcome} code associated with this exception.
	 */
	public final org.hl7.fhir.r4.model.codesystems.OperationOutcome getOperationOutcomeCode() {
		return operationOutcomeCode;
	}
	
	/**
	 * Creates an OperationOutcome representation from this exception. Useful when the exception must be propagated through protocols where Java serialization
	 * cannot be used (eg. HTTP), or the possible receiver cannot understand serialized Java class and object byte sequences.
	 * 
	 * @return {@link OperationOutcome} representation of this {@link FhirException}, never <code>null</code>.
	 */
	public final OperationOutcome toOperationOutcome() {
		
		var operationOutcome = new OperationOutcome();
		
		// attach this exception as issue
		operationOutcome.addIssue(buildIssue(issueSeverity, issueType, getMessage(), operationOutcomeCode, location));
		
		// attach additional info as issues separately
		if (getAdditionalInfo() != null) {
			for (String key : ImmutableSortedSet.copyOf(getAdditionalInfo().keySet())) {
				Object value = getAdditionalInfo().get(key);
				if (value instanceof String) {
					operationOutcome.addIssue(buildIssue(IssueSeverity.ERROR, IssueType.INFORMATIONAL, (String) value, operationOutcomeCode, null));
				}
			}
		}
		
		// attach any additional custom issues from subclasses
		getAdditionalIssues().forEach(operationOutcome::addIssue);
		
		return operationOutcome;
	}

	/**
	 * Subclasses may optionally provide {@link OperationOutcomeIssueComponent} instance that needs to be reported in the final {@link OperationOutcome} when built via {@link #toOperationOutcome()}.
	 */
	protected List<OperationOutcomeIssueComponent> getAdditionalIssues() {
		return List.of();
	}
	
	public static CodeableConcept toDetails(org.hl7.fhir.r4.model.codesystems.OperationOutcome operationOutcomeCode, String location) {
		String operationOutcomeCodeDisplay = operationOutcomeCode.getDisplay();
		// TODO should we raise IAE when location is empty but the display is a template?
		if (operationOutcomeCodeDisplay.contains("%s") && !Strings.isNullOrEmpty(location)) {
			operationOutcomeCodeDisplay = String.format(operationOutcomeCodeDisplay, location);
		}
		return new CodeableConcept()
					.addCoding(
						new Coding()
							.setCode(operationOutcomeCode.toCode())
							.setSystem(operationOutcomeCode.getSystem())
							.setDisplay(operationOutcomeCodeDisplay)
					)
					.setText(operationOutcomeCodeDisplay);
	}

	
}
