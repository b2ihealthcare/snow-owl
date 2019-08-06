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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.SubProperty;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirParameterMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for serializing the Property class.
 * see CodeSystem-lookup
 * @since 6.4
 */
public class PropertySerializationTest extends FhirTest {
	
	@Test
	public void minimalPropertyTest() throws Exception {

		Property property = Property.builder()
			.code("123")
			.build();
		 
		Fhir fhirParameters = new Parameters.Fhir(property);
		
		printPrettyJson(fhirParameters);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(fhirParameters));
		assertThat(jsonPath.getString("resourceType"), equalTo("Parameters"));
		assertThat(jsonPath.getList("parameter").size(), is(1));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("code", FhirDataType.CODE, "123"));
	}
	
	@Test
	public void missingCodeTest() throws Exception {

		Builder builder = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("Property.code")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'code' content is invalid [null]. Violation: may not be null.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Property.builder().build();
	}
	
	@Test
	public void emptyCodeTest() throws Exception {

		Builder builder = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("Property.code.codeValue")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'code.codeValue' content is invalid []. Violation: must match \"[^\\s]+([\\s]?[^\\s]+)*\".")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Property.builder().code("").build();
	}
	
	@Test
	public void basicPropertyTest() throws Exception {

		Property property = Property.builder()
			.code("123")
			.valueInteger(2)
			.description("propertyDescription")
			.build();
		 
		Fhir fhirParameters = new Parameters.Fhir(property);
		
		printPrettyJson(fhirParameters);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(fhirParameters));
		
		assertThat(jsonPath.getString("resourceType"), equalTo("Parameters"));
		assertThat(jsonPath.getList("parameter").size(), is(3));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("code", FhirDataType.CODE, "123"));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("value", FhirDataType.INTEGER, 2));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("description", FhirDataType.STRING, "propertyDescription"));
	}
	
	@Test
	public void dateTimePropertyTest() throws Exception {

		Date date = 	Dates.parse("2018-03-09T20:50:21+0100", FhirConstants.DATE_TIME_FORMAT);
		
		Property property = Property.builder()
			.code("123")
			.valueDateTime(date)
			.description("propertyDescription")
			.build();
		 
		Fhir fhirParameters = new Parameters.Fhir(property);
		
		printPrettyJson(fhirParameters);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(fhirParameters));
		
		assertThat(jsonPath.getString("resourceType"), equalTo("Parameters"));
		assertThat(jsonPath.getList("parameter").size(), is(3));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("code", FhirDataType.CODE, "123"));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("value", FhirDataType.DATETIME, "2018-03-09T19:50:21+0000"));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("description", FhirDataType.STRING, "propertyDescription"));
	}
	
	@Test
	public void subPropertyTest() throws Exception {

		Property property = Property.builder()
			.code("123")
			.valueInteger(2)
			.description("propertyDescription")
			.addSubProperty(SubProperty.builder()
				.code("subCode")
				.description("subDescription")
				.valueInteger(1)
				.build())
			.build();
		 
		Fhir fhirParameters = new Parameters.Fhir(property);
		
		printPrettyJson(fhirParameters);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(fhirParameters));
		
		assertThat(jsonPath.getString("resourceType"), equalTo("Parameters"));
		assertThat(jsonPath.getList("parameter").size(), is(4));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("code", FhirDataType.CODE, "123"));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("value", FhirDataType.INTEGER, 2));
		assertThat(jsonPath, FhirParameterMatcher.hasParameter("description", FhirDataType.STRING, "propertyDescription"));
		
		assertThat(jsonPath.getString("parameter[3].name"), equalTo("subproperty"));
		assertThat(jsonPath.getString("parameter[3].part[0].name"), equalTo("code"));
		assertThat(jsonPath.getString("parameter[3].part[0].valueCode"), equalTo("subCode"));
		assertThat(jsonPath.getString("parameter[3].part[1].name"), equalTo("value"));
		assertThat(jsonPath.getInt("parameter[3].part[1].valueInteger"), equalTo(1));
		assertThat(jsonPath.getString("parameter[3].part[2].name"), equalTo("description"));
		assertThat(jsonPath.getString("parameter[3].part[2].valueString"), equalTo("subDescription"));
	}
	
	@Test
	public void incorrectSubPropertyTest() throws Exception {

		Builder builder = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error");
		
		Issue expectedIssue = builder.addLocation("SubProperty.code")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'code' content is invalid [null]. Violation: may not be null.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Property.builder()
			.code("123")
			.valueInteger(2)
			.description("propertyDescription")
			.addSubProperty(SubProperty.builder()
				//.code("subCode")
				.description("subDescription")
				.valueInteger(1)
				.build())
			.build();
	}
	
}
