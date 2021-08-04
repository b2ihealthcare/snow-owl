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

import com.b2international.snowowl.fhir.core.codesystems.SearchParamType;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Interaction;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.SearchParam;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Interaction}
 * @since 8.0.0
 */
public class SearchParamTest extends FhirTest {
	
	private SearchParam searchParam;

	@Before
	public void setup() throws Exception {
		
		searchParam = SearchParam.builder()
				.definition("definition")
				.documentation("documentation")
				.name("name")
				.type(SearchParamType.STRING)
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(searchParam);
	}
	
	private void validate(SearchParam searchParam) {
		assertEquals("definition", searchParam.getDefinition().getUriValue());
		assertEquals("documentation", searchParam.getDocumentation());
		assertEquals("name", searchParam.getName());
		assertEquals(SearchParamType.STRING.getCode(), searchParam.getType());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(searchParam);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(searchParam));
		assertThat(jsonPath.getString("definition"), equalTo("definition"));
		assertThat(jsonPath.getString("documentation"), equalTo("documentation"));
		assertThat(jsonPath.getString("name"), equalTo("name"));
		assertThat(jsonPath.getString("type"), equalTo("string"));
	}
	
	@Test
	public void deserialize() throws Exception {
		SearchParam readSearchParam = objectMapper.readValue(objectMapper.writeValueAsString(searchParam), SearchParam.class);
		validate(readSearchParam);
	}

}
