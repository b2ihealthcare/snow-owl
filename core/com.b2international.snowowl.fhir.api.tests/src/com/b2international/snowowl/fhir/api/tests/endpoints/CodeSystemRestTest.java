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
import com.jayway.restassured.config.LogConfig;

/**
 * CodeSystem REST end-point test cases
 * @since 6.3
 */
public class CodeSystemRestTest extends FhirTest {
	
	@ClassRule
	public static final RuleChain appRule = RuleChain
			.outerRule(SnowOwlAppRule.snowOwl().clearResources(false).config(PlatformUtil.toAbsolutePath(CodeSystemRestTest.class, "fhir-configuration.yml")))
			.around(new BundleStartRule("org.eclipse.jetty.osgi.boot"))
			.around(new BundleStartRule("com.b2international.snowowl.fhir.api"));
	
	@BeforeClass
	public static void setupSpec() {
		
		LogConfig logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.given().config(RestAssured.config().logConfig(logConfig));
		
		//ResponseSpecBuilder builder = new ResponseSpecBuilder();
		//builder.expectStatusCode(200);
		//builder.expectBody("x.y.size()", is(2));
		//ResponseSpecification responseSpec = builder.build();
	}
	
	//@Test
	public void pingTest() {
		givenAuthenticatedRequest("/fhir")
			.when().get("/CodeSystem/ping")
			.then().assertThat().statusCode(200);
	}
	
	//@Test
	public void printResponse() {
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
			.body("entry.resource.url", hasItem("http://snomed.info/sct"))
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
			.body("entry.resource", not(hasItem("concept"))) //no concept definitions are part of the summary
			.statusCode(200);
	}
	
	@Test
	public void getSnomedCodeSystemTest() {
		givenAuthenticatedRequest("/fhir")
		 	.pathParam("id", "snomedStore/SNOMEDCT") 
			.when().get("/CodeSystem/{id}")
			.then()
			.statusCode(200);
	}
	
}