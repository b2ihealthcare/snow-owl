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
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
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
	
	private static final String FHIR_ISSUE_TYPE_CODESYSTEM_ID = "fhir/issue-type";
	
	@Test
	public void validateFhirCodeSystemCodeTest() throws Exception {
		
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
	public void validateFhirCodeSystemCodeWithDisplayTest() throws Exception {
		
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
	
	private ValidateCodeResult convertToValidateCodeResult(String responseString) throws Exception {
		Fhir parameters = objectMapper.readValue(responseString, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		return objectMapper.convertValue(json, ValidateCodeResult.class);
	}
	

}
