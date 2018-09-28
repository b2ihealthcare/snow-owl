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
package com.b2international.snowowl.fhir.tests;

import org.junit.BeforeClass;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;

/**
 * Superclass for common REST-related test functionality
 * @since 6.9
 */
public class FhirRestTest extends FhirTest {
	
	protected static final String FHIR_ROOT_CONTEXT = "/fhir"; //$NON-NLS-N$
	
	protected static final String SNOMED_VERSION = "2018-07-31";

	@BeforeClass
	public static void setupSpec() {
		RestAssuredConfig config = RestAssured.config();
		LogConfig logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.given().config(config.logConfig(logConfig));
		
		//config.httpClient(HttpClientConfig.httpClientConfig().dontReuseHttpClientInstance());
		
		//ResponseSpecBuilder builder = new ResponseSpecBuilder();
		//builder.expectStatusCode(200);
		//builder.expectBody("x.y.size()", is(2));
		//ResponseSpecification responseSpec = builder.build();
	}
	
}
