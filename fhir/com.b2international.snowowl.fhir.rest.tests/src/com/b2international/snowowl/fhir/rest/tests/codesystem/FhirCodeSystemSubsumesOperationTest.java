/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.fhir.tests.FhirRestTest.Endpoints.CODESYSTEM_SUBSUMES;
import static com.b2international.snowowl.fhir.tests.FhirTestConcepts.BACTERIA;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult.SubsumptionType;
import com.b2international.snowowl.fhir.tests.FhirRestTest;

/**
 * CodeSystem $subsumes operation REST end-point test cases
 * 
 * @since 6.7
 */
public class FhirCodeSystemSubsumesOperationTest extends FhirRestTest {
	
	private static final String PROCEDURE = "71388002";
	private static final String ORGANISM_TOP_LEVEL = "410607006";

	@Test
	public void GET_CodeSystem_$subsumes_Subsumes() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("codeA", ORGANISM_TOP_LEVEL)
			.queryParam("codeB", BACTERIA)
			.queryParam("system", SNOMEDCT_URL)
			.when().get(CODESYSTEM_SUBSUMES)
			.then().assertThat()
			.statusCode(200)
			.body("parameter[0].name", equalTo("outcome"))
			.body("parameter[0].valueCode", equalTo(SubsumptionType.SUBSUMES.name()));
	}
	
	@Test
	public void GET_CodeSystem_$subsumes_SubsumedBy() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("codeA", BACTERIA)
			.queryParam("codeB", ORGANISM_TOP_LEVEL)
			.queryParam("system", SNOMEDCT_URL)
			.when().get(CODESYSTEM_SUBSUMES)
			.then().assertThat()
			.statusCode(200)
			.body("parameter[0].name", equalTo("outcome"))
			.body("parameter[0].valueCode", equalTo(SubsumptionType.SUBSUMED_BY.name()));
	}
	
	@Test
	public void GET_CodeSystem_$subsumes_NotSubsumed() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("codeA", BACTERIA)
			.queryParam("codeB", PROCEDURE)
			.queryParam("system", SNOMEDCT_URL)
			.when().get(CODESYSTEM_SUBSUMES)
			.then().assertThat()
			.statusCode(200)
			.body("parameter[0].name", equalTo("outcome"))
			.body("parameter[0].valueCode", equalTo(SubsumptionType.NOT_SUBSUMED.name()));
	}
	
	@Test
	public void GET_CodeSystem_$subsumes_Equivalent() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("codeA", BACTERIA)
			.queryParam("codeB", BACTERIA)
			.queryParam("system", SNOMEDCT_URL)
			.when().get(CODESYSTEM_SUBSUMES)
			.then().assertThat()
			.statusCode(200)
			.body("parameter[0].name", equalTo("outcome"))
			.body("parameter[0].valueCode", equalTo(SubsumptionType.EQUIVALENT.name()));
	}
	
//	@Test
//	public void subsumedByWithVersionTest() throws Exception {
//		
//		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
//			.queryParam("codeA", BACTERIA) //Bacteria
//			.queryParam("codeB", MICROORGANISM) //Microorganism (parent)
//			.queryParam("system", "http://snomed.info/sct/900000000000207008/version/20180131")
//			.when().get(CODESYSTEM_SUBSUMES)
//			.then().assertThat()
//			.statusCode(200)
//			.extract().asString();
//		
//		SubsumptionResult result = convertToSubsumptionResult(responseString);
//		Assert.assertEquals(SubsumptionType.SUBSUMED_BY, result.getOutcome());
//	}
//	
//	//invalid
//	@Test
//	public void twoVersionsTest() throws Exception {
//		
//		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
//			.queryParam("codeA", BACTERIA) //Bacteria
//			.queryParam("codeB", MICROORGANISM) //Microorganism (parent)
//			.queryParam("system", "http://snomed.info/sct/900000000000207008/version/20170131")
//			.queryParam("version", "2018-01-31")
//			.when().get(CODESYSTEM_SUBSUMES)
//			.then()
//			.body("resourceType", equalTo("OperationOutcome"))
//			.body("issue.severity", hasItem("error"))
//			.body("issue.code", hasItem("invalid"))
//			.body("issue.diagnostics", hasItem("Version specified in the URI [http://snomed.info/sct/900000000000207008/version/20170131] "
//					+ "does not match the version set in the request [2018-01-31]"))
//			.statusCode(400);
//	}
	
}