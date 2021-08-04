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

import com.b2international.snowowl.fhir.core.codesystems.DocumentMode;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Document;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Document}
 * @since 8.0.0
 */
public class DocumentTest extends FhirTest {
	
	private Document document;

	@Before
	public void setup() throws Exception {
		
		document = Document.builder()
				.documentation("documentation")
				.mode(DocumentMode.CONSUMER)
				.profile("profile")
				.build();
	}
	
	private void validate(Document document) {
		assertEquals("documentation", document.getDocumentation());
		assertEquals(DocumentMode.CONSUMER.getCode(), document.getMode());
		assertEquals("profile", document.getProfile().getUriValue());
	}
	
	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(document));
		assertThat(jsonPath.getString("documentation"), equalTo("documentation"));
		assertThat(jsonPath.getString("mode"), equalTo("consumer"));
		assertThat(jsonPath.getString("profile"), equalTo("profile"));
	}
	
	@Test
	public void deserialize() throws Exception {
		Document readDocument = objectMapper.readValue(objectMapper.writeValueAsString(document), Document.class);
		validate(readDocument);
	}
}
