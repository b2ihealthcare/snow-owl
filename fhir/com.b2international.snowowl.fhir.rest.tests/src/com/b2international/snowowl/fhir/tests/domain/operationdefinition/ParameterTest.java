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
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.operationdefinition.Binding;
import com.b2international.snowowl.fhir.core.model.operationdefinition.Parameter;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Parameter}
 * @since 8.0.0
 */
public class ParameterTest extends FhirTest {
	
	private Parameter parameter;

	@Before
	public void setup() throws Exception {
		
		parameter = Parameter.builder()
				.id("id")
				.name("name")
				.use("use")
				.min(1)
				.max(2)
				.documentation("documentation")
				.type(new Code("type"))
				.addTargetProfile(new Uri("targetProfile"))
				.searchType("searchType")
				.binding(Binding.builder()
						.strength("strength")
						.valueSetUri("valueSetUri")
						.build())
				.addParameter(Parameter.builder()
						.id("id2")
						.name("name2")
						.use("use2")
						.min(0)
						.max(1)
						.build())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(parameter);
	}
	
	private void validate(Parameter parameter) {
		assertEquals("id", parameter.getId());
		assertEquals("name", parameter.getName().getCodeValue());
		assertEquals("use", parameter.getUse().getCodeValue());
		assertEquals(1, parameter.getMin().intValue());
		assertEquals("2", parameter.getMax());
		assertEquals("documentation", parameter.getDocumentation());
		assertEquals("type", parameter.getType().getCodeValue());
		assertEquals("targetProfile", parameter.getTargetProfiles().iterator().next().getUriValue());
		assertNotNull(parameter.getBinding());
		assertEquals("id2", parameter.getParameters().iterator().next().getId());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(parameter);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(parameter));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("name"), equalTo("name"));
		assertThat(jsonPath.getString("use"), equalTo("use"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Parameter readParameter = objectMapper.readValue(objectMapper.writeValueAsString(parameter), Parameter.class);
		validate(readParameter);
	}

}
