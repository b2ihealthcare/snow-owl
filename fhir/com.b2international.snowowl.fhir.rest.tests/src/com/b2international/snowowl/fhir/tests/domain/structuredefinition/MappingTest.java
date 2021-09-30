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

import com.b2international.snowowl.fhir.core.model.structuredefinition.Mapping;
import com.b2international.snowowl.fhir.core.model.structuredefinition.MappingElement;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link MappingElement}
 * @since 8.0.0
 */
public class MappingTest extends FhirTest {
	
	private Mapping mapping;

	@Before
	public void setup() throws Exception {
		
		mapping = Mapping.builder()
				.comment("comment")
				.identity("identity")
				.name("name")
				.uri("uri")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(mapping);
	}

	private void validate(Mapping mapping) {
		assertEquals("comment", mapping.getComment());
		assertEquals("identity", mapping.getIdentity().getIdValue());
		assertEquals("name", mapping.getName());
		assertEquals("uri", mapping.getUri().getUriValue());
		
	}

	@Test
	public void serialize() throws Exception {
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(mapping));
		assertThat(jsonPath.getString("comment"), equalTo("comment"));
		assertThat(jsonPath.getString("identity"), equalTo("identity"));
		assertThat(jsonPath.getString("name"), equalTo("name"));
		assertThat(jsonPath.getString("uri"), equalTo("uri"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Mapping readMapping = objectMapper.readValue(objectMapper.writeValueAsString(mapping), Mapping.class);
		validate(readMapping);
	}
}
