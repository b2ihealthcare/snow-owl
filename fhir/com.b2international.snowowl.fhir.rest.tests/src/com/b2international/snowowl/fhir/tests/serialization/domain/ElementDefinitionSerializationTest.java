/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.serialization.domain;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.DiscriminatorType;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Discriminator;
import com.b2international.snowowl.fhir.core.model.structuredefinition.ElementDefinition;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Slicing;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Tests for validating the serialization of the {@link ElementDefinition} class.
 * @since 7.1
 */
public class ElementDefinitionSerializationTest extends FhirTest {
	
	@Test
	public void invalidDiscriminatorTest() {
		
		exception.expect(ValidationException.class);
		exception.expectMessage("2 validation errors");
		Discriminator.builder().build();
	}
	
	@Test
	public void missingPathDiscriminatorTest() {
		
		Issue expectedIssue = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error")
				.addLocation("Discriminator.path")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'path' content is invalid [null]. Violation: may not be null.")
				.build();
			
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Discriminator.builder().type(DiscriminatorType.EXISTS).build();
	}
	
	@Test
	public void missingRulesFromSlicingTest() {
		
		Issue expectedIssue = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error")
				.addLocation("Slicing.rules")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'rules' content is invalid [null]. Violation: may not be null.")
				.build();
			
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Slicing.builder().build();
	}
	
	//@Test
	public void slicingTest() {
		Slicing.builder()
			.addDiscriminator(Discriminator.builder()
					.build())
			.build();
	}

}
