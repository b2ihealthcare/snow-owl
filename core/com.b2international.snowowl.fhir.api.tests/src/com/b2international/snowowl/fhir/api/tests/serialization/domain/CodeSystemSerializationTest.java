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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.property.BooleanConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.CodeConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.CodingConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.ConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.DateTimeConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.StringConceptProperty;
import com.b2international.snowowl.fhir.core.search.FhirBeanPropertyFilter;
import com.b2international.snowowl.fhir.core.search.SummaryParameter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

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
		ConceptProperty<Boolean> conceptProperty = BooleanConceptProperty.builder()
			.code(CommonConceptProperties.INACTIVE.getCode())
			.value(true)
			.build();
		
		printPrettyJson(conceptProperty);
		
		String expectedJson =  "[{\"name\":\"code\","
					+ "\"valueCode\":\"inactive\"},"
					+ "{\"name\":\"valueBoolean\","
					+ "\"valueBoolean\":true}]";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void returnedStringConceptPropertyTest() throws Exception {
		ConceptProperty<String> conceptProperty = StringConceptProperty.builder()
			.code(new Code("String code"))
			.value("text")
			.build();
		
		printPrettyJson(conceptProperty);
		
		String expectedJson =  "[{\"name\":\"code\","
					+ "\"valueCode\":\"String code\"},"
					+ "{\"name\":\"valueString\","
					+ "\"valueString\":\"text\"}]";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void returnedDateConceptPropertyTest() throws Exception {
		
		Date date = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT).parse("2018-03-23T08:49:40+0100");
		ConceptProperty<Date> conceptProperty = DateTimeConceptProperty.builder()
			.code(new Code("String code"))
			.value(date)
			.build();
		
		printPrettyJson(conceptProperty);
		
		String expectedJson =  "[{\"name\":\"code\","
					+ "\"valueCode\":\"String code\"},"
					+ "{\"name\":\"valueDateTime\","
					+ "\"valueDateTime\":\"2018-03-23T07:49:40+0000\"}]";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void returnedCodeConceptPropertyTest() throws Exception {
		CodeConceptProperty conceptProperty = CodeConceptProperty.builder()
			.code(CommonConceptProperties.CHILD.getCode())
			.value(new Code("codeCode"))
			.build();
		
		printPrettyJson(conceptProperty);
		
		String expectedJson =  "[{\"name\":\"code\","
				+ "\"valueCode\":\"child\"},"
				+ "{\"name\":\"valueCode\","
				+ "\"valueCode\":\"codeCode\"}]";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(conceptProperty));
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
		
		String expectedJson = "[{\"name\":\"code\",\"valueCode\":\"child\"},"
				+ "{\"name\":\"valueCoding\","
				+ "\"valueCoding\":{\"code\":\"codingCode\","
				+ "\"system\":\"uri\",\"userSelected\":false}}]";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test 
	public void codeSystemTest() throws Exception {
		
		Identifier identifier = new Identifier(IdentifierUse.OFFICIAL, null, new Uri("www.hl7.org"), "OID:1234.1234");
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
				.addProperty(SupportedConceptProperty.builder(CommonConceptProperties.CHILD).build())
				.description("Code system description")
				.hierarchyMeaning(CodeSystemHierarchyMeaning.IS_A)
				.identifier(identifier)
				.language("en")
				.name("Local code system")
				.narrative(NarrativeStatus.ADDITIONAL, "<div>Some html text</div>")
				.title("title")
				.publisher("B2i")
				.status(PublicationStatus.ACTIVE)
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
				+ "\"text\":{\"status\":\"additional\","
				+ "\"div\":\"<div>Some html text</div>\"},"
				+ "\"url\":\"code system uri\","
				+ "\"identifier\":{\"use\":\"official\",\"system\":\"www.hl7.org\",\"value\":\"OID:1234.1234\"},"
				+ "\"version\":\"2018.01.01\",\"name\":\"Local code system\","
				+ "\"title\":\"title\",\"status\":\"active\",\"description\":\"Code system description\","
				+ "\"hierarchyMeaning\":\"is-a\",\"count\":0,\"property\":[{\"code\":\"child\",\"uri\":\"http://hl7.org/fhir/concept-properties/child\","
				+ "\"description\":\"Child\",\"type\":\"code\"}],"
				+ "\"concept\":[{\"code\":\"conceptCode\",\"display\":\"Label\","
				+ "\"definition\":\"This is a code definition\",\"designation\":[{\"language\":\"uk_en\",\"use\":{\"code\":\"internal\",\"system\":\"http://b2i.sg/test\",\"userSelected\":false},\"value\":\"conceptLabel_uk\"}],\"properties\":[[{\"name\":\"code\",\"valueCode\":\"childConcept\"},{\"name\":\"valueCode\",\"valueCode\":\"childId\"}]]}]}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(codeSystem));
		
	}
	
	@Test
	public void bundleTest() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
			.status(PublicationStatus.ACTIVE)
			.name("Local code system")
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
				+"\"total\":1,"
				+ "\"link\":[{\"relation\":\"self\","
					+ "\"url\":\"http://localhost:8080/snowowl/CodeSystem\"}],"
				+ "\"entry\":[{\"fullUrl\":\"full Url\","
					+ "\"resource\":{\"resourceType\":\"CodeSystem\","
					+ "\"id\":\"repo/shortName\","
					+ "\"language\":\"en\","
					+ "\"url\":\"code system uri\","
					+ "\"name\":\"Local code system\","
					+ "\"status\":\"active\",\"count\":0}}]}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(bundle));
		
	}
	

}
