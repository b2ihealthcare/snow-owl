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
package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.snowowl.fhir.core.model.dt.Code;

/**
 * FHIR Operation outcome codesystem.
 * 
 * @see <a href="http://hl7.org/fhir/operationoutcome-definitions.html#OperationOutcome.issue">FHIR:Operation Outcome:issue</a>
 *  
 * @since 6.3
 */
public enum OperationOutcomeCode implements FhirCodeSystem {
	
	MSG_AUTH_REQUIRED("You must authenticate before you can use this service"),
	MSG_BAD_FORMAT("Bad Syntax: '%s' must be a '%s'"),
	MSG_BAD_SYNTAX("Bad Syntax in %s"),
	MSG_CANT_PARSE_CONTENT("Unable to parse feed (entry content type = '%s')"),	
	MSG_CANT_PARSE_ROOT("Unable to parse feed (root element name = '%s')"),	
	MSG_CREATED("New resource created"),	
	MSG_DATE_FORMAT("The Date value %s is not in the correct format (Xml Date Format required)"),	
	MSG_DELETED("This resource has been deleted"),	
	MSG_DELETED_DONE("Resource deleted"),	
	MSG_DELETED_ID("The resource '%s' has been deleted"),	
	MSG_DUPLICATE_ID("Duplicate Id %s for resource type %s"),	
	MSG_ERROR_PARSING("Error parsing resource Xml (%s)"),	
	MSG_ID_INVALID("Id '%s' has an invalid character '%s'	"),
	MSG_ID_TOO_LONG("Id '%s' too long (length limit 36)"),	
	MSG_INVALID_ID("Id not accepted"),	
	MSG_JSON_OBJECT("Json Source for a resource should start with an object"),	
	MSG_LOCAL_FAIL("Unable to resolve local reference to resource %s"),	
	MSG_NO_MATCH("No Resource found matching the query '%s'"),	
	MSG_NO_EXIST	("Resource Id '%s' does not exist"),	
	MSG_NO_MODULE("No module could be found to handle the request '%s'"),	
	MSG_NO_SUMMARY("No Summary for this resource"),	
	MSG_OP_NOT_ALLOWED("Operation %s not allowed for resource %s (due to local configuration)"),	
	MSG_PARAM_CHAINED("Unknown chained parameter name '%s'"),	
	MSG_PARAM_NO_REPEAT("Parameter '%s' is not allowed to repeat"),	
	MSG_PARAM_UNKNOWN("Parameter '%s' not understood"),	
	MSG_PARAM_INVALID("Parameter '%s' content is invalid"),	
	MSG_PARAM_MODIFIER_INVALID("Parameter '%s' modifier is invalid"),	
	MSG_RESOURCE_EXAMPLE_PROTECTED("Resources with identity 'example' cannot be deleted (for testing/training purposes)"),	
	MSG_RESOURCE_ID_FAIL("Unable to allocate resource id"),	
	MSG_RESOURCE_NOT_ALLOWED("Not allowed to submit a resource for this operation"),	
	MSG_RESOURCE_REQUIRED("A resource is required"),	
	MSG_RESOURCE_ID_MISMATCH("Resource Id Mismatch"),	
	MSG_RESOURCE_ID_MISSING("Resource Id Missing"),	
	MSG_RESOURCE_TYPE_MISMATCH("Resource Type Mismatch"),	
	MSG_SORT_UNKNOWN("Unknown sort parameter name '%s'"),	
	MSG_TRANSACTION_DUPLICATE_ID("Duplicate Identifier in transaction: %s"),	
	MSG_TRANSACTION_MISSING_ID("Missing Identifier in transaction - an entry.id must be provided"),	
	MSG_UNHANDLED_NODE_TYPE("Unhandled xml node type '%s'	"),
	MSG_UNKNOWN_CONTENT("Unknown Content (%s) at %s"),	
	MSG_UNKNOWN_OPERATION("Unknown FHIR http operation"),	
	MSG_UNKNOWN_TYPE("Resource Type '%s' not recognised"),	
	MSG_UPDATED("Existing resource updated"),	
	MSG_VERSION_AWARE("Version aware updates are required for this resource"),	
	MSG_VERSION_AWARE_CONFLICT("Update Conflict (server current version = '%s', client version referenced = '%s')"),	
	MSG_VERSION_AWARE_URL("Version specific URL not recognised"),	
	MSG_WRONG_NS("This does not appear to be a FHIR element or resource (wrong namespace '%s')"),	
	SEARCH_MULTIPLE("Error: Multiple matches exist for %s search parameters '%s'"),	
	UPDATE_MULTIPLE_MATCHES("Error: Multiple matches exist for the conditional update"),	
	DELETE_MULTIPLE_MATCHES("Error: Multiple matches exist for the conditional delete"),	
	SEARCH_NONE("Error: no processable search found for %s search parameters '%s'");
	
	public static final String CODE_SYSTEM_URI = "http://hl7.org/fhir/operation-outcome";
	
	private String displayName;
	
	private OperationOutcomeCode(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public Code getCode() {
		return new Code(name().toLowerCase());
	}
	
	@Override
	public String getCodeValue() {
		return name().toLowerCase();
	}

	
	public String displayName() {
		return displayName;
	}

	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

}
