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

import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r5.model.OperationOutcome.IssueType;

/**
 * Thrown when a request contains incorrect parameters or is otherwise malformed.
 * 
 * @since 4.0
 */
public class BadRequestException extends FhirException {

	private static final long serialVersionUID = 1L;
	
	public BadRequestException(final String message, final org.hl7.fhir.r4.model.codesystems.OperationOutcome operationOutcomeCode, String location) {
		super(OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.INVALID, message, operationOutcomeCode, location);
	}
	
	/**
	 * Error, Invalid, Invalid param parameters
	 * @param message
	 * @param location
	 */
	public BadRequestException(final String message, String location) {
		super(IssueSeverity.ERROR, IssueType.INVALID, message, org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGPARAMINVALID, location);
	}
	
	/**
	 * Error, Invalid, Invalid param parameters
	 * @param message
	 */
	public BadRequestException(final String message) {
		super(IssueSeverity.ERROR, IssueType.INVALID, message, org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGPARAMINVALID);
	}
	
	
}
