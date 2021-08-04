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

import com.b2international.snowowl.fhir.core.model.capabilitystatement.Operation;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Operation}
 * @since 8.0.0
 */
public class OperationTest extends FhirTest {
	
	private Operation operation;

	@Before
	public void setup() throws Exception {
		
		operation = Operation.builder()
				.name("name")
				.definition("definition")
				.documenation("documentation")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(operation);
	}
	
	private void validate(Operation operation) {
		assertEquals("name", operation.getName());
		assertEquals("definition", operation.getDefinition().getUriValue());
		assertEquals("documentation", operation.getDocumenation());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(operation);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(operation));
		assertThat(jsonPath.getString("name"), equalTo("name"));
		assertThat(jsonPath.getString("definition"), equalTo("definition"));
		assertThat(jsonPath.getString("documentation"), equalTo("documentation"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Operation readOperation = objectMapper.readValue(objectMapper.writeValueAsString(operation), Operation.class);
		validate(readOperation);
	}

}
