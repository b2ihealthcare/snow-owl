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

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Range;
import com.b2international.snowowl.fhir.core.model.dt.SimpleQuantity;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Range}
 * @since 8.0.0
 */
public class RangeTest extends FhirTest {
	
	private Range range;
	
	@Before
	public void setup() throws Exception {
		
		SimpleQuantity low = SimpleQuantity.builder()
				.value(12.3)
				.unit("mg")
				.system("uri:LOINC")
				.code("code1")
				.build();
		
		SimpleQuantity high = (SimpleQuantity) SimpleQuantity.builder()
				.value(120.3)
				.unit("mg")
				.system("uri:LOINC")
				.code("code2")
				.build();
		
		range = Range.builder().low(low).high(high).id("id").build();
	}
	
	@Test
	public void build() throws Exception {
		
		assertEquals("id", range.getId());
		SimpleQuantity low = range.getLow();
		assertEquals(Double.valueOf(12.3), low.getValue());
		assertEquals("mg", low.getUnit());
		assertEquals(new Code("code1"), low.getCode());
		SimpleQuantity high = range.getHigh();
		assertEquals(Double.valueOf(120.3), high.getValue());
		assertEquals("mg", high.getUnit());
		assertEquals(new Code("code2"), high.getCode());
		
	}

	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(range));
		assertThat(jsonPath.getDouble("low.value"), equalTo(12.3));
		assertThat(jsonPath.getString("low.unit"), equalTo("mg"));
		assertThat(jsonPath.getString("low.system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("low.code"), equalTo("code1"));
		assertThat(jsonPath.getDouble("high.value"), equalTo(120.3));
		assertThat(jsonPath.getString("high.unit"), equalTo("mg"));
		assertThat(jsonPath.getString("high.system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("high.code"), equalTo("code2"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Range readRange = objectMapper.readValue(objectMapper.writeValueAsString(range), Range.class);
		assertEquals("id", readRange.getId());
		SimpleQuantity low = readRange.getLow();
		assertEquals(Double.valueOf(12.3), low.getValue());
		assertEquals("mg", low.getUnit());
		assertEquals(new Code("code1"), low.getCode());
		SimpleQuantity high = range.getHigh();
		assertEquals(Double.valueOf(120.3), high.getValue());
		assertEquals("mg", high.getUnit());
		assertEquals(new Code("code2"), high.getCode());
	}

}
