/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.snowowl.fhir.core.ResourceNarrative;

/**
 * FHIR Issue type codesystem
 * 
2	  unknown	Unknown User	The user or system was not able to be authenticated (either there is no process, or the proferred token is unacceptable).
2	  expired	Session Expired	User session expired; a login may be required.
2	  forbidden	Forbidden	The user does not have the rights to perform this action.
2	  suppressed	Information Suppressed	Some information was not or may not have been returned due to business rules, consent or privacy rules, or access permission constraints. This information may be accessible through alternate processes.
1	  processing	Processing Failure	Processing issues. These are expected to be final e.g. there is no point resubmitting the same content unchanged.
2	  not-supported	Content not supported	The resource or profile is not supported.
2	  duplicate	Duplicate	An attempt was made to create a duplicate record.
2	  not-found	Not Found	The reference provided was not found. In a pure RESTful environment, this would be an HTTP 404 error, but this code may be used where the content is not found further into the application architecture.
2	  too-long	Content Too Long	Provided content is too long (typically, this is a denial of service protection type of error).
2	  code-invalid	Invalid Code	The code or system could not be understood, or it was not valid in the context of a particular ValueSet.code.
2	  extension	Unacceptable Extension	An extension was found that was not acceptable, could not be resolved, or a modifierExtension was not recognized.
2	  too-costly	Operation Too Costly	The operation was stopped to protect server resources; e.g. a request for a value set expansion on all of SNOMED CT.
2	  business-rule	Business Rule Violation	The content/operation failed to pass some business rule, and so could not proceed.
2	  conflict	Edit Version Conflict	Content could not be accepted because of an edit conflict (i.e. version aware updates) (In a pure RESTful environment, this would be an HTTP 404 error, but this code may be used where the conflict is discovered further into the application architecture.)
2	  incomplete	Incomplete Results	Not all data sources typically accessed could be reached, or responded in time, so the returned information may not be complete.
1	  transient	Transient Issue	Transient processing issues. The system receiving the error may be able to resubmit the same content once an underlying issue is resolved.
2	  lock-error	Lock Error	A resource/record locking failure (usually in an underlying database).
2	  no-store	No Store Available	The persistent store is unavailable; e.g. the database is down for maintenance or similar action.
2	  exception	Exception	An unexpected internal error has occurred.
2	  timeout	Timeout	An internal timeout has occurred.
2	  throttled	Throttled	The system is not prepared to handle this request due to load management.
1	  informational	Informational Note	A message unrelated to the processing success of the completed operation (examples of the latter include things like reminders of password expiry, system maintenance times, etc.).
 * 
 * @since 6.4
 */
@ResourceNarrative("A code that describes the type of issue.")
public enum IssueType implements FhirCodeSystem {
	
	/**
	 * Content invalid against the specification or a profile.
	 */
	INVALID("Invalid Content"),
	
	/**
	 * A structural issue in the content such as wrong namespace, or unable to parse the content completely, or invalid json syntax.
	 */
	STRUCTURE("Structural Issue"),
	
	/**
	 * A required element is missing.
	 */
	REQUIRED("Required element missing"),
	
	/**
	 * An element value is invalid.
	 */
	VALUE("Element value invalid"),
	
	/**
	 * A content validation rule failed - e.g. a schematron rule.
	 */
	INVARIANT("Validation rule failed"),
	
	/**
	 * An authentication/authorization/permissions issue of some kind.
	 */
	SECURITY("Security Problem"),
	
	/**
	 * The client needs to initiate an authentication process.
	 */
	LOGIN("Login Required"),
	UNKNOWN("Unknown User"),
	EXPIRED("Session Expired"),
	FORBIDDEN("Forbidden"),
	SUPPRESSED("Information Suppressed"),
	PROCESSING("Processing Failure"),
	NOT_SUPPORTED("Content not supported"),
	DUPLICATE("Duplicate"),
	NOT_FOUND("Not Found"),
	TOO_LONG("Content too long"),
	CODE_INVALID("Invalid code"),
	EXTENSION("Unacceptable extension"),
	TOO_COSTLY("Operation Too Constly"),
	BUSINESS_RULE("Business Rule Violation"),
	CONFLICT("Edit Version Conflict"),
	INCOMPLETE("Incomplete Results"),
	TRANSIENT("Transient Issue"),
	LOCK_ERROR("Lock Error"),
	NO_STORE("No Store Available"),
	EXCEPTION("Exception"),
	TIMEOUT("Timeout"),
	THROTTLED("Throttled"),
	INFORMATIONAL("Informational Note");
	
	public static final String CODE_SYSTEM_URI = "http://hl7.org/fhir/issue-type";
	
	private String displayName;
	
	private IssueType(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}
}
