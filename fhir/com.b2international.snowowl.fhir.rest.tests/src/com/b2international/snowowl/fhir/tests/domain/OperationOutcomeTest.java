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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

import io.restassured.path.json.JsonPath;

/**
 * Test for validating the {@link OperationOutcome} model object.
 * @since 8.0.0
 */
public class OperationOutcomeTest extends FhirTest {
	
	@Test
	public void build() throws Exception {
		OperationOutcome operationOutcome = OperationOutcome.builder()
				.addIssue(Issue.builder()
						.severity(IssueSeverity.ERROR)
						.code(IssueType.REQUIRED)
						.build())
				.build();
		
		assertEquals(1, operationOutcome.getIssues().size());
		Issue issue = operationOutcome.getIssues().iterator().next();
		
		assertEquals(IssueSeverity.ERROR.getCode(), issue.getSeverity());
		assertEquals(IssueType.REQUIRED.getCode(), issue.getCode());
		
	}
	
	@Test
	public void buildWithMissingIssue() throws Exception {
		
		Issue expectedIssue = validationErrorissueBuilder
			.addLocation("OperationOutcome.issues")
			.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'issues' content is invalid [null]. Violation: may not be empty.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		OperationOutcome.builder().build();
	}
	
	@Test
	public void serialize() throws JsonProcessingException {
		
		OperationOutcome operationOutcome = OperationOutcome.builder()
				.addIssue(Issue.builder()
						.severity(IssueSeverity.ERROR)
						.code(IssueType.REQUIRED)
						.addLocation("location")
						.build())
				.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(operationOutcome));
		assertThat(jsonPath.getString("resourceType"), equalTo("OperationOutcome"));
		jsonPath.setRoot("issue[0]");
		
		assertThat(jsonPath.getString("severity"), equalTo("error"));
		assertThat(jsonPath.getString("code"), equalTo("required"));
		assertThat(jsonPath.getList("location"), equalTo(Lists.newArrayList("location")));
	}
	
	@Test
	public void deserialize() throws Exception {
		OperationOutcome operationOutcome = OperationOutcome.builder()
				.addIssue(Issue.builder()
						.severity(IssueSeverity.ERROR)
						.code(IssueType.REQUIRED)
						.build())
				.build();
		
		OperationOutcome readOperationOutcome = objectMapper.readValue(objectMapper.writeValueAsString(operationOutcome), OperationOutcome.class);
	
		assertEquals(1, readOperationOutcome.getIssues().size());
		Issue issue = readOperationOutcome.getIssues().iterator().next();
		
		assertEquals(IssueSeverity.ERROR.getCode(), issue.getSeverity());
		assertEquals(IssueType.REQUIRED.getCode(), issue.getCode());
	
	}
	
}
