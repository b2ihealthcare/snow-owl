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

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.*;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Discriminator;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Example;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Slicing;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
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
				.label("example")
				.valueString("value")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(example);
	}
	
	private void validate(Example example) {
		assertEquals("id", example.getId());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(example);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(example));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("label"), equalTo("example"));
		assertThat(jsonPath.getString("valueString"), equalTo("value"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Example readExample = objectMapper.readValue(objectMapper.writeValueAsString(example), Example.class);
		validate(readExample);
	}

}
