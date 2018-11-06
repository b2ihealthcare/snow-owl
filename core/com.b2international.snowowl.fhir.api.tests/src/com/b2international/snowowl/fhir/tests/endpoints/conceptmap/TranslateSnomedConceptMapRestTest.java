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
package com.b2international.snowowl.fhir.tests.endpoints.conceptmap;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.hamcrest.core.IsNull;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.fhir.tests.FhirTestConcepts;
import com.b2international.snowowl.snomed.fhir.SnomedUri;

/**
 * Concept Map $translate REST tests for SNOMED Map type reference sets
 * @since 6.7
 */
public class TranslateSnomedConceptMapRestTest extends FhirRestTest {
	
	private static final String SIMPLE_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Map Type Reference Sets";
	private static final String COMPLEX_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Complex Map Type Reference Sets";
	private static final String EXTENDED_MAP_TYPE_REFSET_NAME = "FHIR Automated Test Extended Map Type Reference Sets";
	private static final String FHIR_MAP_TYPE_REFSET_VERSION = "FHIR_MAP_TYPE_REFSET_VERSION";
	
	protected static List<String> mapTypeRefSetIds;


	@BeforeClass
	public static void setupMaps() {
		String mainBranch = IBranchPath.MAIN_BRANCH;
		mapTypeRefSetIds = TestMapTypeReferenceSetCreator.createSimpleMapTypeReferenceSets(mainBranch, 
				SIMPLE_MAP_TYPE_REFSET_NAME, 
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
	
	@Test
	public void translateMappingTest() {
		
		String response = givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("code", FhirTestConcepts.MICROORGANISM) 
			.param("system", SnomedUri.SNOMED_BASE_URI_STRING)
			.param("targetsystem", SnomedUri.SNOMED_BASE_URI_STRING)
			.when()
			.get("/ConceptMap/$translate")
			.prettyPrint();
		
		//System.out.println("Response: " + response);
	}
	
	
	
}