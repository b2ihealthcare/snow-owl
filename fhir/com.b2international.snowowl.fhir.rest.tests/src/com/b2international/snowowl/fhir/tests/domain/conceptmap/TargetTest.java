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

import com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn;
import com.b2international.snowowl.fhir.core.model.conceptmap.Target;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Target}
 * @since 8.0.0
 */
public class TargetTest extends FhirTest {
	
	private Target target;

	@Before
	public void setup() throws Exception {
		
		target = Target.builder()
				.code("code")
				.display("Display")
				.equivalence("Equivalence")
				.comment("comment")
				.addDependsOn(DependsOn.builder()
						.system("system")
						.display("dependsOnDisplay")
						.property("property")
						.value("value")
						.build())
				.addProduct(DependsOn.builder()
						.system("productSystem")
						.display("productDisplay")
						.property("productProperty")
						.value("productValue")
						.build())
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(target);
	}
	
	private void validate(Target target) {

		assertEquals("Display", target.getDisplay());
		assertEquals("Equivalence", target.getEquivalence().getCodeValue());
		assertEquals("code", target.getCode().getCodeValue());
		assertEquals("comment", target.getComment());
		DependsOn dependsOn = target.getDependsOnElements().iterator().next();
		assertEquals("property", dependsOn.getProperty().getUriValue());
		DependsOn product = target.getProducts().iterator().next();
		assertEquals("productProperty", product.getProperty().getUriValue());
		
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(target);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(target));
		assertThat(jsonPath.getString("code"), equalTo("code"));
		assertThat(jsonPath.getString("display"), equalTo("Display"));
		assertThat(jsonPath.getString("equivalence"), equalTo("Equivalence"));
	}
	
	@Test
	public void serializeWithMissingOptionalFields() throws Exception{
		
		Target target = Target.builder().build();
		
		JsonPath jsonPath = getJsonPath(target);
		
		assertNull(jsonPath.get("code"));
		assertNull(jsonPath.get("display"));
		assertNull(jsonPath.get("equivalence"));
		assertNull(jsonPath.get("comment"));
		assertNull(jsonPath.get("dependsOn"));
		assertNull(jsonPath.get("product"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Target readTarget = objectMapper.readValue(objectMapper.writeValueAsString(target), Target.class);
		validate(readTarget);
	}

}
