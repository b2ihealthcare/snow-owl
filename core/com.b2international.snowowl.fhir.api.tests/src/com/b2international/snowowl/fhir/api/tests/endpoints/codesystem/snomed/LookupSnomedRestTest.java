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
package com.b2international.snowowl.fhir.api.tests.endpoints.codesystem.snomed;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;

/**
 * CodeSystem $lookup operation REST end-point test cases
 * 
 * @since 6.6
 */
public class LookupSnomedRestTest extends FhirTest {
	
	@BeforeClass
	public static void setupSpec() {
		
		RestAssuredConfig config = RestAssured.config();
		LogConfig logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.given().config(config.logConfig(logConfig));
	}
	
	//GET SNOMED CT with parameters, default properties
	//@Test
	public void lookupDefaultPropertiesTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct")
			.param("code", "263495000")
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.asString();
		
		System.out.println("Response string: " + responseString);
		LookupResult result = convertToResult(responseString);
		
		assertEquals("SNOMED CT", result.getName());
		assertEquals("Gender", result.getDisplay());
		
		//Designations
		Collection<Designation> designations = result.getDesignation();
		
		Designation ptDesignation = designations.stream()
			.filter(d -> d.getValue().equals("Gender"))
			.findFirst()
			.get();
		
		assertThat("900000000000013009", equalTo(ptDesignation.getUse().getCodeValue()));
		assertThat(ptDesignation.getUse().getDisplay(), equalTo("Synonym"));
		
		Designation fsnDesignation = designations.stream()
				.filter(d -> d.getValue().equals("Gender (observable entity)"))
				.findFirst()
				.get();
		
		assertThat(fsnDesignation.getUse().getCodeValue(), equalTo("900000000000003001"));
		assertThat(fsnDesignation.getUse().getDisplay(), equalTo("Fully specified name"));
		
		//Properties
		Collection<Property> properties = result.getProperty();
		
		properties.forEach(System.out::println);
		
		Property definitionProperty = getProperty(properties, "sufficientlyDefined");
		assertThat(definitionProperty.getValue(), equalTo(false));
		
		Property statusProperty = getProperty(properties, "inactive");
		assertThat(statusProperty.getValue(), equalTo(false));
		
		Property effectiveTimeProperty = getProperty(properties, "effectiveTime");
		assertThat(effectiveTimeProperty.getValue(), equalTo("20020131"));

		Set<String> codeValues = properties.stream().map(p -> p.getCode()).collect(Collectors.toSet());
		assertThat(codeValues, not(hasItem("parent")));
		
	}
	
	//GET SNOMED CT with properties
	@Test
	public void lookupSnomedCodeSystemCodeTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct")
			.param("code", "312984006")
			.param("property", "inactive")
			.param("property", "http://snomed.info/id/116676008") //associated morphology
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.asString();
		
		System.out.println(responseString);
		LookupResult result = convertToResult(responseString);
		
		//Mandatory parameters
		assertEquals("SNOMED CT", result.getName());
		assertEquals("Abnormal uterine bleeding unrelated to menstrual cycle", result.getDisplay());
		
		assertNull(result.getVersion());
		
		//Designations
		Collection<Designation> designations = result.getDesignation();
		assertTrue(designations.isEmpty());
		
		//Properties
		Collection<Property> properties = result.getProperty();
		assertEquals(2, properties.size());
		
		Property inactiveProperty = getProperty(properties, "inactive");
		assertThat(inactiveProperty.getValue(), equalTo(false));
		
		Property associatedMProperty = getProperty(properties, "116676008"); //associated morphology
		assertThat(associatedMProperty.getValue(), equalTo("50960005")); //associated morphology = Hemorrhage
	}
	
	//GET SNOMED CT with properties
	//@Test
	public void lookupSnomedCodeSystemCodeInvalidProperyTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct")
			.param("code", "263495000")
			.param("property", "INACTIVE")
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.details.text", hasItem("Bad Syntax in LookupRequest.property"))
			.statusCode(400);
	}

	private Property getProperty(Collection<Property> properties, String codeValue) {
		Optional<Property> propertyOptional = properties.stream()
			.filter(p -> p.getCode().equals(codeValue))
			.findFirst();
		return propertyOptional.orElseThrow(() -> new RuntimeException("Could not find property " + codeValue + "."));
	}
	
}