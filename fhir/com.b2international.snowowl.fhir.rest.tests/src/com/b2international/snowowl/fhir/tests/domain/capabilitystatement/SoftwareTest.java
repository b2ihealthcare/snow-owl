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
package com.b2international.snowowl.fhir.tests.domain.capabilitystatement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Software;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Software}
 * @since 8.0.0
 */
public class SoftwareTest extends FhirTest {
	
	private Software software;

	@Before
	public void setup() throws Exception {
		
		software = Software.builder()
				.name("name")
				.version("version")
				.releaseDate(FhirDates.parseDate(TEST_DATE_STRING))
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(software);
	}
	
	private void validate(Software software) {
		assertEquals("name", software.getName());
		assertEquals("version", software.getVersion());
		assertEquals(FhirDates.parseDate(TEST_DATE_STRING), software.getReleaseDate());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(software);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(software));
		assertThat(jsonPath.getString("name"), equalTo("name"));
		assertThat(jsonPath.getString("version"), equalTo("version"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Software readSoftware = objectMapper.readValue(objectMapper.writeValueAsString(software), Software.class);
		validate(readSoftware);
	}

}
