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
package com.b2international.snowowl.fhir.api.tests.serialization.parameterized;

import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.api.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Issue.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Property;
import com.b2international.snowowl.fhir.core.model.dt.SubProperty;

/**
 * Test for serializing the Property class.
 * see CodeSystem-lookup
 * @since 6.4
 */
public class PropertySerializationTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void minimalPropertyTest() throws Exception {

		Property property = Property.builder()
			.code("123")
			.build();
		 
		Fhir fhirParameters = new Parameters.Fhir(property);
		
		System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fhirParameters));
		
		String expected = 
				"{\"resourceType\":\"Parameters\","
					+ "\"parameter\":["
						+ "{\"name\":\"code\",\"valueCode\":\"123\"}"
					+ "]" 
				+ "}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(fhirParameters));
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
		
		System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fhirParameters));
		
		String expected = 
				"{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
						+ "{\"name\":\"code\",\"valueCode\":\"123\"},"
						+ "{\"name\":\"value\",\"valueInteger\":2},"
						+ "{\"name\":\"description\",\"valueString\":\"propertyDescription\"}"
					+ "]" 
				+ "}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(fhirParameters));
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
		
		System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fhirParameters));
		
		String expected = 
				"{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
						+ "{\"name\":\"code\",\"valueCode\":\"123\"},"
						+ "{\"name\":\"value\",\"valueDateTime\":\"2018-03-09T19:50:21+0000\"},"
						+ "{\"name\":\"description\",\"valueString\":\"propertyDescription\"}"
					+ "]" 
				+ "}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(fhirParameters));
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
		
		System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fhirParameters));
		
		String expected = 
				"{\"resourceType\":\"Parameters\","
				+ "\"parameter\":["
					+ "{\"name\":\"code\",\"valueCode\":\"123\"},"
					+ "{\"name\":\"value\",\"valueInteger\":2},"
					+ "{\"name\":\"description\",\"valueString\":\"propertyDescription\"},"
					+ "{\"name\":\"subproperty\","
					+ "\"part\":[{\"name\":\"code\",\"valueCode\":\"subCode\"},"
						+ "{\"name\":\"value\",\"valueInteger\":1},"
						+ "{\"name\":\"description\",\"valueString\":\"subDescription\"}]"
						+ "}]" 
				+ "}";
		
		Assert.assertEquals(expected, objectMapper.writeValueAsString(fhirParameters));
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
