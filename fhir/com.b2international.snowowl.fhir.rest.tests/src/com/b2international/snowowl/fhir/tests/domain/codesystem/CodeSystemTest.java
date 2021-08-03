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
package com.b2international.snowowl.fhir.tests.domain.codesystem;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.codesystems.*;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.*;
import com.b2international.snowowl.fhir.core.model.property.CodeConceptProperty;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.restassured.path.json.JsonPath;

/**
 * Test for checking the serialization from model->JSON.
 * @since 6.3
 */
public class CodeSystemTest extends FhirTest {
	
	//status(code)/content(code)
	@Test
	public void incompleteCodeSystemTest() throws Exception {
		
		exception.expect(ValidationException.class);
		exception.expectMessage("2 validation errors");
		
		CodeSystem.builder().build();
	}
	
	@Test 
	public void serializeMinimalCodeSystem() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder()
			.status(PublicationStatus.ACTIVE)
			.content(CodeSystemContentMode.COMPLETE)
			.build();
		
		applyFilter(codeSystem);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(codeSystem));
		
		assertThat(jsonPath.getString("resourceType"), equalTo("CodeSystem"));
		assertThat(jsonPath.getString("status"), equalTo("active"));
		assertThat(jsonPath.getString("content"), equalTo("complete"));
		
		String expectedJson = "{\"resourceType\":\"CodeSystem\","
				+ "\"status\":\"active\","
				+ "\"content\":\"complete\"}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(codeSystem));
	}
	
	@Test
	public void deserialize() throws Exception {
	
		CodeSystem codeSystem = CodeSystem.builder()
				.status(PublicationStatus.ACTIVE)
				.content(CodeSystemContentMode.COMPLETE)
				.date("2021-01-13T08:22:32+00:00")
				.addContact(ContactDetail.builder()
						.name("name")
						.id("id")
						.build())
				.build();
		
		
		CodeSystem readCodeSystem = objectMapper.readValue(objectMapper.writeValueAsString(codeSystem), CodeSystem.class);
			
		assertEquals(PublicationStatus.ACTIVE.getCode(), readCodeSystem.getStatus());
		assertEquals(CodeSystemContentMode.COMPLETE.getCode(), readCodeSystem.getContent());
		assertEquals(FhirDates.parseDate("2021-01-13T08:22:32+00:00"), readCodeSystem.getDate());
		assertEquals("name", readCodeSystem.getContacts().iterator().next().getName());
		
	}
	
	@Test 
	public void buildCodeSystemWithNegativeCount() throws Exception {
		
		Issue expectedIssue = Issue.builder()
				.code(IssueType.INVALID)
				.severity(IssueSeverity.ERROR)
				.diagnostics("1 validation error")
				.addLocation("CodeSystem.count")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'count' content is invalid [-1]. Violation: Count must be equal to or larger than 0.")
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
	public void serializeCodeSystem() throws Exception {
		
		Identifier identifier = Identifier.builder()
			.use(IdentifierUse.OFFICIAL)
			.system(new Uri("www.hl7.org"))
			.value("OID:1234.1234")
			.build();
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
			.addProperty(SupportedConceptProperty.builder(CommonConceptProperties.CHILD).build())
			.description("Code system description")
			.date("2018-03-09T19:50:21.000+0000")
			.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
			.addIdentifier(identifier)
			.language("en")
			.name("Local code system")
			.narrative(NarrativeStatus.ADDITIONAL, "<div>Some html text</div>")
			.title("title")
			.publisher("B2i")
			.status(PublicationStatus.ACTIVE)
			.content(CodeSystemContentMode.COMPLETE)
			.supplements(new Uri("http://b2i.sg/supplement"))
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
				.addProperty(CodeConceptProperty.builder()
						.code("childConcept")
						.value(new Code("childId"))
						.build())
				.build())
			.build();
		
		applyFilter(codeSystem);
		
		JsonPath jsonPath = getJsonPath(codeSystem);
		
		assertThat(jsonPath.getString("resourceType"), equalTo("CodeSystem"));
		assertThat(jsonPath.getString("id"), equalTo("repo/shortName"));
		assertThat(jsonPath.getString("language"), equalTo("en"));
		assertThat(jsonPath.getString("text.status"), equalTo("additional"));
		assertThat(jsonPath.getString("text.div"), equalTo("<div>Some html text</div>"));
		assertThat(jsonPath.getString("url"), equalTo("code system uri"));

		assertThat(jsonPath.getString("identifier[0].use"), equalTo("official"));
		assertThat(jsonPath.getString("identifier[0].system"), equalTo("www.hl7.org"));
		assertThat(jsonPath.getString("identifier[0].value"), equalTo("OID:1234.1234"));
		
		assertThat(jsonPath.getString("version"), equalTo("2018.01.01"));
		assertThat(jsonPath.getString("name"), equalTo("Local code system"));
		assertThat(jsonPath.getString("title"), equalTo("title"));
		assertThat(jsonPath.getString("status"), equalTo("active"));
		assertThat(jsonPath.getString("publisher"), equalTo("B2i"));
		assertThat(jsonPath.getString("description"), equalTo("Code system description"));
		assertThat(jsonPath.getString("hierarchyMeaning"), equalTo("is-a"));
		assertThat(jsonPath.getString("content"), equalTo("complete"));
		assertThat(jsonPath.getString("supplements"), equalTo("http://b2i.sg/supplement"));
		
		assertThat(jsonPath.getString("property[0].code"), equalTo("child"));
		assertThat(jsonPath.getString("property[0].uri"), equalTo("http://hl7.org/fhir/concept-properties/child"));
		assertThat(jsonPath.getString("property[0].description"), equalTo("Child"));
		assertThat(jsonPath.getString("property[0].type"), equalTo("code"));
		
		jsonPath.setRoot("concept[0]");
		
		assertThat(jsonPath.getString("code"), equalTo("conceptCode"));
		assertThat(jsonPath.getString("display"), equalTo("Label"));
		assertThat(jsonPath.getString("definition"), equalTo("This is a code definition"));
		assertThat(jsonPath.getString("designation[0].language"), equalTo("uk_en"));
		assertThat(jsonPath.getString("designation[0].use.code"), equalTo("internal"));
		assertThat(jsonPath.getString("designation[0].use.system"), equalTo("http://b2i.sg/test"));
		assertThat(jsonPath.getString("designation[0].value"), equalTo("conceptLabel_uk"));
		assertThat(jsonPath.getString("designation[0].languageCode"), equalTo("uk_en"));

		assertThat(jsonPath.getString("property[0].code"), equalTo("childConcept"));
		assertThat(jsonPath.getString("property[0].valueCode"), equalTo("childId"));
	}
	
	@Test
	public void deserializeCodeSystem() throws Exception {
			
		URI uri = CodeSystemTest.class.getResource("test_codesystem.json").toURI();
		CodeSystem codeSystem = objectMapper.readValue(Paths.get(uri).toFile(), CodeSystem.class);
	}

}
