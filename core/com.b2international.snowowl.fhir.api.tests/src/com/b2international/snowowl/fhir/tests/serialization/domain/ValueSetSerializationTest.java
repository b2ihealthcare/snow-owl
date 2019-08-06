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
package com.b2international.snowowl.fhir.tests.serialization.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.IntegerExtension;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.StringParameter;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.UriParameter;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

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
		assertThat(jsonPath.get("parameter.name"), hasItem("uriParamName"));
		assertThat(jsonPath.get("parameter.name"), hasItem("paramName"));
	}
	
	@Test
	public void containsTest() throws Exception {
		
		Contains contains = Contains.builder()
			.system("systemUri")
			.isAbstract(true)
			.inactive(false)
			.version("20140131")
			.code("Code")
			.display("displayValue")
			.addDesignation(Designation.builder()
				.language("en-us")
				.value("pt")
				.build())
			.addContains(Contains.builder().build())
			.build();
		
		printPrettyJson(contains);
		
		JsonPath jsonPath = getJsonPath(contains);
		assertThat(jsonPath.getString("system"), equalTo("systemUri"));
		assertThat(jsonPath.getBoolean("abstract"), equalTo(true));
		assertThat(jsonPath.getBoolean("inactive"), equalTo(false));
		assertThat(jsonPath.getString("version"), equalTo("20140131"));
		assertThat(jsonPath.getString("code"), equalTo("Code"));
		assertThat(jsonPath.getString("display"), equalTo("displayValue"));
		assertThat(jsonPath.getString("designation"), notNullValue());
		assertThat(jsonPath.getString("contains"), notNullValue());
	}
	
	@Test
	public void valueSetTest() throws Exception {
		
		UriParameter stringParameter = UriParameter.builder()
			.name("paramName")
			.value(new Uri("paramValue"))
			.build();
			
		UriParameter uriParameter = UriParameter.builder()
			.name("uriParamName")
			.value(new Uri("uriParamValue"))
			.build();
	
		Contains contains = Contains.builder()
			.system("systemUri")
			.isAbstract(true)
			.inactive(false)
			.version("20140131")
			.code("Code")
			.display("displayValue")
			.addDesignation(Designation.builder()
					.language("en-us")
					.value("pt")
					.build())
			.addContains(Contains.builder().build())
			.build();

		Expansion expansion = Expansion.builder()
			.identifier("identifier")
			.timestamp(TEST_DATE_STRING)
			.total(200)
			.addParameter(stringParameter)
			.addParameter(uriParameter)
			.addContains(contains)
			.build();
		
		
		ValueSet valueSet = ValueSet.builder("-1")
			.url("http://who.org")
			.identifier(Identifier.builder()
					.build())
			.version("20130131")
			.name("refsetName")
			.title("refsetTitle")
			.status(PublicationStatus.ACTIVE)
			.date(TEST_DATE_STRING)
			.publisher("b2i")
			.addContact(ContactDetail.builder()
					.addContactPoint(ContactPoint.builder()
						.id("contactPointId")
						.build())
					.build())
			.description("descriptionString")
			//.addUseContext(usageContext)
			//.jurisdiction(jurisdiction)
			.expansion(expansion)
			
			.build();
		
		applyFilter(valueSet);
		printPrettyJson(valueSet);
		
		JsonPath jsonPath = getJsonPath(valueSet);
		assertThat(jsonPath.getString("url"), equalTo("http://who.org"));
		assertThat(jsonPath.getString("version"), equalTo("20130131"));
		assertThat(jsonPath.getString("name"), equalTo("refsetName"));
		assertThat(jsonPath.getString("description"), equalTo("descriptionString"));
		assertThat(jsonPath.getString("title"), equalTo("refsetTitle"));
		assertThat(jsonPath.get("expansion.parameter.name"), hasItem("paramName"));
		assertThat(jsonPath.get("expansion.contains.system"), hasItem("systemUri"));
		
	}

}
