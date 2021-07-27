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
package com.b2international.snowowl.fhir.tests.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Range;
import com.b2international.snowowl.fhir.core.model.dt.SimpleQuantity;
import com.b2international.snowowl.fhir.core.model.usagecontext.RangeUsageContext;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;
/**
 * Test for validating the {@link RangeUsageContext} model object.
 * @since 8.0.0
 */
public class RangeUsageContextTest extends FhirTest {
	
	private RangeUsageContext usageContext;
	
	@Before
	public void setup() throws Exception {
		
		usageContext = RangeUsageContext.builder()
				.code(Coding.builder()
						.code("coding")
						.display("codingDisplay")
						.build())
				.value(Range.builder()
						.low(SimpleQuantity.builder().code("1").id("id").value(1d).unit("ms").build())
						.high(SimpleQuantity.builder().code("10").id("id2").value(10d).unit("ms").build())
						.build())
				.id("usageContextId")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(usageContext);
	}
	
	private void validate(RangeUsageContext usageContext) {
		assertEquals("usageContextId", usageContext.getId());
		assertEquals("coding", usageContext.getCode().getCodeValue());
		
		SimpleQuantity low = usageContext.getValue().getLow();
		assertEquals("1", low.getCode().getCodeValue());
		assertEquals("id", low.getId());
		assertEquals(Double.valueOf(1), low.getValue());
		assertEquals("ms", low.getUnit());

		SimpleQuantity high = usageContext.getValue().getHigh();
		assertEquals("10", high.getCode().getCodeValue());
		assertEquals("id2", high.getId());
		assertEquals(Double.valueOf(10), high.getValue());
		assertEquals("ms", high.getUnit());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(usageContext);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(usageContext));
		assertThat(jsonPath.getString("id"), equalTo("usageContextId"));
		assertThat(jsonPath.getString("valueRange.low.id"), equalTo("id"));
		assertThat(jsonPath.getDouble("valueRange.low.value"), equalTo(1.0));
		assertThat(jsonPath.getString("valueRange.low.unit"), equalTo("ms"));
		assertThat(jsonPath.getString("valueRange.low.code"), equalTo("1"));

		assertThat(jsonPath.getString("valueRange.high.id"), equalTo("id2"));
		assertThat(jsonPath.getDouble("valueRange.high.value"), equalTo(10.0));
		assertThat(jsonPath.getString("valueRange.high.unit"), equalTo("ms"));
		assertThat(jsonPath.getString("valueRange.high.code"), equalTo("10"));
		
	}
	
	@Test
	public void deserialize() throws Exception {
		
		RangeUsageContext readUsageContext = objectMapper.readValue(objectMapper.writeValueAsString(usageContext), RangeUsageContext.class);
		validate(readUsageContext);
	}
	
}
