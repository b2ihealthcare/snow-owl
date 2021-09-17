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

import com.b2international.snowowl.fhir.core.model.capabilitystatement.Interaction;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Interaction}
 * @since 8.0.0
 */
public class InteractionTest extends FhirTest {
	
	private Interaction interaction;

	@Before
	public void setup() throws Exception {
		
		interaction = Interaction.builder()
				.code("code")
				.documentation("documentation")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(interaction);
	}
	
	private void validate(Interaction interaction) {
		assertEquals("code", interaction.getCode().getCodeValue());
		assertEquals("documentation", interaction.getDocumentation());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(interaction);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(interaction));
		assertThat(jsonPath.getString("code"), equalTo("code"));
		assertThat(jsonPath.getString("documentation"), equalTo("documentation"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Interaction readInteraction = objectMapper.readValue(objectMapper.writeValueAsString(interaction), Interaction.class);
		validate(readInteraction);
	}

}
