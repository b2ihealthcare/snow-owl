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

import com.b2international.snowowl.fhir.core.model.capabilitystatement.Endpoint;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Endpoint}
 * @since 8.0.0
 */
public class EndpointTest extends FhirTest {
	
	private Endpoint endpoint;

	@Before
	public void setup() throws Exception {
		
		endpoint = Endpoint.builder()
				.address("address")
				.protocol(Coding.builder().code("code").display("display").system("system").build())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(endpoint);
	}
	
	private void validate(Endpoint endpoint) {
		assertEquals("address", endpoint.getAddress().getUriValue());
		assertEquals("code", endpoint.getProtocol().getCode().getCodeValue());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(endpoint);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(endpoint));
		assertThat(jsonPath.getString("address"), equalTo("address"));
		assertThat(jsonPath.getString("protocol.code"), equalTo("code"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Endpoint readEndpoint = objectMapper.readValue(objectMapper.writeValueAsString(endpoint), Endpoint.class);
		validate(readEndpoint);
	}

}
