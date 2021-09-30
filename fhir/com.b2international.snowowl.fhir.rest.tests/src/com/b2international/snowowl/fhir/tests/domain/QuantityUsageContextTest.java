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

import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Quantity;
import com.b2international.snowowl.fhir.core.model.usagecontext.QuantityUsageContext;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;
/**
 * Test for validating the {@link QuanityUsageContext} model object.
 * @since 8.0.0
 */
public class QuantityUsageContextTest extends FhirTest {
	
	private QuantityUsageContext usageContext;
	
	@Before
	public void setup() throws Exception {
		
		usageContext = QuantityUsageContext.builder()
				.code(Coding.builder()
						.code("coding")
						.display("codingDisplay")
						.build())
				.value(Quantity.builder()
						.code("valueCode")
						.unit("ms")
						.value(Double.valueOf(1))
						.comparator(QuantityComparator.GREATER_THAN)
						.build())
				.id("usageContextId")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(usageContext);
	}
	
	private void validate(QuantityUsageContext usageContext) {
		assertEquals("usageContextId", usageContext.getId());
		assertEquals("coding", usageContext.getCode().getCodeValue());
		assertEquals("codingDisplay", usageContext.getCode().getDisplay());
		assertEquals(">", usageContext.getValue().getComparator().getCodeValue());
		assertEquals("ms", usageContext.getValue().getUnit());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(usageContext);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(usageContext));
		assertThat(jsonPath.getString("id"), equalTo("usageContextId"));
		assertThat(jsonPath.getString("code.code"), equalTo("coding"));
		assertThat(jsonPath.getString("code.display"), equalTo("codingDisplay"));
		assertThat(jsonPath.getString("valueQuantity.value"), equalTo("1.0"));
		assertThat(jsonPath.getString("valueQuantity.comparator"), equalTo(">"));
		assertThat(jsonPath.getString("valueQuantity.unit"), equalTo("ms"));
		assertThat(jsonPath.getString("valueQuantity.code"), equalTo("valueCode"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		QuantityUsageContext readUsageContext = objectMapper.readValue(objectMapper.writeValueAsString(usageContext), QuantityUsageContext.class);
		validate(readUsageContext);
	}
	
}
