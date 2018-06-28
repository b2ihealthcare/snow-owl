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
package com.b2international.snowowl.fhir.api.tests.serialization.domain;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.api.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Period;

/**
 * 
 * Test for domain model serialization.
 * @since 6.3
 */
public class ModelSerializationTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
	
	@Test
	public void contactDetailTest() throws Exception {
		
		ContactPoint cp = ContactPoint.builder()
				.period(new Period(null, null))
				.rank(1)
				.system("system")
				.value("value")
				.build();
		
		ContactDetail cd = ContactDetail.builder()
				.name("name")
				.addContactPoint(cp)
				.build();
		
		printPrettyJson(cd);
		
		String expected = "{\"name\":\"name\","
				+ "\"telecom\":"
					+ "[{\"system\":\"system\","
					+ "\"value\":\"value\","
					+ "\"rank\":1,"
					+ "\"period\":{}}"
					+ "]"
				+ "}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(cd));
				
	}
	
	@Test
	public void operationOutcomeTest() throws Exception {
		OperationOutcome ou = OperationOutcome.builder()
				.addIssue(Issue.builder()
						.severity(IssueSeverity.ERROR)
						.code(IssueType.REQUIRED)
						.build())
				.build();
		
		printPrettyJson(ou);
		
		String expected = "{\"resourceType\":\"OperationOutcome\"," + 
				"\"issue\":[{" + 
					"\"severity\":\"error\"," + 
					"\"code\":\"required\"" + 
					"}]" + 
				"}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(ou));
	}
	
	@Test
	public void missingIssueTest() throws Exception {
		
		Issue expectedIssue = builder.addLocation("OperationOutcome.issues")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'issues' content is invalid [[]]. Violation: may not be empty.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		OperationOutcome.builder().build();
	}
	
}
