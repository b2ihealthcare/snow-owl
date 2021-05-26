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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.codesystem.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

/**
 * CodeSystem $validate-code operation for SNOMED CT REST end-point test cases
 * 
 * @since 7.17.0
 */
public class ValidateSnomedCodeRestTest extends FhirRestTest {
	
	private final static String SYSTEM = "http://snomed.info/sct";
	
	@Test
	public void invalidCodeGetTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "SNOMEDCT/2020-02-04")
			.param("code", "1111")
			.param("_format", "json")
			.when().get("/CodeSystem/{id}/$validate-code")
			.prettyPeek()
			.then().assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
			
			ValidateCodeResult result = convertToValidateCodeResult(responseString);
			
			assertEquals(false, result.getResult());
			assertEquals("Could not find code(s) '[1111]'", result.getMessage());
			assertNull(result.getDisplay());
	}
	
	@Test
	public void validCodeGetTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "SNOMEDCT/2020-02-04")
			.param("code", Concepts.IS_A)
			.param("_format", "json")
			.when().get("/CodeSystem/{id}/$validate-code")
			.prettyPeek()
			.then().assertThat()
			.statusCode(200)
			.extract()
			.body()
			.asString();
			
			ValidateCodeResult result = convertToValidateCodeResult(responseString);
			
			assertEquals(true, result.getResult());
	}
	
	@Test
	public void validCodingTestWithPostTest() throws Exception {
		
		String system = SYSTEM;
		Coding coding = Coding.builder()
				.system(system)
				.code(Concepts.IS_A)
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
	
	private ValidateCodeResult convertToValidateCodeResult(String responseString) throws Exception {
		Fhir parameters = objectMapper.readValue(responseString, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		return objectMapper.convertValue(json, ValidateCodeResult.class);
	}

}
