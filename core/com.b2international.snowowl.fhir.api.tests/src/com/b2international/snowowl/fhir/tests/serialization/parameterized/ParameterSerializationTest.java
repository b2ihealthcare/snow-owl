/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.serialization.parameterized;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Basic FHIR parameter serialization tests.
 * Example: String parameterName = "test" -> {"name":"parameterName", "valueString":"test"}
 * @since 6.4
 */
public class ParameterSerializationTest extends FhirTest {
	
	@Test
	public void stringParameterTest() throws Exception {
		
		@SuppressWarnings("unused")
		@JsonPropertyOrder({"parameterName"})
		class StringTestParameterObject {
			
			private String parameterName = "test";

			public String getParameterName() {
				return parameterName;
			}

			public void setParameterName(String parameterName) {
				this.parameterName = parameterName;
			}
		}
		
		String expected = buildExpectedJson("{\"name\":\"parameterName\",\"valueString\":\"test\"}");
		Fhir fhirParameters = new Parameters.Fhir(new StringTestParameterObject());
		
		printPrettyJson(fhirParameters);
		Assert.assertEquals(expected, objectMapper.writeValueAsString(fhirParameters));
	}
	
	@Test
	public void integerParameterTest() throws Exception {
		
		@SuppressWarnings("unused")
		@JsonPropertyOrder({"parameterName"})
		class Test {
			
			private Integer parameterName = 1;

			public Integer getParameterName() {
				return parameterName;
			}

			public void setParameterName(Integer parameterName) {
				this.parameterName = parameterName;
			}
		}
		
		String expected = buildExpectedJson("{\"name\":\"parameterName\",\"valueInteger\":1}");
		
		Fhir fhirParameters = new Parameters.Fhir(new Test());
		printPrettyJson(fhirParameters);
		Assert.assertEquals(expected, objectMapper.writeValueAsString(fhirParameters));
	}
	
	@Test
	public void codeParameterTest() throws Exception {
		
		@SuppressWarnings("unused")
		@JsonPropertyOrder({"parameterName"})
		class Test {
			
			private Code parameterName = new Code("test");

			public Code getParameterName() {
				return parameterName;
			}

			public void setParameterName(Code parameterName) {
				this.parameterName = parameterName;
			}
		}
		
		String expected = buildExpectedJson("{\"name\":\"parameterName\",\"valueCode\":\"test\"}");
		
		Fhir fhirParameters = new Parameters.Fhir(new Test());
		printPrettyJson(fhirParameters);
		Assert.assertEquals(expected, objectMapper.writeValueAsString(fhirParameters));
	}
	
	private String buildExpectedJson(String parameterString) {
		return "{\"resourceType\":\"Parameters\","
					+ "\"parameter\":["
						+ parameterString
					+ "]"
				+ "}";
	}
}
