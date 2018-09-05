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

import org.hamcrest.core.StringStartsWith;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
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
	public void printValueSet() throws Exception {
		
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
			.root("compose[0].include[0]")
			.body("filter[0].property", equalTo("expression"))
			.body("filter[0].value", equalTo("^" + valueSetId))
			.body("filter[0].op", equalTo("="))
			.statusCode(200);
	}

}