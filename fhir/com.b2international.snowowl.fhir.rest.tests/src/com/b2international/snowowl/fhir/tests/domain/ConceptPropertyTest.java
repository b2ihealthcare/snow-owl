/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.property.*;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link ConceptProperty} implementations.
 * @since 8.0.0
 */
public class ConceptPropertyTest extends FhirTest {
	
	@Test
	public void serializeConceptProperty() throws Exception {
		
		SupportedConceptProperty conceptProperty = SupportedConceptProperty.builder(CommonConceptProperties.INACTIVE).build();
		
		String expectedJson = "{\"code\":\"inactive\","
				+ "\"uri\":\"http://hl7.org/fhir/concept-properties/inactive\","
				+ "\"description\":\"Inactive\","
				+ "\"type\":\"boolean\"}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void serializeBooleanConceptProperty() throws Exception {
		
		BooleanConceptProperty conceptProperty = BooleanConceptProperty.builder()
			.code("childConcept")
			.value(true)
			.build();
		
		String expected = "{\"code\":\"childConcept\",\"valueBoolean\":true}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void serializeStringConceptProperty() throws Exception {
		
		StringConceptProperty conceptProperty = StringConceptProperty.builder()
			.code("childConcept")
			.value("string")
			.build();
		
		String expected = "{\"code\":\"childConcept\",\"valueString\":\"string\"}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void serializeIntegerConceptProperty() throws Exception {
		
		IntegerConceptProperty conceptProperty = IntegerConceptProperty.builder()
			.code("childConcept")
			.value(1)
			.build();
		
		String expected = "{\"code\":\"childConcept\",\"valueInteger\":1}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}

	@Test
	public void serializeDecimalConceptProperty() throws Exception {
		
		DecimalConceptProperty conceptProperty = DecimalConceptProperty.builder()
				.code("childConcept")
				.value(1.12f)
				.build();
		
		String expected = "{\"code\":\"childConcept\",\"valueDecimal\":1.12}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void serializeDateConceptProperty() throws Exception {
		
		Date date = Dates.parse("2018-03-23T08:49:40.000+0100", FhirDates.DATE_TIME_FORMAT);
		
		DateTimeConceptProperty conceptProperty = DateTimeConceptProperty.builder()
			.code("childConcept")
			.value(date)
			.build();
		
		String expected = "{\"code\":\"childConcept\",\"valueDateTime\":\"2018-03-23T07:49:40.000+0000\"}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void serializeCodeConceptProperty() throws Exception {
		
		CodeConceptProperty conceptProperty = CodeConceptProperty.builder()
			.code("childConcept")
			.value(new Code("code"))
			.build();
		
		String expected = "{\"code\":\"childConcept\",\"valueCode\":\"code\"}";
		Assert.assertEquals(expected, objectMapper.writeValueAsString(conceptProperty));
	}
	
	@Test
	public void serializeCodingConceptProperty() throws Exception {
		
		CodingConceptProperty conceptProperty = CodingConceptProperty.builder()
				.code(CommonConceptProperties.CHILD.getCode())
				.value(new Coding.Builder()
					.code("codingCode")
					.system("uri")
					.build())
				.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(conceptProperty));
		
		assertThat(jsonPath.getString("code"), equalTo("child"));
		jsonPath.setRoot("valueCoding");
		assertThat(jsonPath.getString("code"), equalTo("codingCode"));
		assertThat(jsonPath.getString("system"), equalTo("uri"));
	}
	
	@Test
	public void deserializeCodingProperty() throws Exception {
		
		CodingConceptProperty conceptProperty = CodingConceptProperty.builder()
				.code(CommonConceptProperties.CHILD.getCode())
				.value(new Coding.Builder()
					.code("codingCode")
					.system("uri")
					.build())
				.build();
		
		ConceptProperty readProperty = objectMapper.readValue(objectMapper.writeValueAsString(conceptProperty), ConceptProperty.class);
	}

}
