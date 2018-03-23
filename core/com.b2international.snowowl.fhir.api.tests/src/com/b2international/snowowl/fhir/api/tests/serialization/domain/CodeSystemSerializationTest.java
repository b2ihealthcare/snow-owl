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

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.ConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.CodeSystem;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.property.BooleanConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.CodeConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.CodingConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.ConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.DateTimeConceptProperty;
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
		SupportedConceptProperty conceptProperty = SupportedConceptProperty.builder(ConceptProperties.INACTIVE).build();
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
			.code(ConceptProperties.INACTIVE.getCode())
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
			.code(ConceptProperties.CHILD.getCode())
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
				.code(ConceptProperties.CHILD.getCode())
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
					+ "\"status\":\"active\"}}]}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(bundle));
		
	}
	

}
