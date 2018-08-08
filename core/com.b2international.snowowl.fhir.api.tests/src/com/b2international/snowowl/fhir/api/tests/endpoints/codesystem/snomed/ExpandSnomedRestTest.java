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
package com.b2international.snowowl.fhir.api.tests.endpoints.codesystem.snomed;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.hamcrest.core.StringStartsWith;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.api.tests.endpoints.codesystem.TestValueSetCreator;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;

/**
 * ValueSet $expand operation REST end-point test cases
 * 
 * @since 7.0
 */
public class ExpandSnomedRestTest extends FhirTest {
	
	private static final String FHIR_QUERY_TYPE_REFSET_VERSION = "FHIR_QUERY_TYPE_REFSET_VERSION";
	
	@BeforeClass
	public static void setupSpec() {
		
		RestAssuredConfig config = RestAssured.config();
		LogConfig logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.given().config(config.logConfig(logConfig));
	}
	
	@Test
	public void implicitSnomedTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.param("url", "http://snomed.info/sct?fhir_vs=isa/50697003") 
		.when().get("/ValueSet/$expand")
		.then()
		.body("resourceType", equalTo("ValueSet"))
		.body("id", equalTo("snomedStore:MAIN/2018-01-31:723264001"))
		.body("language", equalTo("en-us"))
		.body("version", equalTo("2018-01-31"))
		.body("status", equalTo("active"))
		.body("expansion.total", notNullValue())
		.body("expansion.timestamp", notNullValue())
		.body("expansion.contains.code", hasItem("362460007"))
		.statusCode(200);
	}
	
	//expand simple type reference set
	//@Test
	public void simpleTypeRefsetTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/2018-01-31:723264001") 
			.when().get("/ValueSet/{id}/$expand")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", equalTo("snomedStore:MAIN/2018-01-31:723264001"))
			.body("language", equalTo("en-us"))
			.body("version", equalTo("2018-01-31"))
			.body("status", equalTo("active"))
			.body("expansion.total", notNullValue())
			.body("expansion.timestamp", notNullValue())
			.body("expansion.contains.code", hasItem("362460007"))
			.statusCode(200);
	}
	
	//Expand Query type reference set member into a 'virtual' code system
	//@Test
	public void queryTypeRefsetTest() throws Exception {
		
		String mainBranch = IBranchPath.MAIN_BRANCH;
		String refsetName = "FHIR Automated Test Query Type Refset";
		String refsetLogicalId = TestValueSetCreator.create(mainBranch, refsetName, FHIR_QUERY_TYPE_REFSET_VERSION);
		System.out.println("ExpandSnomedRestTest.queryTypeRefsetTest() " + refsetLogicalId);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/" + FHIR_QUERY_TYPE_REFSET_VERSION + ":" + refsetLogicalId) 
			.when().get("/ValueSet/{id}/$expand")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", StringStartsWith.startsWith("snomedStore:MAIN/FHIR_QUERY_TYPE_REFSET_VERSION"))
			.body("version", equalTo("FHIR_QUERY_TYPE_REFSET_VERSION"))
			.body("name", equalTo("FHIR Automated Test Simple Type Refset"))
			.body("status", equalTo("active"))
			.root("expansion.contains.find { it.code =='49111001'}")
			.body("system", equalTo("http://snomed.info/sct"))
			.body("code", equalTo("49111001"))
			.body("display", equalTo("Adrenal hemorrhage"))
			.statusCode(200);
	}
	
}