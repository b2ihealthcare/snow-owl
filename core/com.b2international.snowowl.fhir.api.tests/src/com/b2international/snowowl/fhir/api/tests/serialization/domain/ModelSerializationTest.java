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

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.dt.Coding;

/**
 * 
 * Test for checking the parameter-converted serialization from model->JSON.
 * @since 6.3
 */
public class ModelSerializationTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void operationOutcomeTest() throws Exception {
		OperationOutcome ou = new OperationOutcome();
		
		Issue issue = Issue.builder()
				.severity(IssueSeverity.ERROR)
				.code(IssueType.REQUIRED)
				.build();
		System.out.println("Issue: " + issue);
		ou.addIssue(issue);
		
		printPrettyJson(ou);
	}
	
	@Test
	public void codingTest() throws Exception {
		
		Coding coding = Coding.builder()
			.code("1234")
			.system("http://snomed.info/sct")
			.version("20180131")
			.build();
		
		String jsonString = objectMapper.writeValueAsString(coding);
		
		String expected = "{\"code\":\"1234\","
				+ "\"system\":\"http://snomed.info/sct\","
				+ "\"version\":\"20180131\",\"userSelected\":false}";
		
		Assert.assertEquals(expected, jsonString);
	}

}
