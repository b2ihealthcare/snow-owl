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
package com.b2international.snowowl.fhir.api.tests.endpoints.valueset;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import org.hamcrest.core.StringStartsWith;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;

/**
 * Generic ValueSet REST end-point test cases for Snow Owl Value sets
 * @since 6.7
 */
public class ValueSetRestTest extends FhirTest {
	
	private static final String VALUE_SET_VERSION = "VALUE_SET_VERSION"; //$NON-NLS-N$
	private static final String VALUE_SET_NAME = "FHIR Automated Value Set"; //$NON-NLS-N$
	private static String valueSetId;
	
	@BeforeClass
	public static void setupSpec() {
		
		String mainBranch = IBranchPath.MAIN_BRANCH;
		valueSetId = TestValueSetCreator.createValueSet(mainBranch, VALUE_SET_NAME, VALUE_SET_VERSION);

		RestAssuredConfig config = RestAssured.config();
		LogConfig logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.given().config(config.logConfig(logConfig));
	}
	
	@Test
	public void valueSetsTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/ValueSet")
		.then()
		.body("resourceType", equalTo("Bundle"))
		.body("type", equalTo("searchset"))
		.body("total", notNullValue())
		
		//SNOMED CT
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/valuesetStore:MAIN/VALUE_SET_VERSION:" + valueSetId + "'}")
		.body("resource.resourceType", equalTo("ValueSet"))
		.body("resource.id", equalTo("valuesetStore:MAIN/VALUE_SET_VERSION:" + valueSetId))
		.body("resource.url", equalTo("http://b2i.sg"))
		.body("resource.version", equalTo(VALUE_SET_VERSION))
		.body("resource.title", equalTo(VALUE_SET_NAME))
		.body("resource.name", equalTo(VALUE_SET_NAME))
		.body("resource.status", equalTo("active"))
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/valuesetStore:MAIN/VALUE_SET_VERSION:"
				 + valueSetId + "'}.resource.compose[0].include[0]")
		//.body("system", equalTo(SnomedUri.SNOMED_BASE_URI_STRING))
		.body("filter.size()", equalTo(1))
		.body("filter[0].property", equalTo("expression"))
		.body("filter[0].value", equalTo("^" + valueSetId))
		.body("filter[0].op", equalTo("="))
		.statusCode(200);
	}
	
	@Test
	public void getValueSetTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "valuesetStore:MAIN/" + VALUE_SET_VERSION + ":" + valueSetId) 
			.when().get("/ValueSet/{id}")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", StringStartsWith.startsWith("valuesetStore:MAIN/VALUE_SET_VERSION"))
			.body("url", equalTo("http://b2i.sg"))
			.body("version", equalTo(VALUE_SET_VERSION))
			.body("name", equalTo(VALUE_SET_NAME))
			.body("status", equalTo("active"))
			.body("publisher", equalTo("B2i"))
			.body("expansion", nullValue())
			.root("compose[0].include[0]")
			.body("filter[0].property", equalTo("expression"))
			.body("filter[0].value", equalTo("^" + valueSetId))
			.body("filter[0].op", equalTo("="))
			.statusCode(200);
	}
	
	//expand
	@Test
	public void expandValueSetTest() throws Exception {
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "valuesetStore:MAIN/" + VALUE_SET_VERSION + ":" + valueSetId) 
			.when().get("/ValueSet/{id}/$expand")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", equalTo("valuesetStore:MAIN/VALUE_SET_VERSION:" + valueSetId))
			//.body("language", equalTo("en-us"))
			.body("version", equalTo(VALUE_SET_VERSION))
			.body("status", equalTo("active"))
			.body("expansion.total", notNullValue())
			.body("expansion.timestamp", notNullValue())
			.body("expansion.contains.code", hasItem(Concepts.ROOT_CONCEPT))
			.statusCode(200);
	}

}