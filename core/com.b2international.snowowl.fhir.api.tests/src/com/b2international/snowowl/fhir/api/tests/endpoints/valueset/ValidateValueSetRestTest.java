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

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.fhir.api.tests.FhirRestTest;
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
	
	//ValueSet
	//validate non-existent member code
	//@Test
	public void nonExistentValueSetsTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "valuesetStore:MAIN/" + VALUE_SET_VERSION + ":" + "invalid") 
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			//.param("version", "2018-01-31")
			.param("code", Concepts.ROOT_CONCEPT)
			.when().get("/ValueSet/{id}/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", startsWith("Could not find a valueset to check against"))
			.statusCode(200);
	}
	
	//ValueSet
	//validate non-existent member code
	//@Test
	public void referencedCodeMissingTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "valuesetStore:MAIN/" + VALUE_SET_VERSION + ":" + valueSetId) 
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			//.param("version", "2018-01-31")
			.param("code", "Invalid")
			.when().get("/ValueSet/{id}/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", startsWith("Could not find a valueset member"))
			.statusCode(200);
	}
	
	//ValueSet
	//validate version mismatch (SNOMED CT ROOT is in the Test ValueSet)
	//@Test
	public void versionMismatchTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "valuesetStore:MAIN/" + VALUE_SET_VERSION + ":" + valueSetId) 
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20170131")
			//.param("version", "2018-01-31")
			.param("code", Concepts.ROOT_CONCEPT)
			.when().get("/ValueSet/{id}/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", startsWith("Versions do not match"))
			.statusCode(200);
	}
	
	//validate (SNOMED CT ROOT is in the Test ValueSet)
	//@Test
	public void validsValueSetTest() throws Exception {
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
	
	//SNOMED CT by logical ID
	//invalid reference set
	//@Test
	public void nonExistingRefsetTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/2018-01-31:12345") //simple type refset
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", "273210008")
			.when().get("/ValueSet/{id}/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", startsWith("Could not find a valueset to check against"))
			.statusCode(200);
	}
	
	//SNOMED CT by logical ID
	//invalid member (Root is not a member)
	//@Test
	public void refsetReferencedCodeMissingTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/2018-01-31:723264001") //simple type refset
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", Concepts.ROOT_CONCEPT)
			.when().get("/ValueSet/{id}/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString",  startsWith("Could not find a valueset member"))
			.statusCode(200);
	}
	
	//SNOMED CT by logical ID
	//validate (273210008 is in the Refset)
	//@Test
	public void validateSnomedCTValueSetTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.pathParam("id", "snomedStore:MAIN/2018-01-31:723264001") //simple type refset
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", "273210008")
			.when().get("/ValueSet/{id}/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", equalTo("OK"))
			.statusCode(200);
	}
	
	//SNOMED CT by URL
	//validate (ROOT is NOT in the Refset)
	//@Test
	public void invalidImpliciteRefsetTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=refset/12345") 	//invalid
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", "273210008")
			.when().get("/ValueSet/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", startsWith("Could not find a valueset to check against"))
			.statusCode(200);
	}
	
	//SNOMED CT by URL
	//validate (ROOT is NOT in the Refset)
	//@Test
	public void invalidImpliciteRefsetMemberTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=refset/723264001") 	
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", Concepts.ROOT_CONCEPT) //invalid
			.when().get("/ValueSet/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", startsWith("Could not find a valueset member"))
			.statusCode(200);
	}
	
	//SNOMED CT by URL
	//validate (273210008 is in the Refset)
	//@Test
	public void validateImpliciteRefsetTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=refset/723264001") 	
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", "273210008")
			.when().get("/ValueSet/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", equalTo("OK"))
			.statusCode(200);
	}
	
	//SNOMED CT by URL
	//validate (ROOT is not in any Refsets) against all refsets
	@Test
	public void invalidMemberInAllImpliciteRefsetMemberTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=refset/723264001") 	
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", Concepts.ROOT_CONCEPT) //invalid
			.when().get("/ValueSet/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", startsWith("Could not find a valueset member"))
			.statusCode(200);
	}
	
	//SNOMED CT by URL
	//validate (273210008 is in a Refset) against all refsets
	@Test
	public void validateAlImpliciteRefsetTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=refset") //all refsets 	
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", "273210008")
			.when().get("/ValueSet/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", equalTo("OK"))
			.statusCode(200);
	}
	
	//SNOMED CT by URL
	//validate (ROOT is not in the iSA) against all <<273187009|Chinese auricular points| (26 children)
	@Test
	public void invalidMemberInIsAImpliciteRefsetTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=isa/273187009") //all refsets 	
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", Concepts.ROOT_CONCEPT) //invalid
			.when().get("/ValueSet/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(false))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", startsWith("Could not find a valueset member"))
			.statusCode(200);
	}
	
	//SNOMED CT by URL
	//validate (273187009 is in the iSA) against all <<273187009|Chinese auricular points| (26 children)
	@Test
	public void validMemberInIsAImpliciteRefsetTest() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.param("url", "http://snomed.info/sct?fhir_vs=isa/273187009") //all refsets 	
			.param("system", SnomedUri.SNOMED_INT_CORE_MODULE_URI.getUriValue() + "/version/20180131")
			.param("code", "273187009") //valid
			.when().get("/ValueSet/$validate-code")
			.then()
			.body("parameter[0].name", equalTo("result"))
			.body("parameter[0].valueBoolean", equalTo(true))
			.body("parameter[1].name", equalTo("message"))
			.body("parameter[1].valueString", equalTo("OK"))
			.statusCode(200);
	}

}
