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
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * CodeSystem $lookup operation for FHIR code systems REST end-point test cases
 * 
 * @since 6.6
 */
public class FhirCodeSystemLookupOperationTest extends FhirRestTest {

	@Test
	public void GET_CodeSystem_$lookup_NonExistentSystem() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", "unknown")
			.queryParam("code", "12345")
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(404)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("not_found"))
			.body("issue.diagnostics", hasItem("CodeSystem with identifier 'unknown' could not be found."));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_NonExistentCode() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SnomedTerminologyComponentConstants.SNOMED_URI_BASE)
			.queryParam("code", "12345")
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(404)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("not_found"))
			.body("issue.diagnostics", hasItem("Concept with identifier '12345' could not be found."));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_Existing() throws Exception {
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
	public void GET_CodeSystem_$lookup_Existing_WithProperty() throws Exception {
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
	public void GET_CodeSystem_$lookup_Existing_WithInvalidProperty() throws Exception {
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
		
		LookupRequest request = LookupRequest.builder()
				.coding(Coding.of(SnomedTerminologyComponentConstants.SNOMED_URI_BASE, Concepts.ROOT_CONCEPT))
				.build();
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toFhirParameters(request))
			.when().post(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("SNOMED CT Concept"));
	}
	
	@Test
	public void POST_CodeSystem_$lookup_Existing_Property() throws Exception {
		LookupRequest request = LookupRequest.builder()
				.coding(Coding.of(SnomedTerminologyComponentConstants.SNOMED_URI_BASE, Concepts.ROOT_CONCEPT))
				.addProperty("parent")
				.build();
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toFhirParameters(request))
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
	
	@Test
	public void POST_CodeSystem_$lookup_Existing_WithInvalidProperty() throws Exception {
		LookupRequest request = LookupRequest.builder()
					.coding(Coding.of(SnomedTerminologyComponentConstants.SNOMED_URI_BASE, Concepts.ROOT_CONCEPT))
					.addProperty("http://snomed.info/id/12345")
					.build();
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toFhirParameters(request))
			.when().post(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics", hasItem("Unrecognized properties [http://snomed.info/id/12345]."));
	}
	
}