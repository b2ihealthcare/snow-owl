/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;

import org.hamcrest.core.StringStartsWith;
import org.junit.Test;

import com.b2international.snowowl.fhir.tests.SnomedFhirRestTest;
import com.b2international.snowowl.snomed.fhir.SnomedUri;

/**
 * Generic ValueSet REST end-point test cases for SNOMED 'valuesets'
 * @since 6.7
 */
public class SnomedValueSetRestTest extends SnomedFhirRestTest {
	
	//@Test
	public void printValueSets() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/ValueSet")
		.prettyPrint();
	}
	
	@Test
	public void valueSetsTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/ValueSet")
		.then()
		.body("resourceType", equalTo("Bundle"))
		.body("type", equalTo("searchset"))
		.body("total", notNullValue())
		
		//SNOMED CT
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/snomedStore:MAIN/FHIR_SIMPLE_TYPE_REFSET_VERSION:" + simpleTypeRefSetId + "'}")
		.body("resource.resourceType", equalTo("ValueSet"))
		.body("resource.id", equalTo("snomedStore:MAIN/FHIR_SIMPLE_TYPE_REFSET_VERSION:" + simpleTypeRefSetId))
		.body("resource.url", startsWith("http://snomed.info/sct/version"))
		.body("resource.version", equalTo(FHIR_SIMPLE_TYPE_REFSET_VERSION))
		.body("resource.title", equalTo(SIMPLE_TYPE_REFSET_NAME))
		.body("resource.name", equalTo(SIMPLE_TYPE_REFSET_NAME))
		.body("resource.status", equalTo("active"))
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/snomedStore:MAIN/FHIR_SIMPLE_TYPE_REFSET_VERSION:" + simpleTypeRefSetId+ "'}.resource.compose[0].include[0]")
		.body("system", equalTo(SnomedUri.SNOMED_BASE_URI_STRING))
		.body("filter.size()", equalTo(1))
		.body("filter[0].property", equalTo("expression"))
		.body("filter[0].value", equalTo("^" + simpleTypeRefSetId))
		.body("filter[0].op", equalTo("="))
		.statusCode(200);
	}
	
	@Test
	public void valueSetsSummaryTest() throws Exception {
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.param("_summary", true)
		.when().get("/ValueSet")
		.then()
		.body("resourceType", equalTo("Bundle"))
		.body("type", equalTo("searchset"))
		.body("total", notNullValue())
		
		//SNOMED CT
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/snomedStore:MAIN/FHIR_SIMPLE_TYPE_REFSET_VERSION:" + simpleTypeRefSetId + "'}")
		.body("resource.resourceType", equalTo("ValueSet"))
		.body("resource.id", equalTo("snomedStore:MAIN/FHIR_SIMPLE_TYPE_REFSET_VERSION:" + simpleTypeRefSetId))
		.body("resource.url", startsWith("http://snomed.info/sct/version"))
		.body("resource.version", equalTo(FHIR_SIMPLE_TYPE_REFSET_VERSION))
		.body("resource.title", equalTo(SIMPLE_TYPE_REFSET_NAME))
		.body("resource.name", equalTo(SIMPLE_TYPE_REFSET_NAME))
		.body("resource.status", equalTo("active"))
		
		//subsetted
		.body("resource.meta.tag[0].code", equalTo("SUBSETTED"))
		
		.statusCode(200);
	}
	
	//'Virtual' value set
	@Test
	public void getSingleQueryTypeValueSetTest() {
		
		System.out.println("Refset concept ID: " + queryTypeRefsetLogicalId);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/" + FHIR_QUERY_TYPE_REFSET_VERSION + ":" + queryTypeRefsetLogicalId) 
			.when().get("/ValueSet/{id}")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", StringStartsWith.startsWith("snomedStore:MAIN/FHIR_QUERY_TYPE_REFSET_VERSION"))
			.body("version", equalTo("FHIR_QUERY_TYPE_REFSET_VERSION"))
			.body("name", equalTo("FHIR Automated Test Simple Type Refset"))
			.body("status", equalTo("active"))
			.root("compose[0].include[0]")
			.body("system", equalTo("http://snomed.info/sct"))
			.body("filter[0].property", equalTo("expression"))
			.body("filter[0].value", equalTo("<<410607006"))
			.body("filter[0].op", equalTo("="))
			.statusCode(200);
	}
	
	
	@Test
	public void getSingleSnomedValueSetTest() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/" + FHIR_SIMPLE_TYPE_REFSET_VERSION + ":" + simpleTypeRefSetId) 
			.when().get("/ValueSet/{id}")
			.then()
			.body("resourceType", equalTo("ValueSet"))
			.body("id", equalTo("snomedStore:MAIN/FHIR_SIMPLE_TYPE_REFSET_VERSION" + ":" + simpleTypeRefSetId))
			.body("language", equalTo("en-us"))
			.body("url", startsWith("http://snomed.info/sct/version"))
			.body("identifier.use", equalTo("official"))
			.body("identifier.system", startsWith("http://snomed.info/sct/version"))
			.body("identifier.value", equalTo("11000154102"))
			
			.body("version", equalTo("FHIR_SIMPLE_TYPE_REFSET_VERSION"))
			.body("name", equalTo("FHIR Automated Test Simple Type Reference Set"))
			.body("title", equalTo("FHIR Automated Test Simple Type Reference Set"))
			.body("status", equalTo("active"))
			.root("compose[0].include[0]")
			.body("system", equalTo("http://snomed.info/sct"))
			.body("filter[0].property", equalTo("expression"))
			.body("filter[0].value", equalTo("^" + simpleTypeRefSetId))
			.body("filter[0].op", equalTo("="))
			.statusCode(200);
	}

}