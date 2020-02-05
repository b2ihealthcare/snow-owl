/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.endpoints.conceptmap;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.core.model.conceptmap.Match;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Json;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.fhir.tests.FhirTestConcepts;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.fhir.SnomedUri;

/**
 * Concept Map $translate REST tests for SNOMED Map type reference sets
 * @since 6.7
 */
public class TranslateSnomedConceptMapRestTest extends FhirRestTest {
	
	private static final String SIMPLE_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Map Type Reference Sets";
	private static final String COMPLEX_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Complex Map Type Reference Sets";
	private static final String COMPLEX_BLOCK_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Complex Map With Map Block Type Reference Sets";
	private static final String EXTENDED_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Extended Map Type Reference Sets";
	private static final String FHIR_MAP_TYPE_REFSET_VERSION = "FHIR_MAP_TYPE_REFSET_VERSION";
	
	protected static List<String> mapTypeRefSetIds;


	@BeforeClass
	public static void setupMaps() {
		String mainBranch = IBranchPath.MAIN_BRANCH;
		mapTypeRefSetIds = TestMapTypeReferenceSetCreator.createSimpleMapTypeReferenceSets(mainBranch, 
				SIMPLE_MAP_TYPE_REFSET_NAME, 
				COMPLEX_MAP_TYPE_REFSET_NAME,
				COMPLEX_BLOCK_MAP_TYPE_REFSET_NAME,
				EXTENDED_MAP_TYPE_REFSET_NAME,
				FHIR_MAP_TYPE_REFSET_VERSION);
	}
	
