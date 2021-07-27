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
package com.b2international.snowowl.fhir.tests.domain.valueset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.FilterOperator;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSetFilter;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link ValueSetFilter}
 * @since 8.0.0
 */
public class ValueSetFilterTest extends FhirTest {
	
	private ValueSetFilter filter;
	
	@Before
	public void setup() throws Exception {
		
		filter = ValueSetFilter.builder()
						.operator(FilterOperator.EQUALS)
						.value("1234567")
						.property("filterProperty")
						.build();
	}
	
	@Test
	public void build() throws Exception {
		
		validate(filter);
		
		
	}
	
	private void validate(ValueSetFilter filter) {
		assertEquals(FilterOperator.EQUALS.getCode(), filter.getOperator());
		assertEquals(new Code("1234567"), filter.getValue());
		assertEquals(new Code("filterProperty"), filter.getProperty());
	}

	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(filter));
		assertThat(jsonPath.getString("op"), equalTo("="));
		assertThat(jsonPath.getString("value"), equalTo("1234567"));
		assertThat(jsonPath.getString("property"), equalTo("filterProperty"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		ValueSetFilter readFilter = objectMapper.readValue(objectMapper.writeValueAsString(filter), ValueSetFilter.class);
		validate(readFilter);
	}

}
