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
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link DependsOn}
 * @since 8.0.0
 */
public class DependsOnTest extends FhirTest {
	
	private DependsOn dependsOn;

	@Before
	public void setup() throws Exception {
		
		dependsOn = DependsOn.builder()
				.system("system")
				.display("dependsOnDisplay")
				.property("property")
				.value("value")
				.build();	
	}
	
	@Test
	public void build() throws Exception {
		validate(dependsOn);
	}
		
	@Test
	public void buildInvalid() {

		exception.expect(ValidationException.class);
		
		DependsOn.builder()
			.value("Value")
			.system("System")
			.display("Display")
			.build();
	}
	
	private void validate(DependsOn dependsOn) {

		assertEquals("system", dependsOn.getSystem().getUriValue());
		assertEquals("dependsOnDisplay", dependsOn.getDisplay());
		assertEquals("property", dependsOn.getProperty().getUriValue());
		assertEquals("value", dependsOn.getValue());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(dependsOn);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(dependsOn));
		assertThat(jsonPath.getString("property"), equalTo("property"));
		assertThat(jsonPath.getString("system"), equalTo("system"));
		assertThat(jsonPath.getString("value"), equalTo("value"));
		assertThat(jsonPath.getString("display"), equalTo("dependsOnDisplay"));
	}
	
	@Test
	public void serializeWithMissingOptionalFields() throws Exception {
		
		DependsOn dependsOn = DependsOn.builder()
		.property("Property")
		.value("Value")
		.build();
		
		JsonPath jsonPath = getJsonPath(dependsOn);
		
		assertThat(jsonPath.get("property"), equalTo("Property"));
		assertNull(jsonPath.get("system"));
		assertThat(jsonPath.get("value"), equalTo("Value"));
		assertNull(jsonPath.get("display"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		DependsOn readDependsOn = objectMapper.readValue(objectMapper.writeValueAsString(dependsOn), DependsOn.class);
		validate(readDependsOn);
	}

}
