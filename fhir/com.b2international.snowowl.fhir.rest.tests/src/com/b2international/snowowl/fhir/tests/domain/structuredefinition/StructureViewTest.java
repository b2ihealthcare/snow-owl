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
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.structuredefinition.*;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link StructureView}
 * @since 8.0.0
 */
public class StructureViewTest extends FhirTest {
	
	private StructureView structureView;

	@Before
	public void setup() throws Exception {
		
		structureView = StructureView.builder()
				.addElementDefinition(ElementDefinition.builder()
						.addAlias("alias")
						.addCode(Coding.builder()
							.code("coding")
							.display("codingDisplay")
							.build())
						.addCondition(new Id("condition"))
						.addMapping(MappingElement.builder()
							.comment("comment")
							.id("id")
							.identity("identity")
							.language("en")
							.map("map")
							.build())
						.path("path")
						.build())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		printPrettyJson(structureView);
		validate(structureView);
	}
	
	private void validate(StructureView structureView) {
		assertEquals(false, structureView.getElementDefinitions().isEmpty());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(structureView);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(structureView));
		assertThat(jsonPath.getString("element[0].path"), equalTo("path"));
	}
	
	@Test
	public void deserialize() throws Exception {
		StructureView readStructureView = objectMapper.readValue(objectMapper.writeValueAsString(structureView), StructureView.class);
		validate(readStructureView);
	}

}
