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

import com.b2international.snowowl.fhir.core.codesystems.AggregationMode;
import com.b2international.snowowl.fhir.core.codesystems.ReferenceVersionRules;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.structuredefinition.Type;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Type}
 * @since 8.0.0
 */
public class TypeTest extends FhirTest {
	
	private Type type;

	@Before
	public void setup() throws Exception {
		
		type = Type.builder()
				.addAggregation(AggregationMode.BUNDLED)
				.id("id")
				.code("code")
				.profile("profile")
				.versioning(ReferenceVersionRules.EITHER)
				.targetProfile("targetProfile")
				.build();
	}
	
	@Test
	public void buildInvalid() {
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		
		Type.builder().build();
	}
	
	@Test
	public void build() throws Exception {
		validate(type);
	}
	
	private void validate(Type type) {
		assertEquals("id", type.getId());
		assertEquals("code", type.getCode().getUriValue());
		assertEquals(AggregationMode.BUNDLED.getCode(), type.getAggregation().iterator().next());
		assertEquals("profile", type.getProfile().getUriValue());
		assertEquals("targetProfile", type.getTargetProfile().getUriValue());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(type);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(type));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("code"), equalTo("code"));
		assertThat(jsonPath.getString("profile"), equalTo("profile"));
		assertThat(jsonPath.getString("targetProfile"), equalTo("targetProfile"));
		assertThat(jsonPath.getString("aggregation[0]"), equalTo("bundled"));
		assertThat(jsonPath.getString("versioning"), equalTo("either"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Type readType = objectMapper.readValue(objectMapper.writeValueAsString(type), Type.class);
		validate(readType);
	}

}
