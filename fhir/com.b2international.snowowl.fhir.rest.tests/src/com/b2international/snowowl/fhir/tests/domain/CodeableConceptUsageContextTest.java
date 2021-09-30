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

import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.usagecontext.CodeableConceptUsageContext;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Test for validating the {@link CodeableUsageContext} model object.
 * @since 8.0.0
 */
public class CodeableConceptUsageContextTest extends FhirTest {
	
	private CodeableConceptUsageContext usageContext;
	
	@Before
	public void setup() throws Exception {
		
		Coding coding = Coding.builder()
				.code("1234")
				.system("http://www.whocc.no/atc")
				.version("20180131")
				.build();
		
		CodeableConcept cc = CodeableConcept.builder()
				.addCoding(coding)
				.text("text")
				.build();
		
		usageContext = CodeableConceptUsageContext.builder()
				.code(Coding.builder()
						.code("coding")
						.display("codingDisplay")
						.build())
				.value(cc)
				.id("usageContextId")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		validate(usageContext);
	}
	
	private void validate(CodeableConceptUsageContext usageContext) {
		assertEquals("usageContextId", usageContext.getId());
		assertEquals("coding", usageContext.getCode().getCodeValue());
		assertEquals("codingDisplay", usageContext.getCode().getDisplay());
		assertEquals("text", usageContext.getValue().getText());
	}

	@Test
	public void serialize() throws Exception {
		
		printPrettyJson(usageContext);
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(usageContext));
		assertThat(jsonPath.getString("id"), equalTo("usageContextId"));
		assertThat(jsonPath.getString("code.code"), equalTo("coding"));
		assertThat(jsonPath.getString("code.display"), equalTo("codingDisplay"));
		assertThat(jsonPath.getString("valueCodeableConcept.text"), equalTo("text"));
		assertThat(jsonPath.getString("valueCodeableConcept.coding[0].code"), equalTo("1234"));
		assertThat(jsonPath.getString("valueCodeableConcept.coding[0].system"), equalTo("http://www.whocc.no/atc"));
		assertThat(jsonPath.getString("valueCodeableConcept.coding[0].version"), equalTo("20180131"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		CodeableConceptUsageContext readUsageContext = objectMapper.readValue(objectMapper.writeValueAsString(usageContext), CodeableConceptUsageContext.class);
		validate(readUsageContext);
	}
	
}
