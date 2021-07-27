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
package com.b2international.snowowl.fhir.tests.domain.conceptmap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement;
import com.b2international.snowowl.fhir.core.model.conceptmap.Group;
import com.b2international.snowowl.fhir.core.model.conceptmap.Target;
import com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link ConceptMapElement}
 * @since 8.0.0
 */
public class ConceptMapElementTest extends FhirTest {
	
	private ConceptMapElement element;

	@Before
	public void setup() throws Exception {
		
		element = ConceptMapElement.builder()
				.code("Code")
				.display("Display")
				.addTarget(Target.builder()
						.code("TargetCode")
						.display("TargetDisplay")
						.equivalence("Equivalence")
						.build())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(element);
	}
	
	private void validate(ConceptMapElement element) {

		assertEquals("Code", element.getCode().getCodeValue());
		assertEquals("Display", element.getDisplay());
		Target target = element.getTargets().iterator().next();
		assertEquals("TargetDisplay", target.getDisplay());
		assertEquals("TargetCode", target.getCode().getCodeValue());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(element);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(element));
		assertThat(jsonPath.getString("code"), equalTo("Code"));
		assertThat(jsonPath.getString("display"), equalTo("Display"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		ConceptMapElement readElement = objectMapper.readValue(objectMapper.writeValueAsString(element), ConceptMapElement.class);
		validate(readElement);
	}

}
