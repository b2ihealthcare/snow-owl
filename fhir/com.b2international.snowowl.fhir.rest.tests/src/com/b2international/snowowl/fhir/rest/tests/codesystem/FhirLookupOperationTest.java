/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest.tests.codesystem;

import static com.b2international.snowowl.fhir.tests.FhirRestTest.Endpoints.CODESYSTEM_LOOKUP;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * CodeSystem $lookup operation for FHIR code systems REST end-point test cases
 * 
 * @since 6.6
 */
public class FhirLookupOperationTest extends FhirRestTest {

	@Test
	public void GET_CodeSystem_$lookup_NonExistent() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SnomedTerminologyComponentConstants.SNOMED_URI_BASE)
			.queryParam("code", "12345")
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(404)
			.log().ifStatusCodeIsEqualTo(404);
	}
	
	@Test
	public void GET_CodeSystem_$lookup_Existing() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SnomedTerminologyComponentConstants.SNOMED_URI_BASE)
			.queryParam("code", Concepts.ROOT_CONCEPT)
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("SNOMED CT Concept"));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_Existing_WithProperty() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SnomedTerminologyComponentConstants.SNOMED_URI_BASE)
			.queryParam("code", Concepts.ROOT_CONCEPT)
			.queryParam("property", "parent")
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("SNOMED CT Concept"))
			.body("parameter[2].name", equalTo("parent"))
			.body("parameter[2].valueString", equalTo("TODO"));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_Existing_WithInvalidProperty() {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SnomedTerminologyComponentConstants.SNOMED_URI_BASE)
			.queryParam("code", Concepts.ROOT_CONCEPT)
			.queryParam("property", "name")
			.queryParam("property", "http://snomed.info/id/116676008") //associated morphology
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Unrecognized properties [name, http://snomed.info/id/116676008]. Supported properties are: [parent, system, display, name, designation, version, child]."));
	}
	
	@Test
	public void POST_CodeSystem_$lookup_Existing() throws Exception {
		
		Coding coding = Coding.builder()
				.system(SnomedTerminologyComponentConstants.SNOMED_URI_BASE)
				.code(Concepts.ROOT_CONCEPT)
				.build();

		LookupRequest request = LookupRequest.builder()
				.coding(coding)
				.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		
		String jsonBody = objectMapper.writeValueAsString(fhirParameters);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(jsonBody)
			.when().post(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("SNOMED CT Concept"));
	}
	
	//POST with request body with property
	@Test
	public void POST_CodeSystem_$lookup_Existing_Property() throws Exception {
		
		Coding coding = Coding.builder()
			.system(SnomedTerminologyComponentConstants.SNOMED_URI_BASE)
			.code(Concepts.ROOT_CONCEPT)
			.build();

		LookupRequest request = LookupRequest.builder()
				.coding(coding)
				.addProperty("parent")
				.build();
		
		Fhir fhirParameters = new Parameters.Fhir(request);
		
		String jsonBody = objectMapper.writeValueAsString(fhirParameters);
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(jsonBody)
			.when().post(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("SNOMED CT Concept"))
			.body("parameter[1].name", equalTo("parent"))
			.body("parameter[1].valueString", equalTo("TODO"));
	}
	
}