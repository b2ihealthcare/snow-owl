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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
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
	}
	
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
	}
}
