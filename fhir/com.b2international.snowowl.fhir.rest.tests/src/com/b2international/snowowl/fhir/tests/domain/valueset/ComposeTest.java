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
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.valueset.Compose;
import com.b2international.snowowl.fhir.core.model.valueset.Include;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSetConcept;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSetFilter;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Compose}
 * @since 8.0.0
 */
public class ComposeTest extends FhirTest {
	
	private Compose compose;

	@Before
	public void setup() throws Exception {
		
		compose = Compose.builder()
				.addInclude(Include.builder()
						.system("includeSystem")
						.addFilters(ValueSetFilter.builder()
								.eclExpression("<<1234")
								.operator(FilterOperator.EQUALS)
								.value("1234567")
								.refsetExpression("^123")
								.property("filterProperty")
								.build())
						.build())
				.addExclude(Include.builder()
						.system("excludeSystem")
						.addConcept(ValueSetConcept.builder()
								.code("code")
								.display("display")
								.addDesignation(Designation.builder()
										.language("gb_en")
										.value("designationValue")
										.use(Coding.builder()
												.code("internal")
												.system("http://b2i.sg/test")
												.build())
										.build())
								.build())
						.build())
				.build();
		
	}
	
	@Test
	public void build() throws Exception {
		validate(compose);
	}
	
	private void validate(Compose compose2) {

		assertEquals(1, compose.getIncludes().size());
		assertEquals("includeSystem", compose.getIncludes().iterator().next().getSystem().getUriValue());
		assertEquals("excludeSystem", compose.getExcludes().iterator().next().getSystem().getUriValue());
		assertEquals(1, compose.getExcludes().size());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(compose);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(compose));
		assertThat(jsonPath.getString("include[0].system"), equalTo("includeSystem"));
		assertThat(jsonPath.getString("exclude[0].system"), equalTo("excludeSystem"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Compose readCompose = objectMapper.readValue(objectMapper.writeValueAsString(compose), Compose.class);
		validate(readCompose);
	}

}
