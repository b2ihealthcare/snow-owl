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
import com.b2international.snowowl.fhir.core.model.capabilitystatement.SupportedMessage;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link SupportedMessage}
 * @since 8.0.0
 */
public class SupportedMessageTest extends FhirTest {
	
	private SupportedMessage supportedMessage;

	@Before
	public void setup() throws Exception {
		
		supportedMessage = SupportedMessage.builder()
				.definition("definition")
				.mode(EventCapabilityMode.RECEIVER)
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(supportedMessage);
	}
	
	private void validate(SupportedMessage supportedMessage) {
		assertEquals(EventCapabilityMode.RECEIVER.getCodeValue(), supportedMessage.getMode().getCodeValue());
		assertEquals("definition", supportedMessage.getDefinition().getUriValue());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(supportedMessage);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(supportedMessage));
		assertThat(jsonPath.getString("mode"), equalTo("receiver"));
		assertThat(jsonPath.getString("definition"), equalTo("definition"));
	}
	
	@Test
	public void deserialize() throws Exception {
		SupportedMessage readSupportedMessage = objectMapper.readValue(objectMapper.writeValueAsString(supportedMessage), SupportedMessage.class);
		printPrettyJson(readSupportedMessage);
		validate(readSupportedMessage);
	}

}
