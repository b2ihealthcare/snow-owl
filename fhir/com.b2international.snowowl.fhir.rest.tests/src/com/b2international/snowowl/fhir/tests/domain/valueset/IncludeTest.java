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
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.Include;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSetConcept;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSetFilter;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link Include}
 * @since 8.0.0
 */
public class IncludeTest extends FhirTest {
	
	private Include include;
	
	@Before
	public void setup() throws Exception {
		
		include = Include.builder()
				.system("system")
				.version("version")
				.addValueSet("valueSetUri")
				.addFilters(ValueSetFilter.builder()
						.operator(FilterOperator.EQUALS)
						.value("1234567")
						.property("filterProperty")
						.build())
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
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(include);
	}
	
	private void validate(Include include) {
		
		assertEquals(new Uri("system"), include.getSystem());
		assertEquals("version", include.getVersion());
		assertEquals("valueSetUri", include.getValueSets().iterator().next().getUriValue());
		ValueSetFilter filter = include.getFilters().iterator().next();
		assertEquals(new Code("filterProperty"), filter.getProperty());
		assertEquals(FilterOperator.EQUALS.getCode(), filter.getOperator());
		assertEquals(new Code("1234567"), filter.getValue());
		
		ValueSetConcept concept = include.getConcepts().iterator().next();
		assertEquals("code", concept.getCode().getCodeValue());
		assertEquals("display", concept.getDisplay());
		
		Designation designation = concept.getDesignations().iterator().next();
		assertEquals("gb_en", designation.getLanguage());
		assertEquals("designationValue", designation.getValue());
		assertEquals("internal", designation.getUse().getCode().getCodeValue());
	}

	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(include));
		assertThat(jsonPath.getString("system"), equalTo("system"));
		assertThat(jsonPath.getString("version"), equalTo("version"));
		assertThat(jsonPath.getString("valueSet[0]"), equalTo("valueSetUri"));
		assertThat(jsonPath.getString("concept[0].code"), equalTo("code"));
		assertThat(jsonPath.getString("concept[0].display"), equalTo("display"));
		assertThat(jsonPath.getString("concept[0].designation[0].language"), equalTo("gb_en"));
		assertThat(jsonPath.getString("concept[0].designation[0].use.code"), equalTo("internal"));
		assertThat(jsonPath.getString("concept[0].designation[0].use.system"), equalTo("http://b2i.sg/test"));
		assertThat(jsonPath.getString("concept[0].designation[0].value"), equalTo("designationValue"));
		assertThat(jsonPath.getString("concept[0].designation[0].languageCode"), equalTo("gb_en"));
		assertThat(jsonPath.getString("filter[0].property"), equalTo("filterProperty"));
		assertThat(jsonPath.getString("filter[0].value"), equalTo("1234567"));
		assertThat(jsonPath.getString("filter[0].op"), equalTo("="));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		Include readInclude = objectMapper.readValue(objectMapper.writeValueAsString(include), Include.class);
		validate(readInclude);
	}

}
