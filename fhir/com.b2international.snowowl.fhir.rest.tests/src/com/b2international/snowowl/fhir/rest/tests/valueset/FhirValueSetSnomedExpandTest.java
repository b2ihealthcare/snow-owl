/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest.tests.valueset;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.Test;

import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * @since 8.0
 */
public class FhirValueSetSnomedExpandTest extends FhirRestTest {

	@Test
	public void expandSnomedCodeSystemURL() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SNOMEDCT_URL)
			.when().get("/ValueSet/$expand")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("expansion.total", equalTo(1943))
			.body("expansion.contains[0].code", equalTo("103335007"))
			.body("expansion.contains[0].system", equalTo(SNOMEDCT_URL))
			.body("expansion.contains[0].display", equalTo("Duration (attribute)"));
	}
	
	@Test
	public void expandSnomedCodeSystemURL_NoModuleFallbackToInt() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SnomedTerminologyComponentConstants.SNOMED_URI_SCT)
			.when().get("/ValueSet/$expand")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("expansion.total", equalTo(1943))
			.body("expansion.contains[0].code", equalTo("103335007"))
			.body("expansion.contains[0].system", equalTo(SNOMEDCT_URL)) // expanded resource has proper full system value
			.body("expansion.contains[0].display", equalTo("Duration (attribute)"));
	}
	
	@Test
	public void expandSnomedCodeSystemURL_FhirVsEmpty() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/900000000000207008?fhir_vs")
			.when().get("/ValueSet/$expand")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("expansion.total", equalTo(1943))
			.body("expansion.contains[0].code", equalTo("103335007"))
			.body("expansion.contains[0].system", equalTo(SNOMEDCT_URL))
			.body("expansion.contains[0].display", equalTo("Duration (attribute)"));
	}
	
	@Test
	public void expandSnomedCodeSystemURL_FhirVsIsaSubstance() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/900000000000207008?fhir_vs=isa/105590001")
			.when().get("/ValueSet/$expand")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("expansion.total", equalTo(4))
			.body("expansion.contains[0].code", equalTo("312412007"))
			.body("expansion.contains[0].system", equalTo(SNOMEDCT_URL))
			.body("expansion.contains[0].display", equalTo("Substance categorized functionally (substance)"));
	}
	
	@Test
	public void expandSnomedCodeSystemURL_FhirVsEclChildOfRoot() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/900000000000207008?fhir_vs=ecl/<!138875005")
			.when().get("/ValueSet/$expand")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("expansion.total", equalTo(16)) // minified dataset only has 16 top level nodes
			.body("expansion.contains[0].code", equalTo("105590001"))
			.body("expansion.contains[0].system", equalTo(SNOMEDCT_URL))
			.body("expansion.contains[0].display", equalTo("Substance (substance)"));
	}
	
	@Test
	public void expandSnomedCodeSystemURL_FhirVsRefSetRoot() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("activeOnly", true)
			.queryParam("url", SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/900000000000207008?fhir_vs=refset/900000000000489007") // testing concept inactivation indicator here to return only active concepts with active indicator
			.when().get("/ValueSet/$expand")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("expansion.total", equalTo(39))
			.body("expansion.contains[0].code", equalTo("103389009"))
			.body("expansion.contains[0].system", equalTo(SNOMEDCT_URL))
			.body("expansion.contains[0].display", equalTo("Route of administration (qualifier value)"));
	}
	
}
