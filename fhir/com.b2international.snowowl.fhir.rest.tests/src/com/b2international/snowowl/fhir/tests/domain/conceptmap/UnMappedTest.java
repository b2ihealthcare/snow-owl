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
package com.b2international.snowowl.fhir.tests.domain.conceptmap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.ConceptMapGroupUnmappedMode;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link UnMapped}
 * @since 8.0.0
 */
public class UnMappedTest extends FhirTest {
	
	private UnMapped unMapped;

	@Before
	public void setup() throws Exception {
		
		unMapped = UnMapped.builder()
			.mode(ConceptMapGroupUnmappedMode.FIXED)
			.code("code")
			.display("display")
			.url("uri")
			.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(unMapped);
	}
	
	@Test
	public void buildInvalid() throws Exception {
		
		exception.expect(ValidationException.class);
		UnMapped.builder()
			.code("Code")
			.display("Display")
			.url("Url")
			.build();
	}
	
	private void validate(UnMapped unMapped) {

		assertEquals("code", unMapped.getCode().getCodeValue());
		assertEquals("fixed", unMapped.getMode().getCodeValue());
		assertEquals("display", unMapped.getDisplay());
		assertEquals("uri", unMapped.getUrl().getUriValue());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(unMapped);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(unMapped));
		assertThat(jsonPath.getString("url"), equalTo("uri"));
		assertThat(jsonPath.getString("display"), equalTo("display"));
		assertThat(jsonPath.getString("code"), equalTo("code"));
		assertThat(jsonPath.getString("mode"), equalTo("fixed"));
	}
	
	@Test
	public void serializeWithMissingOptionalFields() throws Exception {
		
		UnMapped unMapped = UnMapped.builder()
			.mode("Mode")
			.build();

		JsonPath jsonPath = getJsonPath(unMapped);
		assertNull(jsonPath.get("code"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		UnMapped readUnMapped = objectMapper.readValue(objectMapper.writeValueAsString(unMapped), UnMapped.class);
		validate(readUnMapped);
	}

}
