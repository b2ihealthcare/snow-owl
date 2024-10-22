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
package com.b2international.snowowl.fhir.rest.tests.conceptmap;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.rest.tests.FhirRestTest;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * Generic Concept Map REST end-point test cases for SNOMED Map Type reference sets
 * 
 * @since 6.7
 */
public class FhirConceptMapApiTest extends FhirRestTest {
	
	private static final String SIMPLE_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Map Type Reference Sets";
	private static final String COMPLEX_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Complex Map Type Reference Sets";
	private static final String COMPLEX_BLOCK_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Complex Map With Map Block Type Reference Sets";
	private static final String EXTENDED_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Extended Map Type Reference Sets";
	private static final String FHIR_MAP_TYPE_REFSET_VERSION = "FHIR_MAP_TYPE_REFSET_VERSION";
	
	protected static List<String> mapTypeRefSetIds;

	@BeforeClass
	public static void setupMaps() {
		String mainBranch = IBranchPath.MAIN_BRANCH;
		mapTypeRefSetIds = FhirSnomedConceptMapGenerator.createSimpleMapTypeReferenceSets(mainBranch, 
				SIMPLE_MAP_TYPE_REFSET_NAME, 
				COMPLEX_MAP_TYPE_REFSET_NAME,
				COMPLEX_BLOCK_MAP_TYPE_REFSET_NAME,
				EXTENDED_MAP_TYPE_REFSET_NAME,
				FHIR_MAP_TYPE_REFSET_VERSION);
	}
	
