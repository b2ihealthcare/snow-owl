/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.serialization.parameterized;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for serializing the Designation class.
 * see CodeSystem-lookup
 * 
 * @since 6.6
 */
public class DesignationSerializationTest extends FhirTest {
	
	@Test
	public void designationTest() throws Exception {

		Coding coding = Coding.builder()
				.code("1234")
				.system("http://www.whocc.no/atc")
				.version("20180131")
				.build();
		
		Designation designation = Designation.builder()
				.languageCode("en_uk")
				.use(coding)
				.value("dValue")
				.build();
		
		Fhir fhirParameters = new Parameters.Fhir(designation);

		printPrettyJson(fhirParameters);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(fhirParameters));
		
		assertThat(jsonPath.getString("resourceType"), equalTo("Parameters"));
		assertThat(jsonPath.getString("parameter[0].name"), equalTo("language"));
		assertThat(jsonPath.getString("parameter[0].valueCode"), equalTo("en_uk"));
		assertThat(jsonPath.getString("parameter[2].name"), equalTo("value"));
		assertThat(jsonPath.getString("parameter[2].valueString"), equalTo("dValue"));
		
		jsonPath.setRoot("parameter[1]");
		
		assertThat(jsonPath.getString("name"), equalTo("use"));
		assertThat(jsonPath.getString("valueCoding.code"), equalTo("1234"));
		assertThat(jsonPath.getString("valueCoding.system"), equalTo("http://www.whocc.no/atc"));
		assertThat(jsonPath.getString("valueCoding.version"), equalTo("20180131"));
	}
	
	@Test
	public void missingValueTest() throws Exception {

		Builder builder = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("Designation.value")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'value' content is invalid [null]. Violation: may not be empty.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Designation.builder().build();
	}
	
	@Test
	public void emptyValueTest() throws Exception {

		Builder builder = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("Designation.value")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'value' content is invalid []. Violation: may not be empty.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Designation.builder().value("").build();
	}
	
}
