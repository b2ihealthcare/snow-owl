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
package com.b2international.snowowl.fhir.api.tests.endpoints;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.test.commons.BundleStartRule;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.HttpClientConfig;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;

/**
 * CodeSystem REST end-point test cases
 * @since 6.6
 */
public class CodeSystemRestTest extends FhirTest {
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_ID = "issue-type";
	
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
	
	//@AfterClass
	public static void foo() throws InterruptedException {
		Thread.sleep(2000);
	}
	
	//@Test
	public void pingTest() {
		givenAuthenticatedRequest("/fhir")
			.when().get("/CodeSystem/ping")
			.then().assertThat().statusCode(200);
	}
	
	//@Test
	public void printAllCodesystems() {
		givenAuthenticatedRequest("/fhir")
		.when().get("/CodeSystem").prettyPrint();
	}
	
	//All code systems fully detailed
	@Test
	public void getFullCodeSystemsTest() {
		
		givenAuthenticatedRequest("/fhir")
			.when().get("/CodeSystem")
			.then()
			.body("resourceType", equalTo("Bundle"))
			.body("total", notNullValue())
			
			//SNOMED CT
			.body("entry.resource.url", hasItem("http://snomed.info/sct"))
			.root("entry.resource.find { it.url == 'http://snomed.info/sct'}")
			.body("property.size()", equalTo(115))
			
			//FHIR issue type code system has children
			.root("entry.resource.find { it.url == 'http://hl7.org/fhir/issue-type'}")
			.body("concept.size()", equalTo(29))
			.statusCode(200);
	}
	
	@Test
	public void getCodeSystemsSummaryTest() {
		
		givenAuthenticatedRequest("/fhir")
			.param("_summary", true)
			.when().get("/CodeSystem").then()
			.body("resourceType", equalTo("Bundle"))
			.body("total", notNullValue())
			.body("type", equalTo("searchset"))
			
			//no concept definitions are part of the summary
			.body("entry.resource", not(hasItem("concept"))) 
			.statusCode(200);
	}
	
	//Fully detailed SNOMED CT code system
	@Test
	public void getSnomedCodeSystemTest() {
		givenAuthenticatedRequest("/fhir")
		 	.pathParam("id", "snomedStore/SNOMEDCT") 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.statusCode(200);
	}
	
	//Full FHIR code system
	@Test
	public void getFhirCodeSystemTest() {
		givenAuthenticatedRequest("/fhir")
			.param("_summary", false)
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("status", equalTo("active")) //mandatory
			.body("name", equalTo("issue-type")) //summary
			.body("concept", notNullValue()) //optional
			.body("copyright", containsString("2011+ HL7")) //optional
			.statusCode(200);
	}
	
	//Summary-only FHIR code system
	@Test
	public void getFhirCodeSystemSummaryTest() {
		givenAuthenticatedRequest("/fhir")
			.param("_summary", true)
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("meta.tag.code", hasItem("SUBSETTED"))
			.body("status", equalTo("active"))
			//NOT part of the summary
			.body("name", nullValue())
			.body("concept", nullValue()) 
			.body("copyright", nullValue()) 
			.statusCode(200);
	}
	
	//Summary-count should not be allowed for non-search type operations
	@Test
	public void getFhirCodeSystemCountTest() {
		givenAuthenticatedRequest("/fhir")
			.param("_summary", "count")
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.statusCode(500);
	}
	
	//Summary-data FHIR code system (remove text element)
	@Test
	public void getFhirCodeSystemDataTest() {
		givenAuthenticatedRequest("/fhir")
			.param("_summary", "data")
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("meta.tag.code", hasItem("SUBSETTED"))
			//only text, id, meta and mandatory
			.body("text", nullValue())
			.body("status", equalTo("active"))
			.body("id", notNullValue())
			.body("count", notNullValue())
			.body("name", notNullValue())
			.body("concept", notNullValue()) 
			.body("copyright", notNullValue()) 
			.body("url", notNullValue()) 
			.statusCode(200);
	}
	
	//Summary-text FHIR code system (text, id, meta, mandatory)
	@Test
	public void getFhirCodeSystemTextTest() {
		givenAuthenticatedRequest("/fhir")
			.param("_summary", "text")
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("meta.tag.code", hasItem("SUBSETTED"))
			//only text, id, meta and mandatory
			.body("text.div", equalTo("<div>A code that describes the type of issue.</div>"))
			.body("status", equalTo("active"))
			.body("id", notNullValue())
			.body("count", nullValue())
			.body("name", nullValue())
			.body("concept", nullValue()) 
			.body("copyright", nullValue()) 
			.body("url", nullValue()) 
			.statusCode(200);
	}
	
	/*
	 * ?elements=name, url means
	 */
	//@Test
	public void getFhirCodeSystemElementsTest() {
		givenAuthenticatedRequest("/fhir")
			.param("_elements", "name", "url")
		 	.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID) 
			.when().get("/CodeSystem/{id}")
			.then()
			.body("resourceType", equalTo("CodeSystem"))
			.body("meta.tag.code", equalTo("SUBSETTED"))
			.body("status", equalTo("active"))
			.body("text", notNullValue())
			.body("id", notNullValue())
			.body("count", nullValue())
			.body("name", nullValue())
			.body("concept", nullValue()) 
			.body("copyright", nullValue()) 
			.body("url", nullValue()) 
			.statusCode(200);
	}
	
}