	@Test
	public void translateMappingTest() throws Exception {
		
		String response = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("code", FhirTestConcepts.MICROORGANISM) 
			.param("system", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("targetsystem", SnomedUri.SNOMED_BASE_URI_STRING)
			.when()
			.get("/ConceptMap/$translate")
			.asString();
		
		Fhir parameters = objectMapper.readValue(response, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		
		TranslateResult result = objectMapper.convertValue(json, TranslateResult.class);
		
		assertTrue(result.getResult());
		assertEquals("4 match(es).", result.getMessage());
		
		Collection<Match> matches = result.getMatches();
		assertEquals(4, matches.size());
		
		assertMatchExists(matches, 0, "equivalent");
		assertMatchExists(matches, 1, "unmatched");
		assertMatchExists(matches, 2, "unmatched");
		assertMatchExists(matches, 3, "unmatched");
	}
	
	private void assertMatchExists(Collection<Match> matches, int refSetIndex, String equivalenceValue) {
		Optional<Match> optionalMatch = matches.stream()
				.filter(m -> m.getSource().getUriValue().equals("http://snomed.info/sct/id/" + mapTypeRefSetIds.get(refSetIndex)))
				.findFirst();
			
		assertTrue(optionalMatch.isPresent());
		
		Match match = optionalMatch.get();
		assertEquals(equivalenceValue, match.getEquivalence().getCodeValue());
		assertEquals("MO", match.getConcept().getCodeValue());
	}

	@Test
	public void reverseTranslateMappingTest() throws Exception {
		
		String response = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("code", "MO") 
			.param("system", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("targetsystem", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("reverse", true)
			.when()
			.get("/ConceptMap/$translate")
			.then()
			.extract()
			.asString();
		
		Fhir parameters = objectMapper.readValue(response, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		
		TranslateResult result = objectMapper.convertValue(json, TranslateResult.class);
		
		assertTrue(result.getResult());
		assertEquals("4 match(es).", result.getMessage());
		
		Collection<Match> matches = result.getMatches();
		assertEquals(4, matches.size());
		
		assertMatchExists(matches, 0, "equivalent");
		assertMatchExists(matches, 1, "unmatched");
		assertMatchExists(matches, 2, "unmatched");
		assertMatchExists(matches, 3, "unmatched");
	}
	
	//From a specific Map type reference set
	@Test
	public void nonExistingRefsetTest() throws Exception {
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":invalid")
			.param("code", FhirTestConcepts.MICROORGANISM) 
			.param("system", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("targetsystem", SnomedUri.SNOMED_BASE_URI_STRING)
			.when()
			.get("/ConceptMap/{id}/$translate")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.root("issue[0]")
			.body("severity", equalTo("error"))
			.body("code", equalTo("exception"))
			.body("diagnostics", equalTo("Map type reference set with identifier 'invalid' could not be found."))
			.statusCode(404);
	}
	
	//From a specific Map type reference set
	@Test
	public void invalidSystemTest() throws Exception {
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + mapTypeRefSetIds.get(0))
			.param("code", FhirTestConcepts.MICROORGANISM) 
			.param("system", "some_other_than_snomed_system") //invalid
			.param("targetsystem", SnomedUri.SNOMED_BASE_URI_STRING)
			.when()
			.get("/ConceptMap/{id}/$translate")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.root("issue[0]")
			.body("severity", equalTo("error"))
			.body("code", equalTo("invalid"))
			.body("diagnostics", equalTo("Source system URI 'some_other_than_snomed_system' is invalid (not SNOMED CT)."))
			.statusCode(400);
	}
	
	//From a specific Map type reference set
	@Test
	public void invalidTargetTest() throws Exception {
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + mapTypeRefSetIds.get(0))
			.param("code", FhirTestConcepts.MICROORGANISM) 
			.param("system", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("targetsystem", "Invalid_target_codesystem") //invalid
			.when()
			.get("/ConceptMap/{id}/$translate")
			.then()
			.body("resourceType", equalTo("OperationOutcome"))
			.root("issue[0]")
			.body("severity", equalTo("error"))
			.body("code", equalTo("invalid"))
			.body("diagnostics", equalTo("Target system 'Invalid_target_codesystem' not found or invalid."))
			.statusCode(400);
	}
	
	//From a specific Map type reference set
	@Test
	public void noResultTest() throws Exception {
			
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + mapTypeRefSetIds.get(0))
			.param("code", Concepts.ROOT_CONCEPT)  //ROOT has no mapping
			.param("system", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("targetsystem", SnomedUri.SNOMED_BASE_URI_STRING)
			.when()
			.get("/ConceptMap/{id}/$translate")
			.then()
			.body("resourceType", equalTo("Parameters"))
			.root("parameter[0]")
			.body("name", equalTo("result"))
			.body("valueBoolean", equalTo(false))
			.statusCode(200);
	}
	
	//From a specific Map type reference set
	@Test
	public void translateSpecificMappingTest() throws Exception {
		
		String response = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + mapTypeRefSetIds.get(0))
			.param("code", FhirTestConcepts.MICROORGANISM) 
			.param("system", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("targetsystem", SnomedUri.SNOMED_BASE_URI_STRING)
			.when()
			.get("/ConceptMap/{id}/$translate")
			.asString();
		
		Fhir parameters = objectMapper.readValue(response, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		
		TranslateResult result = objectMapper.convertValue(json, TranslateResult.class);
		
		assertTrue(result.getResult());
		assertTrue(result.getMessage().startsWith("Results for reference set"));
		
		Collection<Match> matches = result.getMatches();
		assertEquals(1, matches.size());
		
		Optional<Match> optionalMatch = matches.stream()
			.filter(m -> m.getSource().getUriValue().equals("http://snomed.info/sct/id/" + mapTypeRefSetIds.get(0)))
			.findFirst();
		
		assertTrue(optionalMatch.isPresent());
		
		Match match = optionalMatch.get();
		assertEquals("equivalent", match.getEquivalence().getCodeValue());
		assertEquals("MO", match.getConcept().getCodeValue());
		
	}
	
	//From a specific Map type reference set
	@Test
	public void reverseTranslateSpecificMappingTest() throws Exception {
		
		String response = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/" + FHIR_MAP_TYPE_REFSET_VERSION + ":" + mapTypeRefSetIds.get(0))
			.param("code", "MO") 
			.param("system", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("targetsystem", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("reverse", true)
			.when()
			.get("/ConceptMap/{id}/$translate")
			.asString();
		
		Fhir parameters = objectMapper.readValue(response, Parameters.Fhir.class);
		Json json = new Parameters.Json(parameters);
		
		TranslateResult result = objectMapper.convertValue(json, TranslateResult.class);
		
		assertTrue(result.getResult());
		assertTrue(result.getMessage().startsWith("Results for reference set"));
		
		Collection<Match> matches = result.getMatches();
		assertEquals(1, matches.size());
		
		Optional<Match> optionalMatch = matches.stream()
			.filter(m -> m.getSource().getUriValue().equals("http://snomed.info/sct/id/" + mapTypeRefSetIds.get(0)))
			.findFirst();
		
		assertTrue(optionalMatch.isPresent());
		
		Match match = optionalMatch.get();
		assertEquals("equivalent", match.getEquivalence().getCodeValue());
		assertEquals("MO", match.getConcept().getCodeValue());
		
	}
	
}