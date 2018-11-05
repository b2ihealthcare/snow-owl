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
package com.b2international.snowowl.fhir.tests.endpoints.valueset.conceptmap;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.fhir.SnomedUri;

/**
 * Generic ValueSet REST end-point test cases for SNOMED 'valuesets'
 * @since 6.7
 */
public class SnomedConceptMapRestTest extends FhirRestTest {
	
	private static final String MAP_TYPE_REFSET_NAME = "FHIR Automated Test Map Type Reference Sets";
	private static final String COMPLEX_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Complex Map Type Reference Sets";
	private static final String EXTENDED_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Extended Map Type Reference Sets";
	private static final String FHIR_MAP_TYPE_REFSET_VERSION = "FHIR_SIMPLE_MAP_TYPE_REFSET_VERSION";
	
	protected static List<String> mapTypeRefSetIds;


	@BeforeClass
	public static void setupValueSets() {
		String mainBranch = IBranchPath.MAIN_BRANCH;
		mapTypeRefSetIds = TestMapTypeReferenceSetCreator.createSimpleMapTypeReferenceSets(mainBranch, 
				MAP_TYPE_REFSET_NAME, 
				COMPLEX_MAP_TYPE_REFSET_NAME,
				EXTENDED_MAP_TYPE_REFSET_NAME,
				FHIR_MAP_TYPE_REFSET_VERSION);
	}
	
	//@Test
	public void printConceptMap() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/ConceptMap")
		.prettyPrint();
	}
	
	//@Test
	public void conceptMapsTest() throws Exception {
		
		String simpleMapTypeRefsetId = mapTypeRefSetIds.get(0);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/ConceptMap")
		.then()
		.body("resourceType", equalTo("Bundle"))
		.body("type", equalTo("searchset"))
		.body("total", notNullValue())
		
		//SNOMED CT
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + simpleMapTypeRefsetId + "'}")
		.body("resource.resourceType", equalTo("ValueSet"))
		.body("resource.id", equalTo("snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + simpleMapTypeRefsetId))
		.body("resource.url", startsWith("http://snomed.info/sct/version"))
		.body("resource.version", equalTo(FHIR_MAP_TYPE_REFSET_VERSION))
		.body("resource.title", equalTo(MAP_TYPE_REFSET_NAME))
		.body("resource.name", equalTo(MAP_TYPE_REFSET_NAME))
		.body("resource.status", equalTo("active"))
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + simpleMapTypeRefsetId + "'}.resource.compose[0].include[0]")
		.body("system", equalTo(SnomedUri.SNOMED_BASE_URI_STRING))
		.body("filter.size()", equalTo(1))
		.body("filter[0].property", equalTo("expression"))
		.body("filter[0].value", equalTo("^" + simpleMapTypeRefsetId))
		.body("filter[0].op", equalTo("="))
		.statusCode(200);
	}
	
	//@Test
	public void conceptMapsSummaryTest() throws Exception {
		
		String simpleMapTypeRefsetId = mapTypeRefSetIds.get(0);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.param("_summary", true)
		.when().get("/ConceptMap")
		.then()
		.body("resourceType", equalTo("Bundle"))
		.body("type", equalTo("searchset"))
		.body("total", notNullValue())
		
		//SNOMED CT
		.root("entry.find { it.fullUrl == 'http://localhost:8080/snowowl/fhir/ValueSet/snomedStore:MAIN/FHIR_SIMPLE_TYPE_REFSET_VERSION:" + simpleMapTypeRefsetId + "'}")
		.body("resource.resourceType", equalTo("ValueSet"))
		.body("resource.id", equalTo("snomedStore:MAIN/FHIR_SIMPLE_TYPE_REFSET_VERSION:" + simpleMapTypeRefsetId))
		.body("resource.url", startsWith("http://snomed.info/sct/version"))
//		.body("resource.version", equalTo(FHIR_SIMPLE_TYPE_REFSET_VERSION))
//		.body("resource.title", equalTo(SIMPLE_TYPE_REFSET_NAME))
//		.body("resource.name", equalTo(SIMPLE_TYPE_REFSET_NAME))
		.body("resource.status", equalTo("active"))
		
		//subsetted
		.body("resource.meta.tag[0].code", equalTo("SUBSETTED"))
		
		.statusCode(200);
	}
	
	@Test
	public void getSingleSnomedConceptMapTest() {
		
		String simpleMapTypeRefsetId = mapTypeRefSetIds.get(0);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + simpleMapTypeRefsetId)
		 	.param("_summary", true)
			.when().get("/ConceptMap/{id}")
			.then()
			.body("resourceType", equalTo("ConceptMap"))
			.body("id", equalTo("snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + simpleMapTypeRefsetId))
			.body("language", equalTo("en-us"))
			.body("url", startsWith("http://snomed.info/sct/version"))
			.body("identifier.use", equalTo("official"))
			.body("identifier.system", startsWith("http://snomed.info/sct/version"))
			.body("identifier.value", equalTo(simpleMapTypeRefsetId))
			
			.body("version", equalTo("FHIR_SIMPLE_TYPE_REFSET_VERSION"))
			.body("name", equalTo(MAP_TYPE_REFSET_NAME))
			.body("title", equalTo(MAP_TYPE_REFSET_NAME))
			.body("status", equalTo("active"))
			.root("compose[0].include[0]")
			.body("system", equalTo("http://snomed.info/sct"))
			.body("filter[0].property", equalTo("expression"))
			.body("filter[0].value", equalTo("^" + simpleMapTypeRefsetId))
			.body("filter[0].op", equalTo("="))
			.statusCode(200);
	}
	
}