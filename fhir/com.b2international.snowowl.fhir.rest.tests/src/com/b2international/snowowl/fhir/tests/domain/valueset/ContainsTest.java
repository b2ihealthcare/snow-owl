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

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Contains}
 * @since 8.0.0
 */
public class ContainsTest extends FhirTest {
	
	private Contains contains;

	@Before
	public void setup() throws Exception {
		
		contains = Contains.builder()
						.system("systemUri")
						.isAbstract(true)
						.inactive(false)
						.version("20140131")
						.code("Code")
						.display("displayValue")
						.addDesignation(Designation.builder()
								.language("en-us")
								.value("pt")
								.build())
						.addContains(Contains.builder()
								.code("nestedContains")
								.build())
						.build();
		
	}
	
	@Test
	public void build() throws Exception {
		validate(contains);
	}
	
	private void validate(Contains contains) {

		assertEquals("systemUri", contains.getSystem().getUriValue());
		assertEquals(true, contains.getIsAbstract());
		assertEquals(false, contains.getInactive());
		assertEquals("20140131", contains.getVersion());
		assertEquals("Code", contains.getCode().getCodeValue());
		assertEquals("displayValue", contains.getDisplay());

		Designation designation = contains.getDesignations().iterator().next();
		
		assertEquals("pt", designation.getValue());
		assertEquals("en-us", designation.getLanguage());
		assertEquals("nestedContains", contains.getContains().iterator().next().getCode().getCodeValue());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(contains);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(contains));
		assertThat(jsonPath.getString("system"), equalTo("systemUri"));
		assertThat(jsonPath.getString("inactive"), equalTo("false"));
		assertThat(jsonPath.getString("abstract"), equalTo("true"));
		assertThat(jsonPath.getString("version"), equalTo("20140131"));
		assertThat(jsonPath.getString("code"), equalTo("Code"));
		assertThat(jsonPath.getString("display"), equalTo("displayValue"));
		assertThat(jsonPath.getString("contains[0].code"), equalTo("nestedContains"));
		assertThat(jsonPath.getString("designation[0].language"), equalTo("en-us"));
		assertThat(jsonPath.getString("designation[0].value"), equalTo("pt"));
		assertThat(jsonPath.getString("designation[0].languageCode"), equalTo("en-us"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Contains readContains = objectMapper.readValue(objectMapper.writeValueAsString(contains), Contains.class);
		validate(readContains);
	}

}
