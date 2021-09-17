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

import com.b2international.snowowl.fhir.core.model.structuredefinition.Constraint;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Constraint}
 * @since 8.0.0
 */
public class ConstraintTest extends FhirTest {
	
	private Constraint constraint;

	@Before
	public void setup() throws Exception {
		
		constraint = Constraint.builder()
				.id("id")
				.key("key")
				.human("human")
				.severity("severity")
				.expression("expression")
				.source("source")
				.requirements("requirements")
				.xpath("xpath")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(constraint);
	}

	private void validate(Constraint constraint) {
		assertEquals("id", constraint.getId());
		assertEquals("key", constraint.getKey().getIdValue());
		assertEquals("human", constraint.getHuman());
		assertEquals("severity", constraint.getSeverity().getCodeValue());
		assertEquals("expression", constraint.getExpression());
		assertEquals("source", constraint.getSource().getUriValue());
		assertEquals("requirements", constraint.getRequirements());
		assertEquals("xpath", constraint.getXpath());
	}

	@Test
	public void serialize() throws Exception {
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(constraint));
		assertThat(jsonPath.getString("id"), equalTo("id"));
		assertThat(jsonPath.getString("key"), equalTo("key"));
		assertThat(jsonPath.getString("human"), equalTo("human"));
		assertThat(jsonPath.getString("severity"), equalTo("severity"));
		assertThat(jsonPath.getString("expression"), equalTo("expression"));
		assertThat(jsonPath.getString("source"), equalTo("source"));
		assertThat(jsonPath.getString("requirements"), equalTo("requirements"));
		assertThat(jsonPath.getString("xpath"), equalTo("xpath"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Constraint readConstraint = objectMapper.readValue(objectMapper.writeValueAsString(constraint), Constraint.class);
		validate(readConstraint);
	}

}
