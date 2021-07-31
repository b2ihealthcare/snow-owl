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

import com.b2international.snowowl.fhir.core.model.structuredefinition.Base;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Base}
 * @since 8.0.0
 */
public class BaseTest extends FhirTest {
	
	private Base base;

	@Before
	public void setup() throws Exception {
		
		base = Base.builder()
				.id("id")
				.min(1)
				.max("2")
				.path("path")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(base);
	}
	
	private void validate(Base base) {
		assertEquals("id", base.getId());
		assertEquals("path", base.getPath());
		assertEquals(1, base.getMin());
		assertEquals("2", base.getMax());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(base);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(base));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("path"), equalTo("path"));
		assertThat(jsonPath.getInt("min"), equalTo(1));
		assertThat(jsonPath.getString("max"), equalTo("2"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Base readBase = objectMapper.readValue(objectMapper.writeValueAsString(base), Base.class);
		validate(readBase);
	}

}
