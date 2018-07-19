/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.tests.serialization.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.StringParameter;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.UriParameter;
import com.jayway.restassured.path.json.JsonPath;

/**
 * Test for checking the valueset serialization
 * @since 6.3
 */
public class ValueSetSerializationTest extends FhirTest {
	
	@Test
	public void extensionTest() throws Exception {
		
		Extension<Integer> integerExtension = new IntegerExtension("testUri", 1);
				
		printPrettyJson(integerExtension);
		
		String expectedJson =  "{\"url\":\"testUri\","
					+ "\"valueInteger\":1}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(integerExtension));
	}
	
	@Test
	public void stringParameterTest() throws Exception {
		
		StringParameter parameter = StringParameter.builder()
			.name("paramName")
			.value("paramValue")
			.build();
		
		printPrettyJson(parameter);
		
		JsonPath jsonPath = getJsonPath(parameter);
		assertThat(jsonPath.getString("name"), equalTo("paramName"));
		assertThat(jsonPath.get("valueString"), equalTo("paramValue"));
	}
	
	@Test
	public void uriParameterTest() throws Exception {
		
		UriParameter parameter = UriParameter.builder()
			.name("paramName")
			.value(new Uri("paramValue"))
			.build();
		
		printPrettyJson(parameter);
		
		JsonPath jsonPath = getJsonPath(parameter);
		assertThat(jsonPath.getString("name"), equalTo("paramName"));
		assertThat(jsonPath.get("valueUri"), equalTo("paramValue"));
	}
	
	@Test
	public void expansionTest() throws Exception {
		
		UriParameter stringParameter = UriParameter.builder()
			.name("paramName")
			.value(new Uri("paramValue"))
			.build();
		
		UriParameter uriParameter = UriParameter.builder()
				.name("uriParamName")
				.value(new Uri("uriParamValue"))
				.build();
		
		Expansion expansion = Expansion.builder()
			.identifier("identifier")
			.timestamp(TEST_DATE_STRING)
			.total(200)
			.addParameter(stringParameter)
			.addParameter(uriParameter)
			.build();
		
		printPrettyJson(expansion);
		
		JsonPath jsonPath = getJsonPath(expansion);
		assertThat(jsonPath.getString("identifier"), equalTo("identifier"));
		assertThat(jsonPath.get("parameter[0].name"), equalTo("paramName"));
	}

}
