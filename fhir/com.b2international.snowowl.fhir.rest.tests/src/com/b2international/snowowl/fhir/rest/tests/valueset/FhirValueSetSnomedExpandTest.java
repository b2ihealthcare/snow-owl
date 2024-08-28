/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

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
			.queryParam("url", RestExtensions.encodeQueryParameter(SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/900000000000207008?fhir_vs=ecl/<!138875005"))
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
	public void expandSnomedCodeSystemURL_IncludeDesignations() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", RestExtensions.encodeQueryParameter(SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/900000000000207008?fhir_vs=ecl/<!138875005"))
			.queryParam("includeDesignations", true)
			.when().get("/ValueSet/$expand")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("expansion.total", equalTo(16)) // minified dataset only has 16 top level nodes
			.body("expansion.contains[0].code", equalTo("105590001"))
			.body("expansion.contains[0].system", equalTo(SNOMEDCT_URL))
			.body("expansion.contains[0].display", equalTo("Substance (substance)"))
			.body("expansion.contains[0].designation[0].value", equalTo("Substance"))
			.body("expansion.contains[0].designation[0].language", equalTo("en"))
			.body("expansion.contains[0].designation[1].value", equalTo("Substance"))
			.body("expansion.contains[0].designation[1].language", equalTo("en-x-900000000000508004"))
			.body("expansion.contains[0].designation[2].value", equalTo("Substance"))
			.body("expansion.contains[0].designation[2].language", equalTo("en-x-900000000000509007"))
			.body("expansion.contains[0].designation[3].value", equalTo("Substance (substance)"))
			.body("expansion.contains[0].designation[3].language", equalTo("en"))
			.body("expansion.contains[0].designation[4].value", equalTo("Substance (substance)"))
			.body("expansion.contains[0].designation[4].language", equalTo("en-x-900000000000508004"))
			.body("expansion.contains[0].designation[5].value", equalTo("Substance (substance)"))
			.body("expansion.contains[0].designation[5].language", equalTo("en-x-900000000000509007"));
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
			.body("expansion.total", equalTo(0))
			.body("expansion.contains.code", nullValue());
	}
	
	@Test
	public void expandSnomedCodeSystemURL_AfterAndNext() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("url", RestExtensions.encodeQueryParameter(SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/900000000000207008?fhir_vs=ecl/<!138875005"))
			.queryParam("count", 5)
			.when().get("/ValueSet/$expand")
			.then()
			.statusCode(200)
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("expansion.total", equalTo(16))
			.body("expansion.contains", hasSize(5))
			.body("expansion.contains[0].code", equalTo("105590001"))
			.body("expansion.contains[0].system", equalTo(SNOMEDCT_URL))
			.body("expansion.contains[0].display", equalTo("Substance (substance)"))
			.body("expansion.next", endsWith(FHIR_ROOT_CONTEXT 
				+ "/ValueSet/$expand"
				+ "?url=http://snomed.info/sct/900000000000207008?fhir_vs=ecl/<!138875005"
				+ "&displayLanguage=en-US;q=0.8,en-GB;q=0.6,en;q=0.4"
				+ "&count=5"
				+ "&after=AoIpMjU0MjkxMDAwKTI1NDI5MTAwMA=="));
	}
	
	@Test
	public void expandOnNonExistentSnomedUrlShouldReturnError() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.accept(APPLICATION_FHIR_JSON)
			.body(Json.object(
				"resourceType", "Parameters",
				"parameter", Json.array(
					Json.object(
						"name", "url",
						"valueUri", "http://snomed.info/sct/83821000000107?fhir_vs=ecl/*"
					),
					Json.object(
						"name", "count",
						"valueInteger", 0
					)
				)
				
			))
			.when().post("/ValueSet/$expand")
			.then()
			.assertThat()
			.statusCode(404);
	}
	
	@Test
	public void expandSnomedViaPostRequiresUrlEncoding() throws Exception {
		var encodedEcl = RestExtensions.encodeQueryParameter("* {{ D term = wild:'*immersion*', active = true, typeId=900000000000013009}}{{ C active = true}}");
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.accept(APPLICATION_FHIR_JSON)
			.body(Json.object(
				"resourceType", "Parameters",
				"parameter", Json.array(
					Json.object(
						"name", "url",
						"valueUri", String.format("http://snomed.info/sct/900000000000207008?fhir_vs=ecl/%s", encodedEcl)
					),
					Json.object(
						"name", "count",
						"valueInteger", 0
					)
				)
				
			))
			.when().post("/ValueSet/$expand")
			.then()
			.assertThat()
			.log().ifValidationFails()
			.statusCode(200)
			.body("resourceType", equalTo("ValueSet"))
			.body("id", notNullValue())
			.body("expansion.total", equalTo(0));
	}
}
