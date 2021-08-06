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

import com.b2international.snowowl.fhir.core.model.capabilitystatement.Implementation;
import com.b2international.snowowl.fhir.core.model.dt.Reference;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Implementation}
 * @since 8.0.0
 */
public class ImplementationTest extends FhirTest {
	
	private Implementation implementation;

	@Before
	public void setup() throws Exception {
		
		implementation = Implementation.builder()
				.custodian(Reference.builder().reference("reference")
						.build())
				.url("url")
				.description("description")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(implementation);
	}
	
	private void validate(Implementation implementation) {
		assertEquals("reference", implementation.getCustodian().getReference());
		assertEquals("description", implementation.getDescription());
		assertEquals("url", implementation.getUrl().getUriValue());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(implementation);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(implementation));
		assertThat(jsonPath.getString("custodian.reference"), equalTo("reference"));
		assertThat(jsonPath.getString("description"), equalTo("description"));
		assertThat(jsonPath.getString("url"), equalTo("url"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Implementation readImplementation = objectMapper.readValue(objectMapper.writeValueAsString(implementation), Implementation.class);
		validate(readImplementation);
	}

}
