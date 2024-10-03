/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.b2international.snowowl.fhir.rest.tests.FhirRestTest.Endpoints.CODESYSTEM_LOOKUP;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import java.util.List;

import org.hl7.fhir.r5.model.Coding;
import org.junit.Test;

import com.b2international.fhir.r5.operations.CodeSystemLookupParameters;
import com.b2international.snowowl.fhir.rest.tests.FhirRestTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests;

/**
 * CodeSystem $lookup operation for FHIR code systems REST end-point test cases
 * 
 * @since 6.6
 */
public class FhirSnomedCodeSystemLookupTest extends FhirRestTest {

	private static final String CLINICAL_FINDING = "404684003";

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
			.body("issue.code", hasItem("not-found"))
			.body("issue.diagnostics", hasItem("CodeSystem with identifier 'unknown' could not be found."));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_NonExistentCode() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SNOMEDCT_URL)
			.queryParam("code", "12345")
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(404)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("not-found"))
			.body("issue.diagnostics", hasItem("Concept with identifier '12345' could not be found."));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_Existing() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SNOMEDCT_URL)
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
	public void GET_CodeSystem_$lookup_Existing_Versioned() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", CodeSystemRestRequests.getSnomedIntUrl("version/20020131"))
			.queryParam("code", Concepts.ROOT_CONCEPT)
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT/2002-01-31"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("SNOMED CT Concept"))
			.body("parameter[2].name", equalTo("version"))
			.body("parameter[2].valueString", equalTo("2002-01-31"));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_Existing_Versioned_ViaVersionField() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", String.join("/", SnomedTerminologyComponentConstants.SNOMED_URI_SCT, Concepts.MODULE_SCT_CORE))
			.queryParam("version", "20020131")
			.queryParam("code", Concepts.ROOT_CONCEPT)
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT/2002-01-31"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("SNOMED CT Concept"))
			.body("parameter[2].name", equalTo("version"))
			.body("parameter[2].valueString", equalTo("2002-01-31"));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_Existing_WithProperty() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SNOMEDCT_URL)
			.queryParam("code", CLINICAL_FINDING)
			.queryParam("property", "parent")
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("Clinical finding"))
			.body("parameter[2].name", equalTo("property"))
			.body("parameter[2].part[0].valueCode", equalTo("parent"))
			.body("parameter[2].part[1].valueCode", equalTo(Concepts.ROOT_CONCEPT))
			.body("parameter[2].part[2].valueString", equalTo("SNOMED CT Concept"));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_Existing_WithInvalidProperty() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SNOMEDCT_URL)
			.queryParam("code", Concepts.ROOT_CONCEPT)
			.queryParam("property", "name")
			.queryParam("property", "http://snomed.info/id/12345")
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics[0]", containsString("Unrecognized property [http://snomed.info/id/12345]."));
	}
	
	@Test
	public void GET_CodeSystem_$lookup_Designations() throws Exception {
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.queryParam("system", SNOMEDCT_URL)
			.queryParam("code", CLINICAL_FINDING)
			.queryParam("property", "designation")
			.queryParam("_format", "json")
			.when().get(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("Clinical finding"))
			.body("parameter[2].name", equalTo("designation"))
			.body("parameter[2].part[0].valueCode", equalTo("en"))
			.body("parameter[2].part[1].valueCoding.code", equalTo("900000000000013009"))
			.body("parameter[2].part[2].valueString", equalTo("Clinical finding"))
			.body("parameter[3].name", equalTo("designation"))
			.body("parameter[3].part[0].valueCode", equalTo("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_UK))
			.body("parameter[3].part[1].valueCoding.code", equalTo("900000000000013009"))
			.body("parameter[3].part[2].valueString", equalTo("Clinical finding"))
			.body("parameter[4].name", equalTo("designation"))
			.body("parameter[4].part[0].valueCode", equalTo("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US))
			.body("parameter[4].part[1].valueCoding.code", equalTo("900000000000013009"))
			.body("parameter[4].part[2].valueString", equalTo("Clinical finding"))
			.body("parameter[5].name", equalTo("designation"))
			.body("parameter[5].part[0].valueCode", equalTo("en"))
			.body("parameter[5].part[1].valueCoding.code", equalTo("900000000000003001"))
			.body("parameter[5].part[2].valueString", equalTo("Clinical finding (finding)"))
			.body("parameter[6].name", equalTo("designation"))
			.body("parameter[6].part[0].valueCode", equalTo("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_UK))
			.body("parameter[6].part[1].valueCoding.code", equalTo("900000000000003001"))
			.body("parameter[6].part[2].valueString", equalTo("Clinical finding (finding)"))
			.body("parameter[7].name", equalTo("designation"))
			.body("parameter[7].part[0].valueCode", equalTo("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US))
			.body("parameter[7].part[1].valueCoding.code", equalTo("900000000000003001"))
			.body("parameter[7].part[2].valueString", equalTo("Clinical finding (finding)"));
	}
	
	@Test
	public void POST_CodeSystem_$lookup_Existing() throws Exception {
		
		var parameters = new CodeSystemLookupParameters()
				.setCoding(new Coding().setSystem(SNOMEDCT_URL).setCode(Concepts.ROOT_CONCEPT));
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toJson(parameters.getParameters()))
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
		var parameters = new CodeSystemLookupParameters()
				.setCoding(new Coding().setSystem(SNOMEDCT_URL).setCode(CLINICAL_FINDING))
				.setProperty(List.of("parent"));
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toJson(parameters.getParameters()))
			.when().post(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(200)
			.body("resourceType", equalTo("Parameters"))
			.body("parameter[0].name", equalTo("name"))
			.body("parameter[0].valueString", equalTo("SNOMEDCT"))
			.body("parameter[1].name", equalTo("display"))
			.body("parameter[1].valueString", equalTo("Clinical finding"))
			.body("parameter[2].name", equalTo("property"))
			.body("parameter[2].part[0].valueCode", equalTo("parent"))
			.body("parameter[2].part[1].valueCode", equalTo(Concepts.ROOT_CONCEPT))
			.body("parameter[2].part[2].valueString", equalTo("SNOMED CT Concept"));
	}
	
	@Test
	public void POST_CodeSystem_$lookup_Existing_WithInvalidProperty() throws Exception {
		var parameters = new CodeSystemLookupParameters()
				.setCoding(new Coding().setSystem(SNOMEDCT_URL).setCode(Concepts.ROOT_CONCEPT))
				.setProperty(List.of("http://snomed.info/id/12345"));
		
		givenAuthenticatedRequest(FHIR_ROOT_CONTEXT)
			.contentType(APPLICATION_FHIR_JSON)
			.body(toJson(parameters.getParameters()))
			.when().post(CODESYSTEM_LOOKUP)
			.then().assertThat()
			.statusCode(400)
			.body("resourceType", equalTo("OperationOutcome"))
			.body("issue.severity", hasItem("error"))
			.body("issue.code", hasItem("invalid"))
			.body("issue.diagnostics[0]", containsString("Unrecognized property [http://snomed.info/id/12345]."));
	}
	
}