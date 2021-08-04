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

import com.b2international.snowowl.fhir.core.model.capabilitystatement.Security;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Security}
 * @since 8.0.0
 */
public class SecurityTest extends FhirTest {
	
	private Security security;

	@Before
	public void setup() throws Exception {
		
		security = Security.builder()
				.addService(CodeableConcept.builder()
						.addCoding(Coding.builder()
								.code("serviceCode")
								.display("serviceDisplay")
								.build())
						.text("codingText")
						.build())
				.cors(true)
				.description("description")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(security);
	}
	
	private void validate(Security security) {
		assertEquals("description", security.getDescription());
		assertEquals(true, security.getIsCors());
		assertEquals("codingText", security.getServices().iterator().next().getText());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(security);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(security));
		assertThat(jsonPath.getString("description"), equalTo("description"));
		assertThat(jsonPath.getBoolean("cors"), equalTo(true));
		assertThat(jsonPath.getString("service[0].text"), equalTo("codingText"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Security readSecurity = objectMapper.readValue(objectMapper.writeValueAsString(security), Security.class);
		validate(readSecurity);
	}

}
