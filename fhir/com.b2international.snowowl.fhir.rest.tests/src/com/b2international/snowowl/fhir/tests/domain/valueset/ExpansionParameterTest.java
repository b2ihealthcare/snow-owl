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
package com.b2international.snowowl.fhir.tests.domain.valueset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.*;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Parameter}
 * @since 8.0.0
 */
public class ExpansionParameterTest extends FhirTest {
	
	@Test
	public void serializeStringParameter() throws Exception {
		
		StringParameter parameter = StringParameter.builder()
			.name("paramName")
			.value("paramValue")
			.build();
		
		JsonPath jsonPath = getJsonPath(parameter);
		assertThat(jsonPath.getString("name"), equalTo("paramName"));
		assertThat(jsonPath.get("valueString"), equalTo("paramValue"));
	}
	
	@Test
	public void serializeUriParameter() throws Exception {
		
		UriParameter parameter = UriParameter.builder()
			.name("paramName")
			.value(new Uri("paramValue"))
			.build();
		
		JsonPath jsonPath = getJsonPath(parameter);
		assertThat(jsonPath.getString("name"), equalTo("paramName"));
		assertThat(jsonPath.get("valueUri"), equalTo("paramValue"));
	}
	
	@Test
	public void serializeDateTimeParameter() throws Exception {
		
		Date date = new SimpleDateFormat(FhirDates.DATE_TIME_FORMAT).parse(TEST_DATE_STRING);
		
		DateTimeParameter parameter = DateTimeParameter.builder()
			.name("paramName")
			.value(date)
			.build();
		
		JsonPath jsonPath = getJsonPath(parameter);
		assertThat(jsonPath.getString("name"), equalTo("paramName"));
		assertThat(jsonPath.get("valueDateTime"), equalTo(TEST_DATE_STRING));
	}
	
	@Test
	public void uriParameter() throws Exception {
		
		UriParameter parameter = UriParameter.builder()
				.name("paramName")
				.value(new Uri("paramValue"))
				.build();
		
		assertEquals("paramName", parameter.getName());
		assertEquals("paramValue", parameter.getValue().getUriValue());
		
		printPrettyJson(parameter);
		
		UriParameter readParameter = objectMapper.readValue(objectMapper.writeValueAsString(parameter), UriParameter.class);
		
		assertEquals("paramName", readParameter.getName());
		assertEquals("paramValue", readParameter.getValue().getUriValue());
	
	}
	
	@Test
	public void stringParameter() throws Exception {
		
		StringParameter parameter = StringParameter.builder()
				.name("paramName")
				.value("paramValue")
				.build();
		
		assertEquals("paramName", parameter.getName());
		assertEquals("paramValue", parameter.getValue());
		
		printPrettyJson(parameter);
		
		StringParameter readParameter = objectMapper.readValue(objectMapper.writeValueAsString(parameter), StringParameter.class);
		
		assertEquals("paramName", readParameter.getName());
		assertEquals("paramValue", readParameter.getValue());
	
	}
	
	@Test
	public void integerParameter() throws Exception {
		
		IntegerParameter parameter = IntegerParameter.builder()
				.name("paramName")
				.value(1)
				.build();
		
		assertEquals("paramName", parameter.getName());
		assertEquals(Integer.valueOf(1), parameter.getValue());
		
		printPrettyJson(parameter);
		
		IntegerParameter readParameter = objectMapper.readValue(objectMapper.writeValueAsString(parameter), IntegerParameter.class);
		
		assertEquals("paramName", readParameter.getName());
		assertEquals(Integer.valueOf(1), readParameter.getValue());
	
	}
	
	@Test
	public void decimalParameter() throws Exception {
		
		DecimalParameter parameter = DecimalParameter.builder()
				.name("paramName")
				.value(1.1d)
				.build();
		
		assertEquals("paramName", parameter.getName());
		assertEquals(Double.valueOf(1.1), parameter.getValue());
		
		printPrettyJson(parameter);
		
		DecimalParameter readParameter = objectMapper.readValue(objectMapper.writeValueAsString(parameter), DecimalParameter.class);
		
		assertEquals("paramName", readParameter.getName());
		assertEquals(Double.valueOf(1.1), readParameter.getValue());
	
	}
	
	@Test
	public void dateTimeParameter() throws Exception {
		
		DateTimeParameter parameter = DateTimeParameter.builder()
				.name("paramName")
				.value(FhirDates.parseDate(TEST_DATE_STRING))
				.build();
		
		assertEquals("paramName", parameter.getName());
		assertEquals(FhirDates.parseDate(TEST_DATE_STRING), parameter.getValue());
		
		printPrettyJson(parameter);
		
		DateTimeParameter readParameter = objectMapper.readValue(objectMapper.writeValueAsString(parameter), DateTimeParameter.class);
		
		assertEquals("paramName", readParameter.getName());
		assertEquals(FhirDates.parseDate(TEST_DATE_STRING), readParameter.getValue());
	
	}
	
	@Test
	public void codeParameter() throws Exception {
		
		CodeParameter parameter = CodeParameter.builder()
				.name("paramName")
				.value(new Code("code"))
				.build();
		
		assertEquals("paramName", parameter.getName());
		assertEquals("code", parameter.getValue().getCodeValue());
		
		printPrettyJson(parameter);
		
		CodeParameter readParameter = objectMapper.readValue(objectMapper.writeValueAsString(parameter), CodeParameter.class);
		
		assertEquals("paramName", readParameter.getName());
		assertEquals("code", readParameter.getValue().getCodeValue());
	
	}
	
	@Test
	public void booleanParameter() throws Exception {
		
		BooleanParameter parameter = BooleanParameter.builder()
				.name("paramName")
				.value(true)
				.build();
		
		assertEquals("paramName", parameter.getName());
		assertEquals(true, parameter.getValue());
		
		printPrettyJson(parameter);
		
		BooleanParameter readParameter = objectMapper.readValue(objectMapper.writeValueAsString(parameter), BooleanParameter.class);
		
		assertEquals("paramName", readParameter.getName());
		assertEquals(true, readParameter.getValue());
	
	}
	
	
	
}
