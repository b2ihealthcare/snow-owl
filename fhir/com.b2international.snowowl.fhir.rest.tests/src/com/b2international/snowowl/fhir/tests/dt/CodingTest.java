/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.dt;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.path.json.JsonPath;

/**
 * Test for validating the {@link Coding} data type.
 * @since 8.0.0
 */
public class CodingTest extends FhirTest {
	
	@Test
	public void buildInvalidSnomedVersion() throws Exception {
		
		Issue expectedIssue = validationErrorIssueBuilder
				.addLocation("Coding.versionValid")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'versionValid' content is invalid [false]. "
					+ "Violation: SNOMED CT version is defined as part of the system URI.")
				.build();
				
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
			
		Coding.builder()
				.code("1234")
				.system("http://snomed.info/sct")
				.version("20180131")
				.build();
	}
	
	@Test
	public void buildWithInvalidCode() throws Exception {
		
		Issue expectedIssue = validationErrorIssueBuilder.addLocation("Coding.code.codeValue")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'code.codeValue' content is invalid []. "
						+ "Violation: must match \"[^\\s]+([\\s]?[^\\s]+)*\".")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Coding.builder()
			.code("")
			.system("http://www.whocc.no/atc")
			.version("20180131")
			.build();
	}
	
	@Test
	public void buildWithInvalidSystem() throws Exception {
		Issue expectedIssue = validationErrorIssueBuilder
			.addLocation("Coding.system.uriValue")
			.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'system.uriValue' content is invalid [sys tem]. Violation: uri is invalid.")
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
	
	@Test
	public void build() {
		
		Coding coding = Coding.builder()
			.code("1234")
			.system("http://www.whocc.no/atc")
			.version("20180131")
			.build();
		
		assertEquals("1234", coding.getCode().getCodeValue());
		assertEquals("http://www.whocc.no/atc", coding.getSystem().getUriValue());
		assertEquals("20180131", coding.getVersion());
	}
	
	
	@Test
	public void serialize() throws Exception {
		
		Coding coding = Coding.builder()
			.code("1234")
			.system("http://www.whocc.no/atc")
			.version("20180131")
			.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(coding));
		assertThat(jsonPath.getString("code"), equalTo("1234"));
		assertThat(jsonPath.getString("system"), equalTo("http://www.whocc.no/atc"));
		assertThat(jsonPath.getString("version"), equalTo("20180131"));
	}
	
	@Test
	public void deserializeFromJson() throws Exception {
		
		String jsonCoding = "{\"code\":\"1234\","
				+ "\"system\":\"http://snomed.info/sct/version/20180131\","
				+ "\"userSelected\":false}";
		
		Coding coding = objectMapper.readValue(jsonCoding, Coding.class);
		
		Assert.assertEquals(new Code("1234"), coding.getCode());
		Assert.assertEquals(new Uri("http://snomed.info/sct/version/20180131"), coding.getSystem());
	}
	
	@Test
	public void deserialize() throws JsonProcessingException {
		
		Coding coding = Coding.builder()
				.code("1234")
				.system("http://www.whocc.no/atc")
				.version("20180131")
				.build();
		
		String serializedString = objectMapper.writeValueAsString(coding);
		Coding readCoding = objectMapper.readValue(serializedString, Coding.class);
		assertEquals("1234", readCoding.getCode().getCodeValue());
		assertEquals("http://www.whocc.no/atc", readCoding.getSystem().getUriValue());
		assertEquals("20180131", readCoding.getVersion());
	}

}
