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
package com.b2international.snowowl.fhir.tests;

import org.junit.BeforeClass;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.tests.endpoints.valueset.TestReferenceSetCreator;

/**
 * Common class for SNOMED CT Rest test cases
 * @since 7.1
 *
 */
public class SnomedFhirRestTest extends FhirRestTest {
	
	protected static final String FHIR_SIMPLE_TYPE_REFSET_VERSION = "FHIR_SIMPLE_TYPE_REFSET_VERSION"; //$NON-NLS-N$
	protected static final String SIMPLE_TYPE_REFSET_NAME = "FHIR Automated Test Simple Type Reference Set"; //$NON-NLS-N$
	protected static String simpleTypeRefSetId;

	protected static final String FHIR_QUERY_TYPE_REFSET_VERSION = "FHIR_QUERY_TYPE_REFSET_VERSION"; //$NON-NLS-N$
	protected static final String QUERY_TYPE_REFSET_NAME = "FHIR Automated Test Query Type Refset";
	protected static String queryTypeRefsetLogicalId;
	
	@BeforeClass
	public static void setupValueSets() {
		String mainBranch = IBranchPath.MAIN_BRANCH;
		simpleTypeRefSetId = TestReferenceSetCreator.createSimpleTypeReferenceSet(mainBranch, SIMPLE_TYPE_REFSET_NAME, FHIR_SIMPLE_TYPE_REFSET_VERSION);
		queryTypeRefsetLogicalId = TestReferenceSetCreator.createQueryTypeReferenceSet(mainBranch, QUERY_TYPE_REFSET_NAME, FHIR_QUERY_TYPE_REFSET_VERSION);
	}

}
