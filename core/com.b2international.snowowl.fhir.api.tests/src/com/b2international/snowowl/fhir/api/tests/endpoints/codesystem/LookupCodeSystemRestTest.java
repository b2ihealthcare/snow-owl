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
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.fhir.api.service.BaseFhirRestService;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.LogConfig;
import com.jayway.restassured.config.RestAssuredConfig;

/**
 * CodeSystem $lookup operation REST end-point test cases
 * 
 * @since 6.6
 */
public class LookupCodeSystemRestTest extends FhirTest {
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_URI = "http://hl7.org/fhir/issue-type";
	
	@BeforeClass
	public static void setupSpec() {
		
		RestAssuredConfig config = RestAssured.config();
		LogConfig logConfig = LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.given().config(config.logConfig(logConfig));
	}
	
	//GET FHIR with parameters
	//@Test
	public void lookupFhirCodeSystemCodeTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", FHIR_ISSUE_TYPE_CODESYSTEM_URI)
			.param("code", "login")
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("IssueType"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("Login Required"))
			.statusCode(200);
	}
	
	//POST with request body
	//@Test
	public void lookupFhirCodeSystemCodingTest() throws Exception {
		
		Coding coding = Coding.builder()
				.system("http://hl7.org/fhir/issue-severity")
				.code("fatal")
				.build();

		LookupRequest request = LookupRequest.builder()
				.coding(coding)
				.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		
		String jsonBody = objectMapper.writeValueAsString(fhirParameters);
		printPrettyJson(fhirParameters);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(BaseFhirRestService.APPLICATION_FHIR_JSON)
			.body(jsonBody)
			.when().post("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("IssueSeverity"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("Fatal"))
			.statusCode(200);
	}
	
	//POST with request body
	//@Test
	public void lookupFhirCodeSystemInvalidCodingTest() throws Exception {
		
		Coding coding = Coding.builder()
				//.system("http://hl7.org/fhir/issue-severity")
				.code("fatal")
				.build();

		LookupRequest request = LookupRequest.builder()
				.coding(coding)
				.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		
		String jsonBody = objectMapper.writeValueAsString(fhirParameters);
		printPrettyJson(fhirParameters);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(BaseFhirRestService.APPLICATION_FHIR_JSON)
			.body(jsonBody)
			.when().post("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Parameter 'system' is not specified while code is present in the request."))
			.statusCode(400);
	}
	
	//GET SNOMED CT with parameters
	@Test
	public void lookupSnomedCodeSystemCodeTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", "http://snomed.info/sct")
			.param("code", "263495000")
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.asString();
		
		System.out.println(responseString);
		
		Fhir parameters = objectMapper.readValue(responseString, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		LookupResult result = objectMapper.convertValue(json, LookupResult.class);
		
		assertEquals("SNOMED CT", result.getName());
		assertEquals("Gender", result.getDisplay());
		
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
		
	}
	
}