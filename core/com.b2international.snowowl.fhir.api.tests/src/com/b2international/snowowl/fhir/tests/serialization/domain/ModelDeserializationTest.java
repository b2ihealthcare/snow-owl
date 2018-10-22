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
package com.b2international.snowowl.fhir.tests.serialization.domain;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Test for validating the deserialization of the base domain models.
 * @since 6.6
 */
public class ModelDeserializationTest extends FhirTest {
	
	private Builder builder = Issue.builder()
		.code(IssueType.INVALID)
		.severity(IssueSeverity.ERROR)
		.diagnostics("1 validation error");
	
	@Test
	public void codingTest() throws Exception {
		
		String jsonCoding = "{\"code\":\"1234\","
				+ "\"system\":\"http://snomed.info/sct\","
				+ "\"version\":\"20180131\",\"userSelected\":false}";
		
		Coding coding = objectMapper.readValue(jsonCoding, Coding.class);
		
		Assert.assertEquals(new Code("1234"), coding.getCode());
		Assert.assertEquals(new Uri("http://snomed.info/sct"), coding.getSystem());
		Assert.assertEquals("20180131", coding.getVersion());
	}
	
	@Test
	public void emptyCodeTest() throws Exception {
		
		Issue expectedIssue = builder.addLocation("Coding.code.codeValue")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'code.codeValue' content is invalid []. Violation: must match \"[^\\s]+([\\s]?[^\\s]+)*\".")
			.build();
			
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Coding.builder()
			.code("")
			.system("a")
			.version("20180131")
			.build();
	}
	
	@Test
	public void invalidSystemTest() throws Exception {
		Issue expectedIssue = builder
			.addLocation("Coding.system.uriValue")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'system.uriValue' content is invalid [sys tem]. Violation: uri is invalid.")
			.build();
				
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Coding.builder()
			.code("1233")
			.system("sys tem")
			.version("20180131")
			.build();
	}
	
}
