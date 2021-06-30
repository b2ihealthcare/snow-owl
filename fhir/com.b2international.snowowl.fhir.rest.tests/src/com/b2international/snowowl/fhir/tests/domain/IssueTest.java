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
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.google.common.collect.Lists;

import io.restassured.path.json.JsonPath;
/**
 * Test for validating the {@link Issue} model object.
 * @since 8.0.0
 */
public class IssueTest extends FhirTest {
	
	@Test
	public void build() throws Exception {
		
		Issue issue = Issue.builder()
			.severity(IssueSeverity.ERROR)
			.code(IssueType.REQUIRED)
			.addExpression("Issue expression")
			.addLocation("Issue location")
			.details(CodeableConcept.builder()
					.addCoding(Coding.builder()
							.code("A")
							.display("A display")
							.build())
					.text("Text")
				.build())
			.build();
		
		assertEquals(IssueSeverity.ERROR.getCode(), issue.getSeverity());
		assertEquals(IssueType.REQUIRED.getCode(), issue.getCode());
		assertThat(issue.getExpressions(), hasItems("Issue expression"));
		assertThat(issue.getLocations(), hasItems("Issue location"));
		CodeableConcept details = issue.getDetails();
		assertEquals(1, details.getCodings().size());
		assertEquals("Text", details.getText());
		Coding coding = details.getCodings().iterator().next();
		assertEquals("A", coding.getCode().getCodeValue());
		assertEquals("A display", coding.getDisplay());
	}
	
	@Test
	public void serialize() throws Exception {
		
		Issue issue = Issue.builder()
				.severity(IssueSeverity.ERROR)
				.code(IssueType.REQUIRED)
				.addExpression("Issue expression")
				.addLocation("Issue location")
				.details(CodeableConcept.builder()
						.addCoding(Coding.builder()
								.code("A")
								.display("A display")
								.build())
						.text("Text")
					.build())
				.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(issue));
		assertThat(jsonPath.getString("severity"), equalTo("error"));
		assertThat(jsonPath.getString("code"), equalTo("required"));
		assertThat(jsonPath.getList("expression"), equalTo(Lists.newArrayList("Issue expression")));
		assertThat(jsonPath.getList("location"), equalTo(Lists.newArrayList("Issue location")));

		jsonPath.setRoot("details");
		assertThat(jsonPath.getString("text"), equalTo("Text"));
		jsonPath.setRoot("details.coding[0]");
		assertThat(jsonPath.getString("code"), equalTo("A"));
		assertThat(jsonPath.getString("display"), equalTo("A display"));
		
	
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Issue issue = Issue.builder()
				.severity(IssueSeverity.ERROR)
				.code(IssueType.REQUIRED)
				.addExpression("Issue expression")
				.addLocation("Issue location")
				.details(CodeableConcept.builder()
						.addCoding(Coding.builder()
								.code("A")
								.display("A display")
								.build())
						.text("Text")
					.build())
				.build();
		
		Issue readIssue = objectMapper.readValue(objectMapper.writeValueAsString(issue), Issue.class);
		
		assertEquals(IssueSeverity.ERROR.getCode(), readIssue.getSeverity());
		assertEquals(IssueType.REQUIRED.getCode(), readIssue.getCode());
		assertThat(readIssue.getExpressions(), hasItems("Issue expression"));
		assertThat(readIssue.getLocations(), hasItems("Issue location"));
		CodeableConcept details = readIssue.getDetails();
		assertEquals(1, details.getCodings().size());
		assertEquals("Text", details.getText());
		Coding coding = details.getCodings().iterator().next();
		assertEquals("A", coding.getCode().getCodeValue());
		assertEquals("A display", coding.getDisplay());
	}
	
}
