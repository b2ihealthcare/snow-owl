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

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSetConcept;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link ValueSetConcept}
 * @since 8.0.0
 */
public class ValueSetConceptTest extends FhirTest {
	
	private ValueSetConcept concept;
	
	@Before
	public void setup() throws Exception {
		
		concept = ValueSetConcept.builder()
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
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(concept);
	}
	
	private void validate(ValueSetConcept concept) {
	
		assertEquals(new Code("code"), concept.getCode());
		assertEquals("display", concept.getDisplay());
		Designation designation = concept.getDesignations().iterator().next();
		assertEquals("gb_en", designation.getLanguage());
		assertEquals("designationValue", designation.getValue());
		assertEquals(new Code("internal"), designation.getUse().getCode());
		assertEquals(new Uri("http://b2i.sg/test"), designation.getUse().getSystem());
		
	}

	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(concept));
		assertThat(jsonPath.getString("code"), equalTo("code"));
		assertThat(jsonPath.getString("display"), equalTo("display"));
		assertThat(jsonPath.getString("designation[0].language"), equalTo("gb_en"));
		assertThat(jsonPath.getString("designation[0].use.code"), equalTo("internal"));
		assertThat(jsonPath.getString("designation[0].use.system"), equalTo("http://b2i.sg/test"));
		assertThat(jsonPath.getString("designation[0].value"), equalTo("designationValue"));
		assertThat(jsonPath.getString("designation[0].languageCode"), equalTo("gb_en"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		ValueSetConcept readConcept = objectMapper.readValue(objectMapper.writeValueAsString(concept), ValueSetConcept.class);
		validate(readConcept);
	}

}
