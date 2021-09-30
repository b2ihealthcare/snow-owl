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
package com.b2international.snowowl.fhir.tests.domain.structuredefinition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Example;
import com.b2international.snowowl.fhir.core.model.typedproperty.DateProperty;
import com.b2international.snowowl.fhir.core.model.typedproperty.StringProperty;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Example}
 * @since 8.0.0
 */
public class ExampleTest extends FhirTest {
	
	private Example example;

	@Before
	public void setup() throws Exception {
		
		example = Example.builder()
				.id("id")
				.label("label")
				.value(new StringProperty("string"))
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(example);
	}

	@Test
	public void buildDateType() throws Exception {
		Example dateExample = Example.builder()
				.id("id")
				.label("label")
				.value(new DateProperty(FhirDates.parseDate(TEST_DATE_STRING)))
				.build();
		
		String date = new SimpleDateFormat(FhirDates.DATE_SHORT_FORMAT).format(FhirDates.parseDate(TEST_DATE_STRING));
		
		assertEquals("id", dateExample.getId());
		assertEquals("label", dateExample.getLabel());
		assertEquals(date, dateExample.getValue().getValueString());
	}
	
	private void validate(Example example) {
		assertEquals("id", example.getId());
		assertEquals("label", example.getLabel());
		assertEquals("string", example.getValue().getValueString());
	}

	@Test
	public void serialize() throws Exception {
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(example));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("label"), equalTo("label"));
		assertThat(jsonPath.getString("valueString"), equalTo("string"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Example readExample = objectMapper.readValue(objectMapper.writeValueAsString(example), Example.class);
		validate(readExample);
	}

}
