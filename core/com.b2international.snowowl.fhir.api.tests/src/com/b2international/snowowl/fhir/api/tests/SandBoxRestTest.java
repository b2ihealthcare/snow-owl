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
package com.b2international.snowowl.fhir.api.tests;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;

/**
 * CodeSystem REST end-point test cases
 * @since 6.6
 */
public class SandBoxRestTest extends FhirTest {
	
	@BeforeClass
	public static void setupSpec() {
		
		RestAssuredConfig config = RestAssured.config();
		LogConfig logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.given().config(config.logConfig(logConfig));
		//config.httpClient(HttpClientConfig.httpClientConfig().reuseHttpClientInstance());
		
		//ResponseSpecBuilder builder = new ResponseSpecBuilder();
		//builder.expectStatusCode(200);
		//builder.expectBody("x.y.size()", is(2));
		//ResponseSpecification responseSpec = builder.build();
	}
	
	//Fully detailed SNOMED CT code system
	//@Test
	public void getSnomedCodeSystemTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore/SNOMEDCT") 
			.when().get("/CodeSystem/{id}").prettyPrint();
			/*
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("content", equalTo("not-present"))
			.body("status", equalTo("active"))
			.statusCode(200);
			*/
	}
	
	//All code systems fully detailed
		@Test
		public void getFullCodeSystemsTest() {
			
			givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
				.when().get("/CodeSystem")
				.then()
				.body("resourceType", equalTo("Bundle"))
				.body("total", notNullValue())
				
				//SNOMED CT
				.body("entry.resource.url", hasItem("http://hl7.org/fhir/operation-outcome"))
				.root("entry.resource.find { it.url == 'http://snomed.info/sct/version/20170731'}")
				.body("property.size()", equalTo(116))
				
				//FHIR issue type code system has children
				.root("entry.resource.find { it.url == 'http://hl7.org/fhir/issue-type'}")
				.body("concept.size()", equalTo(29))
				.statusCode(200);
		}

}
