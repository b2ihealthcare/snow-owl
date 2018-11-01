/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.b2international.snowowl.fhir.tests.FhirTestConcepts.*;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult.SubsumptionType;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.tests.FhirRestTest;

/**
 * CodeSystem $subsumes operation REST end-point test cases
 * 
 * @since 6.7
 */
public class SubsumesSnomedRestTest extends FhirRestTest {
	
	@Test
	public void subsumedByWithVersionTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("codeA", BACTERIA) //Bacteria
			.param("codeB", MICROORGANISM) //Microorganism (parent)
			.param("system", "http://snomed.info/sct/900000000000207008/version/20180131")
			.when().get("/CodeSystem/$subsumes")
			.asString();
		
		SubsumptionResult result = convertToSubsumptionResult(responseString);
		Assert.assertEquals(SubsumptionType.SUBSUMED_BY, result.getOutcome());
	}
	
	//invalid
	@Test
	public void twoVersionsTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("codeA", BACTERIA) //Bacteria
			.param("codeB", MICROORGANISM) //Microorganism (parent)
			.param("system", "http://snomed.info/sct/900000000000207008/version/20170131")
			.param("version", "2018-01-31")
			.when().get("/CodeSystem/$subsumes")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Version specified in the URI [http://snomed.info/sct/900000000000207008/version/20170131] "
					+ "does not match the version set in the request [2018-01-31]"))
			.statusCode(400);
	}
	
	@Test
	public void subsumedByTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("codeA", BACTERIA) //Bacteria
			.param("codeB", MICROORGANISM) //Microorganism (parent)
			.param("system", "http://snomed.info/sct")
			.when().get("/CodeSystem/$subsumes")
			.asString();
		
		SubsumptionResult result = convertToSubsumptionResult(responseString);
		Assert.assertEquals(SubsumptionType.SUBSUMED_BY, result.getOutcome());
	}
	
	@Test
	public void subsumesTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("codeA", MICROORGANISM) //Microorganism (parent)
			.param("codeB", BACTERIA) //Bacteria
			.param("system", "http://snomed.info/sct")
			.when().get("/CodeSystem/$subsumes")
			.asString();
		
		SubsumptionResult result = convertToSubsumptionResult(responseString);
		Assert.assertEquals(SubsumptionType.SUBSUMES, result.getOutcome());
	}
	
	@Test
	public void notSubsumesTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("codeA", "71388002") //procedure
			.param("codeB", BACTERIA) //bacteria
			.param("system", "http://snomed.info/sct")
			.when().get("/CodeSystem/$subsumes")
			.asString();
		
		SubsumptionResult result = convertToSubsumptionResult(responseString);
		Assert.assertEquals(SubsumptionType.NOT_SUBSUMED, result.getOutcome());
	}
	
	@Test
	public void equivalentTest() throws Exception {
		
		String responseString = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("codeA", BACTERIA)
			.param("codeB", BACTERIA)
			.param("system", "http://snomed.info/sct")
			.when().get("/CodeSystem/$subsumes")
			.asString();
		
		SubsumptionResult result = convertToSubsumptionResult(responseString);
		Assert.assertEquals(SubsumptionType.EQUIVALENT, result.getOutcome());
	}

	/**
	 * Converts the parameter-formatted response string to a {@link SubsumptionResult} object
	 * @param responseString
	 * @return
	 * @throws Exception
	 */
	protected SubsumptionResult convertToSubsumptionResult(String responseString) throws Exception {
		Fhir parameters = objectMapper.readValue(responseString, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		return objectMapper.convertValue(json, SubsumptionResult.class);
	}
	
}