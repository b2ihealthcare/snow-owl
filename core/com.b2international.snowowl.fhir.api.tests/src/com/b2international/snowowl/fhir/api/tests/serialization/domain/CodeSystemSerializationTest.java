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

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.api.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.FilterOperator;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.property.BooleanConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.CodeConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.CodingConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.DateTimeConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.IntegerConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.StringConceptProperty;

/**
 * Test for checking the serialization from model->JSON.
 * @since 6.3
 */
public class CodeSystemSerializationTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	//Supported property in the code system
	@Test
	public void supportedConceptPropertyTest() throws Exception {
		
		SupportedConceptProperty conceptProperty = SupportedConceptProperty.builder(CommonConceptProperties.INACTIVE).build();
		printPrettyJson(conceptProperty);
		
		String expectedJson = "{\"code\":\"inactive\","
				+ "\"uri\":\"http://hl7.org/fhir/concept-properties/inactive\","
				+ "\"description\":\"Inactive\","
				+ "\"type\":\"boolean\"}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void returnedBooleanConceptPropertyTest() throws Exception {
		
		BooleanConceptProperty conceptProperty = BooleanConceptProperty.builder()
			.code("childConcept")
			.value(true)
			.build();
		
		printPrettyJson(conceptProperty);
		
		String expected = "{\"code\":\"childConcept\",\"valueBoolean\":true}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void returnedStringConceptPropertyTest() throws Exception {
		
		StringConceptProperty conceptProperty = StringConceptProperty.builder()
			.code("childConcept")
			.value("string")
			.build();
		
		printPrettyJson(conceptProperty);
		
		String expected = "{\"code\":\"childConcept\",\"valueString\":\"string\"}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void returnedIntegerConceptPropertyTest() throws Exception {
		
		IntegerConceptProperty conceptProperty = IntegerConceptProperty.builder()
			.code("childConcept")
			.value(1)
			.build();
		
		printPrettyJson(conceptProperty);
		
		String expected = "{\"code\":\"childConcept\",\"valueInteger\":1}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void returnedDateTimeConceptPropertyTest() throws Exception {
		
		Date date = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT).parse("2018-03-23T08:49:40+0100");
		
		DateTimeConceptProperty conceptProperty = DateTimeConceptProperty.builder()
			.code("childConcept")
			.value(date)
			.build();
		
		printPrettyJson(conceptProperty);
		
		String expected = "{\"code\":\"childConcept\",\"valueDateTime\":\"2018-03-23T07:49:40+0000\"}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void returnedCodeConceptPropertyTest() throws Exception {
		
		CodeConceptProperty conceptProperty = CodeConceptProperty.builder()
			.code("childConcept")
			.value(new Code("code"))
			.build();
		
		printPrettyJson(conceptProperty);
		
		String expected = "{\"code\":\"childConcept\",\"valueCode\":\"code\"}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void returnedCodingConceptPropertyTest() throws Exception {
		
		CodingConceptProperty conceptProperty = CodingConceptProperty.builder()
				.code(CommonConceptProperties.CHILD.getCode())
				.value(new Coding.Builder()
					.code("codingCode")
					.system("uri")
					.build())
				.build();
		
		printPrettyJson(conceptProperty);
		
		String expectedJson = "{\"code\":\"child\","
				+ "\"valueCoding\":{\"code\":\"codingCode\",\"system\":\"uri\",\"userSelected\":false}}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void filterMissingFieldsTest() throws Exception {
		exception.expect(ValidationException.class);
		exception.expectMessage("3 validation errors");
		Filter.builder().build();
	}

	@Test
	public void filterMissingCodeTest() throws Exception {
		
		Issue expectedIssue = Issue.builder()
			.code(IssueType.INVALID)
			.severity(IssueSeverity.ERROR)
			.diagnostics("1 validation error")
			.addLocation("Filter.code")
			.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'code' content is invalid [null]. Violation: may not be null.")
			.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		Filter.builder()
			.value("A SNOMED CT code")
			.addOperator(FilterOperator.EQUALS)
			.build();
	}
	
	//status(code)/content(code)
	@Test
	public void incompleteCodeSystemTest() throws Exception {
		
		exception.expect(ValidationException.class);
		exception.expectMessage("2 validation errors");
		
		CodeSystem.builder().build();
	}
	
	@Test 
	public void minimalCodeSystemTest() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder()
			.status(PublicationStatus.ACTIVE)
			.content(CodeSystemContentMode.COMPLETE)
			.build();
		
		applyFilter(codeSystem);
		printPrettyJson(codeSystem);
		
		String expectedJson = "{\"resourceType\":\"CodeSystem\","
				+ "\"status\":\"active\","
				+ "\"content\":\"complete\"}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(codeSystem));
	}
	
	@Test 
	public void negativeCountCodeSystemTest() throws Exception {
		
		Issue expectedIssue = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error")
				.addLocation("CodeSystem.count")
				.codeableConceptWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'count' content is invalid [-1]. Violation: Count must be equal to or larger than 0.")
				.build();
			
			exception.expect(ValidationException.class);
			exception.expectMessage("1 validation error");
			exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		CodeSystem.builder()
			.status(PublicationStatus.ACTIVE)
			.content(CodeSystemContentMode.COMPLETE)
			.count(-1)
			.build();
	}
	
	@Test 
	public void codeSystemTest() throws Exception {
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(new Uri("www.hl7.org"))
			.value("OID:1234.1234")
			.build();
		
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
			.addProperty(SupportedConceptProperty.builder(CommonConceptProperties.CHILD).build())
			.description("Code system description")
			.date("2018-03-09T19:50:21+0000")
			.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
			.identifier(identifier)
			.language("en")
			.name("Local code system")
			.narrative(NarrativeStatus.ADDITIONAL, "<div>Some html text</div>")
			.title("title")
			.publisher("B2i")
			.status(PublicationStatus.ACTIVE)
			.content(CodeSystemContentMode.COMPLETE)
			.url(new Uri("code system uri"))
			.version("2018.01.01")
			.addConcept(Concept.builder()
				.code("conceptCode")
				.definition("This is a code definition")
				.display("Label")
				.addDesignation(Designation.builder()
					.languageCode("uk_en")
					.use(Coding.builder()
						.code("internal")
						.system("http://b2i.sg/test")
						.build()
						)
					.value("conceptLabel_uk")
					.build())
				.addProperties(CodeConceptProperty.builder()
						.code("childConcept")
						.value(new Code("childId"))
						.build())
				.build())
			.build();
		
		applyFilter(codeSystem);
		
		printPrettyJson(codeSystem);
		printJson(codeSystem);
		
		//This is stupid, we should assert parts or use JSONAssert
		String expectedJson = "{\"resourceType\":\"CodeSystem\","
				+ "\"id\":\"repo/shortName\","
				+ "\"language\":\"en\","
				+ "\"text\":{\"status\":\"additional\",\"div\":\"<div>Some html text</div>\"},"
				+ "\"url\":\"code system uri\","
				+ "\"identifier\":{\"use\":\"official\",\"system\":\"www.hl7.org\",\"value\":\"OID:1234.1234\"},"
				+ "\"version\":\"2018.01.01\","
				+ "\"name\":\"Local code system\","
				+ "\"title\":\"title\","
				+ "\"status\":\"active\","
				+ "\"description\":\"Code system description\","
				+ "\"hierarchyMeaning\":\"is-a\","
				+ "\"count\":0,"
				+ "\"property\":"
					+ "[{\"code\":\"child\","
					+ "\"uri\":\"http://hl7.org/fhir/concept-properties/child\","
					+ "\"description\":\"Child\","
					+ "\"type\":\"code\"}],"
				+ "\"concept\":"
					+ "[{\"code\":\"conceptCode\","
					+ "\"display\":\"Label\","
					+ "\"definition\":\"This is a code definition\","
					+ "\"designation\":[{\"language\":\"uk_en\",\"use\":"
							+ "{\"code\":\"internal\",\"system\":"
							+ "\"http://b2i.sg/test\","
							+ "\"userSelected\":false},"
						+ "\"value\":\"conceptLabel_uk\","
						+ "\"languageCode\":\"uk_en\"}],"
					+ "\"properties\":"
						+ "[[{\"name\":\"code\",\"valueCode\":\"childConcept\"},"
						+ "{\"name\":\"valueCode\",\"valueCode\":\"childId\"}]"
						+ "]}"
					+ "]"
				+ "}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(codeSystem));
	}
	
	@Test
	public void bundleTest() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
			.status(PublicationStatus.ACTIVE)
			.name("Local code system")
			.content(CodeSystemContentMode.COMPLETE)
			.url(new Uri("code system uri"))
			.build();
		
		Entry entry = new Entry(new Uri("full Url"), codeSystem);
		
		Bundle bundle = Bundle.builder("bundle_Id?")
			.language("en")
			.total(1)
			.type(BundleType.SEARCHSET)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		printPrettyJson(bundle);
		
		String expectedJson = "{\"resourceType\":\"Bundle\","
				+ "\"id\":\"bundle_Id?\","
				+ "\"language\":\"en\","
				+ "\"type\":\"searchset\","
				+ "\"total\":1,"
				+ "\"link\":"
					+ "[{\"relation\":\"self\","
					+ "\"url\":\"http://localhost:8080/snowowl/CodeSystem\"}],"
					+ "\"entry\":[{\"fullUrl\":\"full Url\",\"resource\":"
						+ "{\"resourceType\":\"CodeSystem\","
						+ "\"id\":\"repo/shortName\","
						+ "\"url\":\"code system uri\","
						+ "\"name\":\"Local code system\","
						+ "\"status\":\"active\","
						+ "\"content\":\"complete\"}"
					+ "}]"
				+ "}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(bundle));
	}
	

}
