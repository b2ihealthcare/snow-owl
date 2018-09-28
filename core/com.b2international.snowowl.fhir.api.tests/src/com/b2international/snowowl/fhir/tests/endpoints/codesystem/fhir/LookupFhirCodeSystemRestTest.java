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
package com.b2international.snowowl.fhir.tests.endpoints.codesystem.fhir;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import org.junit.Test;

import com.b2international.snowowl.fhir.api.service.BaseFhirResourceRestService;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.lookup.LookupRequest;
import com.b2international.snowowl.fhir.tests.FhirRestTest;

/**
 * CodeSystem $lookup operation for FHIR code systems REST end-point test cases
 * 
 * @since 6.6
 */
public class LookupFhirCodeSystemRestTest extends FhirRestTest {
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_URI = "http://hl7.org/fhir/issue-type";
	
	//GET FHIR with parameters
	@Test
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
	
	//GET FHIR with parameters and a property
	@Test
	public void lookupFhirCodeSystemCodeWithPropertyTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", FHIR_ISSUE_TYPE_CODESYSTEM_URI)
			.param("code", "login")
			.param("property", "name")
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
	
	//GET FHIR with parameters and an invalid property
	@Test
	public void lookupFhirCodeSystemCodeWithInvalidPropertyTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("system", FHIR_ISSUE_TYPE_CODESYSTEM_URI)
			.param("code", "login")
			.param("property", "name")
			.param("property", "http://snomed.info/id/116676008") //associated morphology
			.param("_format", "json")
			.when().get("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Unrecognized properties [name, http://snomed.info/id/116676008]. Supported properties are: [parent, system, display, name, designation, version, child]."))
			.statusCode(400);
	}
	
	//POST with request body
	@Test
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
			.contentType(BaseFhirResourceRestService.APPLICATION_FHIR_JSON)
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
	
	//POST with request body with property
	@Test
	public void lookupFhirCodeSystemPropertiesCodingTest() throws Exception {
		
		Coding coding = Coding.builder()
				.system("http://hl7.org/fhir/issue-severity")
				.code("fatal")
				.build();

		LookupRequest request = LookupRequest.builder()
				.coding(coding)
				.addProperty("name")
				.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		
		String jsonBody = objectMapper.writeValueAsString(fhirParameters);
		printPrettyJson(fhirParameters);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(BaseFhirResourceRestService.APPLICATION_FHIR_JSON)
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
	
	
	//POST invalid request body
	@Test
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
			.contentType(BaseFhirResourceRestService.APPLICATION_FHIR_JSON)
			.body(jsonBody)
			.when().post("/CodeSystem/$lookup")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Parameter 'system' is not specified while code is present in the request."))
			.statusCode(400);
	}
	
}