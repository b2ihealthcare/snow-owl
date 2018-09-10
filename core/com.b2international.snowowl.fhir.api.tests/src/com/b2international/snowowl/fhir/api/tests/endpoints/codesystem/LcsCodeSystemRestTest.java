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
package com.b2international.snowowl.fhir.api.tests.endpoints.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.api.tests.endpoints.valueset.TestArtifactCreator;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;

/**
 * CodeSystem REST end-point test cases
 * @since 6.6
 */
public class LcsCodeSystemRestTest extends FhirTest {
	
	private static final String LCS_VERSION = "FHIR_Test_Version";
	private static final String LCS_SHORT_NAME = "FHIR_LCS";
	private static final String LCS_NAME = "FHIR Local Code System";
	
	@BeforeClass
	public static void setupSpec() {
		
		TestArtifactCreator.createLocalCodeSystem(LCS_SHORT_NAME, LCS_NAME, LCS_VERSION);
		
		RestAssuredConfig config = RestAssured.config();
		LogConfig logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.given().config(config.logConfig(logConfig));
	}
	
	@Test
	public void getAllFullCodeSystemsTest() {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get("/CodeSystem")
			.then()
			.body("resourceType", equalTo("Bundle"))
			.body("total", notNullValue())
			
			//SNOMED CT
			.body("entry.resource.url", hasItem("http://b2i.sg/localcodesystems/FHIR_LCS/FHIR_Test_Version"))
			.root("entry.resource.find { it.url == 'http://b2i.sg/localcodesystems/FHIR_LCS/FHIR_Test_Version'}")
			.body("resourceType", equalTo("CodeSystem"))
			.body("id", equalTo("lcsStore:MAIN/FHIR_LCS/FHIR_Test_Version"))
			.body("language", equalTo("en"))
			.body("version", equalTo("FHIR_Test_Version"))
			.body("status", equalTo("active"))
			.body("name", equalTo("FHIR_LCS"))
			.body("title", equalTo("FHIR Local Code System"))
			.body("publisher", equalTo("http://b2i.sg"))
			.body("hierarchyMeaning", equalTo("is-a"))
			.body("content", equalTo("complete"))
			.body("count", equalTo(1))
			.body("property.size()", equalTo(5))
			.body("concept.size()", equalTo(1))
			.statusCode(200);
	}
	
	//Specific LCS Code system
	@Test
	public void getLocalCodeSystemTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "lcsStore:MAIN/FHIR_LCS/FHIR_Test_Version") 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("content", equalTo("complete"))
			.body("url", equalTo("http://b2i.sg/localcodesystems/FHIR_LCS/FHIR_Test_Version"))
			.body("status", equalTo("active"))
			.body("name", equalTo("FHIR_LCS"))
			.body("title", equalTo("FHIR Local Code System"))
			.body("publisher", equalTo("http://b2i.sg"))
			.body("hierarchyMeaning", equalTo("is-a"))
			.body("content", equalTo("complete"))
			.body("count", equalTo(1))
			.body("property.size()", equalTo(5))
			.body("concept.size()", equalTo(1))
			.statusCode(200);
	}
	
	@Test
	public void lookupLCSCodeTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://b2i.sg/localcodesystems/FHIR_LCS")
			.param("version", "FHIR_Test_Version")
			.param("code", "123")
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("FHIR_LCS"))
			.body("parameter[1].name", equalTo("version"))
			.body("parameter[1].valueString", equalTo("FHIR_Test_Version"))
			.body("parameter[2].name", equalTo("display"))
			.body("parameter[2].valueString", equalTo("Test concept"))
			.body("parameter.name", not(hasItem("property")))
			.statusCode(200);
	}
	
	//GET LCS with parameters and the alternative terms property
	@Test
	public void lookupFhirCodeSystemCodeWithPropertyTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://b2i.sg/localcodesystems/FHIR_LCS")
			.param("version", "FHIR_Test_Version")
			.param("code", "123")
			.param("property", "synonym")
			.when().get("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("FHIR_LCS"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("Test concept"))
			.body("parameter.name", hasItem("property"))
			.statusCode(200);
	}

}