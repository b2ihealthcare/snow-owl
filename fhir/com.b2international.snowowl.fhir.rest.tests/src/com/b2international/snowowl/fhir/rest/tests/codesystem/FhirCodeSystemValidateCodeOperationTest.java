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

import static com.b2international.snowowl.fhir.rest.tests.FhirRestTest.Endpoints.CODESYSTEM_VALIDATE_CODE;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

import org.hl7.fhir.r5.model.Coding;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.operations.CodeSystemValidateCodeParameters;
import com.b2international.snowowl.fhir.rest.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

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
			.queryParam("display", RestExtensions.encodeQueryParameter("Unknown display"))
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
		var parameters = new CodeSystemValidateCodeParameters()
				.setUrl(SNOMEDCT_URL)
				.setCoding(new Coding().setSystem(SNOMEDCT_URL).setCode(Concepts.ROOT_CONCEPT));

		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toJson(parameters.getParameters()))
			.when().post(CODESYSTEM_VALIDATE_CODE)
			.then().assertThat()
			.statusCode(200)
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.body("parameter[1]", nullValue());
	}
	
}
