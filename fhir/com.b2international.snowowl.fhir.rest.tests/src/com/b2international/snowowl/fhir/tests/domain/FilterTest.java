/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.FilterOperator;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Tests for {@link Filter}.
 * @since 8.0.0
 */
public class FilterTest extends FhirTest {
	
	
	private Filter filter;

	@Before
	public void setup() {
			filter = Filter.builder()
			.value("A SNOMED CT code")
			.code("code")
			.addOperator(FilterOperator.EQUALS)
			.build();
	}
	
	@Test
	public void filterMissingFieldsTest() throws Exception {
		exception.expect(ValidationException.class);
		exception.expectMessage("3 validation errors");
		Filter.builder().build();
	}

	@Test
	public void filterMissingCodeTest() throws Exception {
		
		Issue expectedIssue = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error")
			.addLocation("Filter.code")
			.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'code' content is invalid [null]. Violation: may not be null.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Filter.builder()
			.value("A SNOMED CT code")
			.addOperator(FilterOperator.EQUALS)
			.build();
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Filter readFilter = objectMapper.readValue(objectMapper.writeValueAsString(filter), Filter.class);
		assertEquals(new Code("code"), readFilter.getCode());
	}
}
