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
package com.b2international.snowowl.fhir.tests.domain.valueset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.FhirDates;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.model.ContactDetail;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Quantity;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.usagecontext.QuantityUsageContext;
import com.b2international.snowowl.fhir.core.model.usagecontext.UsageContext;
import com.b2international.snowowl.fhir.core.model.valueset.Compose;
import com.b2international.snowowl.fhir.core.model.valueset.Include;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Contains;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.DateTimeParameter;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.StringParameter;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.UriParameter;
import com.b2international.snowowl.fhir.tests.FhirTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.restassured.path.json.JsonPath;

/**
 * Test for checking the valueset serialization
 * @since 6.3
 */
public class ValueSetSerializationTest extends FhirTest {
	
	private ValueSet valueSet;

	@Before
	public void setup() throws Exception {
		
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
		
		Coding coding = Coding.builder()
				.code("codingCode")
				.display("codingDisplay")
				.build();
			
		CodeableConcept jurisdiction = CodeableConcept.builder()
				.addCoding(coding)
				.text("codingText")
				.build();
		
		valueSet = ValueSet.builder("-1")
			.url("http://who.org")
			.addIdentifier(Identifier.builder()
					.system("system")
					.use(IdentifierUse.OFFICIAL)
					.type(CodeableConcept.builder()
								.addCoding(coding)
								.text("codingText")
								.build())
					.build())
			.version("20130131")
			.name("refsetName")
			.title("refsetTitle")
			.copyright("copyright")
			.status(PublicationStatus.ACTIVE)
			.date(TEST_DATE_STRING)
			.publisher("b2i")
			.addContact(ContactDetail.builder()
					.addTelecom(ContactPoint.builder()
						.id("contactPointId")
						.build())
					.build())
			.description("descriptionString")
			.addJurisdiction(jurisdiction)
			.addUseContext(QuantityUsageContext.builder()
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
					.build())
			.expansion(expansion)
			.compose(Compose.builder()
					.addInclude(Include.builder()
							.system("uriValue")
							.build())
					.build())
			.build();
		
		applyFilter(valueSet);
	}
	
	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = getJsonPath(valueSet);
		assertThat(jsonPath.getString("url"), equalTo("http://who.org"));
		assertThat(jsonPath.getString("version"), equalTo("20130131"));
		assertThat(jsonPath.getString("name"), equalTo("refsetName"));
		assertThat(jsonPath.getString("description"), equalTo("descriptionString"));
		assertThat(jsonPath.getString("title"), equalTo("refsetTitle"));
		assertThat(jsonPath.get("expansion.parameter.name"), hasItem("paramName"));
		assertThat(jsonPath.get("expansion.contains.system"), hasItem("systemUri"));
	}
	
	@Test
	public void deserialize() throws Exception, JsonProcessingException {
		ValueSet readValueSet = objectMapper.readValue(objectMapper.writeValueAsString(valueSet), ValueSet.class);
	}

	
	@Test
	public void stringParameterTest() throws Exception {
		
		StringParameter parameter = StringParameter.builder()
			.name("paramName")
			.value("paramValue")
			.build();
		
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
		
		JsonPath jsonPath = getJsonPath(parameter);
		assertThat(jsonPath.getString("name"), equalTo("paramName"));
		assertThat(jsonPath.get("valueUri"), equalTo("paramValue"));
	}
	
	@Test
	public void dateTimeParameterTest() throws Exception {
		
		Date date = new SimpleDateFormat(FhirDates.DATE_TIME_FORMAT).parse(TEST_DATE_STRING);
		
		DateTimeParameter parameter = DateTimeParameter.builder()
			.name("paramName")
			.value(date)
			.build();
		
		JsonPath jsonPath = getJsonPath(parameter);
		assertThat(jsonPath.getString("name"), equalTo("paramName"));
		assertThat(jsonPath.get("valueDateTime"), equalTo(TEST_DATE_STRING));
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
	
}
