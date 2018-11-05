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
package com.b2international.snowowl.fhir.tests.endpoints.valueset;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;

import org.hamcrest.core.StringStartsWith;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.tests.FhirTestConcepts;
import com.b2international.snowowl.fhir.tests.SnomedFhirRestTest;

/**
 * ValueSet $expand operation REST end-point test cases
 * 
 * @since 7.0
 */
public class ExpandSnomedRestTest extends SnomedFhirRestTest {
	
	//Single SNOMED CT simple type reference set
	@Test
	public void implicitRefsetTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=refset/" + simpleTypeRefSetId) 
			.when().get("/ValueSet/$expand")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", startsWith("snomedStore:MAIN/"))
			.body("id", endsWith(simpleTypeRefSetId))
			.body("language", equalTo("en-us"))
			.body("version", startsWith("FHIR_"))
			.body("status", equalTo("active"))
			.body("expansion.total", notNullValue())
			.body("expansion.timestamp", notNullValue())
			.body("expansion.contains.code", hasItem(FhirTestConcepts.MICROORGANISM))
			.body("expansion.contains.display", hasItem("Microorganism"))
			.body("expansion.parameter.name", hasItem("version"))
			.statusCode(200);
	}
	
	//all simple type reference sets
	@Test
	public void implicitRefsetsTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=refset") 
			.when().get("/ValueSet/$expand")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("language", equalTo("en-us"))
			.body("version", startsWith("FHIR_"))
			.body("status", equalTo("active"))
			.body("expansion.total", notNullValue())
			.body("expansion.timestamp", notNullValue())
			.body("expansion.contains.code", hasItem(FhirTestConcepts.MICROORGANISM))
			.statusCode(200);
	}
	
	//isA subsumption based value set
	@Test
	public void implicitIsaTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=isa/" + FhirTestConcepts.MICROORGANISM) 
			.when().get("/ValueSet/$expand")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("text.status", equalTo("generated"))
			.body("language", equalTo("en-us"))
			.body("version", startsWith("FHIR_"))
			.body("status", equalTo("active"))
			.body("expansion.total", notNullValue())
			.body("expansion.timestamp", notNullValue())
			.body("expansion.contains.code", hasItem(FhirTestConcepts.BACTERIA))
			.body("expansion.parameter.name", hasItem("version"))
			.statusCode(200);
	}
	
	//all SNOMED CT concepts
	@Test
	public void implicitSnomedCTTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs") 
			.when().get("/ValueSet/$expand")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("text.status", equalTo("generated"))
			.body("language", equalTo("en-us"))
			.body("version", startsWith("FHIR_"))
			.body("status", equalTo("active"))
			.body("expansion.total", notNullValue())
			.body("expansion.timestamp", notNullValue())
			//.body("expansion.contains.code", hasItem(FhirTestConcepts.BACTERIA)) //no guarantee as only the first 50 is returned
			.body("expansion.parameter.name", hasItem("version"))
			.statusCode(200);
	}
	
	//expand simple type reference set
	@Test
	public void simpleTypeRefsetTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/" + FHIR_SIMPLE_TYPE_REFSET_VERSION + ":" + simpleTypeRefSetId) 
			.when().get("/ValueSet/{id}/$expand")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", equalTo("snomedStore:MAIN/" + FHIR_SIMPLE_TYPE_REFSET_VERSION+ ":" + simpleTypeRefSetId))
			.body("language", equalTo("en-us"))
			.body("version", startsWith("FHIR_"))
			.body("status", equalTo("active"))
			.body("expansion.total", notNullValue())
			.body("expansion.timestamp", notNullValue())
			.body("expansion.contains.code", hasItem(FhirTestConcepts.MICROORGANISM))
			.statusCode(200);
	}
	
	//Expand Query type reference set member into a 'virtual' code system
	@Test
	public void queryTypeRefsetTest() throws Exception {
		
		String mainBranch = IBranchPath.MAIN_BRANCH;
		String refsetName = "FHIR Automated Test Query Type Refset";
		String refsetLogicalId = TestArtifactCreator.createQueryTypeReferenceSet(mainBranch, refsetName, FHIR_QUERY_TYPE_REFSET_VERSION);
		System.out.println("ExpandSnomedRestTest.queryTypeRefsetTest() " + refsetLogicalId);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/" + FHIR_QUERY_TYPE_REFSET_VERSION + ":" + refsetLogicalId) 
			.when().get("/ValueSet/{id}/$expand")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", StringStartsWith.startsWith("snomedStore:MAIN/FHIR_QUERY_TYPE_REFSET_VERSION"))
			.body("version", equalTo(FHIR_QUERY_TYPE_REFSET_VERSION))
			.body("name", equalTo("FHIR Automated Test Simple Type Refset"))
			.body("status", equalTo("active"))
			.root("expansion.contains.find { it.code =='410607006'}")
			.body("system", equalTo("http://snomed.info/sct"))
			.body("code", equalTo("410607006"))
			.body("display", equalTo("Organism"))
			.statusCode(200);
	}
	
}