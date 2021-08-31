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
package com.b2international.snowowl.fhir.tests.operationdefinition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.operationdefinition.Overload;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Overload}
 * @since 8.0.0
 */
public class OverloadTest extends FhirTest {
	
	private Overload overload;

	@Before
	public void setup() throws Exception {
		
		overload = Overload.builder()
				.id("id")
				.addParameterName("parameterName")
				.comment("comment")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(overload);
	}
	
	private void validate(Overload overload) {
		assertEquals("id", overload.getId());
		assertEquals("parameterName", overload.getParameterNames().iterator().next());
		assertEquals("comment", overload.getComment());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(overload);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(overload));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("parameterName[0]"), equalTo("parameterName"));
		assertThat(jsonPath.getString("comment"), equalTo("comment"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Overload readOverload = objectMapper.readValue(objectMapper.writeValueAsString(overload), Overload.class);
		validate(readOverload);
	}

}
