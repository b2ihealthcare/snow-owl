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
package com.b2international.snowowl.fhir.api.tests.validation;

import org.junit.Test;

import com.b2international.snowowl.fhir.api.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.api.codesystems.IssueType;
import com.b2international.snowowl.fhir.api.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.api.exceptions.ValidationException;
import com.b2international.snowowl.fhir.api.model.Issue;
import com.b2international.snowowl.fhir.api.model.Issue.Builder;
import com.b2international.snowowl.fhir.api.model.SubProperty;
import com.b2international.snowowl.fhir.api.tests.FhirExceptionIssueMatcher;

/**
 * Deserialized sub-property validation tests
 * 
 * @since 6.3
 */
public class SubPropertyValidationTest extends ValidatorTest<SubProperty> {
	
	private Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
	
	@Test
	public void missingCodeTest() throws Exception {
		
		Issue expectedIssue = builder.addLocation("SubProperty.code")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'code' content is invalid [null]. Violation: may not be null.")
				.build();

		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		SubProperty.builder()
			//.code("123")
			.value(2.1)
			.description("propertyDescription")
			.build()
			.toParameters();
	}
	
	@Test
	public void missingParametersValueTest() throws Exception {

		exception.expect(ValidationException.class);
		exception.expectMessage("2 validation error");
		
		SubProperty.builder()
			.code("")
			//.value(2.1)
			.description("propertyDescription")
			.build();
	}

}
