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

import com.b2international.snowowl.fhir.core.codesystems.EventCapabilityMode;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Endpoint;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Messaging;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.SupportedMessage;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Messaging}
 * @since 8.0.0
 */
public class MessagingTest extends FhirTest {
	
	private Messaging messaging;

	@Before
	public void setup() throws Exception {
		
		messaging = Messaging.builder()
				.addEndpoint(Endpoint.builder()
						.address("address")
						.protocol(Coding.builder()
								.code("code")
								.display("display")
								.system("system")
								.build())
						.build())
				.documentation("documentation")
				.reliableCache(1)
				.addSupportedMessage(SupportedMessage.builder()
						.definition("definition")
						.mode(EventCapabilityMode.SENDER)
						.build())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(messaging);
	}
	
	private void validate(Messaging messaging) {
		assertEquals("documentation", messaging.getDocumentation());
		assertEquals(Integer.valueOf(1), messaging.getReliableCache());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(messaging);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(messaging));
		assertThat(jsonPath.getInt("reliableCache"), equalTo(1));
		assertThat(jsonPath.getString("documentation"), equalTo("documentation"));
		assertThat(jsonPath.getString("endpoint[0].protocol.code"), equalTo("code"));
		assertThat(jsonPath.getString("supportedMessage[0].mode"), equalTo("sender"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Messaging readMessaging = objectMapper.readValue(objectMapper.writeValueAsString(messaging), Messaging.class);
		validate(readMessaging);
	}

}
