/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.fhir.tests.FhirParameterAssert.fhirParameter;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.SubProperty;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for serializing the @see {@link LookupResult} class.
 * see CodeSystem-lookup
 * 
 * @since 6.6
 */
public class LookupResultSerializationTest extends FhirTest {
	
	//@Test
	public void missingNameTest() throws Exception {

		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("LookupResult.name")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'name' content is invalid [null]. Violation: may not be empty.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		LookupResult.builder().display("display").build();
	}
	
	//@Test
	public void emptyNameTest() throws Exception {

		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("LookupResult.name")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'name' content is invalid []. Violation: may not be empty.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		LookupResult.builder().display("display").name("").build();
	}
	
	//@Test
	public void missingDisplayTest() throws Exception {

		Builder builder = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("LookupResult.display")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'display' content is invalid [null]. Violation: may not be empty.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		LookupResult.builder().name("name").build();
	}
	
	//@Test
	public void missingEverythingTest() throws Exception {

		exception.expect(ValidationException.class);
		exception.expectMessage("2 validation error");

		//multiple issues with the exception (name, display both null)
		LookupResult.builder().build();
	}
	
	@Test
	public void lookupResultTest() throws Exception {
		LookupResult lookupResult = LookupResult.builder()
			.name("test")
			.display("display")
			.addDesignation(Designation.builder()
					.value("dValue")
					.languageCode("uk").build())
			.addProperty(Property.builder()
					.code("1234")
					.description("propDescription")
					.valueString("sds")
					.addSubProperty(SubProperty.builder()
						.code("subCode")
						.description("subDescription")
						.valueInteger(1)
						.build())
					.build())
			.build();
		
		Fhir fhirParameters = new Parameters.Fhir(lookupResult);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(fhirParameters));
		
		assertThat(jsonPath.getString("resourceType")).isEqualTo("Parameters");

		assertThat(jsonPath.getList("parameter.name")).containsOnly("name", "display", "designation", "property");
		
		assertThat(jsonPath).has(fhirParameter("name", FhirDataType.STRING, "test"));
		assertThat(jsonPath).has(fhirParameter("display", FhirDataType.STRING, "display"));
		
		assertThat(jsonPath.getString("parameter[2].name")).isEqualTo("designation");
		assertThat(jsonPath.getString("parameter[2].part[0].name")).isEqualTo("language");
		assertThat(jsonPath.getString("parameter[2].part[0].valueCode")).isEqualTo("uk");
		assertThat(jsonPath.getString("parameter[2].part[1].name")).isEqualTo("value");
		assertThat(jsonPath.getString("parameter[2].part[1].valueString")).isEqualTo("dValue");
		
		assertThat(jsonPath.getString("parameter[3].name")).isEqualTo("property");
		assertThat(jsonPath.getString("parameter[3].part[0].name")).isEqualTo("code");
		assertThat(jsonPath.getString("parameter[3].part[0].valueCode")).isEqualTo("1234");
		assertThat(jsonPath.getString("parameter[3].part[1].name")).isEqualTo("value");
		assertThat(jsonPath.getString("parameter[3].part[1].valueString")).isEqualTo("sds");
		assertThat(jsonPath.getString("parameter[3].part[2].name")).isEqualTo("description");
		assertThat(jsonPath.getString("parameter[3].part[2].valueString")).isEqualTo("propDescription");

		assertThat(jsonPath.getString("parameter[3].part[3].name")).isEqualTo("subproperty");
		assertThat(jsonPath.getString("parameter[3].part[3].part[0].name")).isEqualTo("code");
		assertThat(jsonPath.getString("parameter[3].part[3].part[0].valueCode")).isEqualTo("subCode");
		assertThat(jsonPath.getString("parameter[3].part[3].part[1].name")).isEqualTo("value");
		assertThat(jsonPath.getInt("parameter[3].part[3].part[1].valueInteger")).isEqualTo(1);
		assertThat(jsonPath.getString("parameter[3].part[3].part[2].name")).isEqualTo("description");
		assertThat(jsonPath.getString("parameter[3].part[3].part[2].valueString")).isEqualTo("subDescription");
	}
	
}
