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
package com.b2international.snowowl.fhir.rest.tests.codesystem;

import static com.b2international.snowowl.fhir.tests.FhirRestTest.Endpoints.CODESYSTEM_VALIDATE_CODE;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.codesystem.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

/**
 * CodeSystem $validate-code operation for SNOMED CT REST end-point test cases
 * 
 * @since 7.17.0
 */
public class FhirCodeSystemValidateCodeOperationTest extends FhirRestTest {
	
	@Test
	public void GET_CodeSystem_$validate_code_NonExisting() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SNOMEDCT_URL)
			.queryParam("code", "12345")
			.when().get(CODESYSTEM_VALIDATE_CODE)
			.then().assertThat()
			.statusCode(200)
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", equalTo("Could not find code '[12345]'."))
			.body("parameter[2]", nullValue());
	}
	
	@Test
	public void GET_CodeSystem_$validate_code_InvalidDisplay() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SNOMEDCT_URL)
			.queryParam("code", Concepts.ROOT_CONCEPT)
			.queryParam("display", "Unknown display")
			.when().get(CODESYSTEM_VALIDATE_CODE)
			.then().assertThat()
			.statusCode(200)
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", equalTo("Incorrect display 'Unknown display' for code '138875005'."))
			.body("parameter[2].name", equalTo("display"))
			.body("parameter[2].valueString", equalTo("SNOMED CT Concept"));
	}
	
	@Test
	public void GET_CodeSystem_$validate_code_Existing() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SNOMEDCT_URL)
			.queryParam("code", Concepts.ROOT_CONCEPT)
			.when().get(CODESYSTEM_VALIDATE_CODE)
			.then().assertThat()
			.statusCode(200)
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.body("parameter[1]", nullValue());
	}
	
	@Test
	public void POST_CodeSystem_$validate_code_Existing() throws Exception {
		ValidateCodeRequest request = ValidateCodeRequest.builder()
				.url(SNOMEDCT_URL)
				.coding(Coding.of(SNOMEDCT_URL, Concepts.ROOT_CONCEPT))
				.build();

		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toFhirParameters(request))
			.when().post(CODESYSTEM_VALIDATE_CODE)
			.then().assertThat()
			.statusCode(200)
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.body("parameter[1]", nullValue());
	}
	
}
