/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.path.json.JsonPath;

/**
 * Test for validating the {@link CodeableConcept} data type.
 * @since 8.0.0
 */
public class CodeableConcepTest extends FhirTest {
	
	@Test
	public void build() {
		
		Coding coding = Coding.builder()
				.code("codingCode")
				.display("codingDisplay")
				.build();
			
		CodeableConcept codeableConcept = CodeableConcept.builder()
				.addCoding(coding)
				.text("codingText")
				.build();
		
		assertEquals("codingText", codeableConcept.getText());
		Coding builtCoding = codeableConcept.getCodings().iterator().next();
		assertEquals("codingCode", builtCoding.getCode().getCodeValue());
		assertEquals("codingDisplay", builtCoding.getDisplay());
	}
	
	@Test
	public void serialize() throws Exception {
		
		Coding coding = Coding.builder()
				.code("1234")
				.system("http://www.whocc.no/atc")
				.version("20180131")
				.build();
		
		CodeableConcept cc = CodeableConcept.builder()
				.addCoding(coding)
				.text("text")
				.build();
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(cc));
		assertThat(jsonPath.getString("text"), equalTo("text"));
		assertThat(jsonPath.getString("coding[0].code"), equalTo("1234"));
		assertThat(jsonPath.getString("coding[0].system"), equalTo("http://www.whocc.no/atc"));
		assertThat(jsonPath.getString("coding[0].version"), equalTo("20180131"));
		
	}
	
	@Test
	public void deserialize() throws JsonProcessingException {
		
		Coding coding = Coding.builder()
				.code("codingCode")
				.display("codingDisplay")
				.system("http://www.whocc.no/atc")
				.version("20180131")
				.build();
		
		CodeableConcept cc = CodeableConcept.builder()
				.addCoding(coding)
				.text("text")
				.build();
		
		String serializedString = objectMapper.writeValueAsString(cc);
		CodeableConcept codeableConcept = objectMapper.readValue(serializedString, CodeableConcept.class);
		assertEquals("text", codeableConcept.getText());
		Coding builtCoding = codeableConcept.getCodings().iterator().next();
		assertEquals("codingCode", builtCoding.getCode().getCodeValue());
		assertEquals("codingDisplay", builtCoding.getDisplay());
		assertEquals("http://www.whocc.no/atc", builtCoding.getSystem().getUriValue());
		assertEquals("20180131", builtCoding.getVersion());
	}
}
