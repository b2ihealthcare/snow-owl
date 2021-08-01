/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;

import org.junit.Test;

import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.structuredefinition.ElementDefinition;
import com.b2international.snowowl.fhir.core.model.typedproperty.DateProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.DateTimeProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.InstantProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.StringProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.TypedProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.TypedPropertyDeserializer;
import com.b2international.snowowl.fhir.tests.TypedPropertySerializationTest.DeserTestClass;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.restassured.path.json.JsonPath;

/**
 * Custom unwrapping serializer tests to manage fields that are 'typed' e.g. on ElementDefinition.defaultValue[x].
 * 
 * @since 7.1
 */
public class TypedPropertySerializationTest extends FhirTest {
	
	@JsonDeserialize(using = TypedPropertyDeserializer.class)
	public static class DeserTestClass {
		
		@JsonProperty
		private String testString = "test";
		
		@JsonUnwrapped
		@JsonProperty
		private TypedProperty<?> valueObject = new StringProperty("stringValue");
		
	}
	
	@Test
	public void deserializationTest() throws Exception {
		
		DeserTestClass deserTestClass = new DeserTestClass();
		printPrettyJson(deserTestClass);
		
		DeserTestClass readClass = objectMapper.readValue(objectMapper.writeValueAsString(deserTestClass), DeserTestClass.class);
		printPrettyJson(readClass);
	
	}

	
	@Test
	public void stringTypedPropertyTest() throws JsonProcessingException {
		
		final class TestClass {
			
			@JsonProperty
			private String testString = "test";
			
			@JsonUnwrapped
			@JsonProperty
			private TypedProperty<?> valueObject = new StringProperty("stringValue");
		}
		
		
		TestClass testObject = new TestClass();
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(testObject));
		assertThat(jsonPath.getString("testString"), equalTo("test"));
		assertThat(jsonPath.getString("valueString"), equalTo("stringValue"));
	}
	
	@Test
	public void dateTypedPropertyTest() throws Exception {
		
		Date date = Dates.parse(TEST_DATE_STRING, FhirDates.DATE_SHORT_FORMAT);

		final class TestClass {
			
			@JsonUnwrapped
			@JsonProperty
			private TypedProperty<?> valueObject = new DateProperty(date);
		}
		
		TestClass testObject = new TestClass();
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(testObject));
		assertThat(jsonPath.getString("valueDate"), equalTo("2018-03-23T00:00:00.000+0000"));
		
	}
	
	@Test
	public void dateTimeTypedPropertyTest() throws Exception {
		
		Date date = Dates.parse(TEST_DATE_STRING, FhirDates.DATE_TIME_FORMAT);

		final class TestClass {
			
			@JsonUnwrapped
			@JsonProperty
			private TypedProperty<?> valueObject = new DateTimeProperty(date);
		}
		
		TestClass testObject = new TestClass();
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(testObject));
		assertThat(jsonPath.getString("valueDate"), equalTo(TEST_DATE_STRING));
	}
	
	@Test
	public void instantTypedPropertyTest() throws Exception {
		
		Date date = Dates.parse(TEST_DATE_STRING, FhirDates.DATE_TIME_FORMAT);
		Instant instant = Instant.builder().instant(date).build();

		final class TestClass {
			
			@JsonUnwrapped
			@JsonProperty
			private TypedProperty<?> valueObject = new InstantProperty(instant);
		}
		
		TestClass testObject = new TestClass();
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(testObject));
		assertThat(jsonPath.getString("valueInstant"), equalTo("2018-03-23T07:49:40Z"));
	}

}