	//@Test
	public void printConceptMap() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		.when().get("/ConceptMap")
		.prettyPrint();
	}
	
	@Ignore
	@Test
	public void conceptMapsTest() throws Exception {
		
		String simpleMapTypeRefsetId = mapTypeRefSetIds.get(0);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.when().get("/ConceptMap")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("total", notNullValue())
			.root("entry.find { it.fullUrl == 'http://localhost:"+RestExtensions.getPort()+"/snowowl/fhir/ConceptMap/snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + simpleMapTypeRefsetId + "'}")
			.appendRoot("resource")
			.body("resourceType", equalTo("ConceptMap"))
			.body("id", equalTo("snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + simpleMapTypeRefsetId))
			.body("language", equalTo("en-us"))
			.body("url", startsWith("http://snomed.info/sct/version"))
			.body("identifier.use", equalTo("official"))
			.body("identifier.system", startsWith("http://snomed.info/sct/version"))
			.body("identifier.value", equalTo(simpleMapTypeRefsetId))
			
			.body("version", equalTo(FHIR_MAP_TYPE_REFSET_VERSION))
			.body("name", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("title", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("status", equalTo("active"))
			.appendRoot("group.find { it.source == 'http://snomed.info/sct' }")
			.body("sourceVersion", notNullValue())
			.body("target", equalTo("http://snomed.info/sct"))
			.appendRoot("element.find { it.code == '264395009' }")
			.body("display", equalTo("Microorganism"))
			.body("target[0].code", equalTo("MO"))
			.body("target[0].equivalence", equalTo("equivalent"))
			.detachRoot("element.find { it.code == '264395009' }")
			.appendRoot("element.find { it.code == '409822003' }")
			.body("display", equalTo("Bacteria"))
			.body("target[0].code", equalTo("Bacteria Target"))
			.body("target[0].equivalence", equalTo("equivalent"));
	}
	
	@Test
	public void conceptMapsUnsupportedParam() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("unsupportedParam", "value")
			.header("Prefer", "handling=strict")
			.when().get("/ConceptMap")
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("exception"));
	}

	@Ignore
	@Test
	public void getSimpleMapTypeConceptMapTest() {
		
		String simpleMapTypeRefsetId = mapTypeRefSetIds.get(0);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + simpleMapTypeRefsetId)
			.when().get("/ConceptMap/{id}")
			.then()
			.body("resourceType", equalTo("ConceptMap"))
			.body("id", equalTo("snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + simpleMapTypeRefsetId))
			.body("language", equalTo("en-us"))
			.body("url", startsWith("http://snomed.info/sct/version"))
			.body("identifier.use", equalTo("official"))
			.body("identifier.system", startsWith("http://snomed.info/sct/version"))
			.body("identifier.value", equalTo(simpleMapTypeRefsetId))
			
			.body("version", equalTo(FHIR_MAP_TYPE_REFSET_VERSION))
			.body("name", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("title", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("status", equalTo("active"))
			.root("group.find { it.source == 'http://snomed.info/sct' }")
			.body("sourceVersion", notNullValue())
			.body("target", equalTo("http://snomed.info/sct"))
			.appendRoot("element.find { it.code == '264395009' }")
			.body("display", equalTo("Microorganism"))
			.body("target[0].code", equalTo("MO"))
			.body("target[0].equivalence", equalTo("equivalent"))
			.detachRoot("element.find { it.code == '264395009' }")
			.appendRoot("element.find { it.code == '409822003' }")
			.body("display", equalTo("Bacteria"))
			.body("target[0].code", equalTo("Bacteria Target"))
			.body("target[0].equivalence", equalTo("equivalent"))
			.statusCode(200);
	}
	
	@Ignore
	@Test
	public void getComplexMapTypeConceptMapTest() {
		
		//second item is complex map
		String complexMapRefsetId = mapTypeRefSetIds.get(1);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + complexMapRefsetId)
			.when().get("/ConceptMap/{id}")
			.then()
			.body("resourceType", equalTo("ConceptMap"))
			.body("id", equalTo("snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + complexMapRefsetId))
			.body("language", equalTo("en-us"))
			.body("url", startsWith("http://snomed.info/sct/version"))
			.body("identifier.use", equalTo("official"))
			.body("identifier.system", startsWith("http://snomed.info/sct/version"))
			.body("identifier.value", equalTo(complexMapRefsetId))
			
			.body("version", equalTo(FHIR_MAP_TYPE_REFSET_VERSION))
			.body("name", equalTo(COMPLEX_MAP_TYPE_REFSET_NAME))
			.body("title", equalTo(COMPLEX_MAP_TYPE_REFSET_NAME))
			.body("status", equalTo("active"))
			.root("group.find { it.source == 'http://snomed.info/sct' }")
			.body("sourceVersion", notNullValue())
			.body("target", equalTo("http://snomed.info/sct"))
			.appendRoot("element.find { it.code == '264395009' }")
			.body("display", equalTo("Microorganism"))
			.body("target[0].code", equalTo("MO"))
			.body("target[0].equivalence", equalTo("unmatched"))
			.detachRoot("element.find { it.code == '264395009' }")
			.appendRoot("element.find { it.code == '409822003' }")
			.body("display", equalTo("Bacteria"))
			.body("target[0].code", equalTo("Bacteria Target"))
			.body("target[0].equivalence", equalTo("unmatched"))
			.statusCode(200);
	}
	
	@Ignore
	@Test
	public void getExtendedMapTypeConceptMapTest() {
		
		//second item is complex map
		String complexMapRefsetId = mapTypeRefSetIds.get(2);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + complexMapRefsetId)
			.when().get("/ConceptMap/{id}")
			.then()
			.body("resourceType", equalTo("ConceptMap"))
			.body("id", equalTo("snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + complexMapRefsetId))
			.body("language", equalTo("en-us"))
			.body("url", startsWith("http://snomed.info/sct/version"))
			.body("identifier.use", equalTo("official"))
			.body("identifier.system", startsWith("http://snomed.info/sct/version"))
			.body("identifier.value", equalTo(complexMapRefsetId))
			
			.body("version", equalTo(FHIR_MAP_TYPE_REFSET_VERSION))
			.body("name", equalTo(EXTENDED_MAP_TYPE_REFSET_NAME))
			.body("title", equalTo(EXTENDED_MAP_TYPE_REFSET_NAME))
			.body("status", equalTo("active"))
			.root("group.find { it.source == 'http://snomed.info/sct' }")
			.body("sourceVersion", notNullValue())
			.body("target", equalTo("http://snomed.info/sct"))
			.appendRoot("element.find { it.code == '264395009' }")
			.body("display", equalTo("Microorganism"))
			.body("target[0].code", equalTo("MO"))
			.body("target[0].equivalence", equalTo("unmatched"))
			.detachRoot("element.find { it.code == '264395009' }")
			.appendRoot("element.find { it.code == '409822003' }")
			.body("display", equalTo("Bacteria"))
			.body("target[0].code", equalTo("Bacteria Target"))
			.body("target[0].equivalence", equalTo("unmatched"))
			.statusCode(200);
	}
	
	@Test
	public void conceptMapsSummaryTest() throws Exception {
		
		String simpleMapTypeRefsetId = mapTypeRefSetIds.get(0);
		String mapTypeRefsetUri = "SNOMEDCT/" + FHIR_MAP_TYPE_REFSET_VERSION + "/refset/" + simpleMapTypeRefsetId;
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id",mapTypeRefsetUri)
			.param("_summary", true)
			.when().get("/ConceptMap/{id}")
			.then()
			.body("resourceType", equalTo("ConceptMap"))
			.body("meta.tag[0].code", equalTo("SUBSETTED"))
			.body("id", equalTo(mapTypeRefsetUri))
			.body("language", nullValue())
			.body("url", startsWith("http://snomed.info/sct/version"))
			.body("identifier.use", equalTo("official"))
			.body("identifier.system", startsWith("http://snomed.info/sct/version"))
			.body("identifier.value", equalTo(simpleMapTypeRefsetId))
			.body("version", equalTo(FHIR_MAP_TYPE_REFSET_VERSION))
			.body("name", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("title", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("status", equalTo("active"))
			.body("group", nullValue())
			.statusCode(200);
	}
	
	@Test
	public void getConceptMapByIdParam() {
		
		String simpleMapTypeRefsetId = mapTypeRefSetIds.get(0);
		String simpleMapTypeRefsetURI = "SNOMEDCT/" + FHIR_MAP_TYPE_REFSET_VERSION + "/103/" + simpleMapTypeRefsetId;
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.param("_id", simpleMapTypeRefsetURI) 
			.when().get("/ConceptMap")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(1))
			.root("entry.find { it.resource.id == '" + simpleMapTypeRefsetURI + "'}")
			
			.body("resource.resourceType", equalTo("ConceptMap"))
			.body("resource.id", equalTo(simpleMapTypeRefsetURI))
			.body("resource.url", startsWith("http://snomed.info/sct/version"))
			.body("resource.version", equalTo(FHIR_MAP_TYPE_REFSET_VERSION))
			.body("resource.title", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("resource.name", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("resource.status", equalTo("active"))
			
			.body("resource.language", equalTo("en-us"))
			.body("resource.identifier.use", equalTo("official"))
			.body("resource.identifier.system", startsWith("http://snomed.info/sct/version"))
			.body("resource.identifier.value", equalTo("41000154101"))
			
			.appendRoot("resource.group[0]")
			.body("source", equalTo("http://snomed.info/sct"))
			//.body("sourceVersion", equalTo("20210507"))
			.body("target", equalTo("http://snomed.info/sct"));
	}
	
	@Test
	public void getConceptMapsByIdsParam() {
		
		String simpleMapTypeRefsetId = mapTypeRefSetIds.get(0);
		String simpleMapTypeRefsetURI = "SNOMEDCT/" + FHIR_MAP_TYPE_REFSET_VERSION + "/103/" + simpleMapTypeRefsetId;
		String complexMapRefsetId = mapTypeRefSetIds.get(1);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
		 	.param("_id", simpleMapTypeRefsetURI, "SNOMEDCT/" + FHIR_MAP_TYPE_REFSET_VERSION + "/" + complexMapRefsetId) 
			.when().get("/ConceptMap")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("Bundle"))
			.body("type", equalTo("searchset"))
			.body("total", equalTo(2))
			.root("entry.find { it.resource.id == '" + simpleMapTypeRefsetURI + "'}")
			
			.body("resource.resourceType", equalTo("ConceptMap"))
			.body("resource.id", equalTo(simpleMapTypeRefsetURI))
			.body("resource.url", startsWith("http://snomed.info/sct/version"))
			.body("resource.version", equalTo(FHIR_MAP_TYPE_REFSET_VERSION))
			.body("resource.title", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("resource.name", equalTo(SIMPLE_MAP_TYPE_REFSET_NAME))
			.body("resource.status", equalTo("active"))
			
			.body("resource.language", equalTo("en-us"))
			.body("resource.identifier.use", equalTo("official"))
			.body("resource.identifier.system", startsWith("http://snomed.info/sct/version"))
			.body("resource.identifier.value", equalTo("41000154101"))
			
			.appendRoot("resource.group[0]")
			.body("source", equalTo("http://snomed.info/sct"))
			//.body("sourceVersion", equalTo("20210507"))
			.body("target", equalTo("http://snomed.info/sct"));
	}

	
}