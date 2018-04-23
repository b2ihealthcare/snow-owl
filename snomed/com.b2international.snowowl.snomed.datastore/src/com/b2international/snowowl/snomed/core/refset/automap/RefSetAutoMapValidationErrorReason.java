/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.refset.automap;

/**
 * Enum for collection automap validation errors.
 * 
 *
 */
public enum RefSetAutoMapValidationErrorReason {

	NO_EQUIVALENT_COMPONENT_FOUND("The component does not have mapped value."),
	NO_EQUIVALENT_COMPONENT_BY_USER_REQUEST("Mapped value for the component is removed manually by the user."),
	EMPTY_SOURCE_VALUE("Source value is missing."),
	INACTIVE_EQUIVALENT_FOUND("Only inactive equivalent found in the current terminology."),
	EMPTY_ROW("Row was empty.");
	
	private String reason;
	
	RefSetAutoMapValidationErrorReason(String reason) {
		this.reason = reason;
	}
	
	public String getReasonText() {
		return reason;
	}
}