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
package com.b2international.snowowl.fhir.tests.domain.operationdefinition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.operationdefinition.Binding;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Binding}
 * @since 8.0.0
 */
public class BindingTest extends FhirTest {
	
	private Binding binding;

	@Before
	public void setup() throws Exception {
		
		binding = Binding.builder()
				.id("id")
				.strength("strength")
				.valueSetUri("valueSetUri")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(binding);
	}
	
	private void validate(Binding binding) {
		assertEquals("id", binding.getId());
		assertEquals("strength", binding.getStrength().getCodeValue());
		assertEquals("valueSetUri", binding.getValueSetUri().getUriValue());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(binding);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(binding));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("strength"), equalTo("strength"));
		assertThat(jsonPath.getString("valueSetUri"), equalTo("valueSetUri"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Binding readBinding = objectMapper.readValue(objectMapper.writeValueAsString(binding), Binding.class);
		validate(readBinding);
	}

}
