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
package com.b2international.snowowl.fhir.tests.dt;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Period;
import com.b2international.snowowl.fhir.core.model.dt.Reference;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Period}.
 * @since 8.0.0
 */
public class ReferenceTest extends FhirTest {
	
	private Reference reference;
	
	@Before
	public void setup() throws Exception {
		
		Identifier identifier = Identifier.builder()
				.system("system")
				.build();
			
		reference = Reference.builder().reference("reference url")
				.identifier(identifier)
				.display("displayString")
				.build();
	}
	
	@Test
	public void build() throws ParseException {
		
		assertEquals("reference url", reference.getReference());
		assertEquals("displayString", reference.getDisplay());
		assertEquals(new Uri("system"), reference.getIdentifier().getSystem());
	}

	@Test
	public void serialize() throws Exception {
		
		String expected = "{\"reference\":\"reference url\"," + 
				"\"identifier\":{\"system\":\"system\"}," + 
				"\"display\":\"displayString\"}";
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(reference));
		assertThat(jsonPath.getString("reference"), equalTo("reference url"));
		assertThat(jsonPath.getString("identifier.system"), equalTo("system"));
		assertThat(jsonPath.getString("display"), equalTo("displayString"));
		
		assertEquals(expected, objectMapper.writeValueAsString(reference));
	}
	
	
	@Test
	public void deserialize() throws Exception {
		
		Reference readReference = objectMapper.readValue(objectMapper.writeValueAsString(reference), Reference.class);
		assertEquals("reference url", readReference.getReference());
		assertEquals("displayString", readReference.getDisplay());
		assertEquals(new Uri("system"), readReference.getIdentifier().getSystem());
		
	}
}
