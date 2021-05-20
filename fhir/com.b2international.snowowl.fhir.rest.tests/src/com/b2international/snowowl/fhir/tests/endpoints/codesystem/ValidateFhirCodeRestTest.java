/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.endpoints.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.codesystem.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.tests.FhirRestTest;

/**
 * CodeSystem $validate-code operation for FHIR code systems REST end-point test cases
 * 
 * @since 7.17.0
 */
public class ValidateFhirCodeRestTest extends FhirRestTest {
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_URI = "http://hl7.org/fhir/issue-type";
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_ID = "fhir/issue-type";
	
	//instance tests (with ID in the path)
	@Test
	public void invalidCodeGetTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.param("code", "unknownCode")
			.param("_format", "json")
			.when().get("/CodeSystem/{id}/$validate-code")
			.then().assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
			
			ValidateCodeResult result = convertToValidateCodeResult(responseString);
			
			assertEquals(false, result.getResult());
			assertEquals("Could not find code(s) '[unknownCode]'", result.getMessage());
			assertNull(result.getDisplay());
	}
	
	@Test
	public void validCodeGetTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.param("code", "login")
			.param("_format", "json")
			.when().get("/CodeSystem/{id}/$validate-code")
			.then().assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
			
			ValidateCodeResult result = convertToValidateCodeResult(responseString);
			
			assertEquals(true, result.getResult());
	}
	
	@Test
	public void validCodeWithInvalidDisplayGetTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.param("code", "login")
			.param("display", "invalid")
			.param("_format", "json")
			.when().get("/CodeSystem/{id}/$validate-code")
			.then().assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
			
			ValidateCodeResult result = convertToValidateCodeResult(responseString);
			
			assertEquals(false, result.getResult());
			assertEquals("Incorrect display 'invalid' for code 'login'", result.getMessage());
			assertEquals("Login Required", result.getDisplay());
	}
	
	@Test
	public void validCodePostTest() throws Exception {
		
		ValidateCodeRequest request = ValidateCodeRequest.builder()
			.code("login")
			.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		String jsonBody = objectMapper.writeValueAsString(fhirParameters);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.body(jsonBody)
			.when().post("/CodeSystem/{id}/$validate-code")
			.then()
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.statusCode(200);
	}
	
	@Test
	public void invalidParametersOnInstancePostTest() throws Exception {
		
		//URL
		ValidateCodeRequest request = ValidateCodeRequest.builder()
			.url("fhir/issue-severity") //should not be specified on the instance level
			.code("fatal")
			.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		
		String jsonBody = objectMapper.writeValueAsString(fhirParameters);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.body(jsonBody)
			.when().post("/CodeSystem/{id}/$validate-code")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Parameter 'url' cannot be specified when the code system ID is set."))
			.body("issue.details.text", hasItem("Parameter 'ValidateCodeRequest.url' content is invalid"))
			.statusCode(400);
		
		//Coding
		request = ValidateCodeRequest.builder()
				.coding(Coding.builder() //should not be specified on the instance level
						.system("http://hl7.org/fhir/issue-severity")
						.code("fatal")
						.build())
				.build();
			
		fhirParameters = new Parameters.Fhir(request);
			
		jsonBody = objectMapper.writeValueAsString(fhirParameters);
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.body(jsonBody)
			.when().post("/CodeSystem/{id}/$validate-code")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Parameter 'coding' cannot be specified when the code system ID is set."))
			.body("issue.details.text", hasItem("Parameter 'ValidateCodeRequest.coding' content is invalid"))
			.statusCode(400);
		
		//CodeableConcept
		request = ValidateCodeRequest.builder()
				.codeableConcept(CodeableConcept.builder().addCoding(//should not be specified on the instance level
						Coding.builder() 
							.system("http://hl7.org/fhir/issue-severity")
							.code("fatal")
							.build())
						.build())
				.build();
			
		fhirParameters = new Parameters.Fhir(request);
			
		jsonBody = objectMapper.writeValueAsString(fhirParameters);
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.body(jsonBody)
			.when().post("/CodeSystem/{id}/$validate-code")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Parameter 'codeableConcept' cannot be specified when the code system ID is set."))
			.body("issue.details.text", hasItem("Parameter 'ValidateCodeRequest.codeableConcept' content is invalid"))
			.statusCode(400);
	}
	
	/*
	 * Non-instance calls (no ID in the path)
	 */
	
	
	@Test
	public void invalidCodeWithUrlGetTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("code", "unknownCode")
			.param("url", FHIR_ISSUE_TYPE_CODESYSTEM_URI)
			.param("_format", "json")
			.when().get("/CodeSystem/$validate-code")
			.then().assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
			
			ValidateCodeResult result = convertToValidateCodeResult(responseString);
			
			assertEquals(false, result.getResult());
			assertEquals("Could not find code(s) '[unknownCode]'", result.getMessage());
			assertNull(result.getDisplay());
	}
	
	@Test
	public void validCodeGetWithUrl() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("code", "login")
			.param("url", FHIR_ISSUE_TYPE_CODESYSTEM_URI)
			.param("_format", "json")
			.when().get("/CodeSystem/$validate-code")
			.then()
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.assertThat()
			.statusCode(200);
	}
	
	@Test
	public void validCodingTestWithPostTest() throws Exception {
		
		String system = "http://hl7.org/fhir/issue-severity";
		Coding coding = Coding.builder()
				.system(system)
				.code("fatal")
				.build();

		ValidateCodeRequest request = ValidateCodeRequest.builder()
			.url(system)
			.coding(coding)
			.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		String jsonBody = objectMapper.writeValueAsString(fhirParameters);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(jsonBody)
			.when().post("/CodeSystem/$validate-code")
			.then()
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.statusCode(200);
	}
	
	@Test
	public void validCodeableConceptTestWithPostTest() throws Exception {
		
		String system = "http://hl7.org/fhir/issue-severity";
		Coding coding = Coding.builder()
				.system(system)
				.code("fatal")
				.build();
		
		CodeableConcept codeableConcept = CodeableConcept.builder().addCoding(coding).build();

		ValidateCodeRequest request = ValidateCodeRequest.builder()
			.url(system)
			.codeableConcept(codeableConcept)
			.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		String jsonBody = objectMapper.writeValueAsString(fhirParameters);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(jsonBody)
			.when().post("/CodeSystem/$validate-code")
			.then()
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.statusCode(200);
	}
	
	
	private ValidateCodeResult convertToValidateCodeResult(String responseString) throws Exception {
		Fhir parameters = objectMapper.readValue(responseString, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		return objectMapper.convertValue(json, ValidateCodeResult.class);
	}
	

}
