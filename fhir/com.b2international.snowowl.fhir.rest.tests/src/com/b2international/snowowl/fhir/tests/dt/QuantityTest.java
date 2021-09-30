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

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Quantity;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Quantity}
 * @since 8.0.0
 */
public class QuantityTest extends FhirTest {
	
	private Quantity quantity;
	
	@Before
	public void setup() throws Exception {
		
		quantity = Quantity.builder()
				.value(12.3)
				.unit("mg")
				.system("uri:LOINC")
				.code("code")
				.comparator(QuantityComparator.GREATER_OR_EQUAL_TO)
				.build();
	}
	
	@Test
	public void build() throws Exception {
		
		assertEquals(Double.valueOf(12.3), quantity.getValue());
		assertEquals("mg", quantity.getUnit());
		assertEquals(new Uri("uri:LOINC"), quantity.getSystem());
		assertEquals(new Code("code"), quantity.getCode());
		assertEquals(QuantityComparator.GREATER_OR_EQUAL_TO.getCode(), quantity.getComparator());
		
	}
	
	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(quantity));
		assertThat(jsonPath.getDouble("value"), equalTo(12.3));
		assertThat(jsonPath.getString("comparator"), equalTo(">="));
		assertThat(jsonPath.getString("unit"), equalTo("mg"));
		assertThat(jsonPath.getString("system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("code"), equalTo("code"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Quantity readQuantity = objectMapper.readValue(objectMapper.writeValueAsString(quantity), Quantity.class);
		assertEquals(Double.valueOf(12.3), readQuantity.getValue());
		assertEquals("mg", readQuantity.getUnit());
		assertEquals(new Uri("uri:LOINC"), readQuantity.getSystem());
		assertEquals(new Code("code"), readQuantity.getCode());
		assertEquals(QuantityComparator.GREATER_OR_EQUAL_TO.getCode(), readQuantity.getComparator());
		
	}
}
