/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.rest.tests.valueset;

import static com.b2international.snowowl.fhir.rest.tests.FhirRestTest.Endpoints.VALUESET;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;

import org.junit.Test;

import com.b2international.fhir.FhirCodeSystems;
import com.b2international.snowowl.fhir.rest.tests.FhirRestTest;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * FHIR /ValueSet Resource API Tests
 * 
 * @since 6.6
 */
public class FhirValueSetApiTest extends FhirRestTest {
	
	/* 
	 * Note: we do not have write access to VS resources and so result sets will always
	 * be empty in tests.
	 */
	
	@Test
	public void GET_ValueSet_Strict() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("unsupportedParam", "value")
			.header("Prefer", "handling=strict")
			.when().get(VALUESET)
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("exception"));
	}

	@Test
	public void GET_ValueSet_IdFilter_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_id", "non-existent")
			.when().get(VALUESET)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0));
	}
	
	@Test
	public void GET_ValueSet_NameFilter_NoMatch() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("name", RestExtensions.encodeQueryParameter("unknown name"))
			.when().get(VALUESET)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0));
	}
	
	@Test
	public void GET_ValueSet_Elements_Unrecognized() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("_elements", "xyz", "abcs")
			.when().get(VALUESET)
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"));
	}
	
	@Test
	public void GET_ValueSet_Url_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", "http://unknown.com")
			.when().get(VALUESET)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0))
			.body("type", equalTo("searchset"));
	}
	
	@Test
	public void GET_ValueSet_System_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", "http://unknown.com")
			.when().get(VALUESET)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0))
			.body("type", equalTo("searchset"));
	}
	
	@Test
	public void GET_ValueSet_Version_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("version", "unknown-version")
			.when().get(VALUESET)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0))
			.body("type", equalTo("searchset"));
	}
	
	@Test
	public void GET_ValueSet_Status_NoMatch() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("status", "unknown")
			.when().get(VALUESET)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("meta.tag.code", not(hasItem(FhirCodeSystems.CODING_SUBSETTED.getCode())))
			.body("total", equalTo(0))
			.body("type", equalTo("searchset"));
	}
}
