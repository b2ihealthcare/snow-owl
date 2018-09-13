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
package com.b2international.snowowl.fhir.api.tests.endpoints.valueset;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;

import org.hamcrest.core.StringStartsWith;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.api.tests.FhirRestTest;
import com.b2international.snowowl.fhir.core.model.lookup.LookupResult;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.fhir.SnomedUri;

/**
 * ValueSet $validate operation REST end-point test cases
 * 
 * @since 7.0
 */
public class ValidateValueSetRestTest extends FhirRestTest {
	
	private static final String VALUE_SET_VERSION = "VALUE_SET_VERSION"; //$NON-NLS-N$
	private static final String VALUE_SET_NAME = "FHIR Automated Value Set"; //$NON-NLS-N$
	private static String valueSetId;
	
	@BeforeClass
	public static void setupValueSets() {
		String mainBranch = IBranchPath.MAIN_BRANCH;
		valueSetId = TestArtifactCreator.createValueSet(mainBranch, VALUE_SET_NAME, VALUE_SET_VERSION);
	}
	
	//validate (SNOMED CT ROOT is in the Test ValueSet)
	@Test
	public void validateValueSetTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "valuesetStore:MAIN/" + VALUE_SET_VERSION + ":" + valueSetId) 
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			//.param("version", "2018-01-31")
			.param("code", Concepts.ROOT_CONCEPT)
			.when().get("/ValueSet/{id}/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", equalTo("OK"))
			.statusCode(200);
	}

}
