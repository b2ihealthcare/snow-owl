/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.rest.tests.codesystem;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hl7.fhir.r5.formats.JsonParser;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Parameters;
import org.junit.Test;

import com.b2international.fhir.r5.operations.CodeSystemValidateCodeParameters;
import com.b2international.fhir.r5.operations.CodeSystemValidateCodeResultParameters;
import com.b2international.snowowl.fhir.rest.tests.FhirRestTest;

/**
 * CodeSystem $validate-code operation for FHIR code systems REST end-point test cases
 * 
 * TODO move this to the ext repository as the necessary services are there
 * 
 * @since 7.17.0
 */
public class FhirCodeSystemValidateCodeTest extends FhirRestTest {
	
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
			
		CodeSystemValidateCodeResultParameters result = convertToValidateCodeResult(responseString);
		
		assertEquals(false, result.getResult().getValue());
		assertEquals("Could not find code(s) '[unknownCode]'", result.getMessage().getValue());
		assertNull(result.getDisplay().getValue());
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
			
		CodeSystemValidateCodeResultParameters result = convertToValidateCodeResult(responseString);
		
		assertEquals(true, result.getResult().getValue());
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
			
		CodeSystemValidateCodeResultParameters result = convertToValidateCodeResult(responseString);
		
		assertEquals(false, result.getResult().getValue());
		assertEquals("Incorrect display 'invalid' for code 'login'", result.getMessage().getValue());
		assertEquals("Login Required", result.getDisplay().getValue());
	}
	
	@Test
	public void validCodePostTest() throws Exception {
		
		CodeSystemValidateCodeParameters parameters = new CodeSystemValidateCodeParameters()
			.setCode("login");
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.body(toJson(parameters.getParameters()))
			.when().post("/CodeSystem/{id}/$validate-code")
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true));
	}
	
	@Test
	public void invalidParametersOnInstancePostTest() throws Exception {
		
		//URL
		CodeSystemValidateCodeParameters parameters = new CodeSystemValidateCodeParameters()
			.setUrl("fhir/issue-severity") //should not be specified on the instance level
			.setCode("fatal");
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.body(toJson(parameters.getParameters()))
			.when().post("/CodeSystem/{id}/$validate-code")
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Parameter 'url' cannot be specified when the code system ID is set."))
			.body("issue.details.text", hasItem("Parameter 'url' content is invalid"));
		
		//Coding
		parameters = new CodeSystemValidateCodeParameters()
				//should not be specified on the instance level
				.setCoding(new Coding() 
						.setSystem("http://hl7.org/fhir/issue-severity")
						.setCode("fatal"));
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.body(toJson(parameters.getParameters()))
			.when().post("/CodeSystem/{id}/$validate-code")
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Parameter 'coding' cannot be specified when the code system ID is set."))
			.body("issue.details.text", hasItem("Parameter 'coding' content is invalid"));
		
		//CodeableConcept
		parameters = new CodeSystemValidateCodeParameters()
				.setCodeableConcept(new CodeableConcept().addCoding(//should not be specified on the instance level
						new Coding() 
							.setSystem("http://hl7.org/fhir/issue-severity")
							.setCode("fatal")));
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.pathParam("id", FHIR_ISSUE_TYPE_CODESYSTEM_ID)
			.body(toJson(parameters.getParameters()))
			.when().post("/CodeSystem/{id}/$validate-code")
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Parameter 'codeableConcept' cannot be specified when the code system ID is set."))
			.body("issue.details.text", hasItem("Parameter 'codeableConcept' content is invalid"));
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
		
		CodeSystemValidateCodeResultParameters result = convertToValidateCodeResult(responseString);
		
		assertEquals(false, result.getResult().getValue());
		assertEquals("Could not find code(s) '[unknownCode]'", result.getMessage().getValue());
		assertNull(result.getDisplay().getValue());
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
		Coding coding = new Coding()
				.setSystem(system)
				.setCode("fatal");

		var parameters = new CodeSystemValidateCodeParameters()
				.setUrl(system)
				.setCoding(coding);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toJson(parameters.getParameters()))
			.when().post("/CodeSystem/$validate-code")
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true));
	}
	
	@Test
	public void validCodeableConceptTestWithPostTest() throws Exception {
		
		final String system = "http://hl7.org/fhir/issue-severity";
		final CodeableConcept codeableConcept = new CodeableConcept()
				.addCoding(new Coding()
						.setSystem(system)
						.setCode("fatal"));

		var parameters = new CodeSystemValidateCodeParameters()
			.setUrl(system)
			.setCodeableConcept(codeableConcept);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toJson(parameters.getParameters()))
			.when().post("/CodeSystem/$validate-code")
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true));
	}
	
	private CodeSystemValidateCodeResultParameters convertToValidateCodeResult(String responseString) throws Exception {
		return new CodeSystemValidateCodeResultParameters((Parameters) new JsonParser().parse(responseString));
	}

}